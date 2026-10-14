package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ExamDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAttempt(attempt: TestAttemptEntity): Long

  @Query("SELECT * FROM test_attempts ORDER BY timestamp DESC")
  fun getAllAttempts(): Flow<List<TestAttemptEntity>>

  @Query("SELECT * FROM test_attempts WHERE id = :attemptId LIMIT 1")
  suspend fun getAttemptById(attemptId: Long): TestAttemptEntity?

  @Query("SELECT * FROM test_attempts WHERE testId = :testId ORDER BY timestamp DESC LIMIT 1")
  suspend fun getLatestAttemptForTest(testId: String): TestAttemptEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertBookmark(bookmark: BookmarkEntity)

  @Query("DELETE FROM bookmarks WHERE questionId = :questionId")
  suspend fun deleteBookmark(questionId: String)

  @Query("SELECT * FROM bookmarks ORDER BY bookmarkedAt DESC")
  fun getAllBookmarks(): Flow<List<BookmarkEntity>>

  @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE questionId = :questionId)")
  suspend fun isBookmarked(questionId: String): Boolean

  // Admin Custom Questions
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCustomQuestion(question: CustomQuestionEntity)

  @Query("SELECT * FROM custom_questions ORDER BY createdAt DESC")
  fun getAllCustomQuestions(): Flow<List<CustomQuestionEntity>>

  @Query("DELETE FROM custom_questions WHERE id = :questionId")
  suspend fun deleteCustomQuestion(questionId: String)

  // Admin Custom Mock Tests (For ALL exams)
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCustomMockTest(test: CustomMockTestEntity)

  @Query("SELECT * FROM custom_mock_tests ORDER BY createdAt DESC")
  fun getAllCustomMockTests(): Flow<List<CustomMockTestEntity>>

  @Query("SELECT * FROM custom_mock_tests WHERE id = :testId LIMIT 1")
  suspend fun getCustomMockTestById(testId: String): CustomMockTestEntity?

  @Query("DELETE FROM custom_mock_tests WHERE id = :testId")
  suspend fun deleteCustomMockTest(testId: String)

  // User Profile
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun saveUserProfile(profile: UserProfileEntity)

  @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
  fun getUserProfile(): Flow<UserProfileEntity?>

  @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
  suspend fun getUserProfileOnce(): UserProfileEntity?

  // Managed Users (Admin User Control)
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertManagedUser(user: ManagedUserEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertManagedUsers(users: List<ManagedUserEntity>)

  @Query("SELECT * FROM managed_users ORDER BY joinedAt DESC")
  fun getAllManagedUsers(): Flow<List<ManagedUserEntity>>

  @Query("SELECT * FROM managed_users")
  suspend fun getAllManagedUsersOnce(): List<ManagedUserEntity>

  @Query("UPDATE managed_users SET status = :status WHERE id = :userId")
  suspend fun updateUserStatus(userId: String, status: String)

  @Query("UPDATE managed_users SET hasFullTestAccess = :hasAccess WHERE id = :userId")
  suspend fun updateUserTestAccess(userId: String, hasAccess: Boolean)

  @Query("UPDATE managed_users SET role = :role WHERE id = :userId")
  suspend fun updateUserRole(userId: String, role: String)

  @Query("DELETE FROM managed_users WHERE id = :userId")
  suspend fun deleteManagedUser(userId: String)
}
