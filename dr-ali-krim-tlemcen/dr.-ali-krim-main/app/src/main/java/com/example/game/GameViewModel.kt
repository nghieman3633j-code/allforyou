package com.example.game

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

enum class GameScreen {
    HOME, ALPHABET, MATCH, BUILDER, LISTEN
}

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("arabic_tlemcen_prefs", Context.MODE_PRIVATE)

    // Speech events Flow
    private val _speakEvent = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val speakEvent = _speakEvent.asSharedFlow()

    // Persistent & transient game state
    var currentScreen by mutableStateOf(GameScreen.HOME)
    var score by mutableStateOf(0)
        private set
    var hearts by mutableStateOf(5)
        private set

    // Status / Mode markers
    var isGameOver by mutableStateOf(false)
    var isVictory by mutableStateOf(false)

    // --- Alphabet Game States ---
    var selectedLetterIdx by mutableStateOf(0)
    val currentLetter: ArabicLetter
        get() = GameData.LETTERS[selectedLetterIdx]
    val strokes = mutableStateListOf<List<Offset>>()

    // --- Word Match Game States ---
    var matchRoundIndex by mutableStateOf(0) // 0 to 3
    var matchArabicList = mutableStateListOf<String>()
    var matchTranslationList = mutableStateListOf<String>()
    var selectedArabic by mutableStateOf<String?>(null)
    var selectedTranslation by mutableStateOf<String?>(null)
    val matchedPairs = mutableStateListOf<String>()
    var failedArabic by mutableStateOf<String?>(null)
    var failedTranslation by mutableStateOf<String?>(null)
    var isCheckingMatch by mutableStateOf(false)

    // --- Sentence Builder States ---
    var sentenceIndex by mutableStateOf(0)
    val currentSentence: ArabicSentence
        get() = GameData.SENTENCES[sentenceIndex]
    var builtSentenceWords = mutableStateListOf<String>()
    var scrambledPoolWords = mutableStateListOf<String>()
    var checkedSentenceCorrect by mutableStateOf<Boolean?>(null) // null = not checked, true = correct, false = wrong

    // --- Listen & Pick States ---
    var listenIndex by mutableStateOf(0)
    lateinit var listenTarget: ArabicWord
    val listenChoices = mutableStateListOf<ArabicWord>()
    var selectedListenWord by mutableStateOf<ArabicWord?>(null)
    var checkListenCorrect by mutableStateOf<Boolean?>(null)

    init {
        // Load persistent score
        score = prefs.getInt("arabic-score", 0)
        setupWordMatchRound()
        setupSentenceRound()
        setupListenRound()
    }

    private fun saveScore(newScore: Int) {
        score = newScore
        prefs.edit().putInt("arabic-score", newScore).apply()
    }

    fun addScore(points: Int) {
        saveScore(score + points)
    }

    fun loseHeart() {
        hearts = (hearts - 1).coerceAtLeast(0)
        if (hearts == 0) {
            isGameOver = true
        }
    }

    fun restartGame() {
        hearts = 5
        saveScore(0)
        isGameOver = false
        isVictory = false
        currentScreen = GameScreen.HOME
        matchRoundIndex = 0
        sentenceIndex = 0
        listenIndex = 0
        setupWordMatchRound()
        setupSentenceRound()
        setupListenRound()
    }

    fun restoreHeartsOnly() {
        hearts = 5
        isGameOver = false
    }

    fun navigateToHome() {
        currentScreen = GameScreen.HOME
        isGameOver = false
        isVictory = false
    }

    fun triggerSpeech(text: String) {
        _speakEvent.tryEmit(text)
    }

    // --- Alphabet Logic ---
    fun selectLetter(index: Int) {
        selectedLetterIdx = index
        clearStrokes()
        triggerSpeech(GameData.LETTERS[index].exampleAr)
    }

    fun nextLetter() {
        if (selectedLetterIdx < GameData.LETTERS.lastIndex) {
            selectLetter(selectedLetterIdx + 1)
        } else {
            selectLetter(0)
        }
    }

    fun prevLetter() {
        if (selectedLetterIdx > 0) {
            selectLetter(selectedLetterIdx - 1)
        } else {
            selectLetter(GameData.LETTERS.lastIndex)
        }
    }

    fun clearStrokes() {
        strokes.clear()
    }

    // --- Word Match Logic ---
    fun setupWordMatchRound() {
        selectedArabic = null
        selectedTranslation = null
        matchedPairs.clear()
        failedArabic = null
        failedTranslation = null
        isCheckingMatch = false

        // Select 4 words based on round index:
        // Pack 0: academic, Pack 1: literature, Pack 2: tools, Pack 3: culture
        val categories = listOf("academic", "literature", "tools", "culture")
        val currentCategory = categories[matchRoundIndex % categories.size]
        val roundWords = GameData.WORDS.filter { it.category == currentCategory }.take(4)

        matchArabicList.clear()
        matchArabicList.addAll(roundWords.map { it.arabic }.shuffled())

        matchTranslationList.clear()
        matchTranslationList.addAll(roundWords.map { it.translation }.shuffled())
    }

    fun selectArabicWord(arabic: String) {
        if (isCheckingMatch || matchedPairs.contains(arabic)) return
        selectedArabic = if (selectedArabic == arabic) null else arabic
        checkMatchSelection()
    }

    fun selectTranslation(translation: String) {
        if (isCheckingMatch) return
        val testArabic = GameData.WORDS.firstOrNull { it.translation == translation }?.arabic ?: ""
        if (matchedPairs.contains(testArabic)) return

        selectedTranslation = if (selectedTranslation == translation) null else translation
        checkMatchSelection()
    }

    private fun checkMatchSelection() {
        val sa = selectedArabic
        val st = selectedTranslation
        if (sa != null && st != null) {
            // Check if they are correct
            val correctTrans = GameData.WORDS.firstOrNull { it.arabic == sa }?.translation
            if (correctTrans == st) {
                // Correct Match
                matchedPairs.add(sa)
                selectedArabic = null
                selectedTranslation = null
                addScore(10)
                // Trigger celebratory sound/speech
                triggerSpeech(sa)

                if (matchedPairs.size == 4) {
                    viewModelScope.launch {
                        delay(1200)
                        if (matchRoundIndex < 3) {
                            matchRoundIndex++
                            setupWordMatchRound()
                        } else {
                            isVictory = true
                        }
                    }
                }
            } else {
                // Incorrect match display
                failedArabic = sa
                failedTranslation = st
                isCheckingMatch = true
                loseHeart()

                viewModelScope.launch {
                    delay(1000)
                    failedArabic = null
                    failedTranslation = null
                    selectedArabic = null
                    selectedTranslation = null
                    isCheckingMatch = false
                }
            }
        }
    }

    // --- Sentence Builder Logic ---
    fun setupSentenceRound() {
        builtSentenceWords.clear()
        checkedSentenceCorrect = null

        val sentence = GameData.SENTENCES[sentenceIndex % GameData.SENTENCES.size]
        scrambledPoolWords.clear()
        scrambledPoolWords.addAll(sentence.correctOrder.shuffled())
    }

    fun addWordToSentence(word: String) {
        if (checkedSentenceCorrect == true) return
        builtSentenceWords.add(word)
        scrambledPoolWords.remove(word)
        checkedSentenceCorrect = null
    }

    fun removeWordFromSentence(word: String) {
        if (checkedSentenceCorrect == true) return
        builtSentenceWords.remove(word)
        scrambledPoolWords.add(word)
        checkedSentenceCorrect = null
    }

    fun verifySentence() {
        if (builtSentenceWords.isEmpty()) return
        val currentCorrectOrder = currentSentence.correctOrder
        if (builtSentenceWords.toList() == currentCorrectOrder) {
            checkedSentenceCorrect = true
            addScore(20)
            triggerSpeech(currentSentence.rawArabic)
            viewModelScope.launch {
                delay(2000)
                if (sentenceIndex < GameData.SENTENCES.lastIndex) {
                    sentenceIndex++
                    setupSentenceRound()
                } else {
                    isVictory = true
                }
            }
        } else {
            checkedSentenceCorrect = false
            loseHeart()
            viewModelScope.launch {
                delay(1500)
                // Return words back to scrambled pool on failure
                resetSentenceRound()
            }
        }
    }

    fun resetSentenceRound() {
        setupSentenceRound()
    }

    // --- Listen & Pick Logic ---
    fun setupListenRound() {
        selectedListenWord = null
        checkListenCorrect = null

        // Pick target word
        val pool = GameData.WORDS
        val target = pool[listenIndex % pool.size]
        listenTarget = target

        // Pick 3 distractors
        val distractors = pool.filter { it.arabic != target.arabic }.shuffled().take(3)
        val choices = (distractors + target).shuffled()

        listenChoices.clear()
        listenChoices.addAll(choices)

        // Trigger TTS read
        viewModelScope.launch {
            delay(500)
            speakListenTarget()
        }
    }

    fun speakListenTarget() {
        triggerSpeech(listenTarget.arabic)
    }

    fun makeListenSelection(word: ArabicWord) {
        if (checkListenCorrect == true) return
        selectedListenWord = word
        if (word.arabic == listenTarget.arabic) {
            checkListenCorrect = true
            addScore(15)
            viewModelScope.launch {
                delay(2000)
                if (listenIndex < GameData.WORDS.lastIndex) {
                    listenIndex++
                    setupListenRound()
                } else {
                    isVictory = true
                }
            }
        } else {
            checkListenCorrect = false
            loseHeart()
            viewModelScope.launch {
                delay(1500)
                selectedListenWord = null
                checkListenCorrect = null
            }
        }
    }
}
