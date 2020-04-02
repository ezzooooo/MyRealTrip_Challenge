package com.mrt.myrealtrip

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_news_webview.*

class NewsWebviewActivity : AppCompatActivity() {

    private val NEWS_TITLE = "newsTitle"
    private val NEWS_KEYWORD1 = "newsKeyword1"
    private val NEWS_KEYWORD2 = "newsKeyword2"
    private val NEWS_KEYWORD3 = "newsKeyword3"
    private val NEWS_URL = "newsUrl"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_webview)

        val intent = intent
        val url = intent.getStringExtra(NEWS_URL)
        val title = intent.getStringExtra(NEWS_TITLE)
        val keyword1 = intent.getStringExtra(NEWS_KEYWORD1)
        val keyword2 = intent.getStringExtra(NEWS_KEYWORD2)
        val keyword3 = intent.getStringExtra(NEWS_KEYWORD3)

        webview_title.text = title
        webview_keyword1.text = keyword1
        webview_keyword2.text = keyword2
        webview_keyword3.text = keyword3

        news_webview.settings.javaScriptEnabled = true
        news_webview.webViewClient = myWebViewClient()

        news_webview.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                webview_progressbar.progress = newProgress
            }
        }

        news_webview.loadUrl(url)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && news_webview.canGoBack()) {
            news_webview.goBack()
            return true
        }

        return super.onKeyDown(keyCode, event)
    }

    inner class myWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?, url: String): Boolean {
            webview_progressbar.visibility = View.VISIBLE
            view?.loadUrl(url)
            return true
        }


        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            webview_progressbar.visibility = View.GONE
        }
    }
}
