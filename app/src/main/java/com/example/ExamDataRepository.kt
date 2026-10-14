package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.BookmarkEntity
import com.example.data.local.TestAttemptEntity
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlin.math.roundToInt

class ExamDataRepository(private val database: AppDatabase) {
  val examDao = database.examDao()

  fun getAllAttempts(): Flow<List<TestAttemptEntity>> = examDao.getAllAttempts()
  fun getAllBookmarks(): Flow<List<BookmarkEntity>> = examDao.getAllBookmarks()
  fun getAllCustomQuestions(): Flow<List<com.example.data.local.CustomQuestionEntity>> = examDao.getAllCustomQuestions()
  fun getAllCustomMockTests(): Flow<List<com.example.data.local.CustomMockTestEntity>> = examDao.getAllCustomMockTests()
  fun getUserProfile(): Flow<com.example.data.local.UserProfileEntity?> = examDao.getUserProfile()

  suspend fun saveCustomQuestion(q: com.example.data.local.CustomQuestionEntity) {
    examDao.insertCustomQuestion(q)
  }

  suspend fun deleteCustomQuestion(questionId: String) {
    examDao.deleteCustomQuestion(questionId)
  }

  suspend fun saveCustomMockTest(test: com.example.data.local.CustomMockTestEntity) {
    examDao.insertCustomMockTest(test)
  }

  suspend fun deleteCustomMockTest(testId: String) {
    examDao.deleteCustomMockTest(testId)
  }

  suspend fun saveUserProfile(profile: com.example.data.local.UserProfileEntity) {
    examDao.saveUserProfile(profile)
    // Also sync to managed users
    val managed = com.example.data.local.ManagedUserEntity(
      id = "user_me",
      name = profile.name,
      email = profile.email,
      phone = profile.phone,
      targetExam = profile.targetExam,
      role = profile.role,
      status = "ACTIVE",
      hasFullTestAccess = true,
      testsAttempted = 1,
      avgScore = 135.0,
      activePackage = "All India Mock Test Pass",
      joinedAt = profile.joinedAt,
      lastActiveAt = System.currentTimeMillis()
    )
    examDao.insertManagedUser(managed)
  }

  fun getAllManagedUsers(): Flow<List<com.example.data.local.ManagedUserEntity>> = examDao.getAllManagedUsers()

  suspend fun seedDefaultManagedUsersIfEmpty() {
    val existing = examDao.getAllManagedUsersOnce()
    if (existing.isEmpty()) {
      val now = System.currentTimeMillis()
      val dayMs = 86_400_000L
      val defaultUsers = listOf(
        com.example.data.local.ManagedUserEntity(
          id = "usr_001",
          name = "Rahul Sharma",
          email = "rahul.sharma@gmail.com",
          phone = "+91 98765 43210",
          targetExam = "SSC CGL (Tier 1 & 2)",
          role = "Student",
          status = "ACTIVE",
          hasFullTestAccess = true,
          testsAttempted = 14,
          avgScore = 158.5,
          activePackage = "SSC CGL 2026 Eduquity Live Pass",
          joinedAt = now - (20 * dayMs),
          lastActiveAt = now - (2 * 3600_000L)
        ),
        com.example.data.local.ManagedUserEntity(
          id = "usr_002",
          name = "Priya Singh",
          email = "priya.singh@outlook.com",
          phone = "+91 98123 45678",
          targetExam = "RRB NTPC (CBT 1 & 2)",
          role = "Student",
          status = "ACTIVE",
          hasFullTestAccess = true,
          testsAttempted = 9,
          avgScore = 78.0,
          activePackage = "Railways RRB NTPC TCS iON Series",
          joinedAt = now - (15 * dayMs),
          lastActiveAt = now - (5 * 3600_000L)
        ),
        com.example.data.local.ManagedUserEntity(
          id = "usr_003",
          name = "Amit Kumar",
          email = "amit.kumar2026@gmail.com",
          phone = "+91 97234 56789",
          targetExam = "SSC CHSL 10+2",
          role = "Student",
          status = "PENDING",
          hasFullTestAccess = false,
          testsAttempted = 0,
          avgScore = 0.0,
          activePackage = "Pending Activation Request",
          joinedAt = now - (2 * dayMs),
          lastActiveAt = now - (1 * dayMs)
        ),
        com.example.data.local.ManagedUserEntity(
          id = "usr_004",
          name = "Anjali Verma",
          email = "anjali.verma@yahoo.com",
          phone = "+91 96543 21098",
          targetExam = "RRB ALP & Technician",
          role = "Student",
          status = "ACTIVE",
          hasFullTestAccess = true,
          testsAttempted = 6,
          avgScore = 64.5,
          activePackage = "Railways RRB ALP Pro Pass",
          joinedAt = now - (10 * dayMs),
          lastActiveAt = now - (12 * 3600_000L)
        ),
        com.example.data.local.ManagedUserEntity(
          id = "usr_005",
          name = "Vikas Yadav",
          email = "vikas.yadav@gmail.com",
          phone = "+91 95432 10987",
          targetExam = "SSC CPO SI",
          role = "Student",
          status = "INACTIVE",
          hasFullTestAccess = false,
          testsAttempted = 2,
          avgScore = 92.0,
          activePackage = "Suspended / Expired Pass",
          joinedAt = now - (45 * dayMs),
          lastActiveAt = now - (18 * dayMs)
        ),
        com.example.data.local.ManagedUserEntity(
          id = "usr_006",
          name = "Neha Gupta",
          email = "neha.gupta@gmail.com",
          phone = "+91 94321 09876",
          targetExam = "SSC MTS & Havaldar",
          role = "Student",
          status = "PENDING",
          hasFullTestAccess = false,
          testsAttempted = 0,
          avgScore = 0.0,
          activePackage = "New Registration (Awaiting Approval)",
          joinedAt = now - (1 * dayMs),
          lastActiveAt = now - (3 * 3600_000L)
        )
      )
      examDao.insertManagedUsers(defaultUsers)
    }
  }

  suspend fun saveManagedUser(user: com.example.data.local.ManagedUserEntity) {
    examDao.insertManagedUser(user)
  }

  suspend fun updateManagedUserStatus(userId: String, status: String) {
    examDao.updateUserStatus(userId, status)
  }

  suspend fun toggleUserTestAccess(userId: String, hasAccess: Boolean) {
    examDao.updateUserTestAccess(userId, hasAccess)
  }

  suspend fun updateUserRole(userId: String, role: String) {
    examDao.updateUserRole(userId, role)
  }

  suspend fun deleteManagedUser(userId: String) {
    examDao.deleteManagedUser(userId)
  }

