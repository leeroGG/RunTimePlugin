package com.leero.runtimeplugin

import android.util.Log

class TimePluginTest {


    fun testPlugin() {
        val l2 = System.currentTimeMillis()
        val l1 = System.currentTimeMillis()
        Log.e("com.leero.runtimeplugin.MainActivity", "com.leero.runtimeplugin.MainActivity/test 耗时：" + (l2 - l1) + ".ms")
    }
}