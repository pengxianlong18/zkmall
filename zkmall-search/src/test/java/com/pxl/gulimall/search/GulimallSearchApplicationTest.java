package com.pxl.zkmall.search;


import com.alibaba.fastjson.JSON;
import com.pxl.zkmall.search.config.ElasticSearchConfig;
import lombok.Data;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@SpringBootTest
@RunWith(SpringRunner.class)
public class zkmallSearchApplicationTest {


    @Autowired
    private RestHighLevelClient client;

    @Data
    static public class data {

        private int account_number;
        private int balance;
        private String firstname;
        private String lastname;
        private int age;
        private String gender;
        private String address;
        private String employer;
        private String email;
        private String city;
        private String state;
    }


    @Test
    public void searchData() throws IOException {
        //1 创建检索请求
        SearchRequest searchRequest = new SearchRequest();
        // 指定检索
        searchRequest.indices("blank");
        //指定DSL，检索条件  new SearchSourceBuilder
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //1.1 构造检索条件
        sourceBuilder.query(QueryBuilders.matchQuery("address","mill"));
        //1.2按照年龄聚会
        TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms("ageAgg").field("age").size(10);
        sourceBuilder.aggregation(aggregationBuilder);

        //1.3 计算平均薪资
        AvgAggregationBuilder balanceAvg = AggregationBuilders.avg("balanceAvg").field("balance");
        sourceBuilder.aggregation(balanceAvg);

        System.out.println(sourceBuilder.toString());
        searchRequest.source(sourceBuilder);

        //2 执行检索
        SearchResponse search = client.search(searchRequest, ElasticSearchConfig.COMMON_OPTIONS);


        // 3、分析结果
        System.out.println(search.toString());
        //Map map = JSON.parseObject(search.toString(), Map.class);

        SearchHits hits = search.getHits();
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            String sourceAsString = hit.getSourceAsString();
            data data = JSON.parseObject(sourceAsString, data.class);
            System.out.println(data);

        }

        //3 .2 获取检索到的信息
        Aggregations aggregations = search.getAggregations();
        Terms ageAgg = aggregations.get("ageAgg");
        for (Terms.Bucket bucket : ageAgg.getBuckets()) {
            System.out.println("年龄：" + bucket.getKeyAsNumber());
        }

        Avg balanceAvg1 = aggregations.get("balanceAvg");
        System.out.println("平均薪资：" + balanceAvg1.getValue());
    }

    /**
     * 测试存储数据es
     */
    @Test
    public void testEs() throws IOException {
        IndexRequest indexRequest = new IndexRequest("users");
        indexRequest.id("1");
        User user = new User();
        user.setName("张三");
        user.setAge(20);
        String jsonString = JSON.toJSONString(user);
        indexRequest.source(jsonString, XContentType.JSON);

        IndexResponse index = client.index(indexRequest, ElasticSearchConfig.COMMON_OPTIONS);

        System.out.println(index);

    }

    @Data
    class User{
        private String name;
        private Integer age;
    }

    @Test
    public void connect(){
        System.out.println(client);
    }

}