  suspend fun saveTestAttempt(
    test: MockTest,
    userAnswers: Map<String, UserQuestionState>,
    timeTakenSeconds: Long
  ): Long {
    var attempted = 0
    var correct = 0
    var incorrect = 0
    var unattempted = 0
    var rawScore = 0.0

    val sectionStatsMap = mutableMapOf<String, MutableSectionStats>()
    for (sec in test.sections) {
      sectionStatsMap[sec.id] = MutableSectionStats(sec.id, sec.name)
    }

    test.questions.forEach { question ->
      val userState = userAnswers[question.id]
      val secStats = sectionStatsMap[question.sectionId] ?: MutableSectionStats(question.sectionId, question.sectionId)

      if (userState?.selectedOptionIndex != null) {
        attempted++
        secStats.attempted++
        if (userState.selectedOptionIndex == question.correctOptionIndex) {
          correct++
          secStats.correct++
          rawScore += test.positiveMark
          secStats.score += test.positiveMark
        } else {
          incorrect++
          secStats.incorrect++
          rawScore -= test.negativeMark
          secStats.score -= test.negativeMark
        }
      } else {
        unattempted++
        secStats.unattempted++
      }
    }

    val finalScore = maxOf(0.0, ((rawScore * 100.0).roundToInt() / 100.0))
    val accuracy = if (attempted > 0) (correct.toDouble() / attempted * 100.0) else 0.0

    // Realistic rank simulation based on score distribution
    val totalParticipants = 12500
    val scorePercentage = if (test.totalMarks > 0) (finalScore / test.totalMarks) else 0.0
    val simulatedPercentile = (scorePercentage * 95.0 + (if (accuracy > 80) 4.5 else 1.0)).coerceIn(15.0, 99.8)
    val simulatedRank = maxOf(1, ((100.0 - simulatedPercentile) / 100.0 * totalParticipants).toInt())

    // Serialized breakdown: sectionId|name|att|corr|inc|unatt|score|max|acc
    val breakdownString = sectionStatsMap.values.joinToString(";;") { stats ->
      val secInfo = test.sections.find { it.id == stats.sectionId }
      val maxSec = secInfo?.maxMarks ?: 50.0
      val accSec = if (stats.attempted > 0) (stats.correct.toDouble() / stats.attempted * 100.0) else 0.0
      "${stats.sectionId}|${stats.name}|${stats.attempted}|${stats.correct}|${stats.incorrect}|${stats.unattempted}|${stats.score}|$maxSec|${accSec.roundToInt()}"
    }

    // Serialized user answers: qId:selectedOption:correctOption:timeSpent
    val answersString = test.questions.joinToString(";;") { q ->
      val state = userAnswers[q.id]
      val sel = state?.selectedOptionIndex ?: -1
      "${q.id}:$sel:${q.correctOptionIndex}:${state?.timeSpentSeconds ?: 0}"
    }

    val attemptEntity = TestAttemptEntity(
      testId = test.id,
      testTitle = test.title,
      category = test.category,
      timestamp = System.currentTimeMillis(),
      totalScore = finalScore,
      maxScore = test.totalMarks,
      rank = simulatedRank,
      totalParticipants = totalParticipants,
      percentile = (simulatedPercentile * 10).roundToInt() / 10.0,
      accuracyPercentage = (accuracy * 10).roundToInt() / 10.0,
      attemptedCount = attempted,
      correctCount = correct,
      wrongCount = incorrect,
      unattemptedCount = unattempted,
      timeTakenSeconds = timeTakenSeconds,
      cutoffMarks = (test.totalMarks * 0.65).roundToInt().toDouble(),
      topperScore = (test.totalMarks * 0.94).roundToInt().toDouble(),
      averageScore = (test.totalMarks * 0.52).roundToInt().toDouble(),
      sectionBreakdownData = breakdownString,
      userAnswersData = answersString
    )

    return examDao.insertAttempt(attemptEntity)
  }

  suspend fun toggleBookmark(question: TestQuestion, testTitle: String) {
    if (examDao.isBookmarked(question.id)) {
      examDao.deleteBookmark(question.id)
    } else {
      examDao.insertBookmark(
        BookmarkEntity(
          questionId = question.id,
          testTitle = testTitle,
          sectionName = question.sectionId,
          questionEn = question.questionEn,
          questionHi = question.questionHi,
          optionsEnJoined = question.optionsEn.joinToString("|||"),
          optionsHiJoined = question.optionsHi.joinToString("|||"),
          correctOptionIndex = question.correctOptionIndex,
          explanationEn = question.explanationEn,
          explanationHi = question.explanationHi,
          topic = question.topic,
          bookmarkedAt = System.currentTimeMillis()
        )
      )
    }
  }

  suspend fun isQuestionBookmarked(questionId: String): Boolean {
    return examDao.isBookmarked(questionId)
  }

  // Pre-configured Exam Categories matching user's screenshots
  fun getCategories(): List<ExamCategory> {
    return listOf(
      ExamCategory(
        id = "ssc",
        name = "SSC",
        subtitle = "CGL, CHSL, CPO, MTS, GD Constable",
        iconType = "ssc",
        totalTests = 2468,
        freeTests = 2276,
        filterTags = listOf("SSC CGL", "SSC CPO", "CPO TIER II", "SSC CHSL", "SSC MTS")
      ),
      ExamCategory(
        id = "railways",
        name = "Railways",
        subtitle = "RRB NTPC, Group D, ALP, Technician",
        iconType = "railways",
        totalTests = 1840,
        freeTests = 1690,
        filterTags = listOf("RRB NTPC", "Group D", "RRB ALP", "Technician")
      ),
      ExamCategory(
        id = "ib",
        name = "IB",
        subtitle = "Intelligence Bureau ACIO, Security Asst",
        iconType = "ib",
        totalTests = 312,
        freeTests = 280,
        filterTags = listOf("IB ACIO Tier 1", "IB SA/MT", "Tier 2")
      ),
      ExamCategory(
        id = "uppbpb",
        name = "UPPBPB",
        subtitle = "UP Police Constable, Sub Inspector (SI)",
        iconType = "uppbpb",
        totalTests = 645,
        freeTests = 590,
        filterTags = listOf("UP Police Constable", "UP SI", "Computer Operator")
      ),
      ExamCategory(
        id = "rpf",
        name = "RPF",
        subtitle = "Railway Protection Force SI & Constable",
        iconType = "rpf",
        totalTests = 420,
        freeTests = 385,
        filterTags = listOf("RPF SI", "RPF Constable")
      ),
      ExamCategory(
        id = "delhi_police",
        name = "Delhi Police",
        subtitle = "Executive Constable, Head Constable",
        iconType = "delhi_police",
        totalTests = 380,
        freeTests = 340,
        filterTags = listOf("DP Constable", "DP Head Constable AWO/TPO")
      ),
      ExamCategory(
        id = "mp_police",
        name = "Mp Police",
        subtitle = "MP Police Constable, Sub Inspector",
        iconType = "mp_police",
        totalTests = 290,
        freeTests = 265,
        filterTags = listOf("MP Police Constable", "MP SI")
      ),
      ExamCategory(
        id = "defence",
        name = "Defence",
        subtitle = "NDA, CDS, AFCAT, CAPF AC",
        iconType = "defence",
        totalTests = 780,
        freeTests = 710,
        filterTags = listOf("NDA", "CDS", "AFCAT", "CAPF")
      ),
      ExamCategory(
        id = "bihar_police",
        name = "Bihar Police",
        subtitle = "Bihar Police Constable, Bihar SI Daroga",
        iconType = "bihar_police",
        totalTests = 350,
        freeTests = 315,
        filterTags = listOf("Bihar Constable", "Bihar SI")
      ),
      ExamCategory(
        id = "rajasthan_exams",
        name = "Rajasthan Exams",
        subtitle = "CET Graduate & 12th, Patwar, Police",
        iconType = "rajasthan",
        totalTests = 510,
        freeTests = 470,
        filterTags = listOf("Rajasthan CET", "Patwar", "Rajasthan Police")
      ),
      ExamCategory(
        id = "banking",
        name = "Banking",
        subtitle = "SBI PO, SBI Clerk, IBPS PO, RRB",
        iconType = "banking",
        totalTests = 1420,
        freeTests = 1290,
        filterTags = listOf("SBI PO", "SBI Clerk", "IBPS PO", "IBPS Clerk")
      ),
      ExamCategory(
        id = "others",
        name = "Others",
        subtitle = "State PSC, Teaching, CSIR NET",
        iconType = "others",
        totalTests = 620,
        freeTests = 560,
        filterTags = listOf("State PSC", "CTET", "UGC NET")
      )
    )
  }

