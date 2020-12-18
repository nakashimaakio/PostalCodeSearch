package com.photograph.postalcodesearch

import retrofit2.Call
import retrofit2.http.GET

interface IApiService {
    @GET("search?zipcode=1600000")
    fun apiDemo(): Call<AddressData>
}