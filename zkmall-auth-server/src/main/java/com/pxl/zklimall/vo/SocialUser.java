/**
  * Copyright 2024 bejson.com 
  */
package com.pxl.zklimall.vo;


import lombok.Data;

@Data
public class SocialUser {

    private String access_token;
    private String token_type;
    private long expires_in;
    private String refresh_token;
    private String scope;
    private long created_at;

}