package com.app.myapplication

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.app.myapplication.databinding.ActivityWebviewBinding
import kotlinx.android.synthetic.main.activity_webview.*


const val EXTRA_MESSAGE = "com.app.myapplication.MESSAGE"

class WebViewActivity : AppCompatActivity() {

    private var binding: ActivityWebviewBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // setContentView(R.layout.activity_webview)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_webview)

        val descriptionUrl = intent.getStringExtra(EXTRA_MESSAGE)
        webView.webViewClient = MyWebViewClient(this)
        webView.loadUrl(descriptionUrl)
    }
    class MyWebViewClient internal constructor(private val activity: Activity) : WebViewClient() {

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {

            val url: String = request?.url.toString();
            view?.loadUrl(url)
            return true
        }

        override fun shouldOverrideUrlLoading(webView: WebView, url: String): Boolean {
            webView.loadUrl(url)
            return true
        }
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            activity.progressBar.visibility= View.GONE

        }


    override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
            Toast.makeText(activity, "Got Error! $error", Toast.LENGTH_SHORT).show()
        }
    }
}