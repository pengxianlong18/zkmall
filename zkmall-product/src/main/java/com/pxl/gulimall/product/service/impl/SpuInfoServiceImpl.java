package com.pxl.zkmall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.pxl.common.constant.ProductConstant;
import com.pxl.common.to.SkuHasStockVo;
import com.pxl.common.to.SkuReductionTo;
import com.pxl.common.to.SpuBoundTo;
import com.pxl.common.to.es.SkuEsModel;
import com.pxl.common.utils.R;
import com.pxl.zkmall.product.entity.*;
import com.pxl.zkmall.product.feign.CouponFeignService;
import com.pxl.zkmall.product.feign.SearchFeignService;
import com.pxl.zkmall.product.feign.WareFeignService;
import com.pxl.zkmall.product.service.*;
import com.pxl.zkmall.product.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pxl.common.utils.PageUtils;
import com.pxl.common.utils.Query;

import com.pxl.zkmall.product.dao.SpuInfoDao;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    SkuInfoService skuInfoService;
    @Autowired
    SpuInfoDescService descService;
    @Autowired
    SpuInfoService spuInfoService;
    @Autowired
    SpuImagesService imagesService;
    @Autowired
    AttrService attrService;
    @Autowired
    ProductAttrValueService  productAttrValueService;

    @Autowired
    SkuImagesService skuImagesService;
    @Autowired
    SkuSaleAttrValueService saleAttrValueService;
    @Autowired
    CouponFeignService couponFeignService;

    @Autowired
    BrandService brandService;
    @Autowired
    CategoryService categoryService;

    @Autowired
    WareFeignService wareFeignService;
    @Autowired
    SearchFeignService searchFeignService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveVo saveVo) {
        //1保存spu基本信息 pms_spu_info
        SpuInfoEntity infoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(saveVo,infoEntity);
        infoEntity.setCreateTime(new Date());
        infoEntity.setUpdateTime(new Date());
        this.saveBsaeInfo(infoEntity);

        //2、保存spu的描述信息pms_spu_info_desc
        List<String> decript = saveVo.getDecript();
        SpuInfoDescEntity descEntity = new SpuInfoDescEntity();
        descEntity.setSpuId(infoEntity.getId());
        descEntity.setDecript(String.join(",",decript));
        descService.saveSpuInfoDesc(descEntity);

        //3、保存spu的图片集 pms_spu_images
        List<String> images = saveVo.getImages();
        imagesService.saveImages(infoEntity.getId(),images);

        //4、保存spu的规格参数;pms_product_attr_value
        List<BaseAttrs> baseAttrs = saveVo.getBaseAttrs();
        List<ProductAttrValueEntity> collect = baseAttrs.stream().map((attr) -> {
            ProductAttrValueEntity valueEntity = new ProductAttrValueEntity();
            valueEntity.setAttrId(attr.getAttrId());
            AttrEntity attrEntity = attrService.getById(attr.getAttrId());
            valueEntity.setAttrName(attrEntity.getAttrName());
            valueEntity.setQuickShow(attr.getShowDesc());
            valueEntity.setSpuId(infoEntity.getId());
            valueEntity.setAttrValue(attr.getAttrValues());

            return valueEntity;
        }).collect(Collectors.toList());
        productAttrValueService.saveProductAttr(collect);

        //5、保存spu的积分信息；zkmall_sms->sms_spu_bounds
        SpuBoundTo spuBoundTo = new SpuBoundTo();
        Bounds bounds = saveVo.getBounds();
        BeanUtils.copyProperties(bounds,spuBoundTo);
        spuBoundTo.setSpuId(infoEntity.getId());
        R r = couponFeignService.saveSpuBounds(spuBoundTo);
        if (r.getCode() != 0){
            log.error("远程保存spu积分信息失败");
        }

        //5、保存当前spu对应的所有sku信息
        //5.1、sku的基本信息；pms_sku_info
        List<Skus> skus = saveVo.getSkus();
        if (skus != null && skus.size() > 0){
            skus.forEach((item)->{
                String defaultImg = "";
                for (Images image : item.getImages()) {
                    if (image.getDefaultImg() == 1){
                        defaultImg=image.getImgUrl();
                    }
                }
                /**
                 *  private String skuName;
                 *     private BigDecimal price;
                 *     private String skuTitle;
                 *     private String skuSubtitle;
                 */
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(item,skuInfoEntity);
                skuInfoEntity.setSpuId(infoEntity.getId());
                skuInfoEntity.setCatalogId(infoEntity.getCatalogId());
                skuInfoEntity.setBrandId(infoEntity.getBrandId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSkuDefaultImg(defaultImg);
                //5.1、sku的基本信息；pms_sku_info
                skuInfoService.saveSkuInfo(skuInfoEntity);


                Long skuId = skuInfoEntity.getSkuId();
                List<SkuImagesEntity> imagesEntities = item.getImages().stream().map((img) -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setImgUrl(img.getImgUrl());
                    skuImagesEntity
                            .setDefaultImg(img.getDefaultImg());
                    return skuImagesEntity;
                }).filter((entity)->{

                    return !StringUtils.isEmpty(entity.getImgUrl());
                }).collect(Collectors.toList());
                //5.2、sku的图片信息  pms_sku_images
                // 这里需要判断一下，如果图片地址为空，就不进行保存
                skuImagesService.saveBatch(imagesEntities);

                //5.3、sku的销售属性信息 pms_sku_sale_attr_value
                List<Attr> attr = item.getAttr();
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attr.stream().map((a) -> {
                    SkuSaleAttrValueEntity saleAttrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(a, saleAttrValueEntity);
                    saleAttrValueEntity.setSkuId(skuId);
                    return saleAttrValueEntity;
                }).collect(Collectors.toList());
                saleAttrValueService.saveBatch(skuSaleAttrValueEntities);

                //5.4 sku的优惠、满减等信息；zkmall_sms -》 sms_sku_ladder
                //zkmall_sms -》 sms_sku_full_reduction
                //zkmall_sms -》 sms_member_price

                SkuReductionTo skuReductionTo = new SkuReductionTo();

                BeanUtils.copyProperties(item,skuReductionTo);
                skuReductionTo.setSkuId(skuId);
                if (skuReductionTo.getFullCount() > 0 || skuReductionTo.getFullPrice()
                        .compareTo(new BigDecimal("0")) == 1){
                    R r1 = couponFeignService.saveSkuReduction(skuReductionTo);
                    if (r1.getCode() != 0){
                        log.error("远程保存sku优惠信息失败");
                    }
                }

            });
        }
    }

    //保存spu基本信息
    @Override
    public void saveBsaeInfo(SpuInfoEntity infoEntity) {
        this.baseMapper.insert(infoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> queryWrapper = new QueryWrapper<>();

        /**
         * status
         * key
         * brandId
         * catelogId
         */
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)){
            queryWrapper.and((wq)->{
                wq.eq("id",key).or().like("spu_name",key);
            });
        }

        String status = (String) params.get("status");
        if (!StringUtils.isEmpty(status)){
            queryWrapper.eq("publish_status",status);
        }

        String brandId = (String) params.get("brandId");
        if (!StringUtils.isEmpty(brandId) && !"0".equals(brandId)){
            queryWrapper.eq("brand_id", brandId);
        }
        String catelogId = (String) params.get("catelogId");
        if (!StringUtils.isEmpty(catelogId) && !"0".equals(catelogId)){
            queryWrapper.eq("catalog_id", catelogId);
        }

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void spuUp(Long spuId) {

        //1、组装需要的数据
        SkuEsModel skuEsModel = new SkuEsModel();
        //1、查出当前spuId对应的sku信息，品牌名字
       List<SkuInfoEntity> skus = skuInfoService.getSkusBySpuId(spuId);
        List<Long> skuIds = skus.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());

        // TODO 4、查询当前sku的所有可以被检索的规格属性。
        List<ProductAttrValueEntity> entities = productAttrValueService.baseAttrListForSpu(spuId);
        List<Long> attrIds = entities.stream().map((attr) -> {
            return attr.getAttrId();
        }).collect(Collectors.toList());

        List<Long> attrs  =   attrService.selectSearchAttrs(attrIds);
        Set<Long> idSet =new HashSet<>(attrs);

        List<SkuEsModel.Attrs> attrsList = entities.stream().filter(item -> {
            return idSet.contains(item.getAttrId());
        }).map((item -> {
            SkuEsModel.Attrs attrs1 = new SkuEsModel.Attrs();
            BeanUtils.copyProperties(item, attrs1);
            return attrs1;
        })).collect(Collectors.toList());

        //TODO 1、发送远程调用，库存系统查询是否有库存
        Map<Long, Boolean> stockMap = null;
        try {
            R skuHasStock = wareFeignService.getSkuHasStock(skuIds);
            //
            TypeReference<List<SkuHasStockVo>> typeReference = new TypeReference<List<SkuHasStockVo>>() {};
            stockMap = skuHasStock.getData(typeReference).stream()
                    .collect(Collectors.toMap(SkuHasStockVo::getSkuId, item -> item.getHasStock()));
        } catch (Exception e) {
            log.error("库存服务查询异常：原因{}",e);
        }

        //2、封装每个sku信息
        Map<Long, Boolean> finalStockMap = stockMap;
        List<SkuEsModel> collect = skus.stream().map((sku) -> {
            SkuEsModel esModel = new SkuEsModel();
            BeanUtils.copyProperties(sku, esModel);
            //skuPrice,skuImg;
            esModel.setSkuPrice(sku.getPrice());
            esModel.setSkuImg(sku.getSkuDefaultImg());
            //hasStock;hotScore;
            if (finalStockMap == null){
                esModel.setHasStock(true);
            }else {
                esModel.setHasStock(finalStockMap.get(sku.getSkuId()));
            }

            //TODO 2、热度评分
            esModel.setHotScore(0L);

            //TODO 3、查询品牌和分类的信息   private String brandName; private String brandImg;
            BrandEntity brand = brandService.getById(esModel.getBrandId());
            esModel.setBrandName(brand.getName());
            esModel.setBrandImg(brand.getLogo());

            //   private String catalogName;
            CategoryEntity category = categoryService.getById(esModel.getCatalogId());
            esModel.setCatalogName(category.getName());

            // 设备检索属性
            esModel.setAttrs(attrsList);

            return esModel;
        }).collect(Collectors.toList());

        // TODO 5、将数据发送给es进行保存
        R r = searchFeignService.productStatusUp(collect);
        if (r.getCode() == 0 ){
            //远程调用成功
            // TODO 6. 修改当前spu的状态
           // baseMapper.updateSpuStatus(spuId, ProductConstant.StatusEnum.SPU_UP.getCode());
            SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
            spuInfoEntity.setId(spuId);
            spuInfoEntity.setPublishStatus(ProductConstant.StatusEnum.SPU_UP.getCode());
            this.updateById(spuInfoEntity);
        }else {
            //远程调用失败
            // TODO 7、重复调用？ 接口幂等性
        }


    }

    @Override
    public SpuInfoEntity getSpuInfoBySkuId(Long skuId) {
        SkuInfoEntity byId = skuInfoService.getById(skuId);
        Long spuId = byId.getSpuId();
        SpuInfoEntity spuInfoEntity = this.getById(spuId);
        Long brandId = spuInfoEntity.getBrandId();
        BrandEntity brandEntity = brandService.getById(brandId);
        spuInfoEntity.setBrandName(brandEntity.getName());

        return spuInfoEntity;
    }


}