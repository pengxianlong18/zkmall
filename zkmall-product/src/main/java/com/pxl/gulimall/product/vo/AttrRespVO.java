package com.pxl.zkmall.product.vo;

import com.pxl.zkmall.product.entity.AttrEntity;
import lombok.Data;

@Data
public class AttrRespVO extends AttrEntity {
    private Long attrGroupId;

    /**
     * "catelogName": "手机/数码/手机", //所属分类名字
     * 	"groupName": "主体", //所属分组名字
     */
    private String catelogName;
    private String groupName;

    private Long[] catelogPath;
}
