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

    /** 郵便番号検索のリクエスト送信 + 取得した結果を画面に表示 */
    private fun postalSearchRequest() {
        create(PostalSearchService::class.java)
            .address(binding.postalCode.text.toString())
            .enqueue(object : retrofit2.Callback<AddressData> {

                override fun onFailure(call: Call<AddressData>?, t: Throwable) {
                    binding.address.text = getString(R.string.network_error)
                }

                override fun onResponse(
                    call: Call<AddressData>?,
                    response: Response<AddressData>
                ) {
                    binding.address.text = response.body()?.getAddress()
                }
            })
    }

    /**
     * レスポンスデータから画面表示用のテキストに変換
     *
     * @return 画面表示用のテキスト
     */
    private fun AddressData?.getAddress(): String {
        if (this == null) return getString(R.string.error)
        if (status != 200) return message ?: getString(R.string.error)
        return results?.firstOrNull()?.let {
            it.address1 + it.address2 + it.address3
        } ?: getString(R.string.no_data)
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
}