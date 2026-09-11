package com.example

import com.example.model.AppLanguage
import com.example.ui.components.EvalResult
import com.example.ui.components.evaluateCalculatorExpression
import com.example.util.Strings
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    private val stringsBn = Strings(AppLanguage.BN)
    private val stringsEn = Strings(AppLanguage.EN)

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testCalculatorBasicAdditionAndSubtraction() {
        val res1 = evaluateCalculatorExpression("120+80", stringsBn)
        assertTrue(res1 is EvalResult.Success)
        assertEquals(200.0, (res1 as EvalResult.Success).value, 0.001)

        val res2 = evaluateCalculatorExpression("500-120", stringsEn)
        assertTrue(res2 is EvalResult.Success)
        assertEquals(380.0, (res2 as EvalResult.Success).value, 0.001)
    }

    @Test
    fun testCalculatorOperatorPrecedence() {
        // Multiplications and divisions should execute before addition and subtraction
        val res = evaluateCalculatorExpression("50+20×2", stringsEn)
        assertTrue(res is EvalResult.Success)
        assertEquals(90.0, (res as EvalResult.Success).value, 0.001)

        val res2 = evaluateCalculatorExpression("100-40÷2", stringsEn)
        assertTrue(res2 is EvalResult.Success)
        assertEquals(80.0, (res2 as EvalResult.Success).value, 0.001)
    }

    @Test
    fun testCalculatorDecimalOperations() {
        val res = evaluateCalculatorExpression("12.5+7.5", stringsEn)
        assertTrue(res is EvalResult.Success)
        assertEquals(20.0, (res as EvalResult.Success).value, 0.001)
    }

    @Test
    fun testCalculatorDivisionByZero() {
        val res = evaluateCalculatorExpression("100÷0", stringsBn)
        assertTrue(res is EvalResult.Error)
        assertEquals(stringsBn.divideByZeroError, (res as EvalResult.Error).message)
    }

    @Test
    fun testCalculatorLivePreviewWithTrailingOperator() {
        val res = evaluateCalculatorExpression("150+", stringsEn)
        assertTrue(res is EvalResult.Success)
        assertEquals(150.0, (res as EvalResult.Success).value, 0.001)
    }
}

