package com.example.ui.theme

import androidx.compose.material3.LightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

// تعريف الألوان الرسمية للمشروع
val BleuPetrole = Color(0xFF0A4D68)  // منارة المنصورة الرئيسية
val VertZellij = Color(0xFF14746F)   // السيراميك والزليج
val DoreCalligraphie = Color(0xFFC99A2E) // الخط العربي واللمسات الجمالية
val TerreCuite = Color(0xFF9C4221)  // الطوب التقليدي
val IvoireFond = Color(0xFFFBF7EF)  // خلفية مريحة للعين
val EncreTexte = Color(0xFF15262B)  // تباين نصوص ممتاز

val TlemcenColorScheme = LightColorScheme(
    primary = BleuPetrole,
    secondary = VertZellij,
    tertiary = DoreCalligraphie,
    background = IvoireFond,
    surface = IvoireFond,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = EncreTexte,
    onSurface = EncreTexte,
    error = TerreCuite
)