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

# ============================================================
#  قواعد أمان لتفعيل تقليص الكود (R8) دون أعطال
#  التطبيق يعمل دون اتصال؛ مكتبات الشبكة المُعلَنة غير مستخدمة
#  وسيُزيلها R8 بأمان. القواعد التالية تمنع أي تحذيرات/أعطال.
# ============================================================

# نماذج بيانات اللعبة (احتياطًا لأي وصول انعكاسي مستقبلي)
-keep class com.example.game.** { *; }

# Kotlin metadata و Coroutines
-keepclassmembers class kotlin.Metadata { *; }
-dontwarn kotlinx.coroutines.**

# مكتبات مُعلَنة وغير مستخدمة حاليًا (تجنّب تحذيرات R8)
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn com.squareup.moshi.**
-dontwarn com.google.firebase.**
-dontwarn org.jetbrains.annotations.**
