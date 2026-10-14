package com.example.ui.screens.cbt

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UserProfileEntity
import com.example.data.model.ExamCbtVendor
import com.example.data.model.MockTest
import com.example.data.model.QuestionStatus
import com.example.data.model.TestQuestion
import com.example.data.model.UserQuestionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EduquityExamView(
  test: MockTest,
  currentQuestion: TestQuestion?,
  currentSectionIndex: Int,
  currentQuestionIndex: Int,
  userAnswers: Map<String, UserQuestionState>,
  timeRemainingSeconds: Long,
  isEnglish: Boolean,
  isPaletteOpen: Boolean,
  userProfile: UserProfileEntity?,
  questionsForCurrentSection: List<TestQuestion>,
  onSelectOption: (Int) -> Unit,
  onClearResponse: () -> Unit,
  onSaveAndNext: () -> Unit,
  onMarkForReviewAndNext: () -> Unit,
  onSaveAndMarkForReview: () -> Unit,
  onPreviousQuestion: () -> Unit,
  onSwitchSection: (Int) -> Unit,
  onJumpToQuestion: (Int) -> Unit,
  onToggleLanguage: () -> Unit,
  onTogglePalette: (Boolean) -> Unit,
  onToggleSubmitDialog: () -> Unit,
  onToggleQuestionPaper: () -> Unit,
  onToggleInstructions: () -> Unit,
  onToggleBookmark: () -> Unit,
  onSwitchVendor: (ExamCbtVendor) -> Unit,
  onExitTest: () -> Unit,
  modifier: Modifier = Modifier
) {
  val hours = timeRemainingSeconds / 3600
  val minutes = (timeRemainingSeconds % 3600) / 60
  val seconds = timeRemainingSeconds % 60
  val timerText = if (hours > 0) {
    String.format("%02d:%02d:%02d", hours, minutes, seconds)
  } else {
    String.format("%02d:%02d", minutes, seconds)
  }
  val isLowTime = timeRemainingSeconds < 300

  val candidateName = userProfile?.name?.ifBlank { "Candidate" } ?: "Asharam Meena"
  val rollNo = "2201048291"

  Scaffold(
    modifier = modifier
      .fillMaxSize()
      .testTag("eduquity_exam_view"),
    containerColor = Color(0xFF0F172A),
    topBar = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color(0xFF0369A1)) // Eduquity Ocean Blue Header
      ) {
        // Row 1: Eduquity SSC Official Branding & Candidate Info
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 10.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(
            onClick = onExitTest,
            modifier = Modifier.size(36.dp)
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Exit",
              tint = Color.White
            )
          }

          // SSC Emblem & Title
          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "STAFF SELECTION COMMISSION",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.5.sp
              )
              Spacer(modifier = Modifier.width(6.dp))
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(3.dp))
                  .background(Color(0xFF0284C7))
                  .padding(horizontal = 4.dp, vertical = 1.dp)
              ) {
                Text("Eduquity CBT", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
              }
            }
            Text(
              text = test.title,
              color = Color(0xFFE0F2FE),
              fontSize = 13.sp,
              fontWeight = FontWeight.SemiBold,
              maxLines = 1
            )
          }

          // Candidate Info Box
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(Color(0xFF075985))
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Box(
              modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(Color(0xFF38BDF8)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF0C4A6E), modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(6.dp))
            Column {
              Text(candidateName, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
              Text("Roll: $rollNo", color = Color(0xFFBAE6FD), fontSize = 10.sp)
            }
          }
        }

        // Row 2: Timer, Vendor Switcher, Language & Actions
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF075985))
            .padding(horizontal = 10.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          // Timer Box
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(if (isLowTime) Color(0xFF991B1B) else Color(0xFF0C4A6E))
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Timer,
              contentDescription = "Timer",
              tint = if (isLowTime) Color(0xFFFECACA) else Color(0xFF38BDF8),
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Time Left: $timerText",
              color = Color.White,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.testTag("eduquity_timer")
            )
          }

          // Vendor Switcher Pill (Eduquity <-> TCS iON)
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(Color(0xFF0284C7))
              .clickable { onSwitchVendor(ExamCbtVendor.TCS_ION) }
              .padding(horizontal = 8.dp, vertical = 3.dp)
              .testTag("vendor_switch_to_tcs")
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text("⚡ Vendor: Eduquity (SSC)", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
              Spacer(modifier = Modifier.width(3.dp))
              Icon(Icons.Default.SwapHoriz, contentDescription = "Switch", tint = Color.White, modifier = Modifier.size(12.dp))
            }
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            // Language Button
            TextButton(
              onClick = onToggleLanguage,
              colors = ButtonDefaults.textButtonColors(contentColor = Color.White),
              modifier = Modifier
                .height(28.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFF0369A1))
            ) {
              Text(if (isEnglish) "ENG" else "हिन्", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Palette Toggle
            IconButton(
              onClick = { onTogglePalette(true) },
              modifier = Modifier.size(28.dp)
            ) {
              Icon(Icons.Default.GridView, contentDescription = "Palette", tint = Color.White, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Submit Button
            Button(
              onClick = onToggleSubmitDialog,
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
              shape = RoundedCornerShape(4.dp),
              contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
              modifier = Modifier.height(28.dp)
            ) {
              Text("Submit", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }
        }

        // Row 3: Eduquity Section Navigation Tabs
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0F172A))
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 8.dp, vertical = 5.dp),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          test.sections.forEachIndexed { index, sec ->
            val isSelected = index == currentSectionIndex
            val sectionQs = test.questions.filter { it.sectionId == sec.id }
            val answeredCount = sectionQs.count {
              val s = userAnswers[it.id]?.status
              s == QuestionStatus.ANSWERED || s == QuestionStatus.ANSWERED_AND_MARKED_FOR_REVIEW
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(if (isSelected) Color(0xFF0284C7) else Color(0xFF1E293B))
                .border(
                  1.dp,
                  if (isSelected) Color(0xFF38BDF8) else Color(0xFF334155),
                  RoundedCornerShape(4.dp)
                )
                .clickable { onSwitchSection(index) }
                .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = sec.name.split(" ").take(2).joinToString(" "),
                  color = if (isSelected) Color.White else Color(0xFF94A3B8),
                  fontSize = 11.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "($answeredCount/${sectionQs.size})",
                  color = if (isSelected) Color(0xFFE0F2FE) else Color(0xFF64748B),
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }
      }
    },
    bottomBar = {
      // Bottom Bar with Eduquity Layout
      Surface(
        color = Color(0xFF1E293B),
        modifier = Modifier
          .fillMaxWidth()
          .navigationBarsPadding(),
        tonalElevation = 6.dp
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 8.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          // Top Row: Mark for Review & Next | Clear Response | Save & Mark for Review
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Button(
              onClick = onMarkForReviewAndNext,
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6)),
              shape = RoundedCornerShape(4.dp),
              modifier = Modifier
                .weight(1f)
                .height(38.dp)
            ) {
              Text("Mark for Review & Next", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
            }

            OutlinedButton(
              onClick = onClearResponse,
              colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE2E8F0)),
              border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF475569)),
              shape = RoundedCornerShape(4.dp),
              modifier = Modifier
                .weight(0.7f)
                .height(38.dp)
            ) {
              Text("Clear", fontSize = 11.sp)
            }

            Button(
              onClick = onSaveAndMarkForReview,
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
              shape = RoundedCornerShape(4.dp),
              modifier = Modifier
                .weight(1f)
                .height(38.dp)
            ) {
              Text("Save & Mark Review", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
            }
          }

          // Bottom Row: Previous | Question Paper | Save & Next
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            OutlinedButton(
              onClick = onPreviousQuestion,
              colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFCBD5E1)),
              border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF475569)),
              shape = RoundedCornerShape(4.dp),
              modifier = Modifier
                .weight(0.8f)
                .height(42.dp)
            ) {
              Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(3.dp))
              Text("Previous", fontSize = 12.sp)
            }

            OutlinedButton(
              onClick = onToggleQuestionPaper,
              colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF38BDF8)),
              border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF0284C7)),
              shape = RoundedCornerShape(4.dp),
              modifier = Modifier
                .weight(0.9f)
                .height(42.dp)
            ) {
              Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(3.dp))
              Text("Question Paper", fontSize = 11.sp)
            }

            Button(
              onClick = onSaveAndNext,
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
              shape = RoundedCornerShape(4.dp),
              modifier = Modifier
                .weight(1.2f)
                .height(42.dp)
                .testTag("eduquity_save_and_next")
            ) {
              Text("Save & Next", fontSize = 13.sp, fontWeight = FontWeight.Bold)
              Spacer(modifier = Modifier.width(4.dp))
              Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp))
            }
          }
        }
      }
    }
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .verticalScroll(rememberScrollState())
        .padding(12.dp)
    ) {
      if (currentQuestion != null) {
        val qState = userAnswers[currentQuestion.id]

        // Question Info Header Bar
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFF1E293B))
            .padding(horizontal = 10.dp, vertical = 6.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "Question No. ${currentQuestionIndex + 1} of ${questionsForCurrentSection.size}",
              color = Color.White,
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(6.dp))
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(3.dp))
                .background(Color(0xFF334155))
                .padding(horizontal = 5.dp, vertical = 2.dp)
            ) {
              Text(currentQuestion.topic, color = Color(0xFF94A3B8), fontSize = 10.sp)
            }
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            Text("+${test.positiveMark.toInt()}", color = Color(0xFF10B981), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(" / -${test.negativeMark}", color = Color(0xFFEF4444), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(6.dp))
            IconButton(onClick = onToggleBookmark, modifier = Modifier.size(28.dp)) {
              Icon(
                imageVector = if (qState?.isBookmarked == true) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                contentDescription = "Bookmark",
                tint = if (qState?.isBookmarked == true) Color(0xFFF59E0B) else Color(0xFF94A3B8),
                modifier = Modifier.size(18.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Question Statement Card
        Card(
          colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Text(
              text = if (isEnglish) currentQuestion.questionEn else currentQuestion.questionHi,
              color = Color.White,
              fontSize = 15.sp,
              lineHeight = 22.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Options List (A, B, C, D)
        val opts = if (isEnglish) currentQuestion.optionsEn else currentQuestion.optionsHi
        opts.forEachIndexed { optIndex, optText ->
          val isSelected = qState?.selectedOptionIndex == optIndex
          val optLetter = ('A' + optIndex).toString()

          Card(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 4.dp)
              .clip(RoundedCornerShape(8.dp))
              .clickable { onSelectOption(optIndex) }
              .testTag("eduquity_opt_$optIndex"),
            colors = CardDefaults.cardColors(
              containerColor = if (isSelected) Color(0xFF0369A1).copy(alpha = 0.25f) else Color(0xFF1E293B)
            ),
            border = androidx.compose.foundation.BorderStroke(
              width = if (isSelected) 2.dp else 1.dp,
              color = if (isSelected) Color(0xFF38BDF8) else Color(0xFF334155)
            ),
            shape = RoundedCornerShape(8.dp)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(28.dp)
                  .clip(CircleShape)
                  .background(if (isSelected) Color(0xFF0284C7) else Color(0xFF334155)),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = optLetter,
                  color = if (isSelected) Color.White else Color(0xFF94A3B8),
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold
                )
              }

              Spacer(modifier = Modifier.width(12.dp))

              Text(
                text = optText,
                color = if (isSelected) Color.White else Color(0xFFCBD5E1),
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                modifier = Modifier.weight(1f)
              )

              if (isSelected) {
                Icon(
                  imageVector = Icons.Default.CheckCircle,
                  contentDescription = "Selected",
                  tint = Color(0xFF38BDF8),
                  modifier = Modifier.size(20.dp)
                )
              }
            }
          }
        }
      }
    }
  }

  // Question Palette Bottom Sheet / Modal
  if (isPaletteOpen) {
    ModalBottomSheet(
      onDismissRequest = { onTogglePalette(false) },
      containerColor = Color(0xFF1E293B),
      dragHandle = { BottomSheetDefaults.DragHandle(color = Color(0xFF64748B)) }
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp, vertical = 6.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text("Eduquity Question Palette", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text("Candidate: $candidateName", color = Color(0xFF94A3B8), fontSize = 11.sp)
          }
          IconButton(onClick = { onTogglePalette(false) }) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Official Legend Row
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          val answeredCount = userAnswers.values.count { it.status == QuestionStatus.ANSWERED }
          val notAnsweredCount = userAnswers.values.count { it.status == QuestionStatus.NOT_ANSWERED }
          val markedCount = userAnswers.values.count { it.status == QuestionStatus.MARKED_FOR_REVIEW }
          val ansMarkedCount = userAnswers.values.count { it.status == QuestionStatus.ANSWERED_AND_MARKED_FOR_REVIEW }
          val notVisitedCount = userAnswers.values.count { it.status == QuestionStatus.NOT_VISITED }

          EduquityLegendItem(Color(0xFF10B981), "Answered ($answeredCount)")
          EduquityLegendItem(Color(0xFFEF4444), "Not Answered ($notAnsweredCount)")
          EduquityLegendItem(Color(0xFF8B5CF6), "Marked ($markedCount)")
          EduquityLegendItem(Color(0xFF6366F1), "Ans & Marked ($ansMarkedCount)")
          EduquityLegendItem(Color(0xFF475569), "Not Visited ($notVisitedCount)")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Question Grid
        LazyVerticalGrid(
          columns = GridCells.Adaptive(minSize = 44.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 280.dp)
        ) {
          itemsIndexed(test.questions) { globalIndex, q ->
            val state = userAnswers[q.id]?.status ?: QuestionStatus.NOT_VISITED
            val isCurrent = currentQuestion?.id == q.id
            EduquityStatusBadge(
              number = globalIndex + 1,
              status = state,
              isSelected = isCurrent,
              onClick = { onJumpToQuestion(globalIndex) }
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
          onClick = {
            onTogglePalette(false)
            onToggleSubmitDialog()
          },
          modifier = Modifier.fillMaxWidth(),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
          shape = RoundedCornerShape(6.dp)
        ) {
          Text("Submit Final Examination", fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
fun EduquityLegendItem(color: Color, text: String) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .clip(RoundedCornerShape(4.dp))
      .background(Color(0xFF0F172A))
      .padding(horizontal = 6.dp, vertical = 4.dp)
  ) {
    Box(modifier = Modifier.size(10.dp).background(color, RoundedCornerShape(2.dp)))
    Spacer(modifier = Modifier.width(4.dp))
    Text(text, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Medium)
  }
}
