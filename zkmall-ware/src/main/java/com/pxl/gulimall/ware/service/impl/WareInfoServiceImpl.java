package com.pxl.zkmall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.pxl.common.utils.R;
import com.pxl.zkmall.ware.Vo.FareVo;
import com.pxl.zkmall.ware.Vo.MemberAddressVo;
import com.pxl.zkmall.ware.feign.MemberFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pxl.common.utils.PageUtils;
import com.pxl.common.utils.Query;

import com.pxl.zkmall.ware.dao.WareInfoDao;
import com.pxl.zkmall.ware.entity.WareInfoEntity;
import com.pxl.zkmall.ware.service.WareInfoService;
import org.springframework.util.StringUtils;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

    @Autowired
    MemberFeignService memberFeignService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareInfoEntity> queryWrapper = new QueryWrapper<>();

        String  key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)){
            queryWrapper.eq("id",key).or()
                    .like("name",key)
                    .or().like("address",key)
                    .or().like("areacode",key);
        }

        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public FareVo getFare(Long addId) {
        R r = memberFeignService.getAddrInfo(addId);
        FareVo fareVo = new FareVo();
        MemberAddressVo data = r.getData("memberReceiveAddress", new TypeReference<MemberAddressVo>() {
        });
        if (data != null){
            String phone = data.getPhone();
            String substring = phone.substring(phone.length() - 1, phone.length());
            fareVo.setAddress(data);
            fareVo.setFare(new BigDecimal(substring));
            return fareVo;
            //TODO 远程查询地区表，判断是否包邮，计算邮费
        }
        return null;
    }

}