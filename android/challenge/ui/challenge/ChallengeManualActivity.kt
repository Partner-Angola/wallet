package com.joeware.android.gpulumera.challenge.ui.challenge

import android.graphics.Bitmap
import android.os.Build
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import com.google.android.material.tabs.TabLayout
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.base.BaseActivity
import com.joeware.android.gpulumera.databinding.ActivityChallengeManualBinding


class ChallengeManualActivity  : BaseActivity() {

    private lateinit var binding: ActivityChallengeManualBinding
    private var locale_url = "en"

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_challenge_manual)
        binding.lifecycleOwner = this
    }

    override fun setObserveData() {

    }

    override fun init() {
        binding.btnClose.setOnClickListener { finish() }
        binding.lyTab.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab?.position == 0) {
                    binding.webView.loadUrl("https://agla.io/challenge_manual/challenge_join_manual_$locale_url.html")
                } else if (tab?.position == 1) {
                    binding.webView.loadUrl("https://agla.io/challenge_manual/challenge_vote_manual_$locale_url.html")
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })
        val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) resources.configuration.locales.get(0) else resources.configuration.locale
        locale_url = when (locale.language) {
            "ko" -> locale.language
            else -> "en"
        }
        binding.webView.webViewClient = object: WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                binding.progress.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                binding.progress.visibility = View.GONE
            }
        }
//        binding.webView.webChromeClient = WebChromeClient()
        binding.webView.settings.loadWithOverviewMode = true
        binding.webView.settings.useWideViewPort = true
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.cacheMode = WebSettings.LOAD_DEFAULT
        binding.webView.loadUrl("https://agla.io/challenge_manual/challenge_join_manual_$locale_url.html")
    }
}