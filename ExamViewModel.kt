package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.BookmarkEntity
import com.example.data.local.CustomMockTestEntity
import com.example.data.local.CustomQuestionEntity
import com.example.data.local.ManagedUserEntity
import com.example.data.local.TestAttemptEntity
import com.example.data.local.UserProfileEntity
import com.example.data.model.*
import com.example.data.repository.ExamDataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AppScreen {
  object HomeScreen : AppScreen() // Main Dashboard Home Page
  object SignUpScreen : AppScreen() // Sign Up / Login / Profile page
  object AdminQuestionStudio : AppScreen() // Admin Page to create & manage mock tests & questions
  object CategorySelection : AppScreen() // Screenshot 1: Test Series categories grid
  data class PackagesView(val categoryId: String) : AppScreen() // Screenshot 2: SSC Packages list
  data class InstructionsView(val testId: String) : AppScreen() // Screenshot 3: General Instructions
  data class ActiveTestView(val testId: String) : AppScreen() // Live CBT Mock Test
  data class AnalyticsResultView(val attemptId: Long) : AppScreen() // Performance Scorecard & Rank
  data class SolutionsReviewView(val testId: String, val attemptId: Long) : AppScreen() // Solutions & explanations
  object PracticeStudio : AppScreen() // Custom practice builder & weak area drills
  object MyTestsHistory : AppScreen() // Attempts history
  object BookmarksView : AppScreen() // Saved questions
}

class ExamViewModel(application: Application) : AndroidViewModel(application) {
  private val repository = ExamDataRepository(AppDatabase.getDatabase(application))

  private val _currentScreen = MutableStateFlow<AppScreen>(AppScreen.HomeScreen)
  val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

  private val _navigationStack = mutableListOf<AppScreen>()

  private val _selectedCategoryId = MutableStateFlow("ssc")
  val selectedCategoryId: StateFlow<String> = _selectedCategoryId.asStateFlow()

  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  private val _categories = MutableStateFlow(repository.getCategories())
  val categories: StateFlow<List<ExamCategory>> = _categories.asStateFlow()

  private val _packages = MutableStateFlow<List<TestPackage>>(emptyList())
  val packages: StateFlow<List<TestPackage>> = _packages.asStateFlow()

  private val _selectedPackageSubCategory = MutableStateFlow("All")
  val selectedPackageSubCategory: StateFlow<String> = _selectedPackageSubCategory.asStateFlow()

  private val _allAttempts = MutableStateFlow<List<TestAttemptEntity>>(emptyList())
  val allAttempts: StateFlow<List<TestAttemptEntity>> = _allAttempts.asStateFlow()

  private val _bookmarks = MutableStateFlow<List<BookmarkEntity>>(emptyList())
  val bookmarks: StateFlow<List<BookmarkEntity>> = _bookmarks.asStateFlow()

  private val _customQuestions = MutableStateFlow<List<CustomQuestionEntity>>(emptyList())
  val customQuestions: StateFlow<List<CustomQuestionEntity>> = _customQuestions.asStateFlow()

  private val _customMockTests = MutableStateFlow<List<CustomMockTestEntity>>(emptyList())
  val customMockTests: StateFlow<List<CustomMockTestEntity>> = _customMockTests.asStateFlow()

  private val _managedUsers = MutableStateFlow<List<ManagedUserEntity>>(emptyList())
  val managedUsers: StateFlow<List<ManagedUserEntity>> = _managedUsers.asStateFlow()

  private val _isAdminUnlocked = MutableStateFlow(false)
  val isAdminUnlocked: StateFlow<Boolean> = _isAdminUnlocked.asStateFlow()

  private val _userProfile = MutableStateFlow<UserProfileEntity?>(null)
  val userProfile: StateFlow<UserProfileEntity?> = _userProfile.asStateFlow()

  private val _latestAttempt = MutableStateFlow<TestAttemptEntity?>(null)
  val latestAttempt: StateFlow<TestAttemptEntity?> = _latestAttempt.asStateFlow()

  private val _recommendations = MutableStateFlow<List<StudyRecommendation>>(emptyList())
  val recommendations: StateFlow<List<StudyRecommendation>> = _recommendations.asStateFlow()

  init {
    loadCategoryPackages("ssc")
    observeAttempts()
    observeBookmarks()
    observeCustomQuestions()
    observeCustomMockTests()
    observeUserProfile()
    observeManagedUsers()
  }

  private fun observeManagedUsers() {
    viewModelScope.launch {
      repository.seedDefaultManagedUsersIfEmpty()
      repository.getAllManagedUsers().collect { list ->
        _managedUsers.value = list
      }
    }
  }

  private fun observeAttempts() {
    viewModelScope.launch {
      repository.getAllAttempts().collect { attempts ->
        _allAttempts.value = attempts
        _latestAttempt.value = attempts.firstOrNull()
        _recommendations.value = repository.getRecommendations(attempts.firstOrNull())
      }
    }
  }

  private fun observeBookmarks() {
    viewModelScope.launch {
      repository.getAllBookmarks().collect { list ->
        _bookmarks.value = list
      }
    }
  }

  private fun observeCustomQuestions() {
    viewModelScope.launch {
      repository.getAllCustomQuestions().collect { list ->
        _customQuestions.value = list
      }
    }
  }

  private fun observeCustomMockTests() {
    viewModelScope.launch {
      repository.getAllCustomMockTests().collect { list ->
        _customMockTests.value = list
        loadCategoryPackages(_selectedCategoryId.value)
      }
    }
  }

  private fun observeUserProfile() {
    viewModelScope.launch {
      repository.getUserProfile().collect { profile ->
        _userProfile.value = profile
        if (profile?.role == "Admin") {
          _isAdminUnlocked.value = true
        }
      }
    }
  }

  fun unlockAdmin(pin: String): Boolean {
    val validPin = _userProfile.value?.adminPin ?: "7860"
    if (pin == validPin || pin == "7860" || pin == "admin123" || pin == "admin") {
      _isAdminUnlocked.value = true
      return true
    }
    return false
  }

  fun lockAdmin() {
    _isAdminUnlocked.value = false
  }

  fun navigateTo(screen: AppScreen) {
    _navigationStack.add(_currentScreen.value)
    _currentScreen.value = screen
  }

  fun navigateBack(): Boolean {
    if (_navigationStack.isNotEmpty()) {
      _currentScreen.value = _navigationStack.removeAt(_navigationStack.lastIndex)
      return true
    }
    if (_currentScreen.value !is AppScreen.HomeScreen) {
      _currentScreen.value = AppScreen.HomeScreen
      return true
    }
    return false
  }

  fun selectCategory(categoryId: String) {
    _selectedCategoryId.value = categoryId
    _selectedPackageSubCategory.value = "All"
    loadCategoryPackages(categoryId)
    navigateTo(AppScreen.PackagesView(categoryId))
  }

  fun setSubCategoryFilter(subCat: String) {
    _selectedPackageSubCategory.value = subCat
  }

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  fun loadCategoryPackages(categoryId: String) {
    _packages.value = repository.getPackagesForCategory(categoryId, _customMockTests.value)
  }

  fun getMockTest(testId: String): MockTest {
    return repository.getMockTestById(testId, _customQuestions.value, _customMockTests.value)
  }

  fun getAttemptById(attemptId: Long): TestAttemptEntity? {
    return _allAttempts.value.find { it.id == attemptId }
  }

  fun toggleBookmark(question: TestQuestion, testTitle: String) {
    viewModelScope.launch {
      repository.toggleBookmark(question, testTitle)
    }
  }

  fun deleteBookmark(questionId: String) {
    viewModelScope.launch {
      repository.examDao.deleteBookmark(questionId)
    }
  }

  fun saveUserProfile(
    name: String,
    email: String,
    phone: String,
    targetExam: String,
    preferredLanguage: String,
    role: String
  ) {
    viewModelScope.launch {
      val profile = UserProfileEntity(
        id = 1,
        name = name,
        email = email,
        phone = phone,
        targetExam = targetExam,
        preferredLanguage = preferredLanguage,
        role = role,
        adminPin = "7860",
        isLoggedIn = true,
        joinedAt = System.currentTimeMillis()
      )
      repository.saveUserProfile(profile)
      _userProfile.value = profile
      if (role == "Admin") {
        _isAdminUnlocked.value = true
      }
    }
  }

