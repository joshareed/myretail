package com.myretail.redsky

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import retrofit2.Retrofit
import retrofit2.adapter.java8.Java8CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory

@Configuration
class RedSkyApiFactory {

    @Value('${redSky.baseUrl}')
    String baseUrl

    @Bean
    RedSkyApi redSkyApi(ObjectMapper objectMapper) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .addCallAdapterFactory(Java8CallAdapterFactory.create())
                .build()
        retrofit.create(RedSkyApi)
    }
}
