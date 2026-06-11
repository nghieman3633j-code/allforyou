package com.example.game

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun GameApp(viewModel: GameViewModel) {
    // Force RTL local direction for Arabic language layouts, but allow English translations to render correctly
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            when (viewModel.currentScreen) {
                GameScreen.HOME -> HomeView(viewModel)
                else -> {
                    GameShell(
                        viewModel = viewModel,
                        title = when (viewModel.currentScreen) {
                            GameScreen.ALPHABET -> "تحسين نطق وكتابة الحروف"
                            GameScreen.MATCH -> "مطابقة الكلمات الأكاديمية"
                            GameScreen.BUILDER -> "تركيب الجمل الأدبية"
                            GameScreen.LISTEN -> "الاستماع والاستيعاب السمعي"
                            else -> ""
                        }
                    ) {
                        when (viewModel.currentScreen) {
                            GameScreen.ALPHABET -> AlphabetGameView(viewModel)
                            GameScreen.MATCH -> MatchGameView(viewModel)
                            GameScreen.BUILDER -> BuilderGameView(viewModel)
                            GameScreen.LISTEN -> ListenGameView(viewModel)
                            else -> {}
                        }
                    }
                }
            }

            // Game Over Dialog
            if (viewModel.isGameOver) {
                GameOverOverlay(viewModel)
            }

            // Victory Dialog
            if (viewModel.isVictory) {
                VictoryOverlay(viewModel)
            }
        }
    }
}

