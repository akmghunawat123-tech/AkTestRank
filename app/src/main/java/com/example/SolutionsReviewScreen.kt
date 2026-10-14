package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.TestAttemptEntity
import com.example.data.model.MockTest
import com.example.data.model.TestQuestion

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolutionsReviewScreen(
  test: MockTest,
  attempt: TestAttemptEntity?,
  onBookmarkToggle: (TestQuestion) -> Unit,
  isBookmarked: (String) -> Boolean,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedFilter by remember { mutableStateOf("All") }
  var isEnglish by remember { mutableStateOf(true) }

  // Parse user answers from attempt: map of qId -> selectedOptionIdx (-1 if unattempted)
  val userAnswersMap = remember(attempt) {
    val map = mutableMapOf<String, Int>()
    if (attempt != null) {
      val items = attempt.userAnswersData.split(";;")
      for (item in items) {
        val parts = item.split(":")
        if (parts.size >= 2) {
          map[parts[0]] = parts[1].toIntOrNull() ?: -1
        }
      }
    }
    map
  }

  val filteredQuestions = remember(test.questions, selectedFilter, userAnswersMap) {
    when (selectedFilter) {
      "Incorrect" -> test.questions.filter { q ->
        val userSel = userAnswersMap[q.id] ?: -1
        userSel != -1 && userSel != q.correctOptionIndex
      }
      "Unattempted" -> test.questions.filter { q ->
        val userSel = userAnswersMap[q.id] ?: -1
        userSel == -1
      }
      "Correct" -> test.questions.filter { q ->
        val userSel = userAnswersMap[q.id] ?: -1
        userSel == q.correctOptionIndex
      }
      "Bookmarked" -> test.questions.filter { q ->
        isBookmarked(q.id)
      }
      else -> test.questions
    }
  }

  Scaffold(
    modifier = modifier
      .fillMaxSize()
      .testTag("solutions_review_screen"),
    containerColor = Color(0xFF141722),
    topBar = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color(0xFF0A2B78))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 8.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
          }

          Text(
            text = "Detailed Solutions",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            modifier = Modifier.weight(1f)
          )

          // Language Toggle
          TextButton(
            onClick = { isEnglish = !isEnglish },
            colors = ButtonDefaults.textButtonColors(contentColor = Color.White),
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(Color(0xFF1E3A8A))
              .height(32.dp)
          ) {
            Text(
              text = if (isEnglish) "ENG | हिन्" else "हिन् | ENG",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
    ) {
      // Filter Chips
      val filters = listOf("All", "Incorrect", "Unattempted", "Correct", "Bookmarked")
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState())
          .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        filters.forEach { filter ->
          val isSelected = filter == selectedFilter
          val count = when (filter) {
            "Incorrect" -> attempt?.wrongCount ?: 0
            "Unattempted" -> attempt?.unattemptedCount ?: 0
            "Correct" -> attempt?.correctCount ?: 0
            else -> test.questions.size
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(16.dp))
              .background(if (isSelected) Color(0xFF0E43AF) else Color(0xFF222635))
              .border(
                1.dp,
                if (isSelected) Color(0xFF3B82F6) else Color(0xFF333B50),
                RoundedCornerShape(16.dp)
              )
              .clickable { selectedFilter = filter }
              .padding(horizontal = 14.dp, vertical = 6.dp)
          ) {
            Text(
              text = if (filter == "All") "All (${test.questions.size})" else "$filter ($count)",
              color = if (isSelected) Color.White else Color(0xFF94A3B8),
              fontSize = 12.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
          }
        }
      }

      if (filteredQuestions.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "No questions found for '$selectedFilter'",
            color = Color(0xFF94A3B8),
            fontSize = 14.sp
          )
        }
      } else {
        LazyColumn(
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp),
          modifier = Modifier.fillMaxSize()
        ) {
          itemsIndexed(filteredQuestions, key = { _, q -> q.id }) { _, q ->
            val userSel = userAnswersMap[q.id] ?: -1
            val isCorr = userSel == q.correctOptionIndex
            val isUnatt = userSel == -1

            QuestionSolutionCard(
              question = q,
              userOptionIndex = userSel,
              isCorrect = isCorr,
              isUnattempted = isUnatt,
              isEnglish = isEnglish,
              bookmarked = isBookmarked(q.id),
              onBookmarkToggle = { onBookmarkToggle(q) }
            )
          }
        }
      }
    }
  }
}

