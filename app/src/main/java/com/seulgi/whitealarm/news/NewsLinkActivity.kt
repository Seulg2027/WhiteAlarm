package com.seulgi.whitealarm.news

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import com.seulgi.whitealarm.R

class NewsLinkActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_link)

        val getUrl = intent.getStringExtra("url")

        val webView : WebView = findViewById(R.id.web)
        webView.loadUrl(getUrl.toString()) // url 로드
    }
}