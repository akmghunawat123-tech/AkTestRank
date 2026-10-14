package com.example.data.model

data class ExamCategory(
  val id: String,
  val name: String,
  val subtitle: String,
  val iconType: String,
  val totalTests: Int,
  val freeTests: Int,
  val filterTags: List<String>
)

data class TestPackage(
  val id: String,
  val categoryId: String,
  val subCategory: String, // e.g. "SSC CGL", "SSC CPO", "CPO TIER II", "SSC CHSL"
  val title: String,
  val totalTests: Int,
  val freeTests: Int,
  val isUnlocked: Boolean = true,
  val tests: List<MockTestSummary>
)

data class MockTestSummary(
  val id: String,
  val packageId: String,
  val title: String,
  val durationMinutes: Int,
  val totalQuestions: Int,
  val totalMarks: Double,
  val isFree: Boolean = true,
  val attemptsCount: Int = 18450,
  val bestScore: Double? = null
)

data class ExamSectionInfo(
  val id: String,
  val name: String,
  val hindiName: String,
  val questionCount: Int,
  val maxMarks: Double
)

enum class ExamCbtVendor(val displayName: String, val vendorTag: String, val primaryExamTarget: String) {
  EDUQUITY("Eduquity Interface", "SSC Official Pattern", "Staff Selection Commission (CGL, CHSL, CPO, MTS, GD)"),
  TCS_ION("TCS iON Interface", "Railways Official Pattern", "Railway Recruitment Boards (RRB NTPC, ALP, Group D)")
}

data class MockTest(
  val id: String,
  val title: String,
  val category: String,
  val subCategory: String,
  val durationMinutes: Int,
  val totalQuestions: Int,
  val totalMarks: Double,
  val positiveMark: Double = 2.0,
  val negativeMark: Double = 0.50,
  val sections: List<ExamSectionInfo>,
  val questions: List<TestQuestion>,
  val vendor: ExamCbtVendor = ExamCbtVendor.EDUQUITY
)

data class TestQuestion(
  val id: String,
  val testId: String,
  val sectionId: String,
  val questionNumber: Int,
  val questionEn: String,
  val questionHi: String,
  val optionsEn: List<String>,
  val optionsHi: List<String>,
  val correctOptionIndex: Int,
  val explanationEn: String,
  val explanationHi: String,
  val topic: String,
  val difficulty: String = "Medium"
)

enum class QuestionStatus {
  NOT_VISITED,
  NOT_ANSWERED,
  ANSWERED,
  MARKED_FOR_REVIEW,
  ANSWERED_AND_MARKED_FOR_REVIEW
}

data class UserQuestionState(
  val questionId: String,
  val selectedOptionIndex: Int? = null,
  val status: QuestionStatus = QuestionStatus.NOT_VISITED,
  val timeSpentSeconds: Long = 0,
  val isBookmarked: Boolean = false
)

data class SectionPerformance(
  val sectionId: String,
  val sectionName: String,
  val attempted: Int,
  val correct: Int,
  val incorrect: Int,
  val unattempted: Int,
  val score: Double,
  val maxScore: Double,
  val accuracyPercentage: Double,
  val timeSpentSeconds: Long
)

data class StudyRecommendation(
  val topic: String,
  val sectionName: String,
  val accuracy: Double,
  val priority: String, // "HIGH", "MEDIUM", "LOW"
  val advice: String,
  val recommendedDrillCount: Int = 10
)
