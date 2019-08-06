package com.example.eric.clockdemo

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception


class MainActivity : AppCompatActivity() {

    var remainTimes = 3

    var tvRemainTimes: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ContextCompat.checkSelfPermission(this@MainActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {   //权限还没有授予，需要在这里写申请权限的代码
            // 第二个参数是一个字符串数组，里面是你需要申请的权限 可以设置申请多个权限
            // 最后一个参数是标志你这次申请的权限，该常量在onRequestPermissionsResult中使用到
            ActivityCompat.requestPermissions(this@MainActivity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                111)
        }

        tvRemainTimes = findViewById(R.id.tv_remain_times)
        tvRemainTimes?.text = "您还有${remainTimes}次机会"
        btn.setOnClickListener {
            btn.isEnabled = false
            clock_view.start()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 222) {
                    // 获取图片
                    try {
                        //该uri是上一个Activity返回的
                        val imageUri = data?.data
                        if(imageUri!=null) {
                            val bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri))
                            iv.setImageBitmap(bitmap)
                            clock_view.visibility = View.GONE
                            btn.visibility = View.GONE
                            ll.visibility = View.GONE
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}
