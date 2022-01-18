package cn.mfuns.webapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import cn.mfuns.webapp.databinding.ActivityPhotoViewBinding
import cn.mfuns.webapp.util.DownloadRequest
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import java.io.File

class PhotoViewActivity : AppCompatActivity() {
    companion object {
        private var queue: RequestQueue? = null
    }

    private lateinit var binding: ActivityPhotoViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Bind view
        binding = ActivityPhotoViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Download Photo
        Runnable {
            // Check URL
            val url = intent.getStringExtra("url")
            if (url.isNullOrBlank()) {
                Toast.makeText(this, R.string.viewer_download_failed, Toast.LENGTH_SHORT).show()
                finish()
            }
            val filename = url!!.split('/').last()

            // Create temp file
            val viewerProviderFolder = File(cacheDir, "viewerprovider")
            viewerProviderFolder.mkdirs()
            val file = File(viewerProviderFolder, filename)

            // Make download request
            if (queue == null) queue = Volley.newRequestQueue(this)
            val request = DownloadRequest(url, { response ->
                run {
                    file.writeBytes(response)
                    val uri = FileProvider.getUriForFile(this, "com.mfuns.webapp.viewerprovider", file)
                    binding.photoView.setImageURI(uri)
                }
            }, {
                Toast.makeText(this, R.string.viewer_download_failed, Toast.LENGTH_SHORT).show()
                finish()
            })

            // Enqueue request
            queue!!.add(request)
        }.run()
    }
}