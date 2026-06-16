android {
    namespace = "com.aistudio.arabiclearn.tlemcen" // تم التعديل لمنع التعارض
    compileSdk = 36

    defaultConfig {
        applicationId = "com.aistudio.arabiclearn.tlemcen"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true   // حماية الكود من الهندسة العكسية
            isShrinkResources = true // حذف الصور والملفات غير المستخدمة لتقليل الحجم
            isCrunchPngs = true      // ضغط الصور تلقائياً
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}