package com.photograph.postalcodesearch

import retrofit2.Call
import retrofit2.http.GET

interface PostalSearchService {
    @GET("search?zipcode=1600000")
    fun address(): Call<AddressData>
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