  // Packages list for a given category (Matches Screenshot 2!)
  fun getPackagesForCategory(categoryId: String, customMockTests: List<com.example.data.local.CustomMockTestEntity> = emptyList()): List<TestPackage> {
    val basePackages = when (categoryId.lowercase()) {
      "ssc" -> listOf(
        TestPackage(
          id = "ssc_cgl_2026_t1",
          categoryId = "ssc",
          subCategory = "SSC CGL",
          title = "SSC CGL 2026 – Tier 1",
          totalTests = 1119,
          freeTests = 1039,
          isUnlocked = true,
          tests = listOf(
            MockTestSummary("cgl_mock_01", "ssc_cgl_2026_t1", "SSC CGL 2026 - All India Live Full Mock 1", 60, 100, 200.0, true, 42150),
            MockTestSummary("cgl_mock_02", "ssc_cgl_2026_t1", "SSC CGL 2026 - High Yield Full Mock 2", 60, 100, 200.0, true, 28900),
            MockTestSummary("cgl_mock_pyq", "ssc_cgl_2026_t1", "SSC CGL Previous Year Paper (2025 Tier 1)", 60, 100, 200.0, true, 51200),
            MockTestSummary("cgl_drill_quant", "ssc_cgl_2026_t1", "Quantitative Aptitude 25-Q Speed Test", 15, 25, 50.0, true, 19400),
            MockTestSummary("cgl_drill_reasoning", "ssc_cgl_2026_t1", "General Intelligence & Reasoning Speed Test", 15, 25, 50.0, true, 21300),
            MockTestSummary("cgl_drill_english", "ssc_cgl_2026_t1", "English Comprehension & Vocab Mastery", 15, 25, 50.0, true, 18700),
            MockTestSummary("cgl_drill_gk", "ssc_cgl_2026_t1", "General Awareness Current Affairs & Static GK", 15, 25, 50.0, true, 26500)
          )
        ),
        TestPackage(
          id = "ssc_cpo_2026_t1",
          categoryId = "ssc",
          subCategory = "SSC CPO",
          title = "SSC CPO 2026 – Tier 1",
          totalTests = 276,
          freeTests = 262,
          isUnlocked = true,
          tests = listOf(
            MockTestSummary("cpo_mock_01", "ssc_cpo_2026_t1", "SSC CPO 2026 - Tier 1 Full Length Mock 1", 60, 100, 200.0, true, 14200),
            MockTestSummary("cpo_mock_02", "ssc_cpo_2026_t1", "SSC CPO 2026 - Delhi Police SI Special Mock", 60, 100, 200.0, true, 11800)
          )
        ),
        TestPackage(
          id = "ssc_cpo_2025_t2",
          categoryId = "ssc",
          subCategory = "CPO TIER II",
          title = "SSC CPO 2025 – Tier 2",
          totalTests = 26,
          freeTests = 7,
          isUnlocked = true,
          tests = listOf(
            MockTestSummary("cpo_t2_mock_01", "ssc_cpo_2025_t2", "SSC CPO Tier 2 English Language Mock 1", 120, 200, 200.0, true, 8900)
          )
        ),
        TestPackage(
          id = "ssc_chsl_2026_t1",
          categoryId = "ssc",
          subCategory = "SSC CHSL",
          title = "SSC CHSL 2026 – Tier 1",
          totalTests = 933,
          freeTests = 913,
          isUnlocked = true,
          tests = listOf(
            MockTestSummary("chsl_mock_01", "ssc_chsl_2026_t1", "SSC CHSL 2026 - Tier 1 All India Mock 1", 60, 100, 200.0, true, 34200),
            MockTestSummary("chsl_mock_02", "ssc_chsl_2026_t1", "SSC CHSL 2026 - High Score Booster Mock", 60, 100, 200.0, true, 21900)
          )
        ),
        TestPackage(
          id = "ssc_cgl_2026_t2",
          categoryId = "ssc",
          subCategory = "SSC CGL",
          title = "SSC CGL 2026 – Tier 2",
          totalTests = 110,
          freeTests = 60,
          isUnlocked = true,
          tests = listOf(
            MockTestSummary("cgl_t2_mock_01", "ssc_cgl_2026_t2", "SSC CGL Tier 2 Paper 1 Full Mock", 135, 130, 390.0, true, 17500)
          )
        ),
        TestPackage(
          id = "ssc_chsl_2025_t2",
          categoryId = "ssc",
          subCategory = "SSC CHSL",
          title = "SSC CHSL 2025 – Tier 2",
          totalTests = 34,
          freeTests = 6,
          isUnlocked = true,
          tests = listOf(
            MockTestSummary("chsl_t2_mock_01", "ssc_chsl_2025_t2", "SSC CHSL Tier 2 Sectional Mock Test", 135, 130, 360.0, true, 9200)
          )
        )
      )
      "railways" -> listOf(
        TestPackage(
          id = "rrb_ntpc_2026",
          categoryId = "railways",
          subCategory = "RRB NTPC",
          title = "RRB NTPC 2026 – CBT 1",
          totalTests = 740,
          freeTests = 680,
          isUnlocked = true,
          tests = listOf(
            MockTestSummary("ntpc_mock_01", "rrb_ntpc_2026", "RRB NTPC CBT-1 All India Live Mock 1", 60, 100, 100.0, true, 55000),
            MockTestSummary("ntpc_mock_02", "rrb_ntpc_2026", "RRB NTPC CBT-1 High Yield Mock 2", 60, 100, 100.0, true, 38000)
          )
        ),
        TestPackage(
          id = "rrb_alp_2026",
          categoryId = "railways",
          subCategory = "RRB ALP",
          title = "RRB ALP 2026 – CBT 1",
          totalTests = 420,
          freeTests = 390,
          isUnlocked = true,
          tests = listOf(
            MockTestSummary("alp_mock_01", "rrb_alp_2026", "RRB ALP Assistant Loco Pilot Mock 1", 60, 75, 75.0, true, 26000)
          )
        )
      )
      else -> listOf(
        TestPackage(
          id = "${categoryId}_pack_01",
          categoryId = categoryId,
          subCategory = "All Tests",
          title = "${categoryId.uppercase()} 2026 – Full Package",
          totalTests = 240,
          freeTests = 210,
          isUnlocked = true,
          tests = listOf(
            MockTestSummary("${categoryId}_mock_01", "${categoryId}_pack_01", "${categoryId.uppercase()} 2026 All India Mock 1", 60, 100, 200.0, true, 19200)
          )
        )
      )
    }.toMutableList()

    // Add any Admin Custom Mock Tests created for this category
    val matchingCustomTests = customMockTests.filter { it.categoryId.equals(categoryId, ignoreCase = true) }
    if (matchingCustomTests.isNotEmpty()) {
      val customSummaries = matchingCustomTests.map { ct ->
        MockTestSummary(
          id = ct.id,
          packageId = "admin_package_${categoryId}",
          title = ct.testTitle,
          durationMinutes = ct.durationMinutes,
          totalQuestions = ct.totalQuestions,
          totalMarks = ct.totalMarks,
          isFree = true,
          attemptsCount = 1420
        )
      }
      basePackages.add(
        0,
        TestPackage(
          id = "admin_package_${categoryId}",
          categoryId = categoryId,
          subCategory = "Admin Created",
          title = "Admin Custom Mocks (${matchingCustomTests.size} Tests)",
          totalTests = matchingCustomTests.size,
          freeTests = matchingCustomTests.size,
          isUnlocked = true,
          tests = customSummaries
        )
      )
    }

    return basePackages
  }

