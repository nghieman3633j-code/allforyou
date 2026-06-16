plugins {
    id("com.android.application") version "9.1.1" apply false
    id("com.android.library") version "9.1.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.20" apply false
}

// إعدادات بناء المشروع الفرعي الافتراضية
subprojects {
    repositories {
        google()
        mavenCentral()
    }
}
