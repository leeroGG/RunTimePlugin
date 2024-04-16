package com.custom.plugin

import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import com.custom.plugin.config.RunTimeConfigMgr
import com.custom.plugin.config.RunTimePluginConfig
import org.gradle.api.Plugin
import org.gradle.api.Project

class RunTimePlugin : Plugin<Project> {

    companion object {
        private const val CONFIG_NAME = "runTimePluginConfig"
    }

    override fun apply(target: Project) {
        println("/*** 自定义插件 RunTimePlugin *****\\")

        val androidComponents = target.extensions.getByType(AndroidComponentsExtension::class.java)
        target.extensions.create(CONFIG_NAME, RunTimePluginConfig::class.java)

        androidComponents.onVariants { variant ->

            val config = target.extensions.findByName(CONFIG_NAME) as RunTimePluginConfig?
            if (config != null) {
                RunTimeConfigMgr.runTimePluginConfig = config
                println("配置包名：" + config.applyPackageName)
            }

            // 添加字节码转换处理逻辑
            variant.instrumentation.transformClassesWith(
                RunTimeTransform::class.java, InstrumentationScope.ALL
            ) {}

            variant.instrumentation.setAsmFramesComputationMode(FramesComputationMode.COPY_FRAMES)
        }
    }

}