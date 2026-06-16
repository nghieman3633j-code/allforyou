plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

// إعدادات بناء المشروع الفرعي الافتراضية
subprojects {
    repositories {
        google()
        mavenCentral()
    }
}
