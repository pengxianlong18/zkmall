package com.pxl.zkmall.order.feign;

import com.pxl.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("zkmall-product")
public interface ProductFeignService {

    @GetMapping("/product/spuinfo/skuId/{id}")
    public R getSpuInfoBySkuId(@PathVariable("id") Long skuId);
}