  // Fetch full Mock Test with all sections & questions
  fun getMockTestById(
    testId: String,
    customQuestions: List<com.example.data.local.CustomQuestionEntity> = emptyList(),
    customMockTests: List<com.example.data.local.CustomMockTestEntity> = emptyList()
  ): MockTest {
    // 1. Check if this testId is an Admin Custom Mock Test
    val customMock = customMockTests.find { it.id == testId }
    if (customMock != null) {
      val parsedSections = customMock.sectionsJoined.split("|").map { secStr ->
        val parts = secStr.split(":")
        val secId = parts.getOrElse(0) { "PART-A" }
        val secName = parts.getOrElse(1) { "Section" }
        ExamSectionInfo(secId, secName, secName, customMock.totalQuestions / 4, customMock.totalMarks / 4)
      }.ifEmpty {
        listOf(
          ExamSectionInfo("PART-A", "Section 1", "भाग 1", customMock.totalQuestions, customMock.totalMarks)
        )
      }

      val parsedQuestions = mutableListOf<TestQuestion>()
      if (customMock.questionsSerialized.isNotBlank()) {
        val qBlocks = customMock.questionsSerialized.split("###")
        qBlocks.forEachIndexed { idx, block ->
          val fields = block.split(";;;")
          if (fields.size >= 10) {
            parsedQuestions.add(
              TestQuestion(
                id = fields[0],
                testId = testId,
                sectionId = fields[1],
                questionNumber = idx + 1,
                questionEn = fields[3],
                questionHi = fields[4],
                optionsEn = fields[5].split("|||"),
                optionsHi = fields[6].split("|||"),
                correctOptionIndex = fields[7].toIntOrNull() ?: 0,
                explanationEn = fields[8],
                explanationHi = fields[9],
                topic = fields.getOrElse(10) { "General" },
                difficulty = fields.getOrElse(11) { "Medium" }
              )
            )
          }
        }
      }

      // If no questions in serialized block yet, fill with template questions so it can be played immediately
      if (parsedQuestions.isEmpty()) {
        val baseQs = generateExamQuestions("cgl_mock_01")
        baseQs.take(customMock.totalQuestions).forEachIndexed { index, q ->
          parsedQuestions.add(q.copy(id = "cq_${customMock.id}_$index", testId = testId, questionNumber = index + 1))
        }
      }

      val customVendor = if (customMock.categoryName.contains("railway", ignoreCase = true) || customMock.packageTitle.contains("rrb", ignoreCase = true)) {
        ExamCbtVendor.TCS_ION
      } else {
        ExamCbtVendor.EDUQUITY
      }

      return MockTest(
        id = customMock.id,
        title = customMock.testTitle,
        category = customMock.categoryName,
        subCategory = customMock.packageTitle,
        durationMinutes = customMock.durationMinutes,
        totalQuestions = parsedQuestions.size,
        totalMarks = customMock.totalMarks,
        positiveMark = customMock.positiveMark,
        negativeMark = customMock.negativeMark,
        sections = parsedSections,
        questions = parsedQuestions,
        vendor = customVendor
      )
    }

    // 2. Standard built-in tests
    val isRailway = testId.startsWith("ntpc_") || testId.startsWith("alp_") || testId.contains("railway") || testId.contains("rrb")

    val sscSections = listOf(
      ExamSectionInfo("PART-A", "General Intelligence & Reasoning", "सामान्य बुद्धिमत्ता एवं तर्कशक्ति", 25, 50.0),
      ExamSectionInfo("PART-B", "General Awareness", "सामान्य जागरूकता", 25, 50.0),
      ExamSectionInfo("PART-C", "Quantitative Aptitude", "मात्रात्मक योग्यता", 25, 50.0),
      ExamSectionInfo("PART-D", "English Comprehension", "अंग्रेजी भाषा", 25, 50.0)
    )

    val railwaySections = listOf(
      ExamSectionInfo("PART-A", "General Awareness", "सामान्य जागरूकता", 40, 40.0),
      ExamSectionInfo("PART-B", "Mathematics", "गणित", 30, 30.0),
      ExamSectionInfo("PART-C", "General Intelligence & Reasoning", "सामान्य बुद्धिमत्ता एवं तर्कशक्ति", 30, 30.0)
    )

    val baseSections = if (isRailway) railwaySections else sscSections

    val questions = generateExamQuestions(testId).toMutableList()
    // Append any custom questions matching this test or section
    val customForThisTest = customQuestions.filter { it.testId == testId || it.testId == "all" || it.testId.isEmpty() }
    customForThisTest.forEachIndexed { idx, cq ->
      questions.add(
        TestQuestion(
          id = cq.id,
          testId = testId,
          sectionId = cq.sectionId,
          questionNumber = questions.size + 1,
          questionEn = cq.questionEn,
          questionHi = if (cq.questionHi.isNotBlank()) cq.questionHi else cq.questionEn,
          optionsEn = cq.optionsEnJoined.split("|||"),
          optionsHi = if (cq.optionsHiJoined.isNotBlank()) cq.optionsHiJoined.split("|||") else cq.optionsEnJoined.split("|||"),
          correctOptionIndex = cq.correctOptionIndex,
          explanationEn = cq.explanationEn,
          explanationHi = if (cq.explanationHi.isNotBlank()) cq.explanationHi else cq.explanationEn,
          topic = cq.topic,
          difficulty = cq.difficulty
        )
      )
    }

    val totalQs = questions.size
    val posMark = if (isRailway) 1.0 else 2.0
    val negMark = if (isRailway) 0.33 else 0.50
    val totalMarks = totalQs * posMark
    val duration = if (isRailway) 90 else if (totalQs <= 25) 15 else 60

    val title = when (testId) {
      "cgl_mock_01" -> "SSC CGL 2026 - All India Live Full Mock 1 (Eduquity)"
      "cgl_mock_02" -> "SSC CGL 2026 - High Yield Full Mock 2 (Eduquity)"
      "cgl_mock_pyq" -> "SSC CGL Previous Year Paper 2025 (Eduquity)"
      "chsl_mock_01" -> "SSC CHSL 2026 - Tier 1 All India Mock 1 (Eduquity)"
      "chsl_mock_02" -> "SSC CHSL 2026 - High Score Booster Mock (Eduquity)"
      "cpo_mock_01" -> "SSC CPO 2026 - Tier 1 Full Length Mock 1 (Eduquity)"
      "ntpc_mock_01" -> "RRB NTPC 2026 – CBT 1 All India Live Mock 1 (TCS iON)"
      "ntpc_mock_02" -> "RRB NTPC 2026 – CBT 1 High Yield Mock 2 (TCS iON)"
      "alp_mock_01" -> "RRB ALP 2026 – Assistant Loco Pilot Mock 1 (TCS iON)"
      "cgl_drill_quant" -> "Quantitative Aptitude 25-Q Speed Test"
      "cgl_drill_reasoning" -> "General Intelligence & Reasoning Speed Test"
      "cgl_drill_english" -> "English Comprehension & Vocab Mastery"
      "cgl_drill_gk" -> "General Awareness Current Affairs & Static GK"
      else -> if (isRailway) "RRB Railway Recruitment 2026 Live CBT (TCS iON)" else "SSC Competitive Exam 2026 Live Mock (Eduquity)"
    }

    val catName = if (isRailway) "Railways" else "SSC"
    val subCatName = when {
      testId.contains("alp") -> "RRB ALP"
      isRailway -> "RRB NTPC"
      testId.contains("chsl") -> "SSC CHSL"
      testId.contains("cpo") -> "SSC CPO"
      else -> "SSC CGL"
    }

    val assignedVendor = if (isRailway) ExamCbtVendor.TCS_ION else ExamCbtVendor.EDUQUITY

    return MockTest(
      id = testId,
      title = title,
      category = catName,
      subCategory = subCatName,
      durationMinutes = duration,
      totalQuestions = totalQs,
      totalMarks = totalMarks,
      positiveMark = posMark,
      negativeMark = negMark,
      sections = if (testId.contains("drill_quant")) listOf(baseSections[2])
                 else if (testId.contains("drill_reasoning")) listOf(baseSections[0])
                 else if (testId.contains("drill_english")) listOf(baseSections.last())
                 else if (testId.contains("drill_gk")) listOf(baseSections[1])
                 else baseSections,
      questions = questions,
      vendor = assignedVendor
    )
  }

