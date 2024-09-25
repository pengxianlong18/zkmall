package com.pxl.zkmall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.pxl.common.to.es.SkuEsModel;
import com.pxl.common.utils.R;
import com.pxl.zkmall.search.config.ElasticSearchConfig;
import com.pxl.zkmall.search.constant.EsConstant;
import com.pxl.zkmall.search.feign.ProductFeignService;
import com.pxl.zkmall.search.service.MallSearchService;
import com.pxl.zkmall.search.vo.AttrResponseVo;
import com.pxl.zkmall.search.vo.BrandVo;
import com.pxl.zkmall.search.vo.SearchParam;
import com.pxl.zkmall.search.vo.SearchResult;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MallSearchServiceImpl implements MallSearchService {

    @Autowired
    private RestHighLevelClient client;
    @Autowired
    ProductFeignService productFeignService;

    @Override
    public SearchResult search(SearchParam param) {

        //1. 准备检索请求
        SearchRequest searchRequest = buildSearchRequest(param);

        SearchResult result = null;
        try {
            // 2、执行检索请求
            SearchResponse response = client.search(searchRequest, ElasticSearchConfig.COMMON_OPTIONS);

            //3、分析响应数据，封装成我们需要的格式
            result = buildSearchResult(response,param);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return  result;
    }

    /**
     *构建结果数据
     * @param response
     * @return
     */
    private SearchResult buildSearchResult(SearchResponse response,SearchParam param) {
        SearchResult result = new SearchResult();
       // 命中的记录数
        SearchHits hits = response.getHits();

        List<SkuEsModel> esModels =new ArrayList<>();
        if (hits.getHits() != null && hits.getHits().length > 0){
            for (SearchHit hit : hits.getHits()){
                String sourceAsString = hit.getSourceAsString();
                SkuEsModel esModel = JSON.parseObject(sourceAsString, SkuEsModel.class);
                //设置高亮
                if (!StringUtils.isEmpty(param.getKeyword())){
                    String skuTitle = hit.getHighlightFields().get("skuTitle").getFragments()[0].toString();
                    esModel.setSkuTitle(skuTitle);
                }
                esModels.add(esModel);
            }
        }
        result.setProducts(esModels);

        //当前所有商品涉及到的所有属性信息
        ParsedLongTerms catalog_agg = response.getAggregations().get("catalog_agg");
        List<? extends Terms.Bucket> buckets = catalog_agg.getBuckets();

        List<SearchResult.CatalogVo> catalogVos = new ArrayList<>();
        for (Terms.Bucket bucket : buckets) {
            SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();
            String keyAsString = bucket.getKeyAsString();
            catalogVo.setCatalogId(Long.parseLong(keyAsString));

            //得到分类名字
            ParsedStringTerms catalog_name_agg = bucket.getAggregations().get("catalog_name_agg");
            String catalog_name = catalog_name_agg.getBuckets().get(0).getKeyAsString();
            catalogVo.setCatalogName(catalog_name);
            catalogVos.add(catalogVo);
        }
        result.setCatalogs(catalogVos);

        //当前所有商品涉及到的所有品牌信息
        List<SearchResult.BrandVo> brandVos = new ArrayList<>();
        ParsedLongTerms brand_agg = response.getAggregations().get("brand_agg");
        List<? extends Terms.Bucket> brand_buckets = brand_agg.getBuckets();
        for (Terms.Bucket bucket : brand_buckets) {
            SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
            //品牌id
            long brandId = bucket.getKeyAsNumber().longValue();
            brandVo.setBrandId(brandId);
            //品牌名
            String brandName = ((ParsedStringTerms) bucket.getAggregations().get("brand_name_agg")).getBuckets().get(0).getKeyAsString();
            brandVo.setBrandName(brandName);
            //品牌图片
            String brandImg = ((ParsedStringTerms) bucket.getAggregations().get("brand_img_agg")).
                    getBuckets().get(0).getKeyAsString();
            brandVo.setBrandImg(brandImg);

            brandVos.add(brandVo);
        }
        result.setBrands(brandVos);

        // 当前所有商品涉及到的所有属性信息
        List<SearchResult.AttrVo> attrVos = new ArrayList<>();
        ParsedNested attr_agg = response.getAggregations().get("attr_agg");

        ParsedLongTerms attr_id_agg = attr_agg.getAggregations().get("attr_id_agg");
        for (Terms.Bucket bucket : attr_id_agg.getBuckets()) {
            SearchResult.AttrVo attrVo = new SearchResult.AttrVo();
            //属性的id
            long attrId = bucket.getKeyAsNumber().longValue();
            //属性的名字
            String attrName = ((ParsedStringTerms) bucket.getAggregations().get("attr_name_agg")).getBuckets().get(0).getKeyAsString();

            //属性的所有值
            List<String> attrValues = ((ParsedStringTerms) bucket.getAggregations().get("attr_value_agg")).getBuckets()
                    .stream().map(item -> {
                        String keyAsString = item.getKeyAsString();
                        return keyAsString;
                    }).collect(Collectors.toList());
            attrVo.setAttrId(attrId);
            attrVo.setAttrName(attrName);
            attrVo.setAttrValue(attrValues);

            attrVos.add(attrVo);
        }

        result.setAttrs(attrVos);
        //页码
        result.setPageNum(param.getPageNum());
        // 分页信息-总记录数
        long total = hits.getTotalHits().value;
        result.setTotal(total);
        //总页码
        int totalPage =  (int) (total + EsConstant.PRODUCT_PAGESIZE - 1) / EsConstant.PRODUCT_PAGESIZE;
        result.setTotalPages(totalPage);

        List<Integer> pageNavs = new ArrayList<>();
        int start, end;
        if (totalPage <= 5) {
            start = 1;
            end = totalPage;
        } else {
            if (param.getPageNum() <= 3) {
                start = 1;
                end = 5;
            } else if (param.getPageNum() >= totalPage - 2) {
                start = totalPage - 4;
                end = totalPage;
            } else {
                start = param.getPageNum() - 2;
                end = param.getPageNum() + 2;
            }
        }
        for (int i = start; i <= end; i++) {
            pageNavs.add(i);
        }
        result.setPageNavs(pageNavs);

        if (param.getAttrs() != null && param.getAttrs().size() > 0){
            // 构建面包屑导航
            List<SearchResult.NavVo> collect = param.getAttrs().stream().map(attr -> {
                SearchResult.NavVo navVo = new SearchResult.NavVo();
                String[] s = attr.split("_");
                navVo.setNavValue(s[1]);
                R r = productFeignService.attrInfo(Long.parseLong(s[0]));
                result.getAttrIds().add(Long.parseLong(s[0]));
                if (r.getCode() == 0) {
                    AttrResponseVo data = r.getData("attr", new TypeReference<AttrResponseVo>() {
                    });
                    navVo.setNavName(data.getAttrName());
                }else {
                    navVo.setNavName(s[0]);
                }

                // 取消这个面包屑以后，要跳转到某个地方，将请求地址的url里面的当前置空
                // 拿到所有的查询条件，去掉当前的
                String replace = replaceQueryString(param, attr,"attrs");
                navVo.setLink("http://search.zkmall.com/list.html?" + replace);
                return navVo;
            }).collect(Collectors.toList());

            result.setNavs(collect);
        }


        //品牌 分类
        if (param.getBrandId() != null && param.getBrandId().size() > 0) {
            List<SearchResult.NavVo> navs = result.getNavs();
            SearchResult.NavVo navVo = new SearchResult.NavVo();
            navVo.setNavName("品牌");
            R r = productFeignService.brandInfo(param.getBrandId());
            if (r.getCode() == 0){
                List<BrandVo> brand = r.getData("brand", new TypeReference<List<BrandVo>>() {
                });
                StringBuffer buffer = new StringBuffer();
                String replace = "";
                for (BrandVo brandVo : brand) {
                   buffer.append( brandVo.getName()+";");
                   replace = replaceQueryString(param, brandVo.getBrandId()+"","brandId");

                }
                navVo.setNavValue(buffer.toString());
                navVo.setLink("http://search.zkmall.com/list.html?" + replace);
            }
            navs.add(navVo);
        }

        // TODO 分类，不需要导航取消


        return result;
    }

    private String replaceQueryString(SearchParam param, String value,String key) {
        String encode = null;
        try {
            encode = URLEncoder.encode(value,"UTF-8");
            encode = encode.replace("%28","(").replace("%29",")").replace("+","%20");  //浏览器对空格的编码和Java不一样，差异化处理
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String replace = param.get_queryString().replace("&"+key+"=" + encode, "");
        return replace;
    }

    /**
     *  准备检索请求
     * @return
     */
    private SearchRequest buildSearchRequest(SearchParam param) {

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder(); //构建DSL语句

        /**
         * 模糊匹配，过滤（按照属性，分类，品牌，价格区间，库存）
         */
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        // 1.1 must
        if(!StringUtils.isEmpty(param.getKeyword())){
            boolQuery.must(QueryBuilders.matchQuery("skuTitle",param.getKeyword()));
        }
        // 1.2 filter 按照三级分类id查询
        if (param.getCatalog3Id() != null){
            boolQuery.filter(QueryBuilders.termQuery("catalogId",param.getCatalog3Id()));
        }
        // 1.2 filter 按照品牌id查询
        if (param.getBrandId() != null && param.getBrandId().size() > 0){
            boolQuery.filter(QueryBuilders.termsQuery("brandId",param.getBrandId()));
        }
        // 1.2 filter 按照所有指定的属性 attes=1_6寸:8寸
        if (param.getAttrs() != null && param.getAttrs().size() > 0){

            for (String attrStr :  param.getAttrs()){
                BoolQueryBuilder nestBoolQueryBuilder = QueryBuilders.boolQuery();

                String[] s = attrStr.split("_");
                String attrId =  s[0];
                String[] attrValue = s[1].split(":");

                nestBoolQueryBuilder.must(QueryBuilders.termQuery("attrs.attrId",attrId));
                nestBoolQueryBuilder.must(QueryBuilders.termsQuery("attrs.attrValue",attrValue));

                NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery("attrs", nestBoolQueryBuilder, ScoreMode.None);
                boolQuery.filter(nestedQuery);
            }


        }

        // 1.2 filter 按照库存查询 0无库存 ， 1 有库存
        if (param.getHasStock() != null){
            boolQuery.filter(QueryBuilders.termQuery("hasStock",param.getHasStock()==1));
        }

        // 1.2 filter 按照价格区间 1_600/_600/600_
        if (!StringUtils.isEmpty(param.getSkuPrice())){
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("skuPrice");
            String[] s = param.getSkuPrice().split("_");
            if (s.length == 2){
                rangeQuery.gte(s[0]).lte(s[1]);
            }else if (s.length == 1){
                if (param.getSkuPrice().startsWith("_")){
                    rangeQuery.lte(s[0]);
                }
                if (param.getSkuPrice().endsWith("_")){
                    rangeQuery.gte(s[0]);
                }
            }

            boolQuery.filter(rangeQuery);
        }

        // 把所有的条件都那俩进行封装
        sourceBuilder.query(boolQuery);

        /**
         * 排序、分页、高亮
         */
        // 2.1排序 sotr=hostScore_asc/desc
        if (!StringUtils.isEmpty(param.getSort())){
            String sort = param.getSort();
            String[] s = sort.split("_");
            SortOrder order = s[1].equalsIgnoreCase("asc")?SortOrder.ASC:SortOrder.DESC;
            sourceBuilder.sort(s[0],order);
        }

        //2.2 分页

        sourceBuilder.from((param.getPageNum()-1)*EsConstant.PRODUCT_PAGESIZE);
        sourceBuilder.size(EsConstant.PRODUCT_PAGESIZE);

        // 2.3 高亮
        if (!StringUtils.isEmpty(param.getKeyword())){
            HighlightBuilder builder =new HighlightBuilder();
            builder.field("skuTitle");
            builder.preTags("<b style = 'color:red'>");
            builder.postTags("</b>");
            sourceBuilder.highlighter(builder);
        }

        /**
         * 聚合分析
         */
        // 1、品牌聚合
        TermsAggregationBuilder brand_agg = AggregationBuilders.terms("brand_agg").field("brandId").size(50);

        //品牌的聚合
        brand_agg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName").size(1));
        brand_agg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg").size(1));

        sourceBuilder.aggregation(brand_agg);

        //2、分类聚合
        TermsAggregationBuilder catalog_agg = AggregationBuilders.terms("catalog_agg").field("catalogId").size(10);
        catalog_agg.subAggregation(AggregationBuilders.terms("catalog_name_agg").field("catalogName").size(1));
        sourceBuilder.aggregation(catalog_agg);

        //3、属性聚合
        NestedAggregationBuilder attr_agg = AggregationBuilders.nested("attr_agg", "attrs");

        TermsAggregationBuilder attr_id_agg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId").size(20);

        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_name_agg")
                .field("attrs.attrName").size(1));

         attr_id_agg.subAggregation(AggregationBuilders.terms("attr_value_agg")
                 .field("attrs.attrValue").size(50));

        attr_agg.subAggregation(attr_id_agg);
        sourceBuilder.aggregation(attr_agg);

        String s = sourceBuilder.toString();
//        System.out.println("构建DSL语句：" + s);
        SearchRequest searchRequest = new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX},  sourceBuilder);

        return searchRequest;
    }
}
