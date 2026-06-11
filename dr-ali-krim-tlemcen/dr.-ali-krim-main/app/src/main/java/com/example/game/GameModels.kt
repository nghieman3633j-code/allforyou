package com.example.game

data class ArabicLetter(
    val char: String,
    val nameAr: String,
    val nameEn: String,
    val phonetic: String,
    val exampleAr: String,
    val exampleTrans: String // Bilingual: e.g. "أستاذ (Professor / Professeur)"
)

data class ArabicWord(
    val arabic: String,
    val transliteration: String,
    val translation: String, // e.g. "University / Université"
    val category: String
)

data class ArabicSentence(
    val rawArabic: String,
    val translation: String, // e.g., "I study literature in Tlemcen / J'étudie la littérature à Tlemcen"
    val translationWordQuiz: String, // Simpler for UX display
    val correctOrder: List<String>
)

object GameData {
    val LETTERS = listOf(
        ArabicLetter("أ", "ألف", "Alif", "a / e / i", "أُسْتَاذ", "Professor / Professeur"),
        ArabicLetter("ب", "باء", "Baa", "b", "بَلَاغَة", "Rhetoric / Rhétorique"),
        ArabicLetter("ت", "تاء", "Taa", "t", "تِلِمْسَان", "Tlemcen"),
        ArabicLetter("ث", "ثاء", "Thaa", "th", "ثَقَافَة", "Culture"),
        ArabicLetter("ج", "جيم", "Jeem", "j", "جَامِعَة", "University / Université"),
        ArabicLetter("ح", "حاء", "Haa", "h", "حِوَار", "Dialogue"),
        ArabicLetter("خ", "خاء", "Khaa", "kh", "خَطَّاط", "Calligrapher / Calligraphe"),
        ArabicLetter("د", "دال", "Daal", "d", "دَفْتَر", "Notebook / Cahier"),
        ArabicLetter("ذ", "ذال", "Thaal", "dh", "ذَهَب", "Gold / Or"),
        ArabicLetter("ر", "راء", "Raa", "r", "رِوَايَة", "Novel / Roman"),
        ArabicLetter("ز", "زاي", "Zaay", "z", "زَمِيل", "Colleague / Collègue"),
        ArabicLetter("س", "سين", "Seen", "s", "سَجْع", "Prose Rhyme / Prose rimée"),
        ArabicLetter("ش", "شين", "Sheen", "sh", "شِعْر", "Poetry / Poésie"),
        ArabicLetter("ص", "صاد", "Saad", "s (emphatic)", "صَفْحَة", "Page"),
        ArabicLetter("ض", "ضاد", "Daad", "d (emphatic)", "ضَاد", "Daad / Lettre Daad"),
        ArabicLetter("ط", "طاء", "Taa", "t (emphatic)", "طَالِب", "Student / Étudiant"),
        ArabicLetter("ظ", "ظاء", "Thaa", "th (emphatic)", "ظَاهِر", "Apparent"),
        ArabicLetter("ع", "عين", "Ayn", "‘ (pharyngeal)", "عِلْم", "Science / Savoir"),
        ArabicLetter("غ", "غين", "Ghayn", "gh / r-sound", "غُرْبَة", "Exile / Altérité"),
        ArabicLetter("ف", "فاء", "Faa", "f", "فَصَاحَة", "Eloquence"),
        ArabicLetter("ق", "قاف", "Qaaf", "q (guttural)", "قَصِيدَة", "Ode / Poème"),
        ArabicLetter("ك", "كاف", "Kaaf", "k", "كِتَاب", "Book / Livre"),
        ArabicLetter("ل", "لام", "Laam", "l", "لُغَة", "Language / Langue"),
        ArabicLetter("م", "ميم", "Meem", "m", "مَكْتَبَة", "Library / Bibliothèque"),
        ArabicLetter("ن", "نون", "Noon", "n", "نَقْد", "Criticism / Critique"),
        ArabicLetter("هـ", "هاء", "Haa", "h (soft)", "هَيْكَل", "Structure / Châssis"),
        ArabicLetter("و", "واو", "Waaw", "w / oo", "وَزْن", "Poetic Meter / Mètre poétique"),
        ArabicLetter("ي", "ياء", "Yaa", "y / ee", "يَرَاع", "Reed Pen / Calame")
    )

