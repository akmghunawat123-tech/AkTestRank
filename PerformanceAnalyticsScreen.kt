package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.TestAttemptEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerformanceAnalyticsScreen(
  attempt: TestAttemptEntity?,
  onViewSolutions: () -> Unit,
  onViewRecommendations: () -> Unit,
  onRetakeTest: () -> Unit,
  onBackToHome: () -> Unit,
  modifier: Modifier = Modifier
) {
  if (attempt == null) {
    Box(modifier = modifier.fillMaxSize().background(Color(0xFF141722)), contentAlignment = Alignment.Center) {
      Text("No attempt report found.", color = Color.White)
    }
    return
  }

  val isCleared = attempt.totalScore >= attempt.cutoffMarks
  val minutesTaken = attempt.timeTakenSeconds / 60
  val secondsTaken = attempt.timeTakenSeconds % 60

  Scaffold(
    modifier = modifier
      .fillMaxSize()
      .testTag("performance_analytics_screen"),
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
          IconButton(
            onClick = onBackToHome,
            modifier = Modifier.testTag("analytics_back_btn")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              tint = Color.White
            )
          }

          Text(
            text = "Performance Report & Rank",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            modifier = Modifier.padding(start = 8.dp)
          )
        }
      }
    },
    bottomBar = {
      Surface(
        color = Color(0xFF1B1E2B),
        modifier = Modifier
          .fillMaxWidth()
          .navigationBarsPadding(),
        tonalElevation = 4.dp
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedButton(
            onClick = onViewRecommendations,
            modifier = Modifier
              .weight(1f)
              .height(44.dp)
              .testTag("view_ai_recommendations_btn"),
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8)),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF38BDF8))
          ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("AI Study Plan", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
          }

          Button(
            onClick = onViewSolutions,
            modifier = Modifier
              .weight(1.2f)
              .height(44.dp)
              .testTag("view_solutions_btn"),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = Color(0xFF0E43AF),
              contentColor = Color.White
            )
          ) {
            Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("View Solutions", fontSize = 13.sp, fontWeight = FontWeight.Bold)
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
        .padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Hero Card: Score, Rank & Percentile
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1D2335)),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFF2E3A59))
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .background(
              Brush.verticalGradient(
                listOf(Color(0xFF1D284B), Color(0xFF171B2A))
              )
            )
            .padding(18.dp)
        ) {
          // Status Pill & Title
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = attempt.testTitle,
              color = Color.White,
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.weight(1f)
            )
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(if (isCleared) Color(0xFF065F46) else Color(0xFF7F1D1D))
                .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
              Text(
                text = if (isCleared) "QUALIFIED" else "BELOW CUTOFF",
                color = if (isCleared) Color(0xFF34D399) else Color(0xFFF87171),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // 3 Big Highlight Metric Columns: Rank, Score, Percentile
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
          ) {
            // Rank
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("All India Rank", color = Color(0xFF94A3B8), fontSize = 11.sp)
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "#${attempt.rank}",
                color = Color(0xFFF59E0B),
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
              )
              Text("out of ${attempt.totalParticipants}", color = Color(0xFF64748B), fontSize = 10.sp)
            }

            VerticalDivider(
              color = Color(0xFF334155),
              modifier = Modifier
                .height(48.dp)
                .width(1.dp)
            )

            // Score
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("Your Score", color = Color(0xFF94A3B8), fontSize = 11.sp)
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "${attempt.totalScore}",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
              )
              Text("out of ${attempt.maxScore.toInt()}", color = Color(0xFF64748B), fontSize = 10.sp)
            }

            VerticalDivider(
              color = Color(0xFF334155),
              modifier = Modifier
                .height(48.dp)
                .width(1.dp)
            )

            // Percentile
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("Percentile", color = Color(0xFF94A3B8), fontSize = 11.sp)
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "${attempt.percentile}%",
                color = Color(0xFF38BDF8),
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
              )
              Text("Top ${(100.0 - attempt.percentile).coerceAtLeast(0.1)}%", color = Color(0xFF10B981), fontSize = 10.sp)
            }
          }
        }
      }

      // Comparison Bar: You vs Cutoff vs Topper vs Average
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2333)),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E364F))
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = "Comparative Benchmark",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
          )

          Spacer(modifier = Modifier.height(12.dp))

          BenchmarkScoreRow("Your Score", attempt.totalScore, attempt.maxScore, Color(0xFF38BDF8))
          Spacer(modifier = Modifier.height(8.dp))
          BenchmarkScoreRow("Expected Cutoff", attempt.cutoffMarks, attempt.maxScore, Color(0xFFF59E0B))
          Spacer(modifier = Modifier.height(8.dp))
          BenchmarkScoreRow("Topper's Score", attempt.topperScore, attempt.maxScore, Color(0xFF10B981))
          Spacer(modifier = Modifier.height(8.dp))
          BenchmarkScoreRow("Average Score", attempt.averageScore, attempt.maxScore, Color(0xFF94A3B8))
        }
      }

      // Quick KPI Cards: Accuracy, Attempted, Correct, Wrong, Time
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Accuracy
        KpiCard(
          title = "Accuracy",
          value = "${attempt.accuracyPercentage}%",
          subtitle = "Quality",
          iconColor = Color(0xFF10B981),
          modifier = Modifier.weight(1f)
        )
        // Attempted
        KpiCard(
          title = "Attempted",
          value = "${attempt.attemptedCount}/${attempt.attemptedCount + attempt.unattemptedCount}",
          subtitle = "${attempt.correctCount} Correct",
          iconColor = Color(0xFF38BDF8),
          modifier = Modifier.weight(1f)
        )
        // Time
        KpiCard(
          title = "Time Spent",
          value = "${minutesTaken}m ${secondsTaken}s",
          subtitle = "Speed",
          iconColor = Color(0xFF8B5CF6),
          modifier = Modifier.weight(1f)
        )
      }

      // Section-wise Performance Breakdown
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2333)),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E364F))
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = "Section-wise Breakdown",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
          )

          Spacer(modifier = Modifier.height(10.dp))

          val sectionLines = attempt.sectionBreakdownData.split(";;")
          sectionLines.forEach { line ->
            val parts = line.split("|")
            if (parts.size >= 9) {
              val secId = parts[0]
              val secName = parts[1]
              val att = parts[2]
              val corr = parts[3]
              val inc = parts[4]
              val score = parts[6]
              val maxS = parts[7]
              val acc = parts[8]

              SectionBreakdownItem(
                sectionId = secId,
                sectionName = secName,
                score = score,
                maxScore = maxS,
                accuracy = "$acc%",
                attempted = att,
                correct = corr,
                incorrect = inc
              )
              Spacer(modifier = Modifier.height(10.dp))
            }
          }
        }
      }

      // Retake Test button
      OutlinedButton(
        onClick = onRetakeTest,
        modifier = Modifier
          .fillMaxWidth()
          .height(46.dp)
          .testTag("retake_test_button"),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF475569))
      ) {
        Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.White)
        Spacer(modifier = Modifier.width(6.dp))
        Text("Retake This Mock Test", color = Color.White, fontWeight = FontWeight.SemiBold)
      }
    }
  }
}

