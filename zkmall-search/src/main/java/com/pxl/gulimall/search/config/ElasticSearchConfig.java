package com.pxl.zkmall.search.config;

import jdk.nashorn.internal.parser.Token;
import org.apache.http.HttpHost;
import org.elasticsearch.client.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticSearchConfig {

    public static final RequestOptions COMMON_OPTIONS;
    static {
        RequestOptions.Builder builder=RequestOptions.DEFAULT.toBuilder();
//        builder.addHeader("Authorization","Bearer"+ Token);
//        builder.setHttpAsyncResponseConsumerFactory(
//                new HttpAsyncResponseConsumerFactory
//                        .HeapBufferedResponseConsumerFactory(30*1024*1024*1024));

        COMMON_OPTIONS=builder.build();
    }

    @Bean
    public RestHighLevelClient esRestClient(){
        RestClientBuilder builder=null;
        builder=RestClient.builder(new HttpHost("192.168.6.128",9200,"http"));
        RestHighLevelClient client=new RestHighLevelClient(builder);

//        RestHighLevelClient client=new RestHighLevelClient(
//                RestClient.builder(
//                        new HttpHost("192.168.6.128",9200,"http")));
        return client;
    }
}
