package xstar.com.kotlintest

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_article.*
import xstar.com.kotlintest.constant.C.INTENT_URL_KEY

class ArticleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article)
        val url = intent.getStringExtra(INTENT_URL_KEY)
        val webclient = WebChromeClient()
        val viewClient = WebViewClient()
        content.settings.javaScriptEnabled = true
        content.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
        content.webChromeClient = webclient
        content.webViewClient = viewClient
        content.loadUrl(url)
    }
}
