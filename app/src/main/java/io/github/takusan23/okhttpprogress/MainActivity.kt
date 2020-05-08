package io.github.takusan23.okhttpprogress

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import java.io.File
import java.io.IOException

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            getData()
        }

    }

    fun getData() {
        val request = Request.Builder().apply {
            url("")
            get()
        }.build()
        val okHttpClient = OkHttpClient()
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            override fun onResponse(call: Call, response: Response) {
                val file = File("${getExternalFilesDir(null)}/test.mp4")
                val inputStream = response.body?.byteStream()
                val outputStream = file.outputStream()
                val buff = ByteArray(1024 * 4)
                val target = response.body?.contentLength() // 合計サイズ
                var progress = 0L
                // プログレスバーの最大値設定
                runOnUiThread {
                    progressBar.max = target?.toInt()!!
                }
                while (true) {
                    val read = inputStream?.read(buff)
                    if (read == -1 || read == null || target == null) {
                        break
                    }
                    progress += read
                    outputStream.write(buff, 0, read)
                    // UI更新
                    runOnUiThread {
                        // プログレスバー進める
                        progressBar.progress = progress.toInt()
                        // パーセントの式。
                        progressTextView.text =
                            "${((progress.toFloat() / target.toFloat()) * 100).toInt()} %"
                    }
                }
                inputStream?.close()
                outputStream.close()
            }
        })
    }
}
