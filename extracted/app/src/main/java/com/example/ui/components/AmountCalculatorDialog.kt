package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.ui.theme.pressScale
import com.example.util.CurrencyFormatter
import com.example.util.Strings
import java.text.DecimalFormat

@Composable
fun AmountCalculatorDialog(
    initialAmount: String = "",
    strings: Strings,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onApplyAmount: (String) -> Unit
) {
    var expression by remember {
        mutableStateOf(initialAmount.filter { it.isDigit() || it == '.' })
    }

    val evaluatedResult by remember(expression) {
        derivedStateOf {
            evaluateCalculatorExpression(expression, strings)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("amount_calculator_dialog"),
        shape = RoundedCornerShape(24.dp),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings.calculatorTitle,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = strings.cancelBtn
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Display Screen (Expression + Result Preview)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp)),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        // Expression Text
                        Text(
                            text = if (expression.isEmpty()) "0" else expression,
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.End,
                            modifier = Modifier.testTag("calc_expression_text")
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Evaluated Result Preview
                        when (val res = evaluatedResult) {
                            is EvalResult.Success -> {
                                val formattedPreview = CurrencyFormatter.format(res.value, language)
                                Text(
                                    text = "= $formattedPreview",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontSize = 26.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.primary,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.testTag("calc_result_text")
                                )
                            }
                            is EvalResult.Error -> {
                                Text(
                                    text = res.message,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = MaterialTheme.colorScheme.error,
                                    textAlign = TextAlign.End
                                )
                            }
                            EvalResult.Empty -> {
                                Text(
                                    text = "= ৳০",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontSize = 26.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                    textAlign = TextAlign.End
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Calculator Keypad
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Row 1: Clear, Backspace, Divide, Multiply
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CalcButton(
                            text = "C",
                            modifier = Modifier.weight(1f).testTag("calc_key_clear"),
                            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f),
                            contentColor = MaterialTheme.colorScheme.onErrorContainer,
                            onClick = { expression = "" }
                        )
                        CalcIconButton(
                            icon = Icons.Default.Backspace,
                            modifier = Modifier.weight(1f).testTag("calc_key_backspace"),
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            onClick = {
                                if (expression.isNotEmpty()) {
                                    expression = expression.dropLast(1)
                                }
                            }
                        )
                        CalcButton(
                            text = "÷",
                            modifier = Modifier.weight(1f).testTag("calc_key_div"),
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            isOperator = true,
                            onClick = { expression = appendOperator(expression, "÷") }
                        )
                        CalcButton(
                            text = "×",
                            modifier = Modifier.weight(1f).testTag("calc_key_mul"),
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            isOperator = true,
                            onClick = { expression = appendOperator(expression, "×") }
                        )
                    }

                    // Row 2: 7, 8, 9, Minus
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CalcButton(text = "7", modifier = Modifier.weight(1f).testTag("calc_key_7"), onClick = { expression += "7" })
                        CalcButton(text = "8", modifier = Modifier.weight(1f).testTag("calc_key_8"), onClick = { expression += "8" })
                        CalcButton(text = "9", modifier = Modifier.weight(1f).testTag("calc_key_9"), onClick = { expression += "9" })
                        CalcButton(
                            text = "-",
                            modifier = Modifier.weight(1f).testTag("calc_key_sub"),
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            isOperator = true,
                            onClick = { expression = appendOperator(expression, "-") }
                        )
                    }

                    // Row 3: 4, 5, 6, Plus
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CalcButton(text = "4", modifier = Modifier.weight(1f).testTag("calc_key_4"), onClick = { expression += "4" })
                        CalcButton(text = "5", modifier = Modifier.weight(1f).testTag("calc_key_5"), onClick = { expression += "5" })
                        CalcButton(text = "6", modifier = Modifier.weight(1f).testTag("calc_key_6"), onClick = { expression += "6" })
                        CalcButton(
                            text = "+",
                            modifier = Modifier.weight(1f).testTag("calc_key_add"),
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            isOperator = true,
                            onClick = { expression = appendOperator(expression, "+") }
                        )
                    }

                    // Row 4: 1, 2, 3, Decimal
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CalcButton(text = "1", modifier = Modifier.weight(1f).testTag("calc_key_1"), onClick = { expression += "1" })
                        CalcButton(text = "2", modifier = Modifier.weight(1f).testTag("calc_key_2"), onClick = { expression += "2" })
                        CalcButton(text = "3", modifier = Modifier.weight(1f).testTag("calc_key_3"), onClick = { expression += "3" })
                        CalcButton(
                            text = ".",
                            modifier = Modifier.weight(1f).testTag("calc_key_dot"),
                            onClick = { expression = appendDecimal(expression) }
                        )
                    }

                    // Row 5: 0, 00, Equals
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CalcButton(text = "0", modifier = Modifier.weight(1f).testTag("calc_key_0"), onClick = { expression += "0" })
                        CalcButton(text = "00", modifier = Modifier.weight(1f).testTag("calc_key_00"), onClick = { expression += "00" })
                        CalcButton(
                            text = "=",
                            modifier = Modifier.weight(2f).testTag("calc_key_equal"),
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            onClick = {
                                if (evaluatedResult is EvalResult.Success) {
                                    val finalVal = (evaluatedResult as EvalResult.Success).value
                                    expression = formatRawNumber(finalVal)
                                }
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalAmount = when (val res = evaluatedResult) {
                        is EvalResult.Success -> formatRawNumber(res.value)
                        else -> expression.filter { it.isDigit() || it == '.' }.ifEmpty { "0" }
                    }
                    onApplyAmount(finalAmount)
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .pressScale()
                    .testTag("calc_apply_amount_btn"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                val applyLabel = when (val res = evaluatedResult) {
                    is EvalResult.Success -> {
                        val formatted = CurrencyFormatter.format(res.value, language)
                        "${strings.calculatorUseResult} ($formatted)"
                    }
                    else -> strings.calculatorUseResult
                }
                Text(
                    text = applyLabel,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        },
        dismissButton = null
    )
}

@Composable
private fun CalcButton(
    text: String,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    isOperator: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(50.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        color = containerColor,
        shape = RoundedCornerShape(14.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = if (isOperator) 22.sp else 18.sp,
                    fontWeight = if (isOperator) FontWeight.Bold else FontWeight.Medium
                ),
                color = contentColor
            )
        }
    }
}

