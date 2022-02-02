package cn.mfuns.webapp.webview

import android.annotation.SuppressLint
import android.content.Context
import android.os.Process
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import cn.mfuns.webapp.R
import com.tencent.smtt.export.external.TbsCoreSettings
import com.tencent.smtt.sdk.CookieManager
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.WebSettings
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient

internal class TbsWebView : MfunsWebView() {
    private var webView: WebView? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun initialize(context: Context, listener: (() -> Unit)) {
        this.listener = listener

        if (webView != null) notifyInitialized()

        // Initialize dex2oat
        val map = HashMap<String?, Any?>()
        map[TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER] = true
        map[TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE] = true
        QbSdk.initTbsSettings(map)

        try {
            webView = WebView(context)
        } catch (e: Exception) {
            AlertDialog.Builder(context).apply {
                setTitle(R.string.webview_missing_title)
                setMessage(R.string.webview_missing_message)
                setPositiveButton(R.string.ok) { _, _ -> Process.killProcess(Process.myPid()) }
                setCancelable(false)
                show()
            }
            return
        }

        // Configure
        webView!!.settings.apply {
            javaScriptEnabled = true
            setSupportZoom(false)
            cacheMode = WebSettings.LOAD_NO_CACHE
            domStorageEnabled = true
            databaseEnabled = true
            setAppCacheEnabled(true)
            setAppCacheMaxSize(1024 * 1024 * 8)
            userAgentString =
                "Mfuns-WebApp/${
                    context.packageManager.getPackageInfo(
                        context.packageName,
                        0
                    ).versionName
                } $userAgentString"
        }

        // Cookie
        CookieManager.getInstance().apply {
            setAcceptCookie(true)
            setAcceptThirdPartyCookies(webView, true)
        }

        // Handle back
        webView!!.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                if (keyCode == KeyEvent.KEYCODE_BACK && webView!!.canGoBack()) {
                    webView!!.goBack()
                    return true
                }
                return false
            }
        })

        // Listen onPageFinished()
        webView!!.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                webView!!.webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                        return MfunsWebViewClient.shouldOverrideUrlLoading(context, url)
                    }
                }
                notifyInitialized()
            }
        }

        webView!!.loadUrl(context.getText(R.string.app_url) as String)
    }

    override fun destroy() {
        webView?.apply {
            loadDataWithBaseURL(null, "", "text/html", "utf-8", null)
            clearHistory()
            (parent as ViewGroup).removeView(this)
            webView = null
        }
    }

    override fun getView(): View = webView as View

    override fun goBack(): Boolean = if (webView!!.canGoBack()) {
        webView!!.goBack()
        true
    } else false
}
