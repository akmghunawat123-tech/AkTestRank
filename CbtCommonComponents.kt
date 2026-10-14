package com.example.ui.screens.cbt

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.MockTest
import com.example.data.model.QuestionStatus
import com.example.data.model.TestQuestion
import com.example.data.model.UserQuestionState

@Composable
fun TcsStatusBadge(
  number: Int,
  status: QuestionStatus,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val (bgColor, textColor, borderColor) = when (status) {
    QuestionStatus.ANSWERED -> Triple(Color(0xFF22C55E), Color.White, Color(0xFF16A34A))
    QuestionStatus.NOT_ANSWERED -> Triple(Color(0xFFEF4444), Color.White, Color(0xFFDC2626))
    QuestionStatus.MARKED_FOR_REVIEW -> Triple(Color(0xFF8B5CF6), Color.White, Color(0xFF7C3AED))
    QuestionStatus.ANSWERED_AND_MARKED_FOR_REVIEW -> Triple(Color(0xFF7C3AED), Color.White, Color(0xFF6D28D9))
    QuestionStatus.NOT_VISITED -> Triple(Color(0xFFE2E8F0), Color(0xFF1E293B), Color(0xFFCBD5E1))
  }

  Box(
    modifier = modifier
      .size(38.dp)
      .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomEnd = 2.dp, bottomStart = 8.dp))
      .background(bgColor)
      .border(
        width = if (isSelected) 2.5.dp else 1.dp,
        color = if (isSelected) Color(0xFFFBBF24) else borderColor,
        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomEnd = 2.dp, bottomStart = 8.dp)
      )
      .clickable(onClick = onClick)
      .testTag("tcs_q_badge_$number"),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = "$number",
      color = textColor,
      fontSize = 13.sp,
      fontWeight = FontWeight.Bold
    )

    // Small green indicator dot for Answered and Marked for Review
    if (status == QuestionStatus.ANSWERED_AND_MARKED_FOR_REVIEW) {
      Box(
        modifier = Modifier
          .align(Alignment.BottomEnd)
          .padding(2.dp)
          .size(8.dp)
          .background(Color(0xFF22C55E), CircleShape)
          .border(0.5.dp, Color.White, CircleShape)
      )
    }
  }
}

@Composable
fun EduquityStatusBadge(
  number: Int,
  status: QuestionStatus,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val (bgColor, textColor, borderColor) = when (status) {
    QuestionStatus.ANSWERED -> Triple(Color(0xFF10B981), Color.White, Color(0xFF059669))
    QuestionStatus.NOT_ANSWERED -> Triple(Color(0xFFEF4444), Color.White, Color(0xFFB91C1C))
    QuestionStatus.MARKED_FOR_REVIEW -> Triple(Color(0xFF8B5CF6), Color.White, Color(0xFF6D28D9))
    QuestionStatus.ANSWERED_AND_MARKED_FOR_REVIEW -> Triple(Color(0xFF6366F1), Color.White, Color(0xFF4F46E5))
    QuestionStatus.NOT_VISITED -> Triple(Color(0xFF334155), Color(0xFFE2E8F0), Color(0xFF475569))
  }

  Box(
    modifier = modifier
      .size(38.dp)
      .clip(RoundedCornerShape(6.dp))
      .background(bgColor)
      .border(
        width = if (isSelected) 2.dp else 1.dp,
        color = if (isSelected) Color(0xFF38BDF8) else borderColor,
        shape = RoundedCornerShape(6.dp)
      )
      .clickable(onClick = onClick)
      .testTag("eduquity_q_badge_$number"),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = "$number",
      color = textColor,
      fontSize = 13.sp,
      fontWeight = FontWeight.Bold
    )

    if (status == QuestionStatus.ANSWERED_AND_MARKED_FOR_REVIEW) {
      Icon(
        imageVector = Icons.Default.Check,
        contentDescription = "Answered and Marked",
        tint = Color(0xFF10B981),
        modifier = Modifier
          .align(Alignment.BottomEnd)
          .size(12.dp)
      )
    }
  }
}

@Composable
fun QuestionPaperDialog(
  test: MockTest,
  isEnglish: Boolean,
  onDismiss: () -> Unit
) {
  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
      shape = RoundedCornerShape(12.dp),
      color = Color(0xFF1E2230)
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Question Paper / प्रश्न पत्र",
              color = Color.White,
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "${test.title} • Total ${test.questions.size} Questions",
              color = Color(0xFF94A3B8),
              fontSize = 12.sp
            )
          }

          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
          }
        }

        HorizontalDivider(color = Color(0xFF334155), modifier = Modifier.padding(vertical = 10.dp))

        LazyColumn(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          itemsIndexed(test.questions) { index, q ->
            Card(
              colors = CardDefaults.cardColors(containerColor = Color(0xFF131722)),
              shape = RoundedCornerShape(8.dp)
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text(
                    text = "Q.${index + 1} (${q.sectionId})",
                    color = Color(0xFF38BDF8),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                  )
                  Text(
                    text = q.topic,
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                  )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                  text = if (isEnglish) q.questionEn else q.questionHi,
                  color = Color.White,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                val opts = if (isEnglish) q.optionsEn else q.optionsHi
                opts.forEachIndexed { optIdx, opt ->
                  val optLetter = ('A' + optIdx).toString()
                  Text(
                    text = "$optLetter. $opt",
                    color = Color(0xFFCBD5E1),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(vertical = 2.dp)
                  )
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
          onClick = onDismiss,
          modifier = Modifier.fillMaxWidth(),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
        ) {
          Text("Close Question Paper")
        }
      }
    }
  }
}

@Composable
fun ExamInstructionsDialog(
  test: MockTest,
  vendorName: String,
  onDismiss: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = Color(0xFF1E2230),
    title = {
      Text(
        text = "Exam Instructions ($vendorName)",
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
      )
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("• Total Duration: ${test.durationMinutes} minutes", color = Color(0xFFCBD5E1), fontSize = 13.sp)
        Text("• Total Marks: ${test.totalMarks}", color = Color(0xFFCBD5E1), fontSize = 13.sp)
        Text("• Marking Scheme: +${test.positiveMark} for Correct, -${test.negativeMark} for Wrong", color = Color(0xFFCBD5E1), fontSize = 13.sp)
        Text("• Sections: ${test.sections.joinToString { it.name }}", color = Color(0xFFCBD5E1), fontSize = 13.sp)
        Text("• Timer at server counts down continuously.", color = Color(0xFFCBD5E1), fontSize = 13.sp)
        Text("• Click 'Save & Next' to save response.", color = Color(0xFFCBD5E1), fontSize = 13.sp)
      }
    },
    confirmButton = {
      Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))) {
        Text("OK")
      }
    }
  )
}