@Composable
fun BenchmarkScoreRow(
  label: String,
  score: Double,
  maxScore: Double,
  color: Color
) {
  val ratio = if (maxScore > 0) (score / maxScore).toFloat().coerceIn(0f, 1f) else 0f

  Column(modifier = Modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(text = label, color = Color(0xFFCBD5E1), fontSize = 12.sp)
      Text(text = "$score / ${maxScore.toInt()}", color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
    Spacer(modifier = Modifier.height(4.dp))
    LinearProgressIndicator(
      progress = { ratio },
      modifier = Modifier
        .fillMaxWidth()
        .height(6.dp)
        .clip(RoundedCornerShape(3.dp)),
      color = color,
      trackColor = Color(0xFF282F42)
    )
  }
}

@Composable
fun KpiCard(
  title: String,
  value: String,
  subtitle: String,
  iconColor: Color,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    colors = CardDefaults.cardColors(containerColor = Color(0xFF22283A)),
    shape = RoundedCornerShape(10.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF31394F))
  ) {
    Column(
      modifier = Modifier.padding(10.dp),
      horizontalAlignment = Alignment.Start
    ) {
      Text(text = title, color = Color(0xFF94A3B8), fontSize = 11.sp)
      Spacer(modifier = Modifier.height(4.dp))
      Text(text = value, color = iconColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
      Spacer(modifier = Modifier.height(2.dp))
      Text(text = subtitle, color = Color(0xFF64748B), fontSize = 10.sp)
    }
  }
}

@Composable
fun SectionBreakdownItem(
  sectionId: String,
  sectionName: String,
  score: String,
  maxScore: String,
  accuracy: String,
  attempted: String,
  correct: String,
  incorrect: String
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(8.dp))
      .background(Color(0xFF262C3E))
      .padding(10.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "$sectionId: $sectionName",
        color = Color.White,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold
      )
      Text(
        text = "$score / $maxScore",
        color = Color(0xFF38BDF8),
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold
      )
    }

    Spacer(modifier = Modifier.height(6.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text("Accuracy: $accuracy", color = Color(0xFF10B981), fontSize = 11.sp, fontWeight = FontWeight.Medium)
      Text("Att: $attempted  |  ✓ $correct  |  ✗ $incorrect", color = Color(0xFF94A3B8), fontSize = 11.sp)
    }
  }
}
