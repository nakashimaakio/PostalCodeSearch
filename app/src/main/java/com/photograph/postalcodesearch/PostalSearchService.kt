package com.photograph.postalcodesearch

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PostalSearchService {
    @GET("search")
    fun address(@Query("zipcode") postalCode: String): Call<AddressData>
}

data class AddressData(
    val message: String?,
    val results: List<Result>?,
    val status: Int
)

data class Result(
    val address1: String,
    val address2: String,
    val address3: String,
    val kana1: String,
    val kana2: String,
    val kana3: String,
    val prefcode: String,
    val zipcode: String
)