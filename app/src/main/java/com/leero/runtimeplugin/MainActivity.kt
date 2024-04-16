package com.leero.runtimeplugin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.custom.pluginconfig.TimeConsume

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        test()
    }

    @TimeConsume
    fun test() {
        var result = 0
        (1..1000).forEach {
            result+=it
        }
    }
}