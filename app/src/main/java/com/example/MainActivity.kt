package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.ActiveTestViewModel
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.ExamViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = MaterialTheme.colorScheme.background
        ) {
          TestRankAppRoot()
        }
      }
    }
  }
}

@Composable
fun TestRankAppRoot(
  examViewModel: ExamViewModel = viewModel(),
  activeTestViewModel: ActiveTestViewModel = viewModel()
) {
  val currentScreen by examViewModel.currentScreen.collectAsState()
  val categories by examViewModel.categories.collectAsState()
  val packages by examViewModel.packages.collectAsState()
  val selectedCategoryId by examViewModel.selectedCategoryId.collectAsState()
  val selectedSubCat by examViewModel.selectedPackageSubCategory.collectAsState()
  val searchQuery by examViewModel.searchQuery.collectAsState()
  val allAttempts by examViewModel.allAttempts.collectAsState()
  val bookmarks by examViewModel.bookmarks.collectAsState()
  val recommendations by examViewModel.recommendations.collectAsState()
  val customQuestions by examViewModel.customQuestions.collectAsState()
  val customMockTests by examViewModel.customMockTests.collectAsState()
  val managedUsers by examViewModel.managedUsers.collectAsState()
  val isAdminUnlocked by examViewModel.isAdminUnlocked.collectAsState()
  val userProfile by examViewModel.userProfile.collectAsState()
  val latestAttempt by examViewModel.latestAttempt.collectAsState()

  // Intercept back press when not on home screen
  BackHandler(enabled = currentScreen !is AppScreen.HomeScreen) {
    examViewModel.navigateBack()
  }

  val selectedCategoryObj = categories.find { it.id == selectedCategoryId }
  val categoryName = selectedCategoryObj?.name ?: "SSC"

  when (val screen = currentScreen) {
    is AppScreen.HomeScreen -> {
      HomeScreen(
        categories = categories,
        latestAttempt = latestAttempt,
        userProfile = userProfile,
        onNavigateToCategory = { catId -> examViewModel.selectCategory(catId) },
        onViewAllCategories = { examViewModel.navigateTo(AppScreen.CategorySelection) },
        onStartLiveMock = {
          examViewModel.navigateTo(AppScreen.InstructionsView("cgl_mock_01"))
        },
        onStartPracticeDrill = { drillId ->
          examViewModel.navigateTo(AppScreen.InstructionsView(drillId))
        },
        onViewAnalytics = { attemptId ->
          examViewModel.navigateTo(AppScreen.AnalyticsResultView(attemptId))
        },
        onSignUpClick = { examViewModel.navigateTo(AppScreen.SignUpScreen) },
        onAdminClick = { examViewModel.navigateTo(AppScreen.AdminQuestionStudio) },
        onBookmarksClick = { examViewModel.navigateTo(AppScreen.BookmarksView) },
        onMyTestsClick = { examViewModel.navigateTo(AppScreen.MyTestsHistory) }
      )
    }

                is AppScreen.SignUpScreen -> {
      SignUpScreen(
        userProfile = userProfile,
        onSaveProfile = { name: String, email: String, phone: String, targetExam: String, lang: String, role: String ->
          examViewModel.saveUserProfile(name, email, phone, targetExam, lang, role)
        },
        onBack = { examViewModel.navigateBack() },
        onAdminClick = { examViewModel.navigateTo(AppScreen.AdminQuestionStudio) }
      )
    }



    is AppScreen.AdminQuestionStudio -> {
      AdminQuestionStudioScreen(
        isAdminUnlocked = isAdminUnlocked,
        onUnlockAdmin = { pin -> examViewModel.unlockAdmin(pin) },
        onLockAdmin = { examViewModel.lockAdmin() },
        categories = categories,
        customQuestions = customQuestions,
        customMockTests = customMockTests,
        managedUsers = managedUsers,
        onUpdateUserStatus = { userId, status -> examViewModel.updateManagedUserStatus(userId, status) },
        onToggleUserAccess = { userId, hasAccess -> examViewModel.toggleManagedUserAccess(userId, hasAccess) },
        onUpdateUserRole = { userId, role -> examViewModel.updateManagedUserRole(userId, role) },
        onDeleteUser = { userId -> examViewModel.deleteManagedUser(userId) },
                onCreateUser = { name: String, email: String, phone: String, targetExam: String, pkg: String, status: String ->
          examViewModel.createManagedUser(name, email, phone, targetExam, pkg, status)
        },

        onCreateMockTest = { catId: String, catName: String, pkgTitle: String, testTitle: String, dur: Int, qCount: Int, marks: Int, pos: Float, neg: Float, secs: String, qs: String ->
          examViewModel.createCustomMockTest(
            categoryId = catId, categoryName = catName, packageTitle = pkgTitle, testTitle = testTitle, durationMinutes = dur, totalQuestions = qCount, totalMarks = marks, positiveMark = pos, negativeMark = neg, sectionsJoined = secs, questionsSerialized = qs
          )
        },
        onDeleteMockTest = { testId -> examViewModel.deleteCustomMockTest(testId) },
        onSaveQuestion = { sectionId: String, testId: String, questionEn: String, questionHi: String, optionsEn: List<String>, optionsHi: List<String>, correctOptionIndex: Int, explanationEn: String, explanationHi: String, topic: String, difficulty: String ->
          examViewModel.saveCustomQuestion(
            sectionId = sectionId, testId = testId, questionEn = questionEn, questionHi = questionHi, optionsEn = optionsEn, optionsHi = optionsHi, correctOptionIndex = correctOptionIndex, explanationEn = explanationEn, explanationHi = explanationHi, topic = topic, difficulty = difficulty
          )
        },

      
        onDeleteQuestion = { questionId ->
          examViewModel.deleteCustomQuestion(questionId)
        },
        onPlayTest = { testId ->
          examViewModel.navigateTo(AppScreen.InstructionsView(testId))
        },
        onBack = { examViewModel.navigateBack() }
      )
    }

    is AppScreen.CategorySelection -> {
      CategorySelectionScreen(
        categories = categories,
        searchQuery = searchQuery,
        onSearchQueryChange = { examViewModel.setSearchQuery(it) },
        onCategorySelected = { catId -> examViewModel.selectCategory(catId) },
        onPracticeClick = { examViewModel.navigateTo(AppScreen.PracticeStudio) },
        onMyTestsClick = { examViewModel.navigateTo(AppScreen.MyTestsHistory) },
        onAnalyticsClick = {
          val latest = allAttempts.firstOrNull()
          if (latest != null) {
            examViewModel.navigateTo(AppScreen.AnalyticsResultView(latest.id))
          } else {
            examViewModel.navigateTo(AppScreen.MyTestsHistory)
          }
        },
        onStudyPlanClick = { examViewModel.navigateTo(AppScreen.PracticeStudio) },
        onBookmarksClick = { examViewModel.navigateTo(AppScreen.BookmarksView) }
      )
    }

    is AppScreen.PackagesView -> {
      PackagesScreen(
        categoryId = screen.categoryId,
        categoryName = categoryName,
        packages = packages,
        selectedSubCategory = selectedSubCat,
        onSubCategorySelect = { examViewModel.setSubCategoryFilter(it) },
        onStartTest = { testId ->
          examViewModel.navigateTo(AppScreen.InstructionsView(testId))
        },
        onBack = { examViewModel.navigateBack() }
      )
    }

    is AppScreen.InstructionsView -> {
      val test = remember(screen.testId, customQuestions, customMockTests) {
        examViewModel.getMockTest(screen.testId)
      }
      GeneralInstructionsScreen(
        test = test,
        onProceedToTest = {
          activeTestViewModel.initTest(screen.testId, customQuestions, customMockTests)
          examViewModel.navigateTo(AppScreen.ActiveTestView(screen.testId))
        },
        onBack = { examViewModel.navigateBack() }
      )
    }

    is AppScreen.ActiveTestView -> {
      ActiveTestScreen(
        viewModel = activeTestViewModel,
        onTestSubmitted = { attemptId ->
          examViewModel.navigateTo(AppScreen.AnalyticsResultView(attemptId))
        },
        onExitTest = {
          examViewModel.navigateBack()
        }
      )
    }

    is AppScreen.AnalyticsResultView -> {
      val attempt = remember(screen.attemptId, allAttempts) {
        examViewModel.getAttemptById(screen.attemptId) ?: allAttempts.firstOrNull()
      }
      PerformanceAnalyticsScreen(
        attempt = attempt,
        onViewSolutions = {
          if (attempt != null) {
            examViewModel.navigateTo(AppScreen.SolutionsReviewView(attempt.testId, attempt.id))
          }
        },
        onViewRecommendations = {
          examViewModel.navigateTo(AppScreen.PracticeStudio)
        },
        onRetakeTest = {
          if (attempt != null) {
            examViewModel.navigateTo(AppScreen.InstructionsView(attempt.testId))
          }
        },
        onBackToHome = {
          examViewModel.navigateTo(AppScreen.HomeScreen)
        }
      )
    }

    is AppScreen.SolutionsReviewView -> {
      val test = remember(screen.testId, customQuestions, customMockTests) {
        examViewModel.getMockTest(screen.testId)
      }
      val attempt = remember(screen.attemptId, allAttempts) {
        examViewModel.getAttemptById(screen.attemptId) ?: allAttempts.firstOrNull()
      }
      val bookmarkedIds = remember(bookmarks) { bookmarks.map { it.questionId }.toSet() }

      SolutionsReviewScreen(
        test = test,
        attempt = attempt,
        onBookmarkToggle = { q -> examViewModel.toggleBookmark(q, test.title) },
        isBookmarked = { qId -> bookmarkedIds.contains(qId) },
        onBack = { examViewModel.navigateBack() }
      )
    }

    is AppScreen.PracticeStudio -> {
      PersonalizedStudyScreen(
        recommendations = recommendations,
        onLaunchDrill = { drillTestId ->
          examViewModel.navigateTo(AppScreen.InstructionsView(drillTestId))
        },
        onBack = { examViewModel.navigateBack() }
      )
    }

    is AppScreen.MyTestsHistory -> {
      MyTestsHistoryScreen(
        attempts = allAttempts,
        onViewReport = { attemptId ->
          examViewModel.navigateTo(AppScreen.AnalyticsResultView(attemptId))
        },
        onBack = { examViewModel.navigateBack() }
      )
    }

    is AppScreen.BookmarksView -> {
      BookmarksScreen(
        bookmarks = bookmarks,
        onRemoveBookmark = { qId -> examViewModel.deleteBookmark(qId) },
        onBack = { examViewModel.navigateBack() }
      )
    }
  }
}