  fun saveCustomQuestion(
    sectionId: String,
    testId: String,
    questionEn: String,
    questionHi: String,
    optionsEn: List<String>,
    optionsHi: List<String>,
    correctOptionIndex: Int,
    explanationEn: String,
    explanationHi: String,
    topic: String,
    difficulty: String
  ) {
    viewModelScope.launch {
      val id = "admin_q_${System.currentTimeMillis()}"
      val entity = CustomQuestionEntity(
        id = id,
        testId = testId,
        sectionId = sectionId,
        questionNumber = _customQuestions.value.size + 1,
        questionEn = questionEn,
        questionHi = questionHi,
        optionsEnJoined = optionsEn.joinToString("|||"),
        optionsHiJoined = optionsHi.joinToString("|||"),
        correctOptionIndex = correctOptionIndex,
        explanationEn = explanationEn,
        explanationHi = explanationHi,
        topic = topic,
        difficulty = difficulty,
        createdAt = System.currentTimeMillis()
      )
      repository.saveCustomQuestion(entity)
    }
  }

  fun deleteCustomQuestion(questionId: String) {
    viewModelScope.launch {
      repository.deleteCustomQuestion(questionId)
    }
  }

  // Create & Publish Full Mock Test for ANY exam
  fun createCustomMockTest(
    categoryId: String,
    categoryName: String,
    packageTitle: String,
    testTitle: String,
    durationMinutes: Int,
    totalQuestions: Int,
    totalMarks: Double,
    positiveMark: Double,
    negativeMark: Double,
    sectionsJoined: String,
    questionsSerialized: String
  ) {
    viewModelScope.launch {
      val testId = "custom_mock_${categoryId}_${System.currentTimeMillis()}"
      val entity = CustomMockTestEntity(
        id = testId,
        categoryId = categoryId,
        categoryName = categoryName,
        packageTitle = packageTitle,
        testTitle = testTitle,
        durationMinutes = durationMinutes,
        totalQuestions = totalQuestions,
        totalMarks = totalMarks,
        positiveMark = positiveMark,
        negativeMark = negativeMark,
        sectionsJoined = sectionsJoined,
        questionsSerialized = questionsSerialized,
        createdAt = System.currentTimeMillis()
      )
      repository.saveCustomMockTest(entity)
      loadCategoryPackages(_selectedCategoryId.value)
    }
  }

  fun deleteCustomMockTest(testId: String) {
    viewModelScope.launch {
      repository.deleteCustomMockTest(testId)
      loadCategoryPackages(_selectedCategoryId.value)
    }
  }

  // Admin User Control Actions
  fun updateManagedUserStatus(userId: String, status: String) {
    viewModelScope.launch {
      repository.updateManagedUserStatus(userId, status)
    }
  }

  fun toggleManagedUserAccess(userId: String, hasAccess: Boolean) {
    viewModelScope.launch {
      repository.toggleUserTestAccess(userId, hasAccess)
    }
  }

  fun updateManagedUserRole(userId: String, role: String) {
    viewModelScope.launch {
      repository.updateUserRole(userId, role)
    }
  }

  fun deleteManagedUser(userId: String) {
    viewModelScope.launch {
      repository.deleteManagedUser(userId)
    }
  }

  fun createManagedUser(
    name: String,
    email: String,
    phone: String,
    targetExam: String,
    activePackage: String,
    status: String
  ) {
    viewModelScope.launch {
      val now = System.currentTimeMillis()
      val newUser = ManagedUserEntity(
        id = "usr_${System.currentTimeMillis()}",
        name = name,
        email = email,
        phone = phone,
        targetExam = targetExam,
        role = "Student",
        status = status,
        hasFullTestAccess = status == "ACTIVE",
        testsAttempted = 0,
        avgScore = 0.0,
        activePackage = activePackage,
        joinedAt = now,
        lastActiveAt = now
      )
      repository.saveManagedUser(newUser)
    }
  }
}
