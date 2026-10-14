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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Computer
import com.example.data.model.ExamCbtVendor
import com.example.data.model.MockTest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneralInstructionsScreen(
  test: MockTest,
  onProceedToTest: () -> Unit,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  var isDeclarationChecked by remember { mutableStateOf(true) }

  Scaffold(
    modifier = modifier
      .fillMaxSize()
      .testTag("general_instructions_screen"),
    containerColor = Color(0xFF1E212B),
    topBar = {
      // Header matching Screenshot 3: Deep blue toolbar with back arrow
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
            onClick = onBack,
            modifier = Modifier.testTag("instructions_back_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              tint = Color.White
            )
          }

          Text(
            text = "General Instructions",
            fontSize = 19.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            modifier = Modifier.padding(start = 8.dp)
          )
        }
      }
    },
    bottomBar = {
      // Bottom navigation buttons matching Screenshot 3: [Go to test] and [Next]
      Surface(
        color = Color(0xFF16181F),
        modifier = Modifier
          .fillMaxWidth()
          .navigationBarsPadding()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          OutlinedButton(
            onClick = onBack,
            modifier = Modifier
              .width(130.dp)
              .height(44.dp)
              .testTag("go_to_test_secondary_btn"),
            shape = RoundedCornerShape(6.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White),
            colors = ButtonDefaults.outlinedButtonColors(
              contentColor = Color.White
            )
          ) {
            Text(
              text = "Go to test",
              color = Color.White,
              fontWeight = FontWeight.SemiBold,
              fontSize = 14.sp
            )
          }

          Button(
            onClick = onProceedToTest,
            enabled = isDeclarationChecked,
            modifier = Modifier
              .width(130.dp)
              .height(44.dp)
              .testTag("next_start_test_btn"),
            shape = RoundedCornerShape(6.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = Color(0xFF0E43AF),
              contentColor = Color.White,
              disabledContainerColor = Color(0xFF334155)
            )
          ) {
            Text(
              text = "Next",
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp
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
        .verticalScroll(rememberScrollState())
        .padding(16.dp)
    ) {
      Text(
        text = "General Instructions:",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White
      )

      Text(
        text = "Instructions, Terms & Conditions",
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        color = Color(0xFFCBD5E1),
        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
      )

      // CBT Interface Vendor Badge (Eduquity for SSC, TCS iON for Railways)
      Card(
        colors = CardDefaults.cardColors(
          containerColor = if (test.vendor == ExamCbtVendor.TCS_ION) Color(0xFF0C2B57) else Color(0xFF074770)
        ),
        border = androidx.compose.foundation.BorderStroke(
          1.dp,
          if (test.vendor == ExamCbtVendor.TCS_ION) Color(0xFF2563EB) else Color(0xFF0284C7)
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 16.dp)
          .testTag("instructions_vendor_badge")
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(42.dp)
              .clip(CircleShape)
              .background(if (test.vendor == ExamCbtVendor.TCS_ION) Color(0xFF1D4ED8) else Color(0xFF0284C7)),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Computer, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = if (test.vendor == ExamCbtVendor.TCS_ION) "TCS iON CBT Assessment Console" else "Eduquity CBT Assessment Platform",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp
            )
            Text(
              text = if (test.vendor == ExamCbtVendor.TCS_ION)
                "Authentic Railways (RRB NTPC/ALP/Group D) console with candidate photo card, TCS standard shape palette, Question Paper viewer, and yellow digital clock."
              else
                "Authentic SSC (CGL/CHSL/CPO/MTS/GD) console with Eduquity candidate registration block, Save & Next, Mark for Review & Next, and official section tabs.",
              color = Color(0xFFE2E8F0),
              fontSize = 11.sp,
              lineHeight = 16.sp
            )
          }
        }
      }

      // 1. Exam Overview matching Screenshot 3
      Text(
        text = "1. Exam Overview / परीक्षा का संक्षिप्त विवरण",
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        modifier = Modifier.padding(bottom = 8.dp)
      )

      Column(
        modifier = Modifier.padding(start = 6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        InstructionBulletPoint(
          text = "Duration: ${test.durationMinutes} minutes / समयावधि: ${test.durationMinutes} मिनट"
        )
        InstructionBulletPoint(
          text = "Total Questions: ${test.totalQuestions} / कुल प्रश्न: ${test.totalQuestions}"
        )
        InstructionBulletPoint(
          text = "Negative Marking: 25% marks deducted for each wrong answer / ऋणात्मक अंकन: प्रत्येक गलत उत्तर पर 25% अंक कटेंगे"
        )
        InstructionBulletPoint(
          text = "Number of Sections displayed at any time: ${test.sections.size} / किसी भी समय पर प्रदर्शित अनुभाग: ${test.sections.size}"
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Section Table directly matching Screenshot 3!
      ExamSectionsTable(test = test)

      Spacer(modifier = Modifier.height(20.dp))

      // 2. Timing & Submission matching Screenshot 3
      Text(
        text = "2. Timing & Submission / समय और उत्तर जमा करना",
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        modifier = Modifier.padding(bottom = 8.dp)
      )

      Column(
        modifier = Modifier.padding(start = 6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        InstructionBulletPoint(
          text = "The clock will be set at the server. Countdown timer in top right displays time left."
        )
        InstructionBulletPoint(
          text = "When timer reaches zero, the examination will end by itself. You will not be required to manually end or submit."
        )
      }

      Spacer(modifier = Modifier.height(18.dp))

      // 3. Question Palette Color Legend
      Text(
        text = "3. Question Palette Symbols / प्रश्न पैलेट संकेत",
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        modifier = Modifier.padding(bottom = 8.dp)
      )

      Column(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.padding(start = 4.dp)
      ) {
        PaletteLegendItem(Color(0xFF10B981), "Answered", "आपने प्रश्न का उत्तर दिया है।")
        PaletteLegendItem(Color(0xFFEF4444), "Not Answered", "आपने प्रश्न का उत्तर नहीं दिया है।")
        PaletteLegendItem(Color(0xFF8B5CF6), "Marked for Review", "आपने उत्तर नहीं दिया, समीक्षा के लिए चिह्नित किया है।")
        PaletteLegendItem(Color(0xFF64748B), "Not Visited", "आप अभी तक इस प्रश्न पर नहीं गए हैं।")
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Declaration Checkbox
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(Color(0xFF282C3A))
          .border(1.dp, Color(0xFF383E52), RoundedCornerShape(8.dp))
          .clickable { isDeclarationChecked = !isDeclarationChecked }
          .padding(12.dp),
        verticalAlignment = Alignment.Top
      ) {
        Checkbox(
          checked = isDeclarationChecked,
          onCheckedChange = { isDeclarationChecked = it },
          colors = CheckboxDefaults.colors(
            checkedColor = Color(0xFF0E43AF),
            checkmarkColor = Color.White
          ),
          modifier = Modifier.testTag("declaration_checkbox")
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "I have read and understood all the above instructions. I undertake not to consult any unauthorized books or internet material during this test.",
          color = Color(0xFFE2E8F0),
          fontSize = 12.sp,
          lineHeight = 16.sp
        )
      }

      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}

@Composable
fun InstructionBulletPoint(text: String) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.Top
  ) {
    Text(
      text = "•  ",
      color = Color.White,
      fontSize = 14.sp,
      fontWeight = FontWeight.Bold
    )
    Text(
      text = text,
      color = Color(0xFFE2E8F0),
      fontSize = 13.sp,
      lineHeight = 18.sp
    )
  }
}

// Table directly matching Screenshot 3!
@Composable
fun ExamSectionsTable(test: MockTest) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .border(1.dp, Color(0xFF475569))
  ) {
    // Header Row
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(Color(0xFF272C3A))
        .padding(vertical = 8.dp, horizontal = 4.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Section",
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        modifier = Modifier.weight(0.9f)
      )
      Text(
        text = "Subject",
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        modifier = Modifier.weight(1.8f)
      )
      Text(
        text = "Number of Questions",
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        modifier = Modifier.weight(1.1f)
      )
      Text(
        text = "Maximum Marks",
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        modifier = Modifier.weight(1f)
      )
    }

    HorizontalDivider(color = Color(0xFF475569), thickness = 1.dp)

    // Data Rows
    test.sections.forEachIndexed { index, sec ->
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(if (index % 2 == 0) Color(0xFF1E222D) else Color(0xFF242836))
          .padding(vertical = 10.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = sec.id,
          color = Color(0xFFE2E8F0),
          fontWeight = FontWeight.SemiBold,
          fontSize = 12.sp,
          modifier = Modifier.weight(0.9f)
        )
        Text(
          text = sec.name,
          color = Color(0xFFE2E8F0),
          fontSize = 12.sp,
          modifier = Modifier.weight(1.8f)
        )
        Text(
          text = "${sec.questionCount}",
          color = Color(0xFF38BDF8),
          fontWeight = FontWeight.SemiBold,
          fontSize = 12.sp,
          modifier = Modifier.weight(1.1f)
        )
        Text(
          text = "${sec.maxMarks.toInt()}",
          color = Color(0xFF10B981),
          fontWeight = FontWeight.SemiBold,
          fontSize = 12.sp,
          modifier = Modifier.weight(1f)
        )
      }

      if (index < test.sections.size - 1) {
        HorizontalDivider(color = Color(0xFF334155), thickness = 0.8.dp)
      }
    }
  }
}

@Composable
fun PaletteLegendItem(color: Color, title: String, description: String) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .size(16.dp)
        .background(color, RoundedCornerShape(4.dp))
    )
    Spacer(modifier = Modifier.width(10.dp))
    Column {
      Text(
        text = title,
        color = Color.White,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold
      )
      Text(
        text = description,
        color = Color(0xFF94A3B8),
        fontSize = 11.sp
      )
    }
  }
}
