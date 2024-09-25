package com.pxl.common.constant;

public class ProductConstant {

    public enum AttrEnum{
        ATTR_TYPE_BASE(1, "基本属性"),ATTR_TYPE_SALE(0, "销售属性");

        private int code;
        private String msg;

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }

        AttrEnum(int code, String msg){
            this.code = code;
            this.msg=msg;
        }
    }

    public enum StatusEnum{
       NEW_SPU(0,"新建"), SPU_UP(1, "商品上架"),SPU_DOWN(0, "商品下架");

        private int code;
        private String msg;

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }

        StatusEnum(int code, String msg){
            this.code = code;
            this.msg=msg;
        }
    }
}
