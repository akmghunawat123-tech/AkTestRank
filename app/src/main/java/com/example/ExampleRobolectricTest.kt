package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.repository.ExamDataRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("TestRank: Exam Mock Tests", appName)
  }

  @Test
  fun `verify exam categories and packages loaded correctly`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = AppDatabase.getDatabase(context)
    val repo = ExamDataRepository(db)

    val categories = repo.getCategories()
    assertTrue(categories.isNotEmpty())
    assertTrue(categories.any { it.id == "ssc" })
    assertTrue(categories.any { it.id == "railways" })

    val sscPackages = repo.getPackagesForCategory("ssc")
    assertTrue(sscPackages.isNotEmpty())
    assertTrue(sscPackages.any { it.title.contains("SSC CGL") })

    val test = repo.getMockTestById("cgl_mock_01")
    assertEquals(4, test.sections.size)
    assertTrue(test.questions.isNotEmpty())
  }

  @Test
  fun `launch MainActivity scenario`() {
    val controller = org.robolectric.Robolectric.buildActivity(MainActivity::class.java)
    controller.setup()
    assertNotNull(controller.get())
  }

  @Test
  fun `verify custom mock test creation for all exams`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = AppDatabase.getDatabase(context)
    val repo = ExamDataRepository(db)

    val customTest = com.example.data.local.CustomMockTestEntity(
      id = "test_custom_railway_01",
      categoryId = "railways",
      categoryName = "Railways",
      packageTitle = "RRB NTPC 2026 Special",
      testTitle = "Admin Custom NTPC Live Mock 1",
      durationMinutes = 90,
      totalQuestions = 100,
      totalMarks = 100.0,
      positiveMark = 1.0,
      negativeMark = 0.33,
      sectionsJoined = "PART-A:General Awareness|PART-B:Mathematics|PART-C:General Intelligence",
      questionsSerialized = ""
    )

    repo.saveCustomMockTest(customTest)
    val packages = repo.getPackagesForCategory("railways", listOf(customTest))
    assertTrue(packages.any { it.title.contains("Admin Custom Mocks") })

    val loadedTest = repo.getMockTestById("test_custom_railway_01", emptyList(), listOf(customTest))
    assertEquals("Admin Custom NTPC Live Mock 1", loadedTest.title)
    assertEquals(90, loadedTest.durationMinutes)
  }
}