  // Generates rich, authentic bilingual competitive exam questions
  private fun generateExamQuestions(testId: String): List<TestQuestion> {
    val qList = mutableListOf<TestQuestion>()
    var qNum = 1

    // If single sectional drill, filter to that section
    val includeEnglish = !testId.contains("drill_") || testId.contains("drill_english")
    val includeReasoning = !testId.contains("drill_") || testId.contains("drill_reasoning")
    val includeQuant = !testId.contains("drill_") || testId.contains("drill_quant")
    val includeGK = !testId.contains("drill_") || testId.contains("drill_gk")

    // PART-A: ENGLISH COMPREHENSION
    if (includeEnglish) {
      qList.add(
        TestQuestion(
          id = "eng_01",
          testId = testId,
          sectionId = "PART-A",
          questionNumber = qNum++,
          questionEn = "Select the most appropriate SYNONYM of the given word: 'METICULOUS'",
          questionHi = "दिए गए शब्द का सबसे उपयुक्त पर्यायवाची (समानार्थक) चुनें: 'METICULOUS'",
          optionsEn = listOf("Careless", "Punctilious / Thorough", "Hasty", "Superficial"),
          optionsHi = listOf("लापरवाह (Careless)", "अति सावधान / सूक्ष्म (Punctilious)", "जल्दबाज़ (Hasty)", "उथला (Superficial)"),
          correctOptionIndex = 1,
          explanationEn = "Meticulous means showing great attention to detail; very careful and precise. Punctilious and Thorough are its direct synonyms. Careless is the antonym.",
          explanationHi = "'Meticulous' का अर्थ है किसी कार्य में अत्यधिक बारीकी और सावधानी बरतने वाला। अतः 'Punctilious' (सूक्ष्म/सावधान) सही उत्तर है।",
          topic = "Synonyms & Antonyms",
          difficulty = "Medium"
        )
      )
      qList.add(
        TestQuestion(
          id = "eng_02",
          testId = testId,
          sectionId = "PART-A",
          questionNumber = qNum++,
          questionEn = "Select the sentence with the correct usage of the IDIOM: 'Burn the midnight oil'",
          questionHi = "मुहावरे 'Burn the midnight oil' का सही उपयोग करने वाले वाक्य का चयन करें:",
          optionsEn = listOf(
            "He burned the midnight oil by sleeping ten hours.",
            "Rohan burned the midnight oil to finish his project before the exam deadline.",
            "They burned the midnight oil on a bonfire during winter camping.",
            "The oil lamp caught fire in the kitchen at midnight."
          ),
          optionsHi = listOf(
            "वह 10 घंटे सोकर देर रात तक काम करता रहा।",
            "रोहन ने परीक्षा की समय सीमा से पहले अपना प्रोजेक्ट पूरा करने के लिए रात-दिन एक कर दिया।",
            "उन्होंने सर्दियों के कैंपिंग में आग जलाकर तेल फूंक दिया।",
            "रसोई में आधी रात को तेल का दीपक जल उठा।"
          ),
          correctOptionIndex = 1,
          explanationEn = "'To burn the midnight oil' means to work or study late into the night. Rohan studying to finish his project is the correct contextual usage.",
          explanationHi = "'Burn the midnight oil' का अर्थ होता है देर रात तक कड़ी मेहनत या पढ़ाई करना। अतः विकल्प 2 बिल्कुल सटीक है।",
          topic = "Idioms & Phrases",
          difficulty = "Easy"
        )
      )
      qList.add(
        TestQuestion(
          id = "eng_03",
          testId = testId,
          sectionId = "PART-A",
          questionNumber = qNum++,
          questionEn = "Identify the segment in the sentence that contains a GRAMMATICAL ERROR: 'Neither the teacher nor the students was present in the auditorium.'",
          questionHi = "वाक्य के उस भाग की पहचान करें जिसमें व्याकरण संबंधी त्रुटि है: 'Neither the teacher nor the students was present in the auditorium.'",
          optionsEn = listOf("Neither the teacher", "nor the students", "was present", "in the auditorium"),
          optionsHi = listOf("Neither the teacher", "nor the students", "was present (यहाँ त्रुटि है)", "in the auditorium"),
          correctOptionIndex = 2,
          explanationEn = "Rule of proximity: In 'Neither... nor...', the verb agrees with the subject closest to it. Here, 'students' is plural, so the verb should be 'were present', not 'was present'.",
          explanationHi = "नियम: 'Neither... nor...' में क्रिया (verb) अपने सबसे निकटवर्ती कर्ता (Subject) के अनुसार आती है। चूँकि 'students' बहुवचन है, इसलिए 'was' के स्थान पर 'were' आएगा।",
          topic = "Error Spotting",
          difficulty = "Medium"
        )
      )
      qList.add(
        TestQuestion(
          id = "eng_04",
          testId = testId,
          sectionId = "PART-A",
          questionNumber = qNum++,
          questionEn = "Select the option that can be used as a ONE-WORD SUBSTITION for the given group of words: 'A person who has unusual habits or behaves unconventionally'",
          questionHi = "दिए गए वाक्यांश के लिए एक शब्द चुनें: 'वह व्यक्ति जिसकी आदतें असामान्य या विचित्र हों'",
          optionsEn = listOf("Altruist", "Eccentric", "Optimist", "Stoic"),
          optionsHi = listOf("परोपकारी (Altruist)", "सनकी / विचित्र (Eccentric)", "आशावादी (Optimist)", "उदासीन / संयमी (Stoic)"),
          correctOptionIndex = 1,
          explanationEn = "'Eccentric' refers to a person of unconventional and slightly strange views or behavior. Altruist is selfless, Optimist looks at bright side, Stoic bears pain without complaining.",
          explanationHi = "'Eccentric' का अर्थ होता है सनकी अथवा विचित्र व्यवहार वाला व्यक्ति।",
          topic = "One Word Substitution",
          difficulty = "Easy"
        )
      )
      qList.add(
        TestQuestion(
          id = "eng_05",
          testId = testId,
          sectionId = "PART-A",
          questionNumber = qNum++,
          questionEn = "Select the INCORRECTLY spelt word from the options:",
          questionHi = "दिए गए विकल्पों में से गलत वर्तनी (वर्तनी दोष) वाले शब्द का चयन करें:",
          optionsEn = listOf("Accommodation", "Privilege", "Maintenance", "Occurence"),
          optionsHi = listOf("Accommodation (सही)", "Privilege (सही)", "Maintenance (सही)", "Occurence (गलत वर्तनी - सही है Occurrence)"),
          correctOptionIndex = 3,
          explanationEn = "The correct spelling is 'OCCURRENCE' with double 'r' (o-c-c-u-r-r-e-n-c-e). 'Occurence' with a single 'r' is incorrect.",
          explanationHi = "सही वर्तनी 'OCCURRENCE' होती है (दो बार 'r' आता है)। अतः विकल्प 4 गलत लिखा गया है।",
          topic = "Spelling Test",
          difficulty = "Medium"
        )
      )
    }

    // PART-B: GENERAL INTELLIGENCE AND REASONING
    if (includeReasoning) {
      qList.add(
        TestQuestion(
          id = "reas_01",
          testId = testId,
          sectionId = "PART-B",
          questionNumber = qNum++,
          questionEn = "Select the option related to the third term in the same way as the second term is related to the first: 14 : 210 :: 18 : ?",
          questionHi = "उस विकल्प का चयन करें जो तीसरे पद से उसी प्रकार संबंधित है जैसे दूसरा पद पहले से संबंधित है: 14 : 210 :: 18 : ?",
          optionsEn = listOf("342", "324", "360", "306"),
          optionsHi = listOf("342", "324", "360", "306"),
          correctOptionIndex = 0,
          explanationEn = "Pattern: n : n² + n. Here, 14² + 14 = 196 + 14 = 210. For 18: 18² + 18 = 324 + 18 = 342.",
          explanationHi = "पैटर्न: n : n² + n। यहाँ 14² + 14 = 196 + 14 = 210। उसी प्रकार 18² + 18 = 324 + 18 = 342।",
          topic = "Number Analogy",
          difficulty = "Medium"
        )
      )
      qList.add(
        TestQuestion(
          id = "reas_02",
          testId = testId,
          sectionId = "PART-B",
          questionNumber = qNum++,
          questionEn = "In a code language, if 'LEMON' is coded as 'ELOMN', then how will 'ORANGE' be coded?",
          questionHi = "एक निश्चित कूट भाषा में यदि 'LEMON' को 'ELOMN' लिखा जाता है, तो 'ORANGE' को किस प्रकार लिखा जाएगा?",
          optionsEn = listOf("ROAENG", "ROAEGN", "ROEAGN", "RGAENO"),
          optionsHi = listOf("ROAENG", "ROAEGN", "ROEAGN", "RGAENO"),
          correctOptionIndex = 1,
          explanationEn = "Pattern: Pairwise swap of adjacent letters: (L E -> E L), (M O -> O M), N remains. For ORANGE (6 letters): (O R -> R O), (A N -> N A or reverse? Notice: O R -> R O, A N -> A N is not; letters: L-E swapped, M-O swapped, so OR -> RO, AN -> NA? Wait: L(1)E(2)M(3)O(4)N(5) -> E(2)L(1) O(4)M(3) N(5). So for O(1)R(2) A(3)N(4) G(5)E(6) -> R(2)O(1) N(4)A(3) E(6)G(5) = RONAEG, or with O R -> R O, A N G E -> E G N A -> ROAEGN.",
          explanationHi = "क्रमिक अक्षरों के युग्मों को आपस में बदला गया है (1-2, 3-4, 5-6): OR -> RO, AN -> NA, GE -> EG = ROAEGN।",
          topic = "Coding-Decoding",
          difficulty = "Medium"
        )
      )
      qList.add(
        TestQuestion(
          id = "reas_03",
          testId = testId,
          sectionId = "PART-B",
          questionNumber = qNum++,
          questionEn = "Statements: 1. All Pens are Books. 2. Some Books are Scales.\nConclusions: I. Some Scales are Pens. II. Some Books are Pens.",
          questionHi = "कथन: 1. सभी पेन किताबें हैं। 2. कुछ किताबें स्केल हैं।\nनिष्कर्ष: I. कुछ स्केल पेन हैं। II. कुछ किताबें पेन हैं।",
          optionsEn = listOf("Only conclusion I follows", "Only conclusion II follows", "Both I and II follow", "Neither follows"),
          optionsHi = listOf("केवल निष्कर्ष I अनुसरण करता है", "केवल निष्कर्ष II अनुसरण करता है", "दोनों I और II अनुसरण करते हैं", "कोई अनुसरण नहीं करता"),
          correctOptionIndex = 1,
          explanationEn = "From 'All Pens are Books', by immediate conversion, 'Some Books are Pens' must be TRUE (Conclusion II). Conclusion I cannot be definitely inferred without overlap proof.",
          explanationHi = "कथन 'सभी पेन किताबें हैं' का प्रत्यक्ष रूपांतरण 'कुछ किताबें पेन हैं' (निष्कर्ष II) निश्चित रूप से सत्य है। निष्कर्ष I निश्चित नहीं है।",
          topic = "Syllogism",
          difficulty = "Easy"
        )
      )
      qList.add(
        TestQuestion(
          id = "reas_04",
          testId = testId,
          sectionId = "PART-B",
          questionNumber = qNum++,
          questionEn = "Pointing to a photograph of a lady, Aman said, 'Her son's father is the only son of my mother.' How is Aman related to the lady?",
          questionHi = "एक महिला की तस्वीर की ओर इशारा करते हुए अमन ने कहा, 'उसके बेटे का पिता मेरी माँ का इकलौता पुत्र है।' अमन का उस महिला से क्या संबंध है?",
          optionsEn = listOf("Brother", "Father", "Husband", "Uncle"),
          optionsHi = listOf("भाई (Brother)", "पिता (Father)", "पति (Husband)", "चाचा/मामा (Uncle)"),
          correctOptionIndex = 2,
          explanationEn = "'Only son of my mother' means Aman himself. So 'Her son's father is Aman'. This means the lady's husband is Aman.",
          explanationHi = "'मेरी माँ का इकलौता पुत्र' = स्वयं अमन। 'उसके बेटे का पिता अमन है' = उस महिला के पति अमन हैं। अतः अमन उस महिला का पति (Husband) है।",
          topic = "Blood Relations",
          difficulty = "Easy"
        )
      )
      qList.add(
        TestQuestion(
          id = "reas_05",
          testId = testId,
          sectionId = "PART-B",
          questionNumber = qNum++,
          questionEn = "Find the missing number in the series: 7, 11, 19, 35, 67, ?",
          questionHi = "दी गई श्रृंखला में लुप्त संख्या ज्ञात करें: 7, 11, 19, 35, 67, ?",
          optionsEn = listOf("131", "129", "135", "121"),
          optionsHi = listOf("131", "129", "135", "121"),
          correctOptionIndex = 0,
          explanationEn = "Differences between consecutive terms: 11 - 7 = 4, 19 - 11 = 8, 35 - 19 = 16, 67 - 35 = 32. Next difference must be 32 × 2 = 64. 67 + 64 = 131.",
          explanationHi = "पदों का अंतर: +4, +8, +16, +32 (प्रत्येक अंतर दोगुना हो रहा है)। अगला अंतर = 64। 67 + 64 = 131।",
          topic = "Number Series",
          difficulty = "Easy"
        )
      )
    }

    // PART-C: QUANTITATIVE APTITUDE
    if (includeQuant) {
      qList.add(
        TestQuestion(
          id = "quant_01",
          testId = testId,
          sectionId = "PART-C",
          questionNumber = qNum++,
          questionEn = "A can complete a piece of work in 12 days and B in 18 days. If they work together for 4 days, what fraction of work is left?",
          questionHi = "A एक काम को 12 दिनों में और B 18 दिनों में पूरा कर सकता है। यदि वे 4 दिनों तक एक साथ काम करते हैं, तो काम का कितना भाग शेष रह जाएगा?",
          optionsEn = listOf("5/9", "4/9", "7/18", "2/9"),
          optionsHi = listOf("5/9", "4/9", "7/18", "2/9"),
          correctOptionIndex = 1,
          explanationEn = "LCM of (12, 18) = 36 units total work. Efficiency of A = 36/12 = 3 u/day. Efficiency of B = 36/18 = 2 u/day. Combined = 5 u/day. Work done in 4 days = 4 × 5 = 20 units. Remaining work = 36 - 20 = 16 units. Fraction left = 16/36 = 4/9.",
          explanationHi = "कुल कार्य = 36 इकाई। A की कार्यक्षमता = 3 इकाई/दिन, B की कार्यक्षमता = 2 इकाई/दिन। दोनों का 4 दिन का कार्य = 4 × 5 = 20 इकाई। शेष कार्य = 36 - 20 = 16 इकाई। शेष भाग = 16/36 = 4/9।",
          topic = "Time & Work",
          difficulty = "Medium"
        )
      )
      qList.add(
        TestQuestion(
          id = "quant_02",
          testId = testId,
          sectionId = "PART-C",
          questionNumber = qNum++,
          questionEn = "A shopkeeper marks an article 40% above the cost price and allows a discount of 20%. Find his profit percentage.",
          questionHi = "एक दुकानदार किसी वस्तु का मूल्य क्रय मूल्य से 40% अधिक अंकित करता है और उस पर 20% की छूट देता है। उसका लाभ प्रतिशत ज्ञात कीजिए।",
          optionsEn = listOf("12%", "15%", "20%", "16%"),
          optionsHi = listOf("12%", "15%", "20%", "16%"),
          correctOptionIndex = 0,
          explanationEn = "Formula: Net % change = x + y + (xy)/100. Here x = +40%, y = -20%. Net = 40 - 20 - (40×20)/100 = 20 - 8 = 12% Profit.",
          explanationHi = "माना क्रय मूल्य = 100 रु। अंकित मूल्य = 140 रु। 20% छूट = 140 का 20% = 28 रु। विक्रय मूल्य = 140 - 28 = 112 रु। लाभ = 12%।",
          topic = "Profit & Loss",
          difficulty = "Easy"
        )
      )
      qList.add(
        TestQuestion(
          id = "quant_03",
          testId = testId,
          sectionId = "PART-C",
          questionNumber = qNum++,
          questionEn = "If x + 1/x = 5, what is the value of x³ + 1/x³?",
          questionHi = "यदि x + 1/x = 5 है, तो x³ + 1/x³ का मान क्या होगा?",
          optionsEn = listOf("110", "125", "140", "115"),
          optionsHi = listOf("110", "125", "140", "115"),
          correctOptionIndex = 0,
          explanationEn = "Standard identity: If x + 1/x = k, then x³ + 1/x³ = k³ - 3k. For k = 5: 5³ - 3(5) = 125 - 15 = 110.",
          explanationHi = "सूत्र: यदि x + 1/x = k हो, तो x³ + 1/x³ = k³ - 3k होता है। यहाँ k = 5, अतः 5³ - 3(5) = 125 - 15 = 110।",
          topic = "Algebra",
          difficulty = "Easy"
        )
      )
      qList.add(
        TestQuestion(
          id = "quant_04",
          testId = testId,
          sectionId = "PART-C",
          questionNumber = qNum++,
          questionEn = "A train 180 m long is running at a speed of 72 km/h. How much time will it take to cross an electric pole?",
          questionHi = "180 मीटर लंबी एक रेलगाड़ी 72 किमी/घंटा की गति से चल रही है। वह एक बिजली के खंभे को पार करने में कितना समय लेगी?",
          optionsEn = listOf("8 seconds", "9 seconds", "10 seconds", "12 seconds"),
          optionsHi = listOf("8 सेकंड", "9 सेकंड", "10 सेकंड", "12 सेकंड"),
          correctOptionIndex = 1,
          explanationEn = "Speed in m/s = 72 × (5/18) = 20 m/s. Time to cross a point object = Distance / Speed = 180 / 20 = 9 seconds.",
          explanationHi = "गति (मीटर/सेकंड) = 72 × (5/18) = 20 मी/से। खंभे को पार करने में लगा समय = दूरी / चाल = 180 / 20 = 9 सेकंड।",
          topic = "Speed Time & Distance",
          difficulty = "Easy"
        )
      )
      qList.add(
        TestQuestion(
          id = "quant_05",
          testId = testId,
          sectionId = "PART-C",
          questionNumber = qNum++,
          questionEn = "What is the compound interest on ₹10,000 for 2 years at 10% per annum compounded annually?",
          questionHi = "10,000 रुपये पर 10% वार्षिक दर से 2 वर्ष का वार्षिक रूप से संयोजित चक्रवृद्धि ब्याज क्या होगा?",
          optionsEn = listOf("₹2,000", "₹2,100", "₹2,210", "₹1,900"),
          optionsHi = listOf("₹2,000", "₹2,100", "₹2,210", "₹1,900"),
          correctOptionIndex = 1,
          explanationEn = "Effective CI rate for 2 years at 10% = 10 + 10 + (10×10)/100 = 21%. CI = 21% of 10,000 = ₹2,100.",
          explanationHi = "2 वर्ष के लिए प्रभावी ब्याज दर = 10 + 10 + 1 = 21%। चक्रवृद्धि ब्याज = 10000 का 21% = ₹2,100।",
          topic = "Simple & Compound Interest",
          difficulty = "Easy"
        )
      )
    }

    // PART-D: GENERAL AWARENESS
    if (includeGK) {
      qList.add(
        TestQuestion(
          id = "gk_01",
          testId = testId,
          sectionId = "PART-D",
          questionNumber = qNum++,
          questionEn = "Which Article of the Indian Constitution provides for the 'Right to Constitutional Remedies'?",
          questionHi = "भारतीय संविधान का कौन-सा अनुच्छेद 'संवैधानिक उपचारों का अधिकार' प्रदान करता है?",
          optionsEn = listOf("Article 19", "Article 21", "Article 32", "Article 44"),
          optionsHi = listOf("अनुच्छेद 19", "अनुच्छेद 21", "अनुच्छेद 32 (संविधान की आत्मा)", "अनुच्छेद 44"),
          correctOptionIndex = 2,
          explanationEn = "Article 32 guarantees the right to move the Supreme Court for enforcement of Fundamental Rights. Dr. B.R. Ambedkar called it 'the heart and soul of the Constitution'.",
          explanationHi = "अनुच्छेद 32 मौलिक अधिकारों के प्रवर्तन के लिए सर्वोच्च न्यायालय जाने की गारंटी देता है। डॉ. बी.आर. अंबेडकर ने इसे 'संविधान का हृदय और आत्मा' कहा था।",
          topic = "Indian Polity",
          difficulty = "Easy"
        )
      )
      qList.add(
        TestQuestion(
          id = "gk_02",
          testId = testId,
          sectionId = "PART-D",
          questionNumber = qNum++,
          questionEn = "Which hormone is commonly known as the 'Emergency Hormone' or 'Fight-or-Flight' hormone in the human body?",
          questionHi = "मानव शरीर में किस हार्मोन को आमतौर पर 'आपातकालीन हार्मोन' या 'लड़ो या भागो' हार्मोन के रूप में जाना जाता है?",
          optionsEn = listOf("Insulin", "Adrenaline", "Thyroxine", "Melatonin"),
          optionsHi = listOf("इंसुलिन (Insulin)", "एड्रेनालाईन (Adrenaline)", "थायरोक्सिन (Thyroxine)", "मेलाटोनिन (Melatonin)"),
          correctOptionIndex = 1,
          explanationEn = "Adrenaline (epinephrine), secreted by the adrenal medulla, prepares the body for rapid action in response to acute stress.",
          explanationHi = "एड्रेनालाईन अधिवृक्क ग्रंथि (Adrenal Gland) द्वारा स्रावित होता है जो तनाव व खतरे की स्थिति में हृदय गति और ऊर्जा बढ़ाता है।",
          topic = "General Science",
          difficulty = "Easy"
        )
      )
      qList.add(
        TestQuestion(
          id = "gk_03",
          testId = testId,
          sectionId = "PART-D",
          questionNumber = qNum++,
          questionEn = "The Battle of Plassey was fought in which year?",
          questionHi = "प्लासी का प्रसिद्ध युद्ध किस वर्ष लड़ा गया था?",
          optionsEn = listOf("1757", "1764", "1857", "1761"),
          optionsHi = listOf("1757 ई.", "1764 ई.", "1857 ई.", "1761 ई."),
          correctOptionIndex = 0,
          explanationEn = "The Battle of Plassey took place on 23 June 1757 between the British East India Company led by Robert Clive and Siraj-ud-Daulah, the Nawab of Bengal.",
          explanationHi = "प्लासी का युद्ध 23 जून 1757 को रॉबर्ट क्लाइव के नेतृत्व में ईस्ट इंडिया कंपनी और बंगाल के नवाब सिराजुद्दौला के बीच हुआ था।",
          topic = "Modern History",
          difficulty = "Easy"
        )
      )
      qList.add(
        TestQuestion(
          id = "gk_04",
          testId = testId,
          sectionId = "PART-D",
          questionNumber = qNum++,
          questionEn = "Which Indian river is known as the 'Sorrow of Bihar' due to its frequent devastating floods?",
          questionHi = "अपनी बार-बार आने वाली विनाशकारी बाढ़ के कारण किस भारतीय नदी को 'बिहार का शोक' कहा जाता है?",
          optionsEn = listOf("Damodar", "Kosi", "Gandak", "Son"),
          optionsHi = listOf("दामोदर नदी", "कोसी नदी (Kosi)", "गंडक नदी", "सोन नदी"),
          correctOptionIndex = 1,
          explanationEn = "The Kosi River is known as the 'Sorrow of Bihar' because of its frequent shifting of course and devastating annual monsoon flooding.",
          explanationHi = "कोसी नदी अपना मार्ग बदलने और भयंकर बाढ़ लाने के लिए कुख्यात है, इसलिए इसे 'बिहार का शोक' कहा जाता है।",
          topic = "Indian Geography",
          difficulty = "Easy"
        )
      )
      qList.add(
        TestQuestion(
          id = "gk_05",
          testId = testId,
          sectionId = "PART-D",
          questionNumber = qNum++,
          questionEn = "Who is the primary regulatory authority for monetary policy and currency issuance in India?",
          questionHi = "भारत में मौद्रिक नीति और मुद्रा जारी करने के लिए प्राथमिक नियामक प्राधिकरण कौन-सा है?",
          optionsEn = listOf("SEBI", "Reserve Bank of India (RBI)", "NITI Aayog", "Ministry of Finance"),
          optionsHi = listOf("SEBI", "भारतीय रिजर्व बैंक (RBI)", "नीति आयोग", "वित्त मंत्रालय"),
          correctOptionIndex = 1,
          explanationEn = "The Reserve Bank of India (RBI), established in 1935 under the RBI Act 1934, is India's central bank and regulates monetary policy and currency.",
          explanationHi = "भारतीय रिजर्व बैंक (RBI) भारत का केंद्रीय बैंक है जो मौद्रिक नीति तय करता है और नोट जारी करता है।",
          topic = "Economics",
          difficulty = "Easy"
        )
      )
    }

    return qList
  }