@Composable
fun QuestionSolutionCard(
  question: TestQuestion,
  userOptionIndex: Int,
  isCorrect: Boolean,
  isUnattempted: Boolean,
  isEnglish: Boolean,
  bookmarked: Boolean,
  onBookmarkToggle: () -> Unit
) {
  val statusColor = when {
    isCorrect -> Color(0xFF10B981)
    isUnattempted -> Color(0xFF64748B)
    else -> Color(0xFFEF4444)
  }
  val statusText = when {
    isCorrect -> "Correct (+2.0)"
    isUnattempted -> "Unattempted (0.0)"
    else -> "Incorrect (-0.5)"
  }

  Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2333)),
    shape = RoundedCornerShape(12.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E374E))
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // Header: Q Number, Topic, Status, Bookmark
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "Q.${question.questionNumber}",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )
          Spacer(modifier = Modifier.width(6.dp))
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(statusColor.copy(alpha = 0.2f))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(text = statusText, color = statusColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }

        IconButton(onClick = onBookmarkToggle, modifier = Modifier.size(28.dp)) {
          Icon(
            imageVector = if (bookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
            contentDescription = "Bookmark",
            tint = if (bookmarked) Color(0xFFF59E0B) else Color(0xFF94A3B8)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Question Text
      Text(
        text = if (isEnglish) question.questionEn else question.questionHi,
        color = Color(0xFFF8FAFC),
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.Medium
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Options
      val optionsList = if (isEnglish) question.optionsEn else question.optionsHi
      val labels = listOf("A", "B", "C", "D")

      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        optionsList.forEachIndexed { optIdx, optText ->
          val isCorrectOption = optIdx == question.correctOptionIndex
          val isUserSelected = optIdx == userOptionIndex

          val optionBg = when {
            isCorrectOption -> Color(0xFF064E3B)
            isUserSelected && !isCorrectOption -> Color(0xFF7F1D1D)
            else -> Color(0xFF252B3E)
          }
          val optionBorder = when {
            isCorrectOption -> Color(0xFF10B981)
            isUserSelected && !isCorrectOption -> Color(0xFFEF4444)
            else -> Color(0xFF333B50)
          }

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(6.dp))
              .background(optionBg)
              .border(1.dp, optionBorder, RoundedCornerShape(6.dp))
              .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(
                  if (isCorrectOption) Color(0xFF10B981)
                  else if (isUserSelected) Color(0xFFEF4444)
                  else Color(0xFF334155)
                ),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = labels.getOrElse(optIdx) { "$optIdx" },
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
              text = optText,
              color = Color.White,
              fontSize = 13.sp,
              modifier = Modifier.weight(1f)
            )

            if (isCorrectOption) {
              Icon(Icons.Default.Check, contentDescription = "Correct Answer", tint = Color(0xFF34D399), modifier = Modifier.size(18.dp))
            } else if (isUserSelected) {
              Icon(Icons.Default.Close, contentDescription = "Your Choice", tint = Color(0xFFF87171), modifier = Modifier.size(18.dp))
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Explanation Box
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(Color(0xFF192033))
          .border(1.dp, Color(0xFF2C395B), RoundedCornerShape(8.dp))
          .padding(12.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Detailed Solution & Shortcut Trick:",
            color = Color(0xFFF59E0B),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
          )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = if (isEnglish) question.explanationEn else question.explanationHi,
          color = Color(0xFFCBD5E1),
          fontSize = 13.sp,
          lineHeight = 19.sp
        )
      }
    }
  }
}
