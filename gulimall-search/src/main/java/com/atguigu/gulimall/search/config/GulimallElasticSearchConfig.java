package com.atguigu.gulimall.search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GulimallElasticSearchConfig {
    @Value("${spring.elasticsearch.rest.http-host}")
    private String host;
    public static final RequestOptions COMMON_OPTIONS;
    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
//        builder.addHeader("Authorization", "Bearer " + TOKEN);
//        builder.setHttpAsyncResponseConsumerFactory(
//            new HttpAsyncResponseConsumerFactory
//                .HeapBufferedResponseConsumerFactory(30 * 1024 * 1024 * 1024)
//        );
        COMMON_OPTIONS = builder.build();
    }
    @Bean
    public RestHighLevelClient esRestClient() {
         RestHighLevelClient client = new RestHighLevelClient(
             RestClient.builder(
                 new HttpHost(host, 9200, "http")));
         return client;
    }
}
