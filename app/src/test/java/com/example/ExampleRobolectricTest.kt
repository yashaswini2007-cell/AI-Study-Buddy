package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.repository.StudyRepository
import kotlinx.coroutines.flow.first
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
        assertEquals("AI Study Buddy", appName)
    }

    @Test
    fun `repository save and read note`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = StudyRepository(context)

        val id = repo.saveNote("Computer Science", "Sorting Algorithms", "Revision", "Quick sort details")
        assertTrue(id > 0)

        val notes = repo.getAllSavedNotes().first()
        assertTrue(notes.any { it.topic == "Sorting Algorithms" })
    }

    @Test
    fun `repository generates fallback topic explanation`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = StudyRepository(context)

        val res = repo.explainTopic("Binary Search", "Computer Science")
        assertTrue(res.isSuccess)
        val text = res.getOrNull()
        assertNotNull(text)
        assertTrue(text!!.contains("Binary Search"))
    }
}
