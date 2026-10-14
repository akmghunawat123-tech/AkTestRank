package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CustomMockTestEntity
import com.example.data.local.CustomQuestionEntity
import com.example.data.local.ManagedUserEntity
import com.example.data.model.ExamCategory
import com.example.ui.screens.admin.AdminDynamicLoginGate
import com.example.ui.screens.admin.AdminUserControlScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminQuestionStudioScreen(
  isAdminUnlocked: Boolean,
  onUnlockAdmin: (String) -> Boolean,
  onLockAdmin: () -> Unit,
  categories: List<ExamCategory>,
  customQuestions: List<CustomQuestionEntity>,
  customMockTests: List<CustomMockTestEntity>,
  managedUsers: List<ManagedUserEntity> = emptyList(),
  onUpdateUserStatus: (String, String) -> Unit = { _, _ -> },
  onToggleUserAccess: (String, Boolean) -> Unit = { _, _ -> },
  onUpdateUserRole: (String, String) -> Unit = { _, _ -> },
  onDeleteUser: (String) -> Unit = {},
  onCreateUser: (
    name: String,
    email: String,
    phone: String,
    targetExam: String,
    activePackage: String,
    status: String
  ) -> Unit = { _, _, _, _, _, _ -> },
  onCreateMockTest: (
    categoryId: String,
    categoryName: String,
    packageTitle: String,
    testTitle: String,
    durationMinutes: Int,
    totalQuestions: Int,
    totalMarks: Double,
    positiveMark: Double,
    negativeMark: Double,
    sectionsJoined: String,
    questionsSerialized: String
  ) -> Unit,
  onDeleteMockTest: (String) -> Unit,
  onSaveQuestion: (
    sectionId: String,
    testId: String,
    questionEn: String,
    questionHi: String,
    optionsEn: List<String>,
    optionsHi: List<String>,
    correctOptionIndex: Int,
    explanationEn: String,
    explanationHi: String,
    topic: String,
    difficulty: String
  ) -> Unit,
  onDeleteQuestion: (String) -> Unit,
  onPlayTest: (String) -> Unit,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  // If Admin is not unlocked, show Dynamic Login Gate
  if (!isAdminUnlocked) {
    AdminDynamicLoginGate(
      onUnlock = onUnlockAdmin,
      onBack = onBack,
      modifier = modifier
    )
    return
  }

  var selectedTab by remember { mutableStateOf(0) } // 0 = Build Mock Test, 1 = Create Question, 2 = Manage Tests, 3 = Users

  // State for Building Full Mock Test (All Exams)
  var selectedExamCategory by remember { mutableStateOf(categories.firstOrNull()?.id ?: "ssc") }
  var testPackageName by remember { mutableStateOf("Tier 1 Full Mock Series") }
  var testTitle by remember { mutableStateOf("All India Live Mega Mock Test 1") }
  var testDurationMinutes by remember { mutableStateOf("60") }
  var testTotalQuestions by remember { mutableStateOf("25") }
  var testPositiveMark by remember { mutableStateOf("2.0") }
  var testNegativeMark by remember { mutableStateOf("0.50") }

  var includeSectionA by remember { mutableStateOf(true) } // English
  var includeSectionB by remember { mutableStateOf(true) } // Reasoning
  var includeSectionC by remember { mutableStateOf(true) } // Quant
  var includeSectionD by remember { mutableStateOf(true) } // GK

  // Questions added to this mock test during creation
  val draftQuestions = remember { mutableStateListOf<DraftQuestion>() }
  var showTestCreatedDialog by remember { mutableStateOf(false) }

  // State for Creating Single Question
  var qSection by remember { mutableStateOf("PART-C") }
  var qTestTarget by remember { mutableStateOf("all") }
  var qTopic by remember { mutableStateOf("Percentages & Profit") }
  var qDifficulty by remember { mutableStateOf("Medium") }
  var qStatementEn by remember { mutableStateOf("If A's income is 25% more than B's income, by what percent is B's income less than A's?") }
  var qStatementHi by remember { mutableStateOf("यदि A की आय B की आय से 25% अधिक है, तो B की आय A की आय से कितने प्रतिशत कम है?") }
  var optAEn by remember { mutableStateOf("20%") }
  var optAHi by remember { mutableStateOf("20%") }
  var optBEn by remember { mutableStateOf("25%") }
  var optBHi by remember { mutableStateOf("25%") }
  var optCEn by remember { mutableStateOf("16.66%") }
  var optCHi by remember { mutableStateOf("16.66%") }
  var optDEn by remember { mutableStateOf("30%") }
  var optDHi by remember { mutableStateOf("30%") }
  var qCorrectIndex by remember { mutableStateOf(0) } // 20%
  var qExpEn by remember { mutableStateOf("Let B = 100. Then A = 125. Difference = 25. Percentage less = (25/125) * 100 = 20%.") }
  var qExpHi by remember { mutableStateOf("माना B = 100, तो A = 125। कमी = 25। प्रतिशत कमी = (25/125) * 100 = 20%।") }
  var showQuestionSavedNotice by remember { mutableStateOf(false) }

  Scaffold(
    modifier = modifier
      .fillMaxSize()
      .testTag("admin_question_studio_screen"),
    containerColor = Color(0xFF131620),
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
          IconButton(onClick = onBack, modifier = Modifier.testTag("admin_back_btn")) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
          }

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Admin Mock Test Studio",
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
            Text(
              text = "Authenticated Admin Mode • Full Control",
              fontSize = 11.sp,
              color = Color(0xFF34D399),
              fontWeight = FontWeight.Medium
            )
          }

          // Lock Admin button
          IconButton(onClick = onLockAdmin) {
            Icon(Icons.Default.Lock, contentDescription = "Lock Admin", tint = Color(0xFFEF4444))
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
      // Top Tabs: 1. Build Mock Test, 2. Create Question, 3. Manage All Tests, 4. Users Control
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color(0xFF1A1F2E))
          .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp)
      ) {
        AdminTabItem(
          title = "Build Mock",
          icon = Icons.Default.Quiz,
          isSelected = selectedTab == 0,
          modifier = Modifier.weight(1f)
        ) { selectedTab = 0 }

        AdminTabItem(
          title = "Question",
          icon = Icons.Default.AddCircle,
          isSelected = selectedTab == 1,
          modifier = Modifier.weight(1f)
        ) { selectedTab = 1 }

        AdminTabItem(
          title = "Tests (${customMockTests.size})",
          icon = Icons.Default.Folder,
          isSelected = selectedTab == 2,
          modifier = Modifier.weight(1f)
        ) { selectedTab = 2 }

        AdminTabItem(
          title = "Users (${managedUsers.size})",
          icon = Icons.Default.People,
          isSelected = selectedTab == 3,
          modifier = Modifier.weight(1f)
        ) { selectedTab = 3 }
      }

      when (selectedTab) {
        0 -> {
          // TAB 0: BUILD FULL MOCK TEST FOR ANY EXAM
          Column(
            modifier = Modifier
              .fillMaxSize()
              .verticalScroll(rememberScrollState())
              .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
          ) {
            if (showTestCreatedDialog) {
              Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF064E3B)),
                shape = RoundedCornerShape(8.dp)
              ) {
                Row(
                  modifier = Modifier.padding(12.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF34D399))
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = "Mock Test Published! It is now live in the exam package.",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                  )
                }
              }
            }

            // 1. Select Target Exam Category
            Text(
              text = "1. Select Target Exam Category (All Exams Supported):",
              color = Color.White,
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold
            )

            // Horizontal Category Selector Chips
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              categories.forEach { cat ->
                val isSel = cat.id == selectedExamCategory
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSel) Color(0xFF0E43AF) else Color(0xFF222634))
                    .border(1.dp, if (isSel) Color(0xFF38BDF8) else Color(0xFF333B50), RoundedCornerShape(8.dp))
                    .clickable { selectedExamCategory = cat.id }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                  Text(
                    text = cat.name,
                    color = if (isSel) Color.White else Color(0xFF94A3B8),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }
            }

            // 2. Test Details
            Card(
              modifier = Modifier.fillMaxWidth(),
              colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2333)),
              shape = RoundedCornerShape(12.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C354E))
            ) {
              Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Text(
                  text = "2. Test Configuration & Parameters:",
                  color = Color(0xFF38BDF8),
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                  value = testTitle,
                  onValueChange = { testTitle = it },
                  label = { Text("Mock Test Title", color = Color(0xFF94A3B8)) },
                  modifier = Modifier.fillMaxWidth(),
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF161A26),
                    unfocusedContainerColor = Color(0xFF161A26),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                  ),
                  shape = RoundedCornerShape(8.dp),
                  singleLine = true
                )

                OutlinedTextField(
                  value = testPackageName,
                  onValueChange = { testPackageName = it },
                  label = { Text("Package / Tier Name (e.g. Tier 1, CBT-1, Mains)", color = Color(0xFF94A3B8)) },
                  modifier = Modifier.fillMaxWidth(),
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF161A26),
                    unfocusedContainerColor = Color(0xFF161A26),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                  ),
                  shape = RoundedCornerShape(8.dp),
                  singleLine = true
                )

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  OutlinedTextField(
                    value = testDurationMinutes,
                    onValueChange = { testDurationMinutes = it },
                    label = { Text("Duration (Mins)", color = Color(0xFF94A3B8), fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                      focusedContainerColor = Color(0xFF161A26),
                      unfocusedContainerColor = Color(0xFF161A26),
                      focusedTextColor = Color.White,
                      unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                  )

                  OutlinedTextField(
                    value = testTotalQuestions,
                    onValueChange = { testTotalQuestions = it },
                    label = { Text("Total Qs", color = Color(0xFF94A3B8), fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                      focusedContainerColor = Color(0xFF161A26),
                      unfocusedContainerColor = Color(0xFF161A26),
                      focusedTextColor = Color.White,
                      unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                  )

                  OutlinedTextField(
                    value = testPositiveMark,
                    onValueChange = { testPositiveMark = it },
                    label = { Text("+ Mark", color = Color(0xFF94A3B8), fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                      focusedContainerColor = Color(0xFF161A26),
                      unfocusedContainerColor = Color(0xFF161A26),
                      focusedTextColor = Color.White,
                      unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                  )

                  OutlinedTextField(
                    value = testNegativeMark,
                    onValueChange = { testNegativeMark = it },
                    label = { Text("- Mark", color = Color(0xFF94A3B8), fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                      focusedContainerColor = Color(0xFF161A26),
                      unfocusedContainerColor = Color(0xFF161A26),
                      focusedTextColor = Color.White,
                      unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                  )
                }

                // 3. Sections Included Checkboxes
                Text("Sections Included:", color = Color(0xFFCBD5E1), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  FilterChip(
                    selected = includeSectionA,
                    onClick = { includeSectionA = !includeSectionA },
                    label = { Text("PART-A (Eng)", fontSize = 11.sp) }
                  )
                  FilterChip(
                    selected = includeSectionB,
                    onClick = { includeSectionB = !includeSectionB },
                    label = { Text("PART-B (Reas)", fontSize = 11.sp) }
                  )
                  FilterChip(
                    selected = includeSectionC,
                    onClick = { includeSectionC = !includeSectionC },
                    label = { Text("PART-C (Quant)", fontSize = 11.sp) }
                  )
                  FilterChip(
                    selected = includeSectionD,
                    onClick = { includeSectionD = !includeSectionD },
                    label = { Text("PART-D (GK)", fontSize = 11.sp) }
                  )
                }
              }
            }

            // 4. Questions added count & Quick Templates
            Card(
              modifier = Modifier.fillMaxWidth(),
              colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2333)),
              shape = RoundedCornerShape(12.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C354E))
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = "Questions in this Test: ${draftQuestions.size}",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                  )

                  TextButton(
                    onClick = {
                      // Auto-populate 5 high-yield TCS pattern questions to make test immediately playable
                      val sampleQuestions = listOf(
                        DraftQuestion(
                          sectionId = "PART-C",
                          qEn = "A car covers 300 km in 5 hours. What is its speed in m/s?",
                          qHi = "एक कार 5 घंटे में 300 किमी दूरी तय करती है। मी/से में इसकी गति क्या है?",
                          optsEn = listOf("15.5 m/s", "16.66 m/s", "18.2 m/s", "20 m/s"),
                          optsHi = listOf("15.5 मी/से", "16.66 मी/से", "18.2 मी/से", "20 मी/से"),
                          corrIdx = 1,
                          expEn = "Speed = 300/5 = 60 km/h. In m/s = 60 * 5/18 = 16.66 m/s.",
                          expHi = "गति = 300/5 = 60 किमी/घंटा = 60 * 5/18 = 16.66 मी/से।"
                        ),
                        DraftQuestion(
                          sectionId = "PART-B",
                          qEn = "If BOOK is coded as 43, what is the code for PEN?",
                          qHi = "यदि BOOK को 43 के रूप में कूटबद्ध किया जाता है, तो PEN का कूट क्या होगा?",
                          optsEn = listOf("32", "35", "38", "40"),
                          optsHi = listOf("32", "35", "38", "40"),
                          corrIdx = 1,
                          expEn = "Sum of letters: P(16) + E(5) + N(14) = 35.",
                          expHi = "अक्षरों का योग: P(16) + E(5) + N(14) = 35।"
                        ),
                        DraftQuestion(
                          sectionId = "PART-A",
                          qEn = "Choose the synonym of 'CANDID':",
                          qHi = "'CANDID' का पर्यायवाची चुनें:",
                          optsEn = listOf("Secretive", "Frank", "Deceitful", "Shy"),
                          optsHi = listOf("गोपनीय", "स्पष्ट / खरा (Frank)", "कपटी", "शर्मीला"),
                          corrIdx = 1,
                          expEn = "Candid means truthful and straightforward; Frank.",
                          expHi = "Candid का अर्थ है खरा या स्पष्टवादी (Frank)।"
                        ),
                        DraftQuestion(
                          sectionId = "PART-D",
                          qEn = "Who was the first President of Independent India?",
                          qHi = "स्वतंत्र भारत के प्रथम राष्ट्रपति कौन थे?",
                          optsEn = listOf("Dr. Rajendra Prasad", "Dr. S. Radhakrishnan", "Jawaharlal Nehru", "Sardar Patel"),
                          optsHi = listOf("डॉ. राजेंद्र प्रसाद", "डॉ. एस. राधाकृष्णन", "जवाहरलाल नेहरू", "सरदार पटेल"),
                          corrIdx = 0,
                          expEn = "Dr. Rajendra Prasad served as the first President from 1950 to 1962.",
                          expHi = "डॉ. राजेंद्र प्रसाद 1950 से 1962 तक भारत के प्रथम राष्ट्रपति रहे।"
                        )
                      )
                      draftQuestions.addAll(sampleQuestions)
                    }
                  ) {
                    Text("+ Auto-Add 4 Sample Qs", color = Color(0xFF38BDF8), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                  }
                }

                if (draftQuestions.isNotEmpty()) {
                  Column(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                  ) {
                    draftQuestions.forEachIndexed { index, dq ->
                      Row(
                        modifier = Modifier
                          .fillMaxWidth()
                          .clip(RoundedCornerShape(6.dp))
                          .background(Color(0xFF242A3C))
                          .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        Text(
                          text = "Q${index + 1} (${dq.sectionId}): ${dq.qEn.take(38)}...",
                          color = Color.White,
                          fontSize = 11.sp,
                          modifier = Modifier.weight(1f)
                        )
                        IconButton(
                          onClick = { draftQuestions.removeAt(index) },
                          modifier = Modifier.size(24.dp)
                        ) {
                          Icon(Icons.Default.Close, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                        }
                      }
                    }
                  }
                } else {
                  Text(
                    text = "Note: If no custom questions are added, default syllabus questions will automatically be loaded so students can take the test immediately.",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 4.dp)
                  )
                }
              }
            }

            // Publish Full Mock Test Button
            Button(
              onClick = {
                val qCount = testTotalQuestions.toIntOrNull() ?: 25
                val dur = testDurationMinutes.toIntOrNull() ?: 60
                val pos = testPositiveMark.toDoubleOrNull() ?: 2.0
                val neg = testNegativeMark.toDoubleOrNull() ?: 0.50
                val marks = qCount * pos

                val activeSections = mutableListOf<String>()
                if (includeSectionA) activeSections.add("PART-A:English Comprehension")
                if (includeSectionB) activeSections.add("PART-B:General Intelligence and Reasoning")
                if (includeSectionC) activeSections.add("PART-C:Quantitative Aptitude")
                if (includeSectionD) activeSections.add("PART-D:General Awareness")
                val sectionsJoined = activeSections.joinToString("|")

                // Serialize draft questions
                val serializedQuestions = draftQuestions.mapIndexed { idx, dq ->
                  "q_${System.currentTimeMillis()}_$idx;;;${dq.sectionId};;;${idx + 1};;;${dq.qEn};;;${dq.qHi};;;${dq.optsEn.joinToString("|||")};;;${dq.optsHi.joinToString("|||")};;;${dq.corrIdx};;;${dq.expEn};;;${dq.expHi};;;General;;;Medium"
                }.joinToString("###")

                val catObj = categories.find { it.id == selectedExamCategory }
                val catName = catObj?.name ?: selectedExamCategory.uppercase()

                onCreateMockTest(
                  selectedExamCategory,
                  catName,
                  testPackageName,
                  testTitle,
                  dur,
                  qCount,
                  marks,
                  pos,
                  neg,
                  sectionsJoined,
                  serializedQuestions
                )

                showTestCreatedDialog = true
                draftQuestions.clear()
              },
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("admin_publish_mock_test_btn"),
              colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF10B981),
                contentColor = Color.White
              ),
              shape = RoundedCornerShape(8.dp)
            ) {
              Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text("Publish Full Mock Test to $selectedExamCategory.uppercase()", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))
          }
        }

        1 -> {
          // TAB 1: CREATE SINGLE QUESTION
          Column(
            modifier = Modifier
              .fillMaxSize()
              .verticalScroll(rememberScrollState())
              .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
          ) {
            if (showQuestionSavedNotice) {
              Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF064E3B)),
                shape = RoundedCornerShape(8.dp)
              ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF34D399))
                  Spacer(modifier = Modifier.width(8.dp))
                  Text("Question saved to global question bank!", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
              }
            }

            Text("Section & Subject:", color = Color(0xFFCBD5E1), fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              listOf("PART-A" to "English", "PART-B" to "Reasoning", "PART-C" to "Quant", "PART-D" to "GK").forEach { (secId, label) ->
                val isSel = secId == qSection
                Box(
                  modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSel) Color(0xFF0E43AF) else Color(0xFF22283A))
                    .clickable { qSection = secId }
                    .padding(vertical = 8.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(secId, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    Text(label, color = if (isSel) Color(0xFF38BDF8) else Color(0xFF94A3B8), fontSize = 10.sp)
                  }
                }
              }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              OutlinedTextField(
                value = qTopic,
                onValueChange = { qTopic = it },
                label = { Text("Topic / Chapter", color = Color(0xFF94A3B8)) },
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedContainerColor = Color(0xFF1E2333),
                  unfocusedContainerColor = Color(0xFF1E2333),
                  focusedTextColor = Color.White,
                  unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
              )
              OutlinedTextField(
                value = qDifficulty,
                onValueChange = { qDifficulty = it },
                label = { Text("Difficulty", color = Color(0xFF94A3B8)) },
                modifier = Modifier.weight(0.7f),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedContainerColor = Color(0xFF1E2333),
                  unfocusedContainerColor = Color(0xFF1E2333),
                  focusedTextColor = Color.White,
                  unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
              )
            }

            OutlinedTextField(
              value = qStatementEn,
              onValueChange = { qStatementEn = it },
              label = { Text("Question Statement (English)", color = Color(0xFF94A3B8)) },
              modifier = Modifier.fillMaxWidth().heightIn(min = 80.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF1E2333),
                unfocusedContainerColor = Color(0xFF1E2333),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
              ),
              shape = RoundedCornerShape(8.dp)
            )

            OutlinedTextField(
              value = qStatementHi,
              onValueChange = { qStatementHi = it },
              label = { Text("Question Statement (हिन्दी)", color = Color(0xFF94A3B8)) },
              modifier = Modifier.fillMaxWidth().heightIn(min = 80.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF1E2333),
                unfocusedContainerColor = Color(0xFF1E2333),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
              ),
              shape = RoundedCornerShape(8.dp)
            )

            Text("Options (Radio button marks the correct answer):", color = Color(0xFFCBD5E1), fontSize = 13.sp, fontWeight = FontWeight.Bold)

            AdminOptionInput("A", optAEn, optAHi, { optAEn = it }, { optAHi = it }, qCorrectIndex == 0) { qCorrectIndex = 0 }
            AdminOptionInput("B", optBEn, optBHi, { optBEn = it }, { optBHi = it }, qCorrectIndex == 1) { qCorrectIndex = 1 }
            AdminOptionInput("C", optCEn, optCHi, { optCEn = it }, { optCHi = it }, qCorrectIndex == 2) { qCorrectIndex = 2 }
            AdminOptionInput("D", optDEn, optDHi, { optDEn = it }, { optDHi = it }, qCorrectIndex == 3) { qCorrectIndex = 3 }

            OutlinedTextField(
              value = qExpEn,
              onValueChange = { qExpEn = it },
              label = { Text("Detailed Explanation (English)", color = Color(0xFF94A3B8)) },
              modifier = Modifier.fillMaxWidth().heightIn(min = 70.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF1E2333),
                unfocusedContainerColor = Color(0xFF1E2333),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
              ),
              shape = RoundedCornerShape(8.dp)
            )

            Button(
              onClick = {
                if (qStatementEn.isNotBlank()) {
                  onSaveQuestion(
                    qSection,
                    qTestTarget,
                    qStatementEn,
                    qStatementHi,
                    listOf(optAEn, optBEn, optCEn, optDEn),
                    listOf(optAHi, optBHi, optCHi, optDHi),
                    qCorrectIndex,
                    qExpEn,
                    qExpHi,
                    qTopic,
                    qDifficulty
                  )
                  showQuestionSavedNotice = true
                }
              },
              modifier = Modifier.fillMaxWidth().height(48.dp),
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
              shape = RoundedCornerShape(8.dp)
            ) {
              Text("Publish Question to Question Bank", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))
          }
        }

        2 -> {
          // TAB 2: MANAGE CREATED TESTS & QUESTION BANK
          Column(
            modifier = Modifier
              .fillMaxSize()
              .verticalScroll(rememberScrollState())
              .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
          ) {
            Text(
              text = "Custom Mock Tests (${customMockTests.size})",
              color = Color.White,
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold
            )

            if (customMockTests.isEmpty()) {
              Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2333))
              ) {
                Text(
                  text = "No custom mock tests created yet. Use 'Build Mock Test' tab to create tests for any exam.",
                  color = Color(0xFF94A3B8),
                  fontSize = 12.sp,
                  modifier = Modifier.padding(14.dp)
                )
              }
            } else {
              customMockTests.forEach { test ->
                Card(
                  modifier = Modifier.fillMaxWidth(),
                  colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2333)),
                  shape = RoundedCornerShape(10.dp),
                  border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C3852))
                ) {
                  Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Box(
                        modifier = Modifier
                          .clip(RoundedCornerShape(4.dp))
                          .background(Color(0xFF0E43AF))
                          .padding(horizontal = 8.dp, vertical = 2.dp)
                      ) {
                        Text(test.categoryName.uppercase(), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                      }

                      IconButton(
                        onClick = { onDeleteMockTest(test.id) },
                        modifier = Modifier.size(28.dp)
                      ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                      }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                      text = test.testTitle,
                      color = Color.White,
                      fontSize = 14.sp,
                      fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                      text = "${test.packageTitle} • ${test.totalQuestions} Qs • ${test.durationMinutes} Mins • ${test.totalMarks.toInt()} Marks",
                      color = Color(0xFF38BDF8),
                      fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                      onClick = { onPlayTest(test.id) },
                      colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                      shape = RoundedCornerShape(6.dp),
                      modifier = Modifier.fillMaxWidth().height(36.dp)
                    ) {
                      Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                      Spacer(modifier = Modifier.width(6.dp))
                      Text("Launch & Test Live Now", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
              text = "Custom Questions Bank (${customQuestions.size})",
              color = Color.White,
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold
            )

            if (customQuestions.isNotEmpty()) {
              customQuestions.forEach { q ->
                Card(
                  modifier = Modifier.fillMaxWidth(),
                  colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1F2E)),
                  shape = RoundedCornerShape(8.dp)
                ) {
                  Row(
                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Column(modifier = Modifier.weight(1f)) {
                      Text(text = "${q.sectionId}: ${q.topic}", color = Color(0xFF38BDF8), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                      Text(text = q.questionEn, color = Color.White, fontSize = 12.sp, maxLines = 1)
                    }
                    IconButton(
                      onClick = { onDeleteQuestion(q.id) },
                      modifier = Modifier.size(24.dp)
                    ) {
                      Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                    }
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(16.dp))
          }
        }

        3 -> {
          // TAB 3: USER CONTROL & ACTIVATION
          AdminUserControlScreen(
            users = managedUsers,
            onUpdateUserStatus = onUpdateUserStatus,
            onToggleUserAccess = onToggleUserAccess,
            onUpdateUserRole = onUpdateUserRole,
            onDeleteUser = onDeleteUser,
            onCreateUser = onCreateUser
          )
        }
      }
    }
  }
}

