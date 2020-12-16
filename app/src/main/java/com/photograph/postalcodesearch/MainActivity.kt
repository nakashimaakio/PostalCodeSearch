package com.photograph.postalcodesearch

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.eclipsesource.json.Json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private val url = "https://qiita.com/api/v2/items/bf3e4e06022eebe8e3eb" //Qiita APIサービス

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val getButton = findViewById<Button>(R.id.button)
        getButton.setOnClickListener { onParallelGetButtonClick() }
    }

    //非同期処理でHTTP GETを実行
    private fun onParallelGetButtonClick() = GlobalScope.launch(Dispatchers.Main) {
        val http = HttpUtil()
        //Mainスレッドでネットワーク関連処理を実行するとエラーになるためBackgroundで実行
        withContext(Dispatchers.Default) { http.httpGet(url) }.let {
            //minimal-jsonを使って　jsonをパース
            val result = Json.parse(it).asObject()
            val textView = findViewById<TextView>(R.id.Prefecture)
            textView.text = result.get("likes_count").asInt().toString() + "LGTM!"
        }
    }
}