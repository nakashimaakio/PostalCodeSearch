package com.photograph.postalcodesearch

import okhttp3.OkHttpClient
import okhttp3.Request

class HttpUtil {

    fun httpGet(url: String): String? {
        val request = Request.Builder()
            .url(url)
            .build()

        return HttpClient.instance.newCall(request).execute().body?.string()
    }
}

object HttpClient {
    val instance = OkHttpClient()
}