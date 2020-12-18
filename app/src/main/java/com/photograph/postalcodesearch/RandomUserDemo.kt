package com.photograph.postalcodesearch

data class RandomUserDemo(
    var info: Info,
    var results: List<Result2>
)

data class Info(
    var seed: String,
    var results: Int,
    var page: Int,
    var version: String
)

data class Result2(
    var gender: String,
    var email: String,
    var registered: String,
    var dob: String,
    var phone: String,
    var cell: String
)