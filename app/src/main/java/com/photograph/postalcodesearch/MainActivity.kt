package com.photograph.postalcodesearch

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.eclipsesource.json.Json
import com.photograph.postalcodesearch.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    /** 非同期処理で住所検索 */
    private fun postalCodeSearch() = GlobalScope.launch(Dispatchers.Main) {
        val http = HttpUtil()
        withContext(Dispatchers.Default) { http.httpGet(url + binding.postalCode.text) }.let {
            try {
                if (Json.parse(it).asObject().get("status").asInt() == 200) {
                    val a = Json.parse(it).asObject().get("results").toString()
                    if (a == "null") {
                        binding.address.text = getString(R.string.no_data)
                    } else {
                        val result =
                            Json.parse(it).asObject().get("results").asArray()[0].asObject()
                        val address = result.get("address1").asString() +
                                result.get("address2").asString() +
                                result.get("address3").asString()
                        binding.address.text = address
                    }
                } else {
                    binding.address.text = Json.parse(it).asObject().get("message").asString()
                }

            } catch (e: Exception) {
                binding.address.text = getString(R.string.error)
            }
        }
    }
}