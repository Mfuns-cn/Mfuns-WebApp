package cn.mfuns.webapp.webview

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.preference.PreferenceManager
import cn.mfuns.webapp.PhotoViewActivity
import cn.mfuns.webapp.R
import cn.mfuns.webapp.util.Viewer
import java.net.URL

class MfunsWebViewClient {
    companion object {
        fun shouldOverrideUrlLoading(activity: Activity, url: String): Boolean {
            // Parse URL
            var path = URL(url).path
            if (path == "/") return false
            path = url.split(path)[0] + path
            if (path.isNullOrBlank()) return false

            activity.apply {
                when (PreferenceManager.getDefaultSharedPreferences(this).getString(
                    "settings_viewer_default_action",
                    resources.getStringArray(R.array.settings_viewer_action_list)[0]
                )) {
                    resources.getStringArray(R.array.settings_viewer_action_list)[0] -> {
                        val intent = Intent(this, PhotoViewActivity::class.java)
                        intent.putExtra("url", path)
                        startActivity(intent)
                    }
                    resources.getStringArray(R.array.settings_viewer_action_list)[1] -> {
                        Viewer.download(
                            this,
                            url,
                            { f -> Viewer.open(this, f.first) },
                            {
                                Toast.makeText(this, R.string.viewer_download_failed, Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        )
                    }
                    resources.getStringArray(R.array.settings_viewer_action_list)[2] -> {
                        Viewer.download(
                            this,
                            url,
                            { f -> Viewer.save(this, f.third, f.second) },
                            {
                                Toast.makeText(this, R.string.viewer_download_failed, Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        )
                    }
                }
            }

            return true
        }
    }
}
