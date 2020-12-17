package com.photograph.postalcodesearch

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.eclipsesource.json.Json
import com.photograph.postalcodesearch.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MainActivity : AppCompatActivity() {
    private val url = "http://zipcloud.ibsnet.co.jp/api/search?zipcode="
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //検索ボタンタップ
        binding.searchButton.setOnClickListener { postalCodeSearch() }
    }

    /** 非同期処理で住所検索(通信エラー考慮) */
    private fun postalCodeSearch() = GlobalScope.launch(Dispatchers.Main) {
        val http = HttpUtil()
        withContext(Dispatchers.Default) {
            binding.address.text = http.httpGet(url + binding.postalCode.text).getAddress()
        }
    }

    private suspend fun postalCodeSearchRequest(): String = suspendCoroutine { continuation ->
        GlobalScope.launch(Dispatchers.Default) {
            continuation.resume("OK")
        }
    }

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
}