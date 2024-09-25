package com.pxl.zkmall.member.feign;

import com.pxl.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
@FeignClient(name = "zkmall-coupon")
public interface CouponFeognService {

    @RequestMapping("/coupon/coupon/member/list")
    public R membercoupon();
}
