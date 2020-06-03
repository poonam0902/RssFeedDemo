package com.app.myapplication.activity

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
import com.app.myapplication.R
import com.app.myapplication.databinding.ActivityWebviewBinding
import com.app.myapplication.utils.Constants.EXTRA_MESSAGE
import kotlinx.android.synthetic.main.activity_webview.*




class WebViewActivity : AppCompatActivity() {

    private var binding: ActivityWebviewBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_webview)

        /**
         * load webview with passed url in intent extra
         */
        val descriptionUrl = intent.getStringExtra(EXTRA_MESSAGE)
        webView.webViewClient = MyWebViewClient(this)
        webView.loadUrl(descriptionUrl)
    }

    class MyWebViewClient internal constructor(private val activity: Activity) : WebViewClient() {

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {

            val url: String = request?.url.toString()
            view?.loadUrl(url)
            return true
        }

        override fun shouldOverrideUrlLoading(webView: WebView, url: String): Boolean {
            webView.loadUrl(url)
            return true
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            activity.progressBar.visibility = View.GONE //remove progress bar after url page is loaded in webview

        }

        /**
         * Notify user if error occurs in webpage loading
         */
        override fun onReceivedError(view: WebView,request: WebResourceRequest,error: WebResourceError) {
            Toast.makeText(activity, "Got Error! $error", Toast.LENGTH_SHORT).show()
        }
    }
}