# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in E:\zj\AndroidBug5497Workaround\android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-optimizationpasses 5          #代码混淆压缩比，在0~7之间，默认为5，一般不做修改
-dontusemixedcaseclassnames   #混合时不使用大小写混合，混合后的类名为小写
-dontskipnonpubliclibraryclasses   #指定不去忽略非公共库的类
#don‘t optimize 不要优化；将会关闭优化，导致日志语句不会被优化掉。所以不能有这个配置
#-dontoptimize
#不做预校验，preverify是proguard的四个步骤之一，Android不需要preverify，去掉这一步能够加快混淆速度。
-dontpreverify
#这句话能够使我们的项目混淆后产生映射文件包含有类名->混淆后类名的映射关系
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*  # 混淆时所采用的算法
-dontskipnonpubliclibraryclassmembers  #指定不去忽略非公共库的类
#保留Annotation不混淆
-keepattributes *Annotation*,InnerClasses

-keep public class * extends android.app.Activity      # 保持哪些类不被混淆
-keep public class * extends android.app.Fragment       # 保持哪些类不被混淆
-keep public class * extends android.app.Application   # 保持哪些类不被混淆
-keep public class * extends android.app.Service       # 保持哪些类不被混淆
-keep public class * extends android.content.BroadcastReceiver  # 保持哪些类不被混淆
-keep public class * extends android.content.ContentProvider    # 保持哪些类不被混淆
-keep public class * extends android.app.backup.BackupAgentHelper # 保持哪些类不被混淆
-keep public class * extends android.preference.Preference        # 保持哪些类不被混淆
-keep public class com.android.vending.licensing.ILicensingService    # 保持哪些类不被混淆

#保留support下的所有类及其内部类
-keep class android.support.** {*;}

-keepclasseswithmembernames class * {  # 保持 native 方法不被混淆
    native <methods>;
}
-keepclasseswithmembers class * {   # 保持自定义控件类不被混淆
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {# 保持自定义控件类不被混淆
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers class * extends android.app.Activity { # 保持自定义控件类不被混淆
    public void *(android.view.View);
}
-keepclassmembers enum * {     # 保持枚举 enum 类不被混淆
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep class * implements android.os.Parcelable { # 保持 Parcelable 不被混淆
    public static final android.os.Parcelable$Creator *;
}
-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}
#保持 Serializable 不被混淆
-keepnames class * implements java.io.Serializable
#对于带有回调函数的onXXEvent的，不能被混淆
-keepclassmembers class * {
    void *(**On*Event);
}
#保持资源文件不被混淆
-keepclassmembers class **.R$* {
    public static <fields>;
}
-keep class **.R$* { *; }
-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.examples.android.model.** { *; }
#最重要的是，解析时使用的实体类一定不要混淆，不要然会出现闪退问题
-keep class yc.pointer.trip.bean.** { *; } #实体类不参与混淆
-dontwarn okhttp3.**
-dontwarn okio.**
#极光混淆
-dontoptimize
-dontpreverify
-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }
-keep class * extends cn.jpush.android.helpers.JPushMessageReceiver { *; }
-dontwarn cn.jiguang.**
-keep class cn.jiguang.** { *; }
#==================gson && protobuf 2.0.5 ~ 2.1.7 版本有引入 gson 和 protobuf ，增加排除混淆的配置。(2.1.8版本不需配置)========================
-dontwarn com.google.**
-keep class com.google.gson.** {*;}
-keep class com.google.protobuf.** {*;}

#====================腾讯bugly混淆==========================
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}