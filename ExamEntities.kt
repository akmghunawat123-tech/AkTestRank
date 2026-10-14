package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "test_attempts")
data class TestAttemptEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val testId: String,
  val testTitle: String,
  val category: String,
  val timestamp: Long,
  val totalScore: Double,
  val maxScore: Double,
  val rank: Int,
  val totalParticipants: Int,
  val percentile: Double,
  val accuracyPercentage: Double,
  val attemptedCount: Int,
  val correctCount: Int,
  val wrongCount: Int,
  val unattemptedCount: Int,
  val timeTakenSeconds: Long,
  val cutoffMarks: Double,
  val topperScore: Double,
  val averageScore: Double,
  val sectionBreakdownData: String,
  val userAnswersData: String
)

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
  @PrimaryKey
  val questionId: String,
  val testTitle: String,
  val sectionName: String,
  val questionEn: String,
  val questionHi: String,
  val optionsEnJoined: String, // pipe-delimited
  val optionsHiJoined: String,
  val correctOptionIndex: Int,
  val explanationEn: String,
  val explanationHi: String,
  val topic: String,
  val bookmarkedAt: Long
)

@Entity(tableName = "custom_questions")
data class CustomQuestionEntity(
  @PrimaryKey
  val id: String,
  val testId: String,
  val sectionId: String,
  val questionNumber: Int,
  val questionEn: String,
  val questionHi: String,
  val optionsEnJoined: String,
  val optionsHiJoined: String,
  val correctOptionIndex: Int,
  val explanationEn: String,
  val explanationHi: String,
  val topic: String,
  val difficulty: String,
  val createdAt: Long
)

@Entity(tableName = "custom_mock_tests")
data class CustomMockTestEntity(
  @PrimaryKey
  val id: String,
  val categoryId: String,
  val categoryName: String,
  val packageTitle: String,
  val testTitle: String,
  val durationMinutes: Int,
  val totalQuestions: Int,
  val totalMarks: Double,
  val positiveMark: Double = 2.0,
  val negativeMark: Double = 0.50,
  val sectionsJoined: String,
  val questionsSerialized: String,
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
  @PrimaryKey
  val id: Int = 1,
  val name: String,
  val email: String,
  val phone: String,
  val targetExam: String,
  val preferredLanguage: String,
  val role: String = "Student", // "Student" or "Admin"
  val adminPin: String = "7860", // Secret PIN for Admin Panel access
  val isLoggedIn: Boolean = true,
  val joinedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "managed_users")
data class ManagedUserEntity(
  @PrimaryKey
  val id: String,
  val name: String,
  val email: String,
  val phone: String,
  val targetExam: String,
  val role: String = "Student", // "Student" or "Admin"
  val status: String = "ACTIVE", // "ACTIVE", "PENDING", "INACTIVE"
  val hasFullTestAccess: Boolean = true,
  val testsAttempted: Int = 0,
  val avgScore: Double = 0.0,
  val activePackage: String = "All India Mock Test Pass",
  val joinedAt: Long = System.currentTimeMillis(),
  val lastActiveAt: Long = System.currentTimeMillis()
)
