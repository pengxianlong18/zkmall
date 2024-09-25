package com.pxl.zkmall.product;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pxl.zkmall.product.dao.AttrGroupDao;
import com.pxl.zkmall.product.dao.SkuSaleAttrValueDao;
import com.pxl.zkmall.product.entity.BrandEntity;
import com.pxl.zkmall.product.entity.SpuInfoDescEntity;
import com.pxl.zkmall.product.service.BrandService;
import com.pxl.zkmall.product.service.CategoryService;
import com.pxl.zkmall.product.service.SpuInfoDescService;
import com.pxl.zkmall.product.vo.SpuItemAttrGroupVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class zkmallProductApplicationTests {

    @Autowired
    BrandService brandService;

    @Autowired
    CategoryService categoryService;

    @Resource
    StringRedisTemplate stringRedisTemplate;
    RedissonClient redissonClient;
    @Test
    public void redisson(){
        System.out.println(redissonClient);
    }
    @Test
    public void testStringRedisTemplate(){
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        ops.set("hello", "world"+ UUID.randomUUID().toString());
        System.out.println(ops.get("hello"));
    }

    @Autowired
    AttrGroupDao attrGroupDao;
    @Test
    public void test1(){
        List<SpuItemAttrGroupVo> attrGroupWithAttrsBySpuId
                = attrGroupDao.getAttrGroupWithAttrsBySpuId(13L, 225L);
        System.out.println(attrGroupWithAttrsBySpuId);
    }

    @Autowired
    SkuSaleAttrValueDao skuSaleAttrValueDao;
    @Autowired
    SpuInfoDescService spuInfoDescService;
    @Test
    public void test2(){
//        List<SkuItemSaleAttrVo> saleAttrBySpuId = skuSaleAttrValueDao.getSaleAttrBySpuId(13L);
//        System.out.println(saleAttrBySpuId);
        SpuInfoDescEntity spuInfoDescEntity = spuInfoDescService.getById(13L);
        System.out.println(spuInfoDescEntity);
    }

    @Test
    public void testFindPath(){
        Long[] catelogPath = categoryService.findCatelogPath(225L);
        log.info("完整路径：{}", Arrays.asList(catelogPath));
    }

//    @Autowired
//    OSSClient ossClient;
//    @Test
//    public  void testUpload() throws FileNotFoundException {
//
////        String endpoint = "oss-cn-guangzhou.aliyuncs.com";
////        String accessKeyId="LTAI5tFdNH1JoufQJo37YALc";
////        String accessKeySecret="HfncNr2nXaRSaagOg9vhlhYS1Mw7VN";
//
////        OSS ossClient=new OSSClientBuilder().build(endpoint,accessKeyId,accessKeySecret);
//
//        FileInputStream inputStream = new FileInputStream("D:\\BaiduNetdiskDownload\\谷粒商城\\谷粒商城\\课件和文档\\基础篇\\资料\\pics\\f205d9c99a2b4b01.jpg");
//        ossClient.putObject("pzkmall","bug.jpg",inputStream);
//        ossClient.shutdown();
//        System.out.println("上传完成");
//    }
    @Test
    public void test(){
        BrandEntity brandEntity = new BrandEntity();
//        brandEntity.setDescript("华为");
//        brandService.save(brandEntity);
//        System.out.println("保存成功");

//        brandEntity.setBrandId(1L);
//        brandEntity.setName("huawei");
//        brandService.updateById(brandEntity);
//        System.out.println("更新成功");

        List<BrandEntity> list = brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id", 1L));
        list.forEach((item)->{
            System.out.println(item);
        });


    }

}
