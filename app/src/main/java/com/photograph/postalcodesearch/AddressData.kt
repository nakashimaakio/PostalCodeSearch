package com.photograph.postalcodesearch

data class AddressData(
    val message: String,
    val results: List<Result>,
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