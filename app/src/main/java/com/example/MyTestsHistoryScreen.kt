package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.data.local.TestAttemptEntity
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTestsHistoryScreen(
  attempts: List<TestAttemptEntity>,
  onViewReport: (Long) -> Unit,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())

  Scaffold(
    modifier = modifier
      .fillMaxSize()
      .testTag("my_tests_history_screen"),
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
            .padding(horizontal = 8.dp, vertical = 12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
          }

          Text(
            text = "My Test Attempts",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            modifier = Modifier.padding(start = 8.dp)
          )
        }
      }
    }
  ) { paddingValues ->
    if (attempts.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(paddingValues),
        contentAlignment = Alignment.Center
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.padding(24.dp)
        ) {
          Icon(
            imageVector = Icons.Default.AssignmentLate,
            contentDescription = null,
            tint = Color(0xFF64748B),
            modifier = Modifier.size(54.dp)
          )
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = "No Tests Attempted Yet",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
          )
          Text(
            text = "Start taking mock tests from the Test Series to track your All-India ranking and accuracy over time.",
            color = Color(0xFF94A3B8),
            fontSize = 13.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
          )
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(paddingValues)
          .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        items(attempts, key = { it.id }) { attempt ->
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onViewReport(attempt.id) }
              .testTag("attempt_item_${attempt.id}"),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2333)),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E374E))
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = attempt.testTitle,
                  color = Color.White,
                  fontSize = 15.sp,
                  fontWeight = FontWeight.SemiBold,
                  modifier = Modifier.weight(1f)
                )
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF1E3A8A))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                  Text(
                    text = "Rank #${attempt.rank}",
                    color = Color(0xFF38BDF8),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }

              Spacer(modifier = Modifier.height(4.dp))

              Text(
                text = dateFormat.format(Date(attempt.timestamp)),
                color = Color(0xFF64748B),
                fontSize = 11.sp
              )

              Spacer(modifier = Modifier.height(10.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Column {
                  Text("Score", color = Color(0xFF94A3B8), fontSize = 11.sp)
                  Text(
                    text = "${attempt.totalScore} / ${attempt.maxScore.toInt()}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                  )
                }
                Column {
                  Text("Accuracy", color = Color(0xFF94A3B8), fontSize = 11.sp)
                  Text(
                    text = "${attempt.accuracyPercentage}%",
                    color = Color(0xFF10B981),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                  )
                }
                Column {
                  Text("Percentile", color = Color(0xFF94A3B8), fontSize = 11.sp)
                  Text(
                    text = "${attempt.percentile}%",
                    color = Color(0xFFF59E0B),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                  )
                }
              }

              Spacer(modifier = Modifier.height(10.dp))

              Button(
                onClick = { onViewReport(attempt.id) },
                colors = ButtonDefaults.buttonColors(
                  containerColor = Color(0xFF0E43AF),
                  contentColor = Color.White
                ),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.fillMaxWidth().height(36.dp)
              ) {
                Text("View Full Analytics Report", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
              }
            }
          }
        }
      }
    }
  }
}
