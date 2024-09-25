package com.pxl.zkmall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Catalog2Vo {

    private String catalogId; // 1级父分类id
    private List<Catalog3vo> catalog3List; //三级子分类
    private String id;
    private String name;

    @NoArgsConstructor
    @AllArgsConstructor
    @Data   //三级分类Vo
    public static  class Catalog3vo{
        private String catalog2Id; //2级父分类id
        private String id;
        private String name;
    }


}
