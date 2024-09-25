package com.pxl.zkmall.order.feign;

import com.pxl.common.utils.R;
import com.pxl.zkmall.order.vo.WareSkuLockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("zkmall-ware")
public interface WmsFeignService {

    /**
     * 检查库存
     * @param skuIds
     * @return
     */
    @PostMapping("/ware/waresku/hasstock")
    public R getSkuHasStock(@RequestBody List<Long> skuIds);

    /**
     * 查询运费
     * @param addrId
     * @return
     */
    @GetMapping("/ware/wareinfo/fare")
    public R getFare(@RequestParam("addrId") Long addrId);

    /**
     * 锁库存
     * @param vo
     * @return
     */
    @PostMapping("/ware/waresku/lock/order")
    public R orderLockStock(@RequestBody WareSkuLockVo vo);
}
