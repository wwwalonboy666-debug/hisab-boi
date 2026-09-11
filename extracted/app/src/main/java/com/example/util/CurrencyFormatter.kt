package com.example.util

import com.example.model.AppLanguage
import java.text.DecimalFormat

object CurrencyFormatter {
    private val decimalFormat = DecimalFormat("#,##,##0")
    private val decimalFormatWithDecimals = DecimalFormat("#,##,##0.00")

    private val bnDigits = mapOf(
        '0' to '০',
        '1' to '১',
        '2' to '২',
        '3' to '৩',
        '4' to '৪',
        '5' to '৫',
        '6' to '৬',
        '7' to '৭',
        '8' to '৮',
        '9' to '৯'
    )

    fun format(amount: Double, language: AppLanguage = AppLanguage.BN, showFractionIfAny: Boolean = false): String {
        val isFractional = (amount % 1.0) != 0.0
        val formattedNumber = if (showFractionIfAny && isFractional) {
            decimalFormatWithDecimals.format(amount)
        } else {
            decimalFormat.format(Math.round(amount))
        }

        return if (language == AppLanguage.BN) {
            val bnNumber = formattedNumber.map { bnDigits[it] ?: it }.joinToString("")
            "৳$bnNumber"
        } else {
            "৳$formattedNumber"
        }
    }

    private val bnToEnDigits = mapOf(
        '০' to '0',
        '১' to '1',
        '২' to '2',
        '৩' to '3',
        '৪' to '4',
        '৫' to '5',
        '৬' to '6',
        '৭' to '7',
        '৮' to '8',
        '৯' to '9'
    )

    fun parseBanglaDigits(numberStr: String): String {
        return numberStr.map { bnToEnDigits[it] ?: it }.joinToString("")
    }

    fun toBanglaDigits(numberStr: String): String {
        return numberStr.map { bnDigits[it] ?: it }.joinToString("")
    }

    fun formatRawNumber(amount: Double, language: AppLanguage): String {
        val formatted = decimalFormat.format(Math.round(amount))
        return if (language == AppLanguage.BN) {
            formatted.map { bnDigits[it] ?: it }.joinToString("")
        } else {
            formatted
        }
    }
}
