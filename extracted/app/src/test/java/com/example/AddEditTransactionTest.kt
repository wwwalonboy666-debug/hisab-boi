package com.example

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.model.AppLanguage
import com.example.model.TransactionType
import com.example.ui.screens.AddEditTransactionSheet
import com.example.ui.theme.HisabBoiTheme
import com.example.util.Strings
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [36])
class AddEditTransactionTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testAmountInputWith350() {
    var savedAmount: Double? = null
    composeTestRule.setContent {
      HisabBoiTheme {
        AddEditTransactionSheet(
          initialType = TransactionType.EXPENSE,
          existingTransaction = null,
          customCategories = emptyList(),
          language = AppLanguage.BN,
          strings = Strings(AppLanguage.BN),
          onDismiss = {},
          onSave = { _, amount, _, _, _ ->
            savedAmount = amount
          }
        )
      }
    }

    val amountField = composeTestRule.onNodeWithTag("amount_input_field")
    amountField.assertExists()
    amountField.performClick()
    amountField.performTextInput("350")

    val submitBtn = composeTestRule.onNodeWithTag("save_transaction_submit_btn")
    submitBtn.performScrollTo()
    submitBtn.performClick()
    composeTestRule.waitForIdle()
    assertEquals(350.0, savedAmount)
  }

  @Test
  fun testDecimalAmountInput() {
    var savedAmount: Double? = null
    composeTestRule.setContent {
      HisabBoiTheme {
        AddEditTransactionSheet(
          initialType = TransactionType.EXPENSE,
          existingTransaction = null,
          customCategories = emptyList(),
          language = AppLanguage.BN,
          strings = Strings(AppLanguage.BN),
          onDismiss = {},
          onSave = { _, amount, _, _, _ ->
            savedAmount = amount
          }
        )
      }
    }

    val amountField = composeTestRule.onNodeWithTag("amount_input_field")
    amountField.performClick()
    amountField.performTextInput("125.75")
    amountField.assertTextContains("125.75")

    val submitBtn = composeTestRule.onNodeWithTag("save_transaction_submit_btn")
    submitBtn.performScrollTo()
    submitBtn.performClick()
    composeTestRule.waitForIdle()
    assertEquals(125.75, savedAmount)
  }
}
