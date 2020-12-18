package com.photograph.postalcodesearch

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.eclipsesource.json.Json
import com.google.gson.GsonBuilder
import com.photograph.postalcodesearch.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private val url = "http://zipcloud.ibsnet.co.jp/api/search?zipcode="
    private lateinit var binding: ActivityMainBinding

    // core for controller
    private val service: IApiService = create(IApiService::class.java)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //検索ボタンタップ
        binding.searchButton.setOnClickListener { test() }
    }

    /** 非同期処理で住所検索(通信エラー考慮) */
    private fun postalCodeSearch() = GlobalScope.launch(Dispatchers.Main) {
        val http = HttpUtil()
        withContext(Dispatchers.Default) {
            binding.address.text = http.httpGet(url + binding.postalCode.text).getAddress()
        }
    }

    /*
    private suspend fun postalCodeSearchRequest(): RandomUserDemo? = suspendCoroutine { continuation ->
        GlobalScope.launch(Dispatchers.Default) {
            try {
                val response = service.apiDemo().execute()
                if (response.isSuccessful()) {
                    return response.body()
                } else {
                    // failed
                }
            } catch (e: IOException) {
            e.printStackTrace()
        }
        }
    }
     */

    /**
     * 取得したjsonから住所を取得
     *
     * @return 住所の文字列
     */
    private fun String?.getAddress(): String {
        if (this == null) return getString(R.string.error)
        try {
            val json = Json.parse(this).asObject()
            return if (json.get("status").asInt() == 200) {
                if (json.get("results").toString() == "null") {
                    getString(R.string.no_data)
                } else {
                    val result = json.get("results").asArray()[0].asObject()
                    result.get("address1").asString() +
                            result.get("address2").asString() +
                            result.get("address3").asString()
                }
            } else {
                json.get("message").asString()
            }

        } catch (e: Exception) {
            return getString(R.string.error)
        }
    }

    private fun test() {
        service.apiDemo().enqueue(object : retrofit2.Callback<AddressData> {
            override fun onFailure(call: Call<AddressData>?, t: Throwable) {
                Log.d("ApiLog", "failure")
            }

            override fun onResponse(
                call: Call<AddressData>?,
                response: Response<AddressData>
            ) {
                Log.d("ApiLog", "response")
            }
        })
    }

    private val httpBuilder: OkHttpClient.Builder
        get() {
            val httpClient = OkHttpClient.Builder()
                .addInterceptor(Interceptor { chain ->
                    val original = chain.request()

                    //header
                    val request = original.newBuilder()
                        .header("Accept", "application/json")
                        .method(original.method, original.body)
                        .build()

                    return@Interceptor chain.proceed(request)
                })
                .readTimeout(30, TimeUnit.SECONDS)

            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            httpClient.addInterceptor(loggingInterceptor)

            return httpClient
        }

    lateinit var retrofit: Retrofit

    private fun <S> create(serviceClass: Class<S>): S {
        val gson = GsonBuilder()
            .serializeNulls()
            .create()

        // create retrofit
        retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl("http://zipcloud.ibsnet.co.jp/api/")
            .client(httpBuilder.build())
            .build()

        return retrofit.create(serviceClass)
    }
}