@Composable
fun AdminTabItem(
  title: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  isSelected: Boolean,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(8.dp))
      .background(if (isSelected) Color(0xFF0E43AF) else Color.Transparent)
      .clickable(onClick = onClick)
      .padding(vertical = 8.dp),
    contentAlignment = Alignment.Center
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = if (isSelected) Color.White else Color(0xFF94A3B8),
        modifier = Modifier.size(15.dp)
      )
      Spacer(modifier = Modifier.width(4.dp))
      Text(
        text = title,
        color = if (isSelected) Color.White else Color(0xFF94A3B8),
        fontSize = 11.sp,
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
      )
    }
  }
}

@Composable
fun AdminOptionInput(
  label: String,
  valEn: String,
  valHi: String,
  onEnChange: (String) -> Unit,
  onHiChange: (String) -> Unit,
  isCorrect: Boolean,
  onSelectCorrect: () -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(
      containerColor = if (isCorrect) Color(0xFF0F3277) else Color(0xFF1E2333)
    ),
    shape = RoundedCornerShape(8.dp),
    border = androidx.compose.foundation.BorderStroke(
      1.dp,
      if (isCorrect) Color(0xFF38BDF8) else Color(0xFF2C354E)
    )
  ) {
    Column(modifier = Modifier.padding(10.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(
          selected = isCorrect,
          onClick = onSelectCorrect,
          colors = RadioButtonDefaults.colors(
            selectedColor = Color(0xFF10B981),
            unselectedColor = Color(0xFF64748B)
          ),
          modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Option $label " + if (isCorrect) "(Correct Answer)" else "",
          color = if (isCorrect) Color(0xFF10B981) else Color.White,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold
        )
      }

      Spacer(modifier = Modifier.height(6.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        OutlinedTextField(
          value = valEn,
          onValueChange = onEnChange,
          placeholder = { Text("English text", color = Color(0xFF64748B), fontSize = 12.sp) },
          modifier = Modifier.weight(1f),
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color(0xFF161A26),
            unfocusedContainerColor = Color(0xFF161A26),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
          ),
          shape = RoundedCornerShape(6.dp),
          singleLine = true
        )

        OutlinedTextField(
          value = valHi,
          onValueChange = onHiChange,
          placeholder = { Text("हिन्दी टेक्स्ट", color = Color(0xFF64748B), fontSize = 12.sp) },
          modifier = Modifier.weight(1f),
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color(0xFF161A26),
            unfocusedContainerColor = Color(0xFF161A26),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
          ),
          shape = RoundedCornerShape(6.dp),
          singleLine = true
        )
      }
    }
  }
}

data class DraftQuestion(
  val sectionId: String,
  val qEn: String,
  val qHi: String,
  val optsEn: List<String>,
  val optsHi: List<String>,
  val corrIdx: Int,
  val expEn: String,
  val expHi: String
)
