package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.AppLanguage
import com.example.model.CategoryRegistry
import com.example.model.TransactionType
import com.example.util.CurrencyFormatter
import com.example.util.DateUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read app name from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("HisabBoi", appName)
  }

  @Test
  fun `currency formatter formats bangla and english numbers`() {
    val bnFormatted = CurrencyFormatter.format(25450.0, AppLanguage.BN)
    assertEquals("৳২৫,৪৫০", bnFormatted)

    val enFormatted = CurrencyFormatter.format(25450.0, AppLanguage.EN)
    assertEquals("৳25,450", enFormatted)
  }

  @Test
  fun `categories registry includes required categories`() {
    assertTrue(CategoryRegistry.expenseCategories.isNotEmpty())
    assertTrue(CategoryRegistry.incomeCategories.isNotEmpty())

    val food = CategoryRegistry.getCategory("EXP_FOOD")
    assertEquals("খাবার", food.nameBn)
    assertEquals(TransactionType.EXPENSE, food.type)

    val salary = CategoryRegistry.getCategory("INC_SALARY")
    assertEquals("বেতন", salary.nameBn)
    assertEquals(TransactionType.INCOME, salary.type)
  }

  @Test
  fun `custom categories can be resolved dynamically`() {
    val customCat = com.example.data.entity.CustomCategoryEntity(
      id = 101,
      key = "CUSTOM_101",
      nameBn = "জিম",
      nameEn = "Gym Membership",
      type = "EXPENSE",
      colorHex = "#2E7D32",
      iconName = "Fitness"
    )
    val resolved = CategoryRegistry.getCategory("CUSTOM_101", listOf(customCat))
    assertEquals("Gym Membership", resolved.nameEn)
    assertEquals("জিম", resolved.nameBn)
    assertEquals(TransactionType.EXPENSE, resolved.type)
  }

  @Test
  fun `budget calculations handle over budget and safe thresholds`() {
    val totalBudget = 20000.0
    val safeSpent = 12000.0
    val overSpent = 22000.0

    val safeProgress = (safeSpent / totalBudget).toFloat()
    assertEquals(0.6f, safeProgress, 0.001f)

    val overProgress = (overSpent / totalBudget).toFloat()
    assertTrue(overProgress > 1.0f)
    val remainingOver = (totalBudget - overSpent).coerceAtLeast(0.0)
    assertTrue(remainingOver <= 0.0)
  }

  @Test
  fun `date utils generates correct month keys`() {
    val monthKey = DateUtils.toMonthKey(2026, 8) // Sep is index 8 (0-based)
    assertEquals("2026-09", monthKey)

    val parsed = DateUtils.parseMonthKey("2026-09")
    assertEquals(2026, parsed.first)
    assertEquals(8, parsed.second)
  }
}
