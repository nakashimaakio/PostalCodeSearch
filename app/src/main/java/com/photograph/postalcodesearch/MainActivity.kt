package com.photograph.postalcodesearch

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.eclipsesource.json.Json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    val URL = "https://qiita.com/api/v2/items/bf3e4e06022eebe8e3eb" //サンプルとしてQiitaのAPIサービスを利用します
    var result = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val getButton = findViewById(R.id.button) as Button
        getButton.setOnClickListener(object : View.OnClickListener {
            override
            fun onClick(view: View) {
                onParallelGetButtonClick()
            }
        })
    }

    //非同期処理でHTTP GETを実行します。
    fun onParallelGetButtonClick() = GlobalScope.launch(Dispatchers.Main) {
        val http = HttpUtil()
        //Mainスレッドでネットワーク関連処理を実行するとエラーになるためBackgroundで実行
        async(Dispatchers.Default) { http.httpGet(URL) }.await().let {
            //minimal-jsonを使って　jsonをパース
            val result = Json.parse(it).asObject()
            val textView = findViewById(R.id.Prefecture) as TextView
            textView.setText(result.get("likes_count").asInt().toString() + "LGTM!")
        }
    }
}