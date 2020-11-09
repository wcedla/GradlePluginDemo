package com.istrong.gradleplugindemo

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    var btnSilpleClick: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnSilpleClick = findViewById(R.id.btnSimpleClick)
        btnSilpleClick?.setOnClickListener {
            clickAction()
        }
    }

    private fun clickAction() {
        println("点击输出")
    }
}