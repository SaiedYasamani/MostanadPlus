# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\user\Downloads\Compressed\sdk\sdk/tools/proguard/proguard-android.txt
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
-dontwarn com.squareup.picasso.**
-dontwarn com.google.firebase.**
-dontwarn retrofit2.**
-ignorewarnings
-keepattributes Signature
-keep class sun.misc.Unsafe { *; }
-keep class net.jhoobin.jhub.json.** { *; }
#rule for siyamed github library
-dontwarn android.support.v7.**
-keep class android.support.v7.** { *; }
-keep interface android.support.v7.* { *; }
-keepattributes *Annotation,Signature
-dontwarn com.github.siyamed.**
-keep class com.github.siyamed.shapeimageview.**{ *; }
#rule for momo library
-keep class ir.mono.** {*;}
-dontwarn ir.mono.**
-keepnames class ir.mono.**{*;}
#rule for jwplayer
-keepattributes *longtailvideo*
-keep class com.longtailvideo.*{ *; }
-dontwarn com.longtailvideo.*
#rule for bottombar
-dontwarn com.roughike.bottombar.**

#-keep class com.mostanad.plus.interfaces** { *; }