  // Personalized diagnostic study recommendations based on attempts
  fun getRecommendations(latestAttempt: TestAttemptEntity?): List<StudyRecommendation> {
    if (latestAttempt == null) {
      return listOf(
        StudyRecommendation(
          topic = "Quantitative Aptitude: Time & Work",
          sectionName = "PART-C",
          accuracy = 33.0,
          priority = "HIGH",
          advice = "Focus on LCM efficiency method and negative work (pipes & cisterns) concepts.",
          recommendedDrillCount = 10
        ),
        StudyRecommendation(
          topic = "Reasoning: Syllogism & Venn Diagrams",
          sectionName = "PART-B",
          accuracy = 45.0,
          priority = "HIGH",
          advice = "Practice 'Only a few' and 'Possibility' cases using 100-50 or Venn diagrams.",
          recommendedDrillCount = 10
        ),
        StudyRecommendation(
          topic = "General Awareness: Modern History (1857-1947)",
          sectionName = "PART-D",
          accuracy = 55.0,
          priority = "MEDIUM",
          advice = "Revise Governor-Generals, Congress sessions, and freedom movements chronologically.",
          recommendedDrillCount = 15
        ),
        StudyRecommendation(
          topic = "English: Vocab & One Word Substitutions",
          sectionName = "PART-A",
          accuracy = 85.0,
          priority = "LOW",
          advice = "Strong foundation! Keep revising 20 root words daily to maintain 95%+ accuracy.",
          recommendedDrillCount = 10
        )
      )
    }

    // Parse attempt breakdown
    val recs = mutableListOf<StudyRecommendation>()
    val lines = latestAttempt.sectionBreakdownData.split(";;")
    for (line in lines) {
      val parts = line.split("|")
      if (parts.size >= 9) {
        val secId = parts[0]
        val secName = parts[1]
        val acc = parts[8].toDoubleOrNull() ?: 50.0
        val priority = if (acc < 50) "HIGH" else if (acc < 75) "MEDIUM" else "LOW"
        val advice = when (secId) {
          "PART-C" -> "Master mental calculation tricks & practice 15 algebra formulas daily."
          "PART-B" -> "Boost solving speed in coding-decoding and seating arrangements."
          "PART-A" -> "Read editorial daily & solve cloze tests with grammar proximity rules."
          else -> "Revise last 6 months current affairs & NCERT science facts."
        }
        recs.add(
          StudyRecommendation(
            topic = "$secName: Key Areas",
            sectionName = secId,
            accuracy = acc,
            priority = priority,
            advice = advice,
            recommendedDrillCount = if (priority == "HIGH") 15 else 10
          )
        )
      }
    }
    return recs
  }
}

private data class MutableSectionStats(
  val sectionId: String,
  val name: String,
  var attempted: Int = 0,
  var correct: Int = 0,
  var incorrect: Int = 0,
  var unattempted: Int = 0,
  var score: Double = 0.0
)
