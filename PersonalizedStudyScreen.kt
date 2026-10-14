package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.data.model.StudyRecommendation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalizedStudyScreen(
  recommendations: List<StudyRecommendation>,
  onLaunchDrill: (String) -> Unit,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedSubject by remember { mutableStateOf("Quantitative Aptitude") }
  var selectedQuestionCount by remember { mutableStateOf(25) }
  var selectedDifficulty by remember { mutableStateOf("Medium") }

  Scaffold(
    modifier = modifier
      .fillMaxSize()
      .testTag("personalized_study_screen"),
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
            text = "Personalized Study & Practice",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            modifier = Modifier.padding(start = 8.dp)
          )
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
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Diagnostic Header Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2436)),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E3852))
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(46.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(Color(0xFF1E3A8A)),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(24.dp))
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = "AI Diagnostic Weak Area Analysis",
              color = Color.White,
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Personalized drill recommendations computed from your mock test accuracy and time-per-question telemetry.",
              color = Color(0xFF94A3B8),
              fontSize = 12.sp,
              lineHeight = 16.sp
            )
          }
        }
      }

      // Priority Weak Areas List
      Text(
        text = "Recommended Focus Topics",
        color = Color.White,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold
      )

      recommendations.forEach { rec ->
        val priorityColor = when (rec.priority) {
          "HIGH" -> Color(0xFFEF4444)
          "MEDIUM" -> Color(0xFFF59E0B)
          else -> Color(0xFF10B981)
        }

        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = Color(0xFF1D2232)),
          shape = RoundedCornerShape(10.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2F374E))
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = rec.topic,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
              )
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(priorityColor.copy(alpha = 0.2f))
                  .padding(horizontal = 8.dp, vertical = 3.dp)
              ) {
                Text(
                  text = "${rec.priority} PRIORITY",
                  color = priorityColor,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
              text = "Current Accuracy: ${rec.accuracy.toInt()}%  •  Section: ${rec.sectionName}",
              color = Color(0xFF38BDF8),
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
              text = rec.advice,
              color = Color(0xFFCBD5E1),
              fontSize = 12.sp,
              lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
              onClick = {
                val drillId = when (rec.sectionName) {
                  "PART-C" -> "cgl_drill_quant"
                  "PART-B" -> "cgl_drill_reasoning"
                  "PART-A" -> "cgl_drill_english"
                  else -> "cgl_drill_gk"
                }
                onLaunchDrill(drillId)
              },
              colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0E43AF),
                contentColor = Color.White
              ),
              shape = RoundedCornerShape(6.dp),
              modifier = Modifier.fillMaxWidth().height(36.dp)
            ) {
              Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Start Targeted ${rec.recommendedDrillCount}-Q Drill", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Custom Practice Studio Generator
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2030)),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFF2A344E))
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = Color(0xFF10B981))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Practice Studio: Custom Drill Generator",
              color = Color.White,
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Subject Selector
          Text("Select Subject:", color = Color(0xFF94A3B8), fontSize = 12.sp)
          Spacer(modifier = Modifier.height(6.dp))
          val subjects = listOf(
            "Quantitative Aptitude",
            "General Intelligence & Reasoning",
            "English Comprehension",
            "General Awareness"
          )
          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            subjects.forEach { subj ->
              val isSelected = subj == selectedSubject
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(6.dp))
                  .background(if (isSelected) Color(0xFF0E43AF) else Color(0xFF252B3E))
                  .border(1.dp, if (isSelected) Color(0xFF38BDF8) else Color(0xFF333B50), RoundedCornerShape(6.dp))
                  .clickable { selectedSubject = subj }
                  .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                RadioButton(
                  selected = isSelected,
                  onClick = { selectedSubject = subj },
                  colors = RadioButtonDefaults.colors(
                    selectedColor = Color.White,
                    unselectedColor = Color(0xFF64748B)
                  ),
                  modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = subj, color = Color.White, fontSize = 13.sp)
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Question count row
          Text("Question Count:", color = Color(0xFF94A3B8), fontSize = 12.sp)
          Spacer(modifier = Modifier.height(6.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            listOf(10, 25, 50).forEach { count ->
              val isSel = count == selectedQuestionCount
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(6.dp))
                  .background(if (isSel) Color(0xFF10B981) else Color(0xFF252B3E))
                  .clickable { selectedQuestionCount = count }
                  .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "$count Qs",
                  color = Color.White,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          Button(
            onClick = {
              val drillTestId = when (selectedSubject) {
                "Quantitative Aptitude" -> "cgl_drill_quant"
                "General Intelligence & Reasoning" -> "cgl_drill_reasoning"
                "English Comprehension" -> "cgl_drill_english"
                else -> "cgl_drill_gk"
              }
              onLaunchDrill(drillTestId)
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(44.dp)
              .testTag("launch_custom_drill_btn"),
            colors = ButtonDefaults.buttonColors(
              containerColor = Color(0xFF10B981),
              contentColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp)
          ) {
            Text("Launch Custom Practice Test", fontWeight = FontWeight.Bold, fontSize = 14.sp)
          }
        }
      }
    }
  }
}
