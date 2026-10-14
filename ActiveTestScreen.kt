package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExamCbtVendor
import com.example.data.model.QuestionStatus
import com.example.ui.screens.cbt.EduquityExamView
import com.example.ui.screens.cbt.ExamInstructionsDialog
import com.example.ui.screens.cbt.QuestionPaperDialog
import com.example.ui.screens.cbt.TcsIonExamView
import com.example.ui.viewmodel.ActiveTestViewModel

@Composable
fun ActiveTestScreen(
  viewModel: ActiveTestViewModel,
  onTestSubmitted: (Long) -> Unit,
  onExitTest: () -> Unit,
  modifier: Modifier = Modifier
) {
  val test by viewModel.test.collectAsState()
  val selectedVendor by viewModel.selectedVendor.collectAsState()
  val currentQuestion = viewModel.currentQuestion
  val currentSectionIndex by viewModel.currentSectionIndex.collectAsState()
  val currentQuestionIndex by viewModel.currentQuestionIndex.collectAsState()
  val userAnswers by viewModel.userAnswers.collectAsState()
  val timeRemainingSeconds by viewModel.timeRemainingSeconds.collectAsState()
  val isEnglish by viewModel.isEnglishLanguage.collectAsState()
  val isPaletteOpen by viewModel.isPaletteOpen.collectAsState()
  val isSubmitDialogOpen by viewModel.isSubmitDialogOpen.collectAsState()
  val isQuestionPaperOpen by viewModel.isQuestionPaperOpen.collectAsState()
  val isInstructionsOpen by viewModel.isInstructionsOpen.collectAsState()
  val userProfile by viewModel.userProfile.collectAsState(initial = null)

  var showExitConfirmDialog by remember { mutableStateOf(false) }

  BackHandler {
    showExitConfirmDialog = true
  }

  val t = test ?: return

  Box(modifier = modifier.fillMaxSize().testTag("active_test_screen")) {
    when (selectedVendor) {
      ExamCbtVendor.EDUQUITY -> {
        EduquityExamView(
          test = t,
          currentQuestion = currentQuestion,
          currentSectionIndex = currentSectionIndex,
          currentQuestionIndex = currentQuestionIndex,
          userAnswers = userAnswers,
          timeRemainingSeconds = timeRemainingSeconds,
          isEnglish = isEnglish,
          isPaletteOpen = isPaletteOpen,
          userProfile = userProfile,
          questionsForCurrentSection = viewModel.questionsForCurrentSection,
          onSelectOption = { viewModel.selectOption(it) },
          onClearResponse = { viewModel.clearResponse() },
          onSaveAndNext = { viewModel.saveAndNext() },
          onMarkForReviewAndNext = { viewModel.markForReviewAndNext() },
          onSaveAndMarkForReview = { viewModel.saveAndMarkForReview() },
          onPreviousQuestion = { viewModel.goToPreviousQuestion() },
          onSwitchSection = { viewModel.switchSection(it) },
          onJumpToQuestion = { viewModel.jumpToQuestion(it) },
          onToggleLanguage = { viewModel.toggleLanguage() },
          onTogglePalette = { viewModel.togglePalette(it) },
          onToggleSubmitDialog = { viewModel.toggleSubmitDialog(true) },
          onToggleQuestionPaper = { viewModel.toggleQuestionPaper(true) },
          onToggleInstructions = { viewModel.toggleInstructions(true) },
          onToggleBookmark = { viewModel.toggleBookmark() },
          onSwitchVendor = { viewModel.setVendor(it) },
          onExitTest = { showExitConfirmDialog = true }
        )
      }

      ExamCbtVendor.TCS_ION -> {
        TcsIonExamView(
          test = t,
          currentQuestion = currentQuestion,
          currentSectionIndex = currentSectionIndex,
          currentQuestionIndex = currentQuestionIndex,
          userAnswers = userAnswers,
          timeRemainingSeconds = timeRemainingSeconds,
          isEnglish = isEnglish,
          isPaletteOpen = isPaletteOpen,
          userProfile = userProfile,
          questionsForCurrentSection = viewModel.questionsForCurrentSection,
          onSelectOption = { viewModel.selectOption(it) },
          onClearResponse = { viewModel.clearResponse() },
          onSaveAndNext = { viewModel.saveAndNext() },
          onMarkForReviewAndNext = { viewModel.markForReviewAndNext() },
          onSaveAndMarkForReview = { viewModel.saveAndMarkForReview() },
          onPreviousQuestion = { viewModel.goToPreviousQuestion() },
          onSwitchSection = { viewModel.switchSection(it) },
          onJumpToQuestion = { viewModel.jumpToQuestion(it) },
          onToggleLanguage = { viewModel.toggleLanguage() },
          onTogglePalette = { viewModel.togglePalette(it) },
          onToggleSubmitDialog = { viewModel.toggleSubmitDialog(true) },
          onToggleQuestionPaper = { viewModel.toggleQuestionPaper(true) },
          onToggleInstructions = { viewModel.toggleInstructions(true) },
          onToggleBookmark = { viewModel.toggleBookmark() },
          onSwitchVendor = { viewModel.setVendor(it) },
          onExitTest = { showExitConfirmDialog = true }
        )
      }
    }

    // Question Paper Modal
    if (isQuestionPaperOpen) {
      QuestionPaperDialog(
        test = t,
        isEnglish = isEnglish,
        onDismiss = { viewModel.toggleQuestionPaper(false) }
      )
    }

    // In-Exam Instructions Modal
    if (isInstructionsOpen) {
      ExamInstructionsDialog(
        test = t,
        vendorName = if (selectedVendor == ExamCbtVendor.TCS_ION) "TCS iON (Railways)" else "Eduquity (SSC)",
        onDismiss = { viewModel.toggleInstructions(false) }
      )
    }

    // Submit Confirmation Dialog
    if (isSubmitDialogOpen) {
      val totalQs = t.questions.size
      val answered = userAnswers.values.count {
        it.status == QuestionStatus.ANSWERED || it.status == QuestionStatus.ANSWERED_AND_MARKED_FOR_REVIEW
      }
      val notAnswered = userAnswers.values.count { it.status == QuestionStatus.NOT_ANSWERED }
      val marked = userAnswers.values.count {
        it.status == QuestionStatus.MARKED_FOR_REVIEW || it.status == QuestionStatus.ANSWERED_AND_MARKED_FOR_REVIEW
      }
      val notVisited = userAnswers.values.count { it.status == QuestionStatus.NOT_VISITED }

      AlertDialog(
        onDismissRequest = { viewModel.toggleSubmitDialog(false) },
        containerColor = Color(0xFF1E2230),
        title = {
          Text(
            text = "Submit Test Confirmation (${if (selectedVendor == ExamCbtVendor.TCS_ION) "TCS iON" else "Eduquity"})",
            color = Color.White,
            fontWeight = FontWeight.Bold
          )
        },
        text = {
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
              text = "Are you sure you want to submit? Review your attempt summary below:",
              color = Color(0xFFCBD5E1),
              fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            SummaryRow("Total Questions", "$totalQs", Color.White)
            SummaryRow("Answered", "$answered", Color(0xFF10B981))
            SummaryRow("Not Answered", "$notAnswered", Color(0xFFEF4444))
            SummaryRow("Marked for Review", "$marked", Color(0xFF8B5CF6))
            SummaryRow("Not Visited", "$notVisited", Color(0xFF64748B))
          }
        },
        confirmButton = {
          Button(
            onClick = {
              viewModel.submitTest { attemptId ->
                onTestSubmitted(attemptId)
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
            modifier = Modifier.testTag("confirm_submit_test_btn")
          ) {
            Text("Yes, Submit Test")
          }
        },
        dismissButton = {
          OutlinedButton(
            onClick = { viewModel.toggleSubmitDialog(false) }
          ) {
            Text("Resume Test", color = Color.White)
          }
        }
      )
    }

    // Exit Test Warning Dialog
    if (showExitConfirmDialog) {
      AlertDialog(
        onDismissRequest = { showExitConfirmDialog = false },
        containerColor = Color(0xFF1E2230),
        title = { Text("Leave Mock Test?", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
          Text(
            "Your progress will not be saved if you leave before submitting. You can submit now to get your performance scorecard.",
            color = Color(0xFFCBD5E1),
            fontSize = 13.sp
          )
        },
        confirmButton = {
          Button(
            onClick = {
              showExitConfirmDialog = false
              viewModel.submitTest { attemptId ->
                onTestSubmitted(attemptId)
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
          ) {
            Text("Submit & Exit")
          }
        },
        dismissButton = {
          TextButton(
            onClick = {
              showExitConfirmDialog = false
              onExitTest()
            }
          ) {
            Text("Discard & Exit", color = Color(0xFFEF4444))
          }
        }
      )
    }
  }
}

@Composable
fun SummaryRow(label: String, value: String, valueColor: Color) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(text = label, color = Color(0xFF94A3B8), fontSize = 13.sp)
    Text(text = value, color = valueColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
  }
}
