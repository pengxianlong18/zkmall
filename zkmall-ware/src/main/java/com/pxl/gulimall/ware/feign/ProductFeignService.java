package com.pxl.zkmall.ware.feign;

import com.pxl.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("zkmall-product")
public interface ProductFeignService {

    /**  product/skuinfo/info/{id}
     *  1、所有请求过网关 @FeignClient("zkmall-gateway")
     *      api/product/skuinfo/info/{id}
     *
     *    2、直接让后台指定服务处理 @FeignClient("zkmall-product")
     *     product/skuinfo/info/{id}
     * @param id
     * @return
     */
    @RequestMapping("product/skuinfo/info/{id}")
    public R info(@PathVariable("id") Long id);
}