@Composable
fun HomeView(viewModel: GameViewModel) {
    Scaffold(
        bottomBar = {
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                modifier = Modifier
                    .shadow(16.dp, RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .border(
                        androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD9E6EB)),
                        RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Item 1: Home (Active)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { /* Already on Home */ }
                            .padding(vertical = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Home,
                            contentDescription = "الرئيسية",
                            tint = Color(0xFF0A4D68),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "الرئيسية",
                            color = Color(0xFF0A4D68),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Item 2: Lessons (Inactive but matching Tailwind Navigation)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { /* Decorative / No-op in single page */ }
                            .padding(vertical = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Book,
                            contentDescription = "الدروس",
                            tint = Color(0xFF49454F).copy(alpha = 0.6f),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "الدروس",
                            color = Color(0xFF49454F).copy(alpha = 0.6f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Item 3: Account (Inactive but matching Tailwind Navigation)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { /* Decorative / No-op in single page */ }
                            .padding(vertical = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "حسابي",
                            tint = Color(0xFF49454F).copy(alpha = 0.6f),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "حسابي",
                            color = Color(0xFF49454F).copy(alpha = 0.6f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Hero Header representing University of Tlemcen
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(PrimaryEmerald, PrimaryEmerald.copy(alpha = 0.85f))
                            )
                        )
                        .padding(horizontal = 20.dp, vertical = 40.dp)
                ) {
                    // Background decorative pattern
                    Canvas(modifier = Modifier.matchParentSize()) {
                        val w = size.width
                        val h = size.height
                        drawCircle(Color.White.copy(alpha = 0.03f), radius = w * 0.3f, center = Offset(w * 0.1f, h * 0.2f))
                        drawCircle(Color.White.copy(alpha = 0.05f), radius = w * 0.2f, center = Offset(w * 0.9f, h * 0.8f))
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Header badges
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFC3E8F4),
                                modifier = Modifier.padding(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Filled.EmojiEvents,
                                        contentDescription = "Score",
                                        tint = Color(0xFF002A38),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${viewModel.score} نقطة",
                                        color = Color(0xFF002A38),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }

                            // Hearts indicator
                            Row {
                                (1..5).forEach { index ->
                                    Icon(
                                        Icons.Filled.Favorite,
                                        contentDescription = "Heart $index",
                                        tint = if (index <= viewModel.hearts) SecondaryTerracotta else Color.White.copy(alpha = 0.25f),
                                        modifier = Modifier
                                            .size(24.dp)
                                            .padding(horizontal = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "جَامِعَة أَبُو بَكْر بَلْقَايِد - تِلِمْسَان",
                            color = AccentGold,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            letterSpacing = 1.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "قِسْمُ اللُّغَةِ وَالأَدَبِ العَرَبِيِّ",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "بوابة الألعاب اللغوية التفاعلية",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 28.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 36.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "تطبيق متطور موجه للطلبة الأجانب لتعليم لغة الضاد باستخدام أساليب تفاعلية ممتعة.",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { viewModel.restartGame() },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentGold),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.shadow(8.dp, RoundedCornerShape(12.dp))
                        ) {
                            Icon(Icons.Filled.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("تهيئة اللعب والبدء من جديد", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Section header
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 28.dp, start = 20.dp, end = 20.dp, bottom = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "اختر التحدي اللغوي المتاح",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp
                    )
                    Text(
                        text = "تدرّج عبر 4 ألعاب مصممة خصيصاً لتنمية مستواك الأكاديمي",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // 4 Core Games Grid-Like Stack
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Game 1 Card: Alphabet
                    GameMenuItem(
                        title = "الحروف الأبجدية النطق والكتابة",
                        subtitle = "Arabic Alphabet & Path",
                        desc = "تعلّم نطق وكتابة أشكال الحروف الـ28 على لوح كتابة تفاعلي مع أمثلة من مصطلحات الأدب العربي.",
                        icon = Icons.Filled.Translate,
                        containerColor = Color(0xFFF6E5DE),
                        borderColor = Color(0xFFE8C9BC),
                        iconBgColor = Color(0xFF9C4221),
                        onClick = {
                            viewModel.currentScreen = GameScreen.ALPHABET
                        }
                    )

                    // Game 2 Card: Word Match
                    GameMenuItem(
                        title = "طابق الكلمات الأكاديمية",
                        subtitle = "Word Matching Strategy",
                        desc = "طابق المفردات الأكاديمية وصور المصطلحات التلمسانية والأدبية لزيادة حصيلتك اللغوية.",
                        icon = Icons.Filled.GridOn,
                        containerColor = Color(0xFFE0F0EE),
                        borderColor = Color(0xFFBFE0DB),
                        iconBgColor = Color(0xFF14746F),
                        onClick = {
                            viewModel.currentScreen = GameScreen.MATCH
                            viewModel.setupWordMatchRound()
                        }
                    )

                    // Game 3 Card: Sentence Builder
                    GameMenuItem(
                        title = "ابنِ الجملة الأدبية الصحيحة",
                        subtitle = "Sentence Construction",
                        desc = "رتّب المفردات العربية المشوشة لتكوين جمل أكاديمية وبلاغية مفيدة بطريقة بنائية ممتعة.",
                        icon = Icons.Filled.MenuBook,
                        containerColor = Color(0xFFFBF1D6),
                        borderColor = Color(0xFFF0DDA8),
                        iconBgColor = Color(0xFFC99A2E),
                        onClick = {
                            viewModel.currentScreen = GameScreen.BUILDER
                            viewModel.setupSentenceRound()
                        }
                    )

                    // Game 4 Card: Listen & Pick
                    GameMenuItem(
                        title = "استمع للفظ واختر معناه",
                        subtitle = "Listen, Trace & Decipher",
                        desc = "تحدي الاستماع المباشر! أنصت لنطق الكلمة عبر جهازك ثم طابقها مع ترجمتها المقابلة.",
                        icon = Icons.Filled.VolumeUp,
                        containerColor = Color(0xFFDCEEF4),
                        borderColor = Color(0xFFBBD9E4),
                        iconBgColor = Color(0xFF0A4D68),
                        onClick = {
                            viewModel.currentScreen = GameScreen.LISTEN
                            viewModel.setupListenRound()
                        }
                    )
                }
            }

            // Department Info / Stats Section
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "إحصائيات المنهج التعليمي بتلمسان",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = PrimaryEmerald
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            StatCell(value = "28", label = "حرفًا أبجديًا")
                            StatCell(value = "+50", label = "مفردة جامعية")
                            StatCell(value = "4", label = "ألعاب مبتكرة")
                        }
                    }
                }
            }

            // Footer block
            item {
                Text(
                    text = "مشروع مؤسسة ناشئة · قسم اللغة والأدب العربي · جامعة تلمسان\n© 2026 جميع الحقوق محفوظة",
                    textAlign = TextAlign.Center,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
fun GameMenuItem(
    title: String,
    subtitle: String,
    desc: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    containerColor: Color,
    borderColor: Color,
    iconBgColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .shadow(1.dp, RoundedCornerShape(28.dp)),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Background Grid
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = desc,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Arrow Indicator
            Icon(
                Icons.Filled.ArrowBack, // Since RTL, ArrowBack points right (forward)
                contentDescription = null,
                tint = PrimaryEmerald,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun StatCell(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontWeight = FontWeight.Black,
            fontSize = 24.sp,
            color = SecondaryTerracotta
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun GameShell(
    viewModel: GameViewModel,
    title: String,
    content: @Composable () -> Unit
) {
    Scaffold(
        topBar = {
            Surface(
                color = PrimaryEmerald,
                modifier = Modifier.shadow(4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.navigateToHome() }) {
                        Icon(
                            Icons.Filled.ArrowForward, // RTL points back
                            contentDescription = "رجوع",
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = title,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.weight(1f)
                    )

                    // Stats Pills
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.padding(horizontal = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 4.dp, horizontal = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Filled.EmojiEvents,
                                    contentDescription = null,
                                    tint = AccentGold,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${viewModel.score}",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        // Hearts
                        Row(modifier = Modifier.padding(horizontal = 4.dp)) {
                            (1..5).forEach { index ->
                                Icon(
                                    Icons.Filled.Favorite,
                                    contentDescription = null,
                                    tint = if (index <= viewModel.hearts) SecondaryTerracotta else Color.White.copy(alpha = 0.25f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(WarmSandBackground)
        ) {
            content()
        }
    }
}

// =================== GAME Screen 1: ALPHABET ===================
@Composable
fun AlphabetGameView(viewModel: GameViewModel) {
    val letter = viewModel.currentLetter
    val listState = viewModel.strokes

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Horizontal selector for letters
        Text(
            text = "تصفح الحروف الـ28 النطق والتمثيل:",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            FilledIconButton(
                onClick = { viewModel.prevLetter() },
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = PrimaryEmerald)
            ) {
                Icon(Icons.Filled.KeyboardArrowRight, contentDescription = "السابق")
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                modifier = Modifier.padding(horizontal = 12.dp)
            ) {
                Text(
                    text = "الحرف ${viewModel.selectedLetterIdx + 1} من 28",
                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 12.dp),
                    fontWeight = FontWeight.Bold,
                    color = PrimaryEmerald,
                    fontSize = 13.sp
                )
            }

            FilledIconButton(
                onClick = { viewModel.nextLetter() },
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = PrimaryEmerald)
            ) {
                Icon(Icons.Filled.KeyboardArrowLeft, contentDescription = "التالي")
            }
        }

        // Expanded Main Content Card (Split layout conceptually, or vertical stack)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .shadow(2.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Letter details Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "اسم الحرف: ${letter.nameAr} (${letter.nameEn})",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "النطق الصوتي: [ ${letter.phonetic} ]",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = { viewModel.triggerSpeech(letter.char) },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentGold),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Outlined.VolumeUp, contentDescription = "استمع للفظ")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("نطق الحرف", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Handwriting Sandbox tracing frame
                Text(
                    text = "ارسم الحرف لتثبيت طريقة كتابته وبلاغته:",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(WarmSandBackground)
                        .border(1.5.dp, PrimaryEmerald.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    // Transparent guidelines background letter
                    Text(
                        text = letter.char,
                        fontSize = 125.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                        textAlign = TextAlign.Center
                    )

                    val userStrokeColor = SecondaryTerracotta
                    // Canvas to track and draw strokes
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(letter.char) {
                                detectDragGestures(
                                    onDragStart = { offset ->
                                        listState.add(listOf(offset))
                                    },
                                    onDrag = { change, _ ->
                                        change.consume()
                                        if (listState.isNotEmpty()) {
                                            val lastStroke = listState.last()
                                            listState[listState.lastIndex] = lastStroke + change.position
                                        }
                                    }
                                )
                            }
                    ) {
                        listState.forEach { stroke ->
                            for (i in 0 until stroke.size - 1) {
                                drawLine(
                                    color = userStrokeColor,
                                    start = stroke[i],
                                    end = stroke[i + 1],
                                    strokeWidth = 6.dp.toPx(),
                                    cap = StrokeCap.Round
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Control panel for handwriting sandbox & Vocabulary sample
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { viewModel.clearStrokes() },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Filled.DeleteSweep, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("مسح اللوحة", fontSize = 12.sp)
                    }

                    // Vocabulary trigger
                    Card(
                        colors = CardDefaults.cardColors(containerColor = PrimaryEmerald.copy(alpha = 0.06f)),
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 12.dp)
                            .clickable { viewModel.triggerSpeech(letter.exampleAr) },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Filled.MenuBook,
                                contentDescription = null,
                                tint = PrimaryEmerald,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = "مثال أدبي: ${letter.exampleAr}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = PrimaryEmerald
                                )
                                Text(
                                    text = letter.exampleTrans,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// =================== GAME Screen 2: WORD MATCH ===================
@Composable
fun MatchGameView(viewModel: GameViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "تحدي ربط المفردات الأكاديمية بالأدبية:",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "طابق الكلمة العربية مع الترجمة البلاغية الصحيحة بالإنجليزية والفرنسية",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = AccentGold.copy(alpha = 0.15f)
            ) {
                Text(
                    text = "الحقيبة الحالية: ${
                        when (viewModel.matchRoundIndex) {
                            0 -> "جامعية أكاديمية"
                            1 -> "مصطلحات أدبية"
                            2 -> "أدوات الكتابة"
                            else -> "ثقافة وتلمسانية"
                        }
                    }",
                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 12.dp),
                    fontWeight = FontWeight.Bold,
                    color = AccentGold,
                    fontSize = 12.sp
                )
            }

            Text(
                text = "التقدم: ${viewModel.matchedPairs.size} / 4",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13.sp,
                color = PrimaryEmerald
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Left/Right side columns
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Left Column: Arabic Words
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                viewModel.matchArabicList.forEach { arabicWord ->
                    val isMatched = viewModel.matchedPairs.contains(arabicWord)
                    val isSelected = viewModel.selectedArabic == arabicWord
                    val isFailed = viewModel.failedArabic == arabicWord

                    val containerColor = when {
                        isMatched -> Color.Gray.copy(alpha = 0.08f)
                        isFailed -> SecondaryTerracotta.copy(alpha = 0.2f)
                        isSelected -> PrimaryEmerald.copy(alpha = 0.15f)
                        else -> MaterialTheme.colorScheme.surface
                    }

                    val borderColor = when {
                        isMatched -> Color.Transparent
                        isFailed -> SecondaryTerracotta
                        isSelected -> PrimaryEmerald
                        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                    }

                    val textColor = when {
                        isMatched -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        isFailed -> SecondaryTerracotta
                        isSelected -> PrimaryEmerald
                        else -> MaterialTheme.colorScheme.onSurface
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clickable(!isMatched && !viewModel.isCheckingMatch) {
                                viewModel.selectArabicWord(arabicWord)
                            }
                            .border(1.5.dp, borderColor, RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = containerColor)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = arabicWord,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp,
                                    color = textColor
                                )
                                if (isMatched) {
                                    Icon(
                                        Icons.Filled.CheckCircle,
                                        contentDescription = "تمت المطابقة",
                                        tint = PrimaryEmerald,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Right Column: Translation Cards
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                viewModel.matchTranslationList.forEach { translationText ->
                    val testArabic = GameData.WORDS.firstOrNull { it.translation == translationText }?.arabic ?: ""
                    val isMatched = viewModel.matchedPairs.contains(testArabic)
                    val isSelected = viewModel.selectedTranslation == translationText
                    val isFailed = viewModel.failedTranslation == translationText

                    val containerColor = when {
                        isMatched -> Color.Gray.copy(alpha = 0.08f)
                        isFailed -> SecondaryTerracotta.copy(alpha = 0.2f)
                        isSelected -> AccentGold.copy(alpha = 0.15f)
                        else -> MaterialTheme.colorScheme.surface
                    }

                    val borderColor = when {
                        isMatched -> Color.Transparent
                        isFailed -> SecondaryTerracotta
                        isSelected -> AccentGold
                        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                    }

                    val textColor = when {
                        isMatched -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        isFailed -> SecondaryTerracotta
                        isSelected -> AccentGold
                        else -> MaterialTheme.colorScheme.onSurface
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clickable(!isMatched && !viewModel.isCheckingMatch) {
                                viewModel.selectTranslation(translationText)
                            }
                            .border(1.5.dp, borderColor, RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = containerColor)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = translationText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = textColor,
                                textAlign = TextAlign.Center,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// =================== GAME Screen 3: SENTENCE BUILDER ===================
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BuilderGameView(viewModel: GameViewModel) {
    val sentence = viewModel.currentSentence

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "تحدي تراكيب الجمل الفصيحة والبليغة:",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Target Translation prompt card
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = PrimaryEmerald.copy(alpha = 0.08f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Outlined.Book,
                    contentDescription = null,
                    tint = PrimaryEmerald,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "ترجم الجملة التالية:",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        color = PrimaryEmerald
                    )
                    Text(
                        text = sentence.translation,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Built Sentence active board area
        Text(
            text = "جملتك المبنية (انقر على الكلمة لإزالتها):",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(vertical = 6.dp)
                .border(
                    1.5.dp,
                    when (viewModel.checkedSentenceCorrect) {
                        true -> PrimaryEmerald
                        false -> SecondaryTerracotta
                        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                    },
                    RoundedCornerShape(12.dp)
                ),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            FlowRow(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (viewModel.builtSentenceWords.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "انقر على الكلمات بالأسفل للترتيب هنا...",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Light
                        )
                    }
                } else {
                    viewModel.builtSentenceWords.forEach { word ->
                        SuggestionChip(
                            onClick = { viewModel.removeWordFromSentence(word) },
                            label = { Text(word, fontWeight = FontWeight.Black, fontSize = 13.sp) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = PrimaryEmerald.copy(alpha = 0.12f),
                                labelColor = PrimaryEmerald
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Scrambled Selection Pool
        Text(
            text = "مخزن مفردات الجملة المشوشة:",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                contentAlignment = Alignment.Center
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.Center,
                    verticalArrangement = Arrangement.Center,
                    maxItemsInEachRow = 4,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    viewModel.scrambledPoolWords.forEach { word ->
                        Button(
                            onClick = { viewModel.addWordToSentence(word) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.padding(6.dp)
                        ) {
                            Text(
                                text = word,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Control Buttons Block
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { viewModel.resetSentenceRound() },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(0.4f)
            ) {
                Icon(Icons.Filled.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("إعادة تعيين", fontSize = 12.sp)
            }

            Button(
                onClick = { viewModel.verifySentence() },
                enabled = viewModel.builtSentenceWords.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = AccentGold),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(0.6f)
            ) {
                Icon(Icons.Filled.Spellcheck, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("التحقق من الترتيب", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }

        // Live banner feedback
        AnimatedVisibility(
            visible = viewModel.checkedSentenceCorrect != null,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            val correct = viewModel.checkedSentenceCorrect ?: false
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (correct) PrimaryEmerald else SecondaryTerracotta
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        if (correct) Icons.Filled.CheckCircle else Icons.Filled.Dangerous,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (correct) "صحيح! أحسنت وممتاز (+20 نقطة)" else "ترتيب جمل غير صحيح، يرجى المحاولة والتصحيح!",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

// =================== GAME Screen 4: LISTEN & PICK ===================
@Composable
fun ListenGameView(viewModel: GameViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "تحدي الاستماع الصوتي والترجمة:",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "اضغط على زر مكبر الصوت للفظ، ثم طابق اللفظ بمفرده المقابلة من الخيارات",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Glowing Voice Speaker Module
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(PrimaryEmerald.copy(alpha = 0.1f))
                .border(2.dp, PrimaryEmerald, CircleShape)
                .clickable { viewModel.speakListenTarget() },
            contentAlignment = Alignment.Center
        ) {
            // Animated ring decorative placeholder conceptually using simple layouts
            Icon(
                Icons.Filled.VolumeUp,
                contentDescription = "استمع مجدداً",
                tint = PrimaryEmerald,
                modifier = Modifier.size(42.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "اضغط للاستماع",
            fontWeight = FontWeight.Bold,
            color = PrimaryEmerald,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "اختر الترجمة الصحيحة للمفرزة التي سمعتها:",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Multiple choice 4 cards grid layout
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(viewModel.listenChoices) { choice ->
                val isSelected = viewModel.selectedListenWord == choice
                val correct = viewModel.checkListenCorrect

                val containerColor = when {
                    isSelected && correct == true -> PrimaryEmerald.copy(alpha = 0.15f)
                    isSelected && correct == false -> SecondaryTerracotta.copy(alpha = 0.15f)
                    else -> MaterialTheme.colorScheme.surface
                }

                val borderColor = when {
                    isSelected && correct == true -> PrimaryEmerald
                    isSelected && correct == false -> SecondaryTerracotta
                    else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                }

                val tintColor = when {
                    isSelected && correct == true -> PrimaryEmerald
                    isSelected && correct == false -> SecondaryTerracotta
                    else -> MaterialTheme.colorScheme.onSurface
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .clickable { viewModel.makeListenSelection(choice) }
                        .border(1.5.dp, borderColor, RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = containerColor)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = when {
                                    isSelected && correct == true -> Icons.Filled.CheckCircle
                                    isSelected && correct == false -> Icons.Filled.Cancel
                                    else -> Icons.Filled.Translate
                                },
                                contentDescription = null,
                                tint = tintColor.copy(alpha = 0.7f),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = choice.translation,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = tintColor,
                                textAlign = TextAlign.Center,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// =================== OVERLAYS ===================
@Composable
fun GameOverOverlay(viewModel: GameViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.75f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Filled.Dangerous,
                    contentDescription = null,
                    tint = SecondaryTerracotta,
                    modifier = Modifier.size(72.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "نَفِدَتْ قُلُوبُ اللَّعِب!",
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    color = SecondaryTerracotta,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "لقد ارتكبت بعض الأخطاء وفقدت قلوب اللعب المتاحة، المحاولة مستمرة في قسم اللغة العربية بجامعة تلمسان لتجاوز الصعاب!",
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "رصيد النقاط المحقق: ${viewModel.score} نقطة",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = PrimaryEmerald
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.navigateToHome() },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("الرئيسية")
                    }

                    Button(
                        onClick = { viewModel.restartGame() },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentGold),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1.2f)
                    ) {
                        Text("المحاولة مجدداً", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun VictoryOverlay(viewModel: GameViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.75f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .padding(16.dp)
                .border(2.dp, AccentGold, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Filled.EmojiEvents,
                    contentDescription = null,
                    tint = AccentGold,
                    modifier = Modifier.size(80.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "تَهَانِينَا لَكَ! إِجَازَةٌ مُبَارَكَةٌ",
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    color = PrimaryEmerald,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "احسنت عمودا بلاغياً وأكاديمياً متميزاً! لقد أتممت المستوى بنجاح وتوجت بالمركز الأول لطلبة تلمسان الأجانب.",
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "مجموع الرصيد الأكاديمي: ${viewModel.score} نقطة",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = AccentGold
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { viewModel.restartGame() },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("العودة والقسم الجديد من الألعاب", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
