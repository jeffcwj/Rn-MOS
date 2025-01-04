# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-dontwarn com.billflx.csgo.bean.**
-keep class com.billflx.csgo.bean.** { *; }

-dontwarn com.billflx.csgo.**
-keep class com.billflx.csgo.** { *; } # 整个包都别混淆了，真麻烦
-keep interface com.billflx.csgo.** { *; }

-dontwarn com.gtastart.common.**
-keep class com.gtastart.common.** { *; }

-dontwarn com.gtastart.**
-keep class com.gtastart.** { *; }
-keep interface com.gtastart.** { *; }

# 保留 TimeUtils 类
-keep class com.gtastart.util.TimeUtils { *; }

-dontwarn com.pika.sillyboy.**
-keep class com.pika.sillyboy.** { *; }
-keep interface com.pika.sillyboy.** { *; }

-dontwarn com.valvesoftware.**
-keep class com.valvesoftware.** { *; }

-dontwarn me.nillerusr.**
-keep class me.nillerusr.** { *; }

-dontwarn org.libsdl.app.**
-keep class org.libsdl.app.** { *; }

-dontwarn com.gtastart.common.base.bean.**
-keep class com.gtastart.common.base.bean.** { *; }

-keep class com.billflx.csgo.constant.Constants { *; }

-keepclassmembers class * {
    *;
}

## 自动曝光数据的防混淆
#-keep class * implements java.io.Serializable{
#     <fields>;
#    <methods>;
#}

-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod

#-------------- okhttp3 start-------------
# OkHttp3
# https://github.com/square/okhttp
# okhttp
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.squareup.okhttp.* { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**

# okhttp 3
-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**

-keepattributes InnerClasses

# Okio
-dontwarn com.squareup.**
-dontwarn okio.**
-keep public class org.codehaus.* { *; }
-keep public class java.nio.* { *; }
#----------okhttp end--------------


##Glide
-dontwarn com.bumptech.glide.**
-keep class com.bumptech.glide.**{*;}
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# RxJava RxAndroid
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

-keep class com.alibaba.fastjson.**{*;}
-keep class com.bumptech.glide.**{*;}

# Gson 混淆规则
-keepattributes Signature
-keepattributes *Annotation*
#-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.examples.android.model.** { *; }
# Gson
-keep class com.google.gson.stream.** { *; }
-keepattributes EnclosingMethod

# Gson
-keep class com.google.gson.stream.** { *; }
-keepattributes EnclosingMethod
-keep class com.google.gson.examples.android.model.** { <fields>; }

-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

-keepclassmembers,allowobfuscation class * { # Gson 使用的字段反射
  @com.google.gson.annotations.SerializedName <fields>;
}

-keepclassmembers class * { # Gson 反序列化时需要的无参构造函数
    public <init>();
}

# -dontshrink # 暂时先用一下
# retrofit ParameterizedType error 最终解决方案
 -keep,allowobfuscation,allowshrinking interface retrofit2.Call
 -keep,allowobfuscation,allowshrinking class retrofit2.Response
 # With R8 full mode generic signatures are stripped for classes that are not
 # kept. Suspend functions are wrapped in continuations where the type argument
 # is used.
 -keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# retrofit
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes RuntimeVisibleAnnotations
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-keep class retrofit2.**{*;}

-keepclassmembers,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# 保留注解属性
-keepattributes Signature
-keepattributes *Annotation*

# OkHttp 的日志拦截器
-keep class okhttp3.logging.HttpLoggingInterceptor { *; }

# 如果使用 GsonConverterFactory
-keep class com.google.gson.** { *; }
-keepattributes *Annotation*

# 如果使用 ScalarsConverterFactory
-keep class retrofit2.converter.scalars.** { *; }

-keepclassmembers,allowobfuscation class * {
    *** lambda$(...);
}

# ----------- 测试用 ------------

## Hilt 保留规则
#-dontwarn dagger.hilt.**
#-dontwarn javax.inject.**
#-keep class dagger.hilt.** { *; }
#-keep class javax.inject.** { *; }
#-keep class dagger.** { *; }
#-keep class dagger.internal.** { *; }
#-keep class androidx.hilt.** { *; }
#-keep class Hilt_* { *; }
#-keep class * extends dagger.hilt.android.internal.managers.ActivityRetainedComponentManager { *; }
#-keep class * extends dagger.hilt.android.internal.managers.FragmentComponentManager { *; }
#-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager { *; }
#
## 保留 Hilt 的注解和注解处理器生成的代码
#-keepattributes RuntimeVisibleAnnotations
#-keepattributes AnnotationDefault
#
## 保留 Hilt 编译时生成的组件类
#-keep class * extends dagger.hilt.components.SingletonComponent { *; }
#-keep class * extends dagger.hilt.android.internal.lifecycle.HiltWrapper_* { *; }
#
## 如果你使用 Hilt 来注入 ViewModels
#-keep class androidx.lifecycle.ViewModel { *; }
#-keep class * extends androidx.lifecycle.ViewModel { *; }
#
## 如果你使用 Hilt 注入 Activity
#-keep class * extends android.app.Activity { *; }
#
## 如果你使用 Hilt 注入 Fragment
#-keep class * extends androidx.fragment.app.Fragment { *; }
#
## 保留 Hilt 自动生成的 Application 类
#-keep class dagger.hilt.android.internal.managers.ApplicationComponentManager { *; }
#-keep class dagger.hilt.internal.GeneratedComponentManager { *; }
#-keep class dagger.hilt.internal.aggregatedroot.codegen._com_billflx_csgo_CSApplication { *; }
#
#
## 保留 ViewModel 的工厂方法
#-keepclassmembers class * extends androidx.lifecycle.ViewModel {
#    public <init>(...);
#}
#-keep class androidx.lifecycle.DefaultLifecycleObserver { *; }
#-keepclassmembers class androidx.lifecycle.ViewModelProvider$NewInstanceFactory {
#    public *;
#}
#-keepclassmembers class androidx.lifecycle.ViewModelProvider$AndroidViewModelFactory {
#    public *;
#}
#
#
## 保留 Hilt_LauncherActivity
#-keep class me.nillerusr.Hilt_LauncherActivity { *; }
#
## 保留所有 ViewModel 及其构造函数
#-keep class * extends androidx.lifecycle.ViewModel {
#    <init>(...);
#}
#
## 保留 Hilt 注解的 ViewModel 类
#-keep @dagger.hilt.android.lifecycle.HiltViewModel class * { *; }
#
## 保留所有与注解相关的属性
#-keepattributes RuntimeVisibleAnnotations
#
## 保留 LiveData 和协程相关类
#-keep class kotlinx.coroutines.** { *; }
#-keep class androidx.lifecycle.LiveData { *; }
#-keepclassmembers class androidx.lifecycle.LiveData {
#    *;
#}
