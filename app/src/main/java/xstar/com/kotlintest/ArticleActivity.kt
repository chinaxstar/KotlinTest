package xstar.com.kotlintest

import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_article.*
import xstar.com.kotlintest.constant.C.INTENT_URL_KEY
import xstar.com.kotlintest.util.BaseActivity

/**
 * webview 显示网页
 */
class ArticleActivity : BaseActivity(R.layout.activity_article) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
