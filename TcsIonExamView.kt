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
fun TcsIonExamView(
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
  val rollNo = "RRB20261109"

  Scaffold(
    modifier = modifier
      .fillMaxSize()
      .testTag("tcs_ion_exam_view"),
    containerColor = Color(0xFF0F172A),
    topBar = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color(0xFF0B3C7A)) // TCS iON Classic Corporate Navy Blue
      ) {
        // Row 1: Header bar with Railway title & Candidate Passport Card
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 8.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(
            onClick = onExitTest,
            modifier = Modifier.size(34.dp)
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Exit",
              tint = Color.White
            )
          }

          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "RAILWAY RECRUITMENT BOARDS",
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
                Text("TCS iON", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
              }
            }
            Text(
              text = test.title,
              color = Color(0xFF93C5FD),
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              maxLines = 1
            )
          }

          // Candidate Passport Frame Card (exact TCS iON design)
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(Color(0xFF082F49))
              .border(1.dp, Color(0xFF38BDF8), RoundedCornerShape(4.dp))
              .padding(horizontal = 6.dp, vertical = 4.dp)
          ) {
            Box(
              modifier = Modifier
                .size(30.dp, 34.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color(0xFF334155))
                .border(0.5.dp, Color.White, RoundedCornerShape(2.dp)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.AccountBox, contentDescription = null, tint = Color(0xFFE2E8F0), modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(6.dp))
            Column {
              Text(candidateName, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1)
              Text("Roll: $rollNo", color = Color(0xFFBAE6FD), fontSize = 9.sp)
              Text("CEN: 01/2026", color = Color(0xFF7DD3FC), fontSize = 8.sp)
            }
          }
        }

        // Row 2: TCS Digital Yellow Timer & Navigation Tools
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF072B57))
            .padding(horizontal = 8.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          // TCS Classic Yellow Digital Timer
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(Color(0xFF1E293B))
              .border(1.dp, if (isLowTime) Color(0xFFEF4444) else Color(0xFFF59E0B), RoundedCornerShape(4.dp))
              .padding(horizontal = 8.dp, vertical = 3.dp)
          ) {
            Icon(
              imageVector = Icons.Default.HourglassBottom,
              contentDescription = "Time Left",
              tint = if (isLowTime) Color(0xFFEF4444) else Color(0xFFFBBF24),
              modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Time Left : $timerText",
              color = if (isLowTime) Color(0xFFFCA5A5) else Color(0xFFFEF08A),
              fontSize = 12.sp,
              fontWeight = FontWeight.ExtraBold,
              modifier = Modifier.testTag("tcs_timer_display")
            )
          }

          // Vendor Switcher Pill (TCS iON <-> Eduquity)
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(Color(0xFF0F766E))
              .clickable { onSwitchVendor(ExamCbtVendor.EDUQUITY) }
              .padding(horizontal = 8.dp, vertical = 3.dp)
              .testTag("vendor_switch_to_eduquity")
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text("⚡ Vendor: TCS iON (RRB)", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
              Spacer(modifier = Modifier.width(3.dp))
              Icon(Icons.Default.SwapHoriz, contentDescription = "Switch", tint = Color.White, modifier = Modifier.size(12.dp))
            }
          }

          // Language Selector & Palette
          Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(
              onClick = onToggleLanguage,
              colors = ButtonDefaults.textButtonColors(contentColor = Color.White),
              modifier = Modifier
                .height(28.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color(0xFF1E3A8A))
            ) {
              Text(if (isEnglish) "View: ENG" else "View: हिन्", fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.width(4.dp))

            IconButton(
              onClick = { onTogglePalette(true) },
              modifier = Modifier.size(28.dp)
            ) {
              Icon(Icons.Default.GridView, contentDescription = "Palette", tint = Color(0xFFFBBF24), modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.width(4.dp))

            Button(
              onClick = onToggleSubmitDialog,
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF15803D)),
              shape = RoundedCornerShape(3.dp),
              contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
              modifier = Modifier.height(28.dp)
            ) {
              Text("Submit", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }
        }

        // Row 3: TCS Top Utilities (Question Paper & Instructions Buttons)
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0A2540))
            .padding(horizontal = 8.dp, vertical = 3.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(3.dp))
                .background(Color(0xFF1E293B))
                .border(0.5.dp, Color(0xFF64748B), RoundedCornerShape(3.dp))
                .clickable { onToggleQuestionPaper() }
                .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Description, contentDescription = null, tint = Color(0xFF93C5FD), modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Question Paper", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
              }
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(3.dp))
                .background(Color(0xFF1E293B))
                .border(0.5.dp, Color(0xFF64748B), RoundedCornerShape(3.dp))
                .clickable { onToggleInstructions() }
                .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Instructions", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
              }
            }
          }

          Text(
            text = "Total Marks: ${test.totalMarks.toInt()}",
            color = Color(0xFF94A3B8),
            fontSize = 10.sp
          )
        }

        // Row 4: TCS iON Section Tabs (Railways: General Awareness, Mathematics, Reasoning)
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0F172A))
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 6.dp, vertical = 4.dp),
          horizontalArrangement = Arrangement.spacedBy(4.dp)
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
                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp, bottomStart = 0.dp, bottomEnd = 0.dp))
                .background(if (isSelected) Color(0xFF1D4ED8) else Color(0xFF1E293B))
                .border(
                  width = 1.dp,
                  color = if (isSelected) Color(0xFF60A5FA) else Color(0xFF334155),
                  shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp, bottomStart = 0.dp, bottomEnd = 0.dp)
                )
                .clickable { onSwitchSection(index) }
                .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = sec.name,
                  color = if (isSelected) Color.White else Color(0xFF94A3B8),
                  fontSize = 11.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "[$answeredCount/${sectionQs.size}]",
                  color = if (isSelected) Color(0xFFFEF08A) else Color(0xFF64748B),
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
      // TCS iON Classic Bottom Buttons
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
            .padding(horizontal = 8.dp, vertical = 6.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          // Row 1: Mark for Review & Next | Clear Response
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Button(
              onClick = onMarkForReviewAndNext,
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6D28D9)), // TCS Purple
              shape = RoundedCornerShape(3.dp),
              modifier = Modifier
                .weight(1f)
                .height(38.dp)
                .testTag("tcs_mark_for_review_btn")
            ) {
              Text("Mark for Review & Next", fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            }

            OutlinedButton(
              onClick = onClearResponse,
              colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE2E8F0)),
              border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF64748B)),
              shape = RoundedCornerShape(3.dp),
              modifier = Modifier
                .weight(0.7f)
                .height(38.dp)
                .testTag("tcs_clear_response_btn")
            ) {
              Text("Clear Response", fontSize = 11.sp, maxLines = 1)
            }
          }

          // Row 2: Save & Mark for Review | Previous | Save & Next
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Button(
              onClick = onSaveAndMarkForReview,
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
              shape = RoundedCornerShape(3.dp),
              modifier = Modifier
                .weight(1f)
                .height(42.dp)
            ) {
              Text("Save & Mark Review", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
            }

            OutlinedButton(
              onClick = onPreviousQuestion,
              colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE2E8F0)),
              border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF475569)),
              shape = RoundedCornerShape(3.dp),
              modifier = Modifier
                .weight(0.7f)
                .height(42.dp)
            ) {
              Text("Previous", fontSize = 11.sp)
            }

            Button(
              onClick = onSaveAndNext,
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF15803D)), // TCS Green
              shape = RoundedCornerShape(3.dp),
              modifier = Modifier
                .weight(1.1f)
                .height(42.dp)
                .testTag("tcs_save_and_next_btn")
            ) {
              Text("Save & Next", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
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

        // TCS Question Meta Banner
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFF1E293B))
            .border(1.dp, Color(0xFF334155), RoundedCornerShape(4.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Question No. ${currentQuestionIndex + 1} of ${questionsForCurrentSection.size}",
              color = Color.White,
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Question Type: Multiple Choice Question (MCQ)",
              color = Color(0xFF94A3B8),
              fontSize = 10.sp
            )
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            Column(horizontalAlignment = Alignment.End) {
              Text(
                text = "Marks for correct: +${test.positiveMark.toInt()}",
                color = Color(0xFF22C55E),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Negative Marks: -${test.negativeMark}",
                color = Color(0xFFEF4444),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }

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

        // Question Content Card
        Card(
          colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
          shape = RoundedCornerShape(4.dp),
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

        // Options: TCS Style Radio Choices (1, 2, 3, 4)
        val opts = if (isEnglish) currentQuestion.optionsEn else currentQuestion.optionsHi
        opts.forEachIndexed { optIndex, optText ->
          val isSelected = qState?.selectedOptionIndex == optIndex
          val optNum = "${optIndex + 1}"

          Card(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 4.dp)
              .clip(RoundedCornerShape(4.dp))
              .clickable { onSelectOption(optIndex) }
              .testTag("tcs_opt_$optIndex"),
            colors = CardDefaults.cardColors(
              containerColor = if (isSelected) Color(0xFF1E3A8A).copy(alpha = 0.35f) else Color(0xFF1E293B)
            ),
            border = androidx.compose.foundation.BorderStroke(
              width = if (isSelected) 1.5.dp else 1.dp,
              color = if (isSelected) Color(0xFF38BDF8) else Color(0xFF334155)
            ),
            shape = RoundedCornerShape(4.dp)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(24.dp)
                  .clip(CircleShape)
                  .background(if (isSelected) Color(0xFF38BDF8) else Color(0xFF334155)),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = optNum,
                  color = if (isSelected) Color(0xFF0F172A) else Color(0xFF94A3B8),
                  fontSize = 12.sp,
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
                  imageVector = Icons.Default.RadioButtonChecked,
                  contentDescription = "Selected",
                  tint = Color(0xFF38BDF8),
                  modifier = Modifier.size(18.dp)
                )
              }
            }
          }
        }
      }
    }
  }

  // TCS iON Question Palette Bottom Sheet
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
            Text("TCS iON Question Palette (Railways)", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text("Candidate: $candidateName • Roll: $rollNo", color = Color(0xFF94A3B8), fontSize = 11.sp)
          }
          IconButton(onClick = { onTogglePalette(false) }) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // TCS 5 Official Shape Legend Badges
        val answeredCount = userAnswers.values.count { it.status == QuestionStatus.ANSWERED }
        val notAnsweredCount = userAnswers.values.count { it.status == QuestionStatus.NOT_ANSWERED }
        val markedCount = userAnswers.values.count { it.status == QuestionStatus.MARKED_FOR_REVIEW }
        val ansMarkedCount = userAnswers.values.count { it.status == QuestionStatus.ANSWERED_AND_MARKED_FOR_REVIEW }
        val notVisitedCount = userAnswers.values.count { it.status == QuestionStatus.NOT_VISITED }

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            TcsLegendChip(Color(0xFF22C55E), "$answeredCount Answered", Modifier.weight(1f))
            TcsLegendChip(Color(0xFFEF4444), "$notAnsweredCount Not Answered", Modifier.weight(1f))
            TcsLegendChip(Color(0xFFCBD5E1), "$notVisitedCount Not Visited", Modifier.weight(1f))
          }
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            TcsLegendChip(Color(0xFF8B5CF6), "$markedCount Marked for Review", Modifier.weight(1f))
            TcsLegendChip(Color(0xFF6D28D9), "$ansMarkedCount Ans & Marked", Modifier.weight(1f), hasGreenDot = true)
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Grid of TCS Badges
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
            TcsStatusBadge(
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
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF15803D)),
          shape = RoundedCornerShape(4.dp)
        ) {
          Text("Submit Examination", fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
fun TcsLegendChip(color: Color, text: String, modifier: Modifier = Modifier, hasGreenDot: Boolean = false) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier
      .clip(RoundedCornerShape(4.dp))
      .background(Color(0xFF0F172A))
      .padding(horizontal = 6.dp, vertical = 4.dp)
  ) {
    Box(
      modifier = Modifier
        .size(12.dp)
        .background(color, RoundedCornerShape(2.dp))
    ) {
      if (hasGreenDot) {
        Box(
          modifier = Modifier
            .size(5.dp)
            .background(Color(0xFF22C55E), CircleShape)
            .align(Alignment.BottomEnd)
        )
      }
    }
    Spacer(modifier = Modifier.width(4.dp))
    Text(text, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Medium, maxLines = 1)
  }
}
