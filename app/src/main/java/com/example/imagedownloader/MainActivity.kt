package com.example.imagedownloader

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.example.imagedownloader.databinding.ActivityMainBinding
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private val STORAGE_REQUEST_CODE = 1001

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        if (isStoragePermissionGranted()) {
            downloadImages()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                STORAGE_REQUEST_CODE
            )
        }
    }

    private fun downloadImages() {
        val downloadFolderPath = Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            .toString()
        thread {
            val imageFile = File(
                "$downloadFolderPath/image1.jpg")
            downloadImageFromURL("https://i.ytimg.com/vi/qUdDKuxb7bc/maxresdefault.jpg", imageFile){ currentProgress ->
                runOnUiThread {
                    binding.pbFirst.progress = currentProgress
                    binding.tvProgressFirst.text = "$currentProgress/100"
                }
                if (isDownloaded(currentProgress)){
                    if (imageFile.exists()) {
                        val myBitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
                        runOnUiThread {
                            binding.ivFirst.setImageBitmap(myBitmap)
                        }
                    }
                }
            }
        }
        thread {
            val imageFile = File(
                "$downloadFolderPath/image2.jpg")
            downloadImageFromURL("https://wallpaperaccess.com/full/2127781.jpg", imageFile){currentProgress ->
                runOnUiThread {
                    binding.pbSecond.progress = currentProgress
                    binding.tvProgressSecond.text = "$currentProgress/100"
                }
                if (isDownloaded(currentProgress)){
                    if (imageFile.exists()) {
                        val myBitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
                        runOnUiThread {
                            binding.ivSecond.setImageBitmap(myBitmap)
                        }
                    }
                }
            }
        }
        thread {
            val imageFile = File(
                "$downloadFolderPath/image3.jpg")
            downloadImageFromURL("https://wallpaperaccess.com/full/2127804.jpg", imageFile){currentProgress ->
                runOnUiThread {
                    binding.pbThird.progress = currentProgress
                    binding.tvProgressThird.text = "$currentProgress/100"
                }
                if (isDownloaded(currentProgress)){
                    if (imageFile.exists()) {
                        val myBitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
                        runOnUiThread {
                            binding.ivThird.setImageBitmap(myBitmap)
                        }
                    }
                }
            }
        }
    }

    private fun isDownloaded(progress: Int) = progress == 100


    private fun downloadImageFromURL(src: String?, file: File, postProgress: (progress: Int)->Unit) {
        try {
            val url = URL(src)
            val connection: HttpURLConnection = url
                .openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val lenghtOfFile: Int = connection.contentLength
            var count = 0
            val input: InputStream = BufferedInputStream(url.openStream(), 8192)
            val output: OutputStream = FileOutputStream(file)

            val data = ByteArray(1024)

            var total: Long = 0

            while (input.read(data).also { count = it } !== -1) {
                total += count
                logE("progress for ${file.name} is: ${(total * 100 / lenghtOfFile).toInt()}")
                postProgress((total * 100 / lenghtOfFile).toInt())
                output.write(data, 0, count)
            }
            output.flush()
            output.close()
            input.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun isStoragePermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
            ) {
                true
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    STORAGE_REQUEST_CODE
                )
                false
            }
        } else {
            Log.v(TAG, "Permission is granted")
            true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0])
            downloadImages()
        }
    }

    private fun logE(message: String) {
        Log.e(TAG, message)
    }
}