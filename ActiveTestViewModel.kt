package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.data.repository.ExamDataRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ActiveTestViewModel(application: Application) : AndroidViewModel(application) {
  private val repository = ExamDataRepository(AppDatabase.getDatabase(application))

  private val _test = MutableStateFlow<MockTest?>(null)
  val test: StateFlow<MockTest?> = _test.asStateFlow()

  private val _currentSectionIndex = MutableStateFlow(0)
  val currentSectionIndex: StateFlow<Int> = _currentSectionIndex.asStateFlow()

  private val _currentQuestionIndex = MutableStateFlow(0)
  val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

  private val _userAnswers = MutableStateFlow<Map<String, UserQuestionState>>(emptyMap())
  val userAnswers: StateFlow<Map<String, UserQuestionState>> = _userAnswers.asStateFlow()

  private val _timeRemainingSeconds = MutableStateFlow(0L)
  val timeRemainingSeconds: StateFlow<Long> = _timeRemainingSeconds.asStateFlow()

  private val _totalTimeTakenSeconds = MutableStateFlow(0L)
  val totalTimeTakenSeconds: StateFlow<Long> = _totalTimeTakenSeconds.asStateFlow()

  private val _isEnglishLanguage = MutableStateFlow(true)
  val isEnglishLanguage: StateFlow<Boolean> = _isEnglishLanguage.asStateFlow()

  private val _isPaletteOpen = MutableStateFlow(false)
  val isPaletteOpen: StateFlow<Boolean> = _isPaletteOpen.asStateFlow()

  private val _isSubmitDialogOpen = MutableStateFlow(false)
  val isSubmitDialogOpen: StateFlow<Boolean> = _isSubmitDialogOpen.asStateFlow()

  private val _isTestCompleted = MutableStateFlow(false)
  val isTestCompleted: StateFlow<Boolean> = _isTestCompleted.asStateFlow()

  private val _completedAttemptId = MutableStateFlow<Long?>(null)
  val completedAttemptId: StateFlow<Long?> = _completedAttemptId.asStateFlow()

  private val _selectedVendor = MutableStateFlow(ExamCbtVendor.EDUQUITY)
  val selectedVendor: StateFlow<ExamCbtVendor> = _selectedVendor.asStateFlow()

  private val _isQuestionPaperOpen = MutableStateFlow(false)
  val isQuestionPaperOpen: StateFlow<Boolean> = _isQuestionPaperOpen.asStateFlow()

  private val _isInstructionsOpen = MutableStateFlow(false)
  val isInstructionsOpen: StateFlow<Boolean> = _isInstructionsOpen.asStateFlow()

  val userProfile = repository.getUserProfile()

  private var timerJob: Job? = null
  private var questionStartTime = System.currentTimeMillis()

  fun setVendor(vendor: ExamCbtVendor) {
    _selectedVendor.value = vendor
  }

  fun toggleQuestionPaper(open: Boolean) {
    _isQuestionPaperOpen.value = open
  }

  fun toggleInstructions(open: Boolean) {
    _isInstructionsOpen.value = open
  }

  fun initTest(
    testId: String,
    customQuestions: List<com.example.data.local.CustomQuestionEntity> = emptyList(),
    customMockTests: List<com.example.data.local.CustomMockTestEntity> = emptyList()
  ) {
    val loadedTest = repository.getMockTestById(testId, customQuestions, customMockTests)
    _test.value = loadedTest
    _selectedVendor.value = loadedTest.vendor

    val initialDurationSeconds = loadedTest.durationMinutes * 60L
    _timeRemainingSeconds.value = initialDurationSeconds
    _totalTimeTakenSeconds.value = 0L

    val initialAnswers = mutableMapOf<String, UserQuestionState>()
    loadedTest.questions.forEachIndexed { index, question ->
      initialAnswers[question.id] = UserQuestionState(
        questionId = question.id,
        status = if (index == 0) QuestionStatus.NOT_ANSWERED else QuestionStatus.NOT_VISITED
      )
    }
    _userAnswers.value = initialAnswers
    _currentSectionIndex.value = 0
    _currentQuestionIndex.value = 0
    _isTestCompleted.value = false
    _completedAttemptId.value = null

    startTimer()
  }

  private fun startTimer() {
    timerJob?.cancel()
    questionStartTime = System.currentTimeMillis()
    timerJob = viewModelScope.launch {
      while (_timeRemainingSeconds.value > 0 && !_isTestCompleted.value) {
        delay(1000)
        _timeRemainingSeconds.value -= 1
        _totalTimeTakenSeconds.value += 1
        recordTimeForCurrentQuestion()
      }
      if (_timeRemainingSeconds.value <= 0 && !_isTestCompleted.value) {
        submitTest()
      }
    }
  }

  private fun recordTimeForCurrentQuestion() {
    val currentQ = currentQuestion ?: return
    val answers = _userAnswers.value.toMutableMap()
    val state = answers[currentQ.id] ?: UserQuestionState(currentQ.id)
    answers[currentQ.id] = state.copy(timeSpentSeconds = state.timeSpentSeconds + 1)
    _userAnswers.value = answers
  }

  val currentQuestion: TestQuestion?
    get() {
      val t = _test.value ?: return null
      val filtered = questionsForCurrentSection
      return filtered.getOrNull(_currentQuestionIndex.value)
    }

  val currentSection: ExamSectionInfo?
    get() {
      val t = _test.value ?: return null
      return t.sections.getOrNull(_currentSectionIndex.value)
    }

  val questionsForCurrentSection: List<TestQuestion>
    get() {
      val t = _test.value ?: return emptyList()
      val sec = currentSection ?: return emptyList()
      return t.questions.filter { it.sectionId == sec.id }
    }

  fun switchSection(index: Int) {
    if (index in 0 until (_test.value?.sections?.size ?: 0)) {
      _currentSectionIndex.value = index
      _currentQuestionIndex.value = 0
      markCurrentQuestionVisited()
    }
  }

  fun jumpToQuestion(globalIndex: Int) {
    val t = _test.value ?: return
    if (globalIndex in 0 until t.questions.size) {
      val targetQ = t.questions[globalIndex]
      val secIdx = t.sections.indexOfFirst { it.id == targetQ.sectionId }
      if (secIdx >= 0) {
        _currentSectionIndex.value = secIdx
        val secQuestions = t.questions.filter { it.sectionId == targetQ.sectionId }
        val inSecIdx = secQuestions.indexOfFirst { it.id == targetQ.id }
        _currentQuestionIndex.value = if (inSecIdx >= 0) inSecIdx else 0
        markCurrentQuestionVisited()
      }
    }
    _isPaletteOpen.value = false
  }

  private fun markCurrentQuestionVisited() {
    val currentQ = currentQuestion ?: return
    val answers = _userAnswers.value.toMutableMap()
    val state = answers[currentQ.id] ?: UserQuestionState(currentQ.id)
    if (state.status == QuestionStatus.NOT_VISITED) {
      answers[currentQ.id] = state.copy(status = QuestionStatus.NOT_ANSWERED)
      _userAnswers.value = answers
    }
  }

  fun selectOption(optionIndex: Int) {
    val currentQ = currentQuestion ?: return
    val answers = _userAnswers.value.toMutableMap()
    val state = answers[currentQ.id] ?: UserQuestionState(currentQ.id)
    answers[currentQ.id] = state.copy(
      selectedOptionIndex = optionIndex,
      status = QuestionStatus.ANSWERED
    )
    _userAnswers.value = answers
  }

  fun clearResponse() {
    val currentQ = currentQuestion ?: return
    val answers = _userAnswers.value.toMutableMap()
    val state = answers[currentQ.id] ?: UserQuestionState(currentQ.id)
    answers[currentQ.id] = state.copy(
      selectedOptionIndex = null,
      status = QuestionStatus.NOT_ANSWERED
    )
    _userAnswers.value = answers
  }

  fun markForReviewAndNext() {
    val currentQ = currentQuestion ?: return
    val answers = _userAnswers.value.toMutableMap()
    val state = answers[currentQ.id] ?: UserQuestionState(currentQ.id)

    val newStatus = if (state.selectedOptionIndex != null) {
      QuestionStatus.ANSWERED_AND_MARKED_FOR_REVIEW
    } else {
      QuestionStatus.MARKED_FOR_REVIEW
    }

    answers[currentQ.id] = state.copy(status = newStatus)
    _userAnswers.value = answers

    goToNextQuestion()
  }

  fun saveAndMarkForReview() {
    val currentQ = currentQuestion ?: return
    val answers = _userAnswers.value.toMutableMap()
    val state = answers[currentQ.id] ?: UserQuestionState(currentQ.id)

    val newStatus = if (state.selectedOptionIndex != null) {
      QuestionStatus.ANSWERED_AND_MARKED_FOR_REVIEW
    } else {
      QuestionStatus.MARKED_FOR_REVIEW
    }

    answers[currentQ.id] = state.copy(status = newStatus)
    _userAnswers.value = answers

    goToNextQuestion()
  }

  fun saveAndNext() {
    val currentQ = currentQuestion ?: return
    val answers = _userAnswers.value.toMutableMap()
    val state = answers[currentQ.id] ?: UserQuestionState(currentQ.id)

    val newStatus = if (state.selectedOptionIndex != null) {
      QuestionStatus.ANSWERED
    } else {
      QuestionStatus.NOT_ANSWERED
    }

    answers[currentQ.id] = state.copy(status = newStatus)
    _userAnswers.value = answers

    goToNextQuestion()
  }

  fun goToPreviousQuestion() {
    if (_currentQuestionIndex.value > 0) {
      _currentQuestionIndex.value -= 1
      markCurrentQuestionVisited()
    } else if (_currentSectionIndex.value > 0) {
      _currentSectionIndex.value -= 1
      val secQs = questionsForCurrentSection
      _currentQuestionIndex.value = maxOf(0, secQs.size - 1)
      markCurrentQuestionVisited()
    }
  }

  private fun goToNextQuestion() {
    val secQs = questionsForCurrentSection
    if (_currentQuestionIndex.value < secQs.size - 1) {
      _currentQuestionIndex.value += 1
      markCurrentQuestionVisited()
    } else if (_currentSectionIndex.value < (_test.value?.sections?.size ?: 1) - 1) {
      _currentSectionIndex.value += 1
      _currentQuestionIndex.value = 0
      markCurrentQuestionVisited()
    }
  }

  fun toggleLanguage() {
    _isEnglishLanguage.value = !_isEnglishLanguage.value
  }

  fun togglePalette(open: Boolean) {
    _isPaletteOpen.value = open
  }

  fun toggleSubmitDialog(open: Boolean) {
    _isSubmitDialogOpen.value = open
  }

  fun toggleBookmark() {
    val currentQ = currentQuestion ?: return
    val answers = _userAnswers.value.toMutableMap()
    val state = answers[currentQ.id] ?: UserQuestionState(currentQ.id)
    answers[currentQ.id] = state.copy(isBookmarked = !state.isBookmarked)
    _userAnswers.value = answers

    viewModelScope.launch {
      val testTitle = _test.value?.title ?: "SSC CGL"
      repository.toggleBookmark(currentQ, testTitle)
    }
  }

  fun submitTest(onFinished: ((Long) -> Unit)? = null) {
    timerJob?.cancel()
    _isSubmitDialogOpen.value = false
    val currentTest = _test.value ?: return

    viewModelScope.launch {
      val attemptId = repository.saveTestAttempt(
        test = currentTest,
        userAnswers = _userAnswers.value,
        timeTakenSeconds = _totalTimeTakenSeconds.value
      )
      _completedAttemptId.value = attemptId
      _isTestCompleted.value = true
      onFinished?.invoke(attemptId)
    }
  }

  override fun onCleared() {
    super.onCleared()
    timerJob?.cancel()
  }
}