    val WORDS = listOf(
        ArabicWord("جَامِعَة", "Jaami'ah", "University / Université", "academic"),
        ArabicWord("طَالِب", "Taalib", "Student / Étudiant", "academic"),
        ArabicWord("أُسْتَاذ", "Ustaadh", "Professor / Professeur", "academic"),
        ArabicWord("مَكْتَبَة", "Maktabah", "Library / Bibliothèque", "academic"),
        ArabicWord("رِوَايَة", "Riwaayah", "Novel / Roman", "literature"),
        ArabicWord("شِعْر", "Shi'r", "Poetry / Poésie", "literature"),
        ArabicWord("بَلَاغَة", "Balaaghah", "Rhetoric / Rhétorique", "literature"),
        ArabicWord("نَقْد", "Naqd", "Criticism / Critique", "literature"),
        ArabicWord("كِتَاب", "Kitaab", "Book / Livre", "tools"),
        ArabicWord("قَلَم", "Qalam", "Pen / Stylo", "tools"),
        ArabicWord("دَفْتَر", "Daftar", "Notebook / Cahier", "tools"),
        ArabicWord("صَفْحَة", "Safhah", "Page", "tools"),
        ArabicWord("تِلِمْسَان", "Tilimsan", "Tlemcen", "culture"),
        ArabicWord("ثَقَافَة", "Thaqaafah", "Culture", "culture"),
        ArabicWord("حِوَار", "Hiwaar", "Dialogue", "culture"),
        ArabicWord("عِلْم", "'Ilm", "Science / Connaissance", "culture")
    )

    val SENTENCES = listOf(
        ArabicSentence(
            rawArabic = "أَدْرُسُ الأَدَبَ فِي تِلِمْسَان",
            translation = "I study literature in Tlemcen / J'étudie la littérature à Tlemcen",
            translationWordQuiz = "I study literature in Tlemcen",
            correctOrder = listOf("أَدْرُسُ", "الأَدَبَ", "فِي", "تِلِمْسَان")
        ),
        ArabicSentence(
            rawArabic = "اللُّغَةُ العَرَبِيَّةُ لُغَةُ الضَّادِ",
            translation = "Arabic is the language of Daad / L'arabe est la langue du Daad",
            translationWordQuiz = "Arabic is the language of Daad",
            correctOrder = listOf("اللُّغَةُ", "العَرَبِيَّةُ", "لُغَةُ", "الضَّادِ")
        ),
        ArabicSentence(
            rawArabic = "الأُسْتَاذُ يَشْرَحُ قَصِيدَةً بَلِيغَةً",
            translation = "The professor explains an eloquent poem / Le professeur explique un poème éloquent",
            translationWordQuiz = "The professor explains an eloquent poem",
            correctOrder = listOf("الأُسْتَاذُ", "يَشْرَحُ", "قَصِيدَةً", "بَلِيغَةً")
        ),
        ArabicSentence(
            rawArabic = "أَكْتُبُ دَرْسَ البَلَاغَةِ بِالقَلَمِ",
            translation = "I write the rhetoric lesson with a pen / J'écris la leçon de rhétorique avec un stylo",
            translationWordQuiz = "I write the rhetoric lesson with a pen",
            correctOrder = listOf("أَكْتُبُ", "دَرْسَ", "البَلَاغَةِ", "بِالقَلَمِ")
        ),
        ArabicSentence(
            rawArabic = "أَبْحَثُ عَنْ كِتَابٍ فِي المَكْتَبَةِ",
            translation = "I look for a book in the library / Je cherche un livre dans la bibliothèque",
            translationWordQuiz = "I look for a book in the library",
            correctOrder = listOf("أَبْحَثُ", "عَنْ", "كِتَابٍ", "فِي", "المَكْتَبَةِ")
        )
    )
}