@Composable
private fun CalcIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(50.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        color = containerColor,
        shape = RoundedCornerShape(14.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

private fun appendOperator(current: String, op: String): String {
    if (current.isEmpty()) {
        return if (op == "-") "-" else ""
    }
    val lastChar = current.last()
    val isLastOp = lastChar == '+' || lastChar == '-' || lastChar == '×' || lastChar == '÷'
    return if (isLastOp) {
        current.dropLast(1) + op
    } else {
        current + op
    }
}

private fun appendDecimal(current: String): String {
    if (current.isEmpty()) return "0."
    val tokens = current.split('+', '-', '×', '÷')
    val lastToken = tokens.lastOrNull() ?: ""
    return if (!lastToken.contains('.')) {
        if (lastToken.isEmpty()) current + "0." else current + "."
    } else {
        current
    }
}

private fun formatRawNumber(num: Double): String {
    return if (num % 1.0 == 0.0) {
        num.toLong().toString()
    } else {
        val df = DecimalFormat("#.##")
        df.format(num)
    }
}

sealed class EvalResult {
    data class Success(val value: Double) : EvalResult()
    data class Error(val message: String) : EvalResult()
    data object Empty : EvalResult()
}

/**
 * Safely evaluates mathematical expressions like "120+80", "500-120", "50×4", "1000÷5".
 * Respects standard operator precedence (× and ÷ before + and -).
 */
fun evaluateCalculatorExpression(rawExpr: String, strings: Strings): EvalResult {
    val expr = rawExpr.trim()
    if (expr.isEmpty()) return EvalResult.Empty

    // Remove any trailing operator for live preview
    var sanitized = expr
    while (sanitized.isNotEmpty() && (sanitized.last() == '+' || sanitized.last() == '-' || sanitized.last() == '×' || sanitized.last() == '÷')) {
        sanitized = sanitized.dropLast(1)
    }
    if (sanitized.isEmpty()) return EvalResult.Empty

    // Tokenize numbers and operators
    val numbers = mutableListOf<Double>()
    val operators = mutableListOf<Char>()

    var i = 0
    var currentNumber = StringBuilder()

    // Handle leading negative sign
    if (sanitized.startsWith('-')) {
        currentNumber.append('-')
        i = 1
    }

    while (i < sanitized.length) {
        val c = sanitized[i]
        if (c.isDigit() || c == '.') {
            currentNumber.append(c)
        } else if (c == '+' || c == '-' || c == '×' || c == '÷') {
            val numStr = currentNumber.toString()
            if (numStr.isNotEmpty()) {
                val parsed = numStr.toDoubleOrNull() ?: return EvalResult.Error(strings.invalidExpressionError)
                numbers.add(parsed)
                currentNumber = StringBuilder()
            }
            operators.add(c)
        }
        i++
    }

    val finalNumStr = currentNumber.toString()
    if (finalNumStr.isNotEmpty()) {
        val parsed = finalNumStr.toDoubleOrNull() ?: return EvalResult.Error(strings.invalidExpressionError)
        numbers.add(parsed)
    }

    if (numbers.isEmpty()) return EvalResult.Empty
    if (numbers.size == 1 && operators.isEmpty()) return EvalResult.Success(numbers.first())
    if (numbers.size <= operators.size) return EvalResult.Error(strings.invalidExpressionError)

    // Pass 1: Handle high-precedence operators (× and ÷)
    var numIdx = 0
    val reducedNumbers = mutableListOf<Double>()
    val reducedOperators = mutableListOf<Char>()

    reducedNumbers.add(numbers[0])

    for (opIdx in operators.indices) {
        val op = operators[opIdx]
        val nextNum = numbers[opIdx + 1]

        if (op == '×') {
            val lastIdx = reducedNumbers.size - 1
            reducedNumbers[lastIdx] = reducedNumbers[lastIdx] * nextNum
        } else if (op == '÷') {
            if (nextNum == 0.0) {
                return EvalResult.Error(strings.divideByZeroError)
            }
            val lastIdx = reducedNumbers.size - 1
            reducedNumbers[lastIdx] = reducedNumbers[lastIdx] / nextNum
        } else {
            reducedOperators.add(op)
            reducedNumbers.add(nextNum)
        }
    }

    // Pass 2: Handle low-precedence operators (+ and -)
    var result = reducedNumbers[0]
    for (opIdx in reducedOperators.indices) {
        val op = reducedOperators[opIdx]
        val nextNum = reducedNumbers[opIdx + 1]
        if (op == '+') {
            result += nextNum
        } else if (op == '-') {
            result -= nextNum
        }
    }

    return EvalResult.Success(result)
}
