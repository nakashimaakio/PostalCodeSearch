package com.photograph.postalcodesearch

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.GsonBuilder
import com.photograph.postalcodesearch.databinding.ActivityMainBinding
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private val url = "http://zipcloud.ibsnet.co.jp/api/"
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //検索ボタンタップ
        binding.searchButton.setOnClickListener { postalSearchRequest() }
    }

    private fun postalSearchRequest() {
        val service: PostalSearchService = create(PostalSearchService::class.java)
        service.address().enqueue(object : retrofit2.Callback<AddressData> {
            override fun onFailure(call: Call<AddressData>?, t: Throwable) {
                binding.address.text = "通信失敗"
            }

            override fun onResponse(
                call: Call<AddressData>?,
                response: Response<AddressData>
            ) {
                binding.address.text = response.body()?.results?.firstOrNull()?.address1 ?: ""
            }
        })
    }

    private val httpBuilder: OkHttpClient.Builder
        get() {
            val httpClient = OkHttpClient.Builder()
                .addInterceptor(Interceptor { chain ->
                    val original = chain.request()

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

    private fun <S> create(serviceClass: Class<S>): S {
        val gson = GsonBuilder()
            .serializeNulls()
            .create()

        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(url)
            .client(httpBuilder.build())
            .build()

        return retrofit.create(serviceClass)
    }
}