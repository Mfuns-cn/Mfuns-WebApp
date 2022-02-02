package cn.mfuns.webapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import cn.mfuns.webapp.webview.WebViewContainer

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Adapt to full screen
        window.decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                window.attributes.layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
            else window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES

        // Load Splash Image
        val image = ImageView(this)
        image.setImageResource(R.mipmap.splash)
        image.scaleType = ImageView.ScaleType.CENTER_CROP
        setContentView(image)
    }

    override fun onResume() {
        super.onResume()
        Handler(Looper.getMainLooper()).postDelayed({
            val useTbs = PreferenceManager.getDefaultSharedPreferences(this).getString(
                "settings_webview_core",
                resources.getStringArray(R.array.settings_webview_core_list)[0]
            ) == resources.getStringArray(R.array.settings_webview_core_list)[1]
            WebViewContainer.defaultContainer = WebViewContainer(useTbs)
            WebViewContainer.defaultContainer.initialize(this@SplashActivity) {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            }
        }, 100)
    }
}
