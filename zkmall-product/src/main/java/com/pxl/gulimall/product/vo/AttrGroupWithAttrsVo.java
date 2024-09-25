package com.pxl.zkmall.product.vo;

import com.pxl.zkmall.product.entity.AttrEntity;
import com.pxl.zkmall.product.entity.AttrGroupEntity;
import lombok.Data;

import java.util.List;

@Data
public class AttrGroupWithAttrsVo extends AttrGroupEntity {

    private List<AttrEntity> attrs;
}
