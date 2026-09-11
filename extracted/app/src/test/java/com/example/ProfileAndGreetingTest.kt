package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.UserProfileManager
import com.example.model.AppLanguage
import com.example.util.Strings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ProfileAndGreetingTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext<Context>()
        // Clear prefs for test isolation
        context.getSharedPreferences("hisabboi_user_profile", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
    }

    @Test
    fun `fresh user has empty profile and setup is not completed`() {
        val manager = UserProfileManager(context)
        val initialProfile = manager.profile.value

        assertFalse(initialProfile.isSetupCompleted)
        assertEquals("", initialProfile.name)
        assertNull(initialProfile.photoPath)
    }

    @Test
    fun `save profile persists name and marks setup completed`() {
        val manager = UserProfileManager(context)
        manager.saveProfile(name = "MD Arman", photoUri = null, context = context)

        val updated = manager.profile.value
        assertTrue(updated.isSetupCompleted)
        assertEquals("MD Arman", updated.name)

        // Simulate app restart with a new manager instance
        val restartedManager = UserProfileManager(context)
        val loaded = restartedManager.profile.value
        assertTrue(loaded.isSetupCompleted)
        assertEquals("MD Arman", loaded.name)
    }

    @Test
    fun `update name changes name while preserving setup status`() {
        val manager = UserProfileManager(context)
        manager.saveProfile(name = "MD Arman", photoUri = null, context = context)
        assertEquals("MD Arman", manager.profile.value.name)

        manager.updateName("Arman Hossain")
        assertEquals("Arman Hossain", manager.profile.value.name)
        assertTrue(manager.profile.value.isSetupCompleted)
    }

    @Test
    fun `remove photo leaves name intact`() {
        val manager = UserProfileManager(context)
        manager.saveProfile(name = "MD Arman", photoUri = null, context = context)

        manager.removePhoto(context)
        assertNull(manager.profile.value.photoPath)
        assertEquals("MD Arman", manager.profile.value.name)
    }

    @Test
    fun `greeting returns accurate time-based greetings with user name in Bangla and English`() {
        val bnStrings = Strings(AppLanguage.BN)
        val enStrings = Strings(AppLanguage.EN)
        val userName = "MD Arman"

        // Morning (5..11)
        assertEquals("শুভ সকাল 🌅, MD Arman", bnStrings.greeting(8, userName))
        assertEquals("Good morning 🌅, MD Arman", enStrings.greeting(8, userName))

        // Afternoon (12..16)
        assertEquals("শুভ দুপুর ☀️, MD Arman", bnStrings.greeting(13, userName))
        assertEquals("Good afternoon ☀️, MD Arman", enStrings.greeting(13, userName))

        // Evening (17..20)
        assertEquals("শুভ সন্ধ্যা 🌇, MD Arman", bnStrings.greeting(18, userName))
        assertEquals("Good evening 🌇, MD Arman", enStrings.greeting(18, userName))

        // Night (21..4)
        assertEquals("শুভ রাত 🌙, MD Arman", bnStrings.greeting(22, userName))
        assertEquals("Good night 🌙, MD Arman", enStrings.greeting(22, userName))
        assertEquals("শুভ রাত 🌙, MD Arman", bnStrings.greeting(2, userName))
        assertEquals("Good night 🌙, MD Arman", enStrings.greeting(2, userName))
    }

    @Test
    fun `greeting without name returns clean greeting without comma`() {
        val bnStrings = Strings(AppLanguage.BN)
        assertEquals("শুভ সকাল 🌅", bnStrings.greeting(8, ""))
        assertEquals("শুভ দুপুর ☀️", bnStrings.greeting(13, ""))
        assertEquals("শুভ সন্ধ্যা 🌇", bnStrings.greeting(18, ""))
        assertEquals("শুভ রাত 🌙", bnStrings.greeting(22, ""))
    }
}
