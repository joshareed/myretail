package com.myretail.redsky

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

import java.util.concurrent.CompletableFuture

interface RedSkyApi {

    @GET('/v2/pdp/tcin/{id}')
    CompletableFuture<RedSkyApiResponse> get(@Path('id') long id, @Query('excludes') String excludes)

}
