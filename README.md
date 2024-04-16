**计算方法耗时的插件 RunTimePlugin**

- 把插件和library发布到本地maven仓库

- 应用插件

  1、添加本地maven仓库，在settings.gradle下添加 `mavenLocal()`
  
  
  2、项目根目录下build.gradle集成插件
  ```
  buildscript {
    dependencies {
        classpath("com.custom.plugin:RunTimePlugin:1.0.16") // 版本号根据自己发布的去改
    }
  }
  ```
  
  3、app下的build.gradle添加
  ```
  plugins {
    id("RunTimePlugin")
  }
  
  runTimePluginConfig {
    applyToAll = false // true：指定包名全部插桩，false：仅方法上添加了`@TimeConsume`注解的方法才计算耗时
    applyPackageName = "com.leero.runtimeplugin" // 指定插桩的包名
  }

  dependencies {
    implementation("com.custom.library:runtime-plugin-config:1.0.0") // 版本号根据自己发布的去改
  } 
  ```
  
  运行后可以看到输出：com.leero.runtimeplugin.MainActivity/test 耗时：1ms.
