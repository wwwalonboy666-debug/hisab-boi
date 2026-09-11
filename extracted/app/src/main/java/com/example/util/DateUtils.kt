package com.example.util

import com.example.model.AppLanguage
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {

    private val banglaMonths = listOf(
        "জানুয়ারি", "ফেব্রুয়ারি", "মার্চ", "এপ্রিল", "মে", "জুন",
        "জুলাই", "আগস্ট", "সেপ্টেম্বর", "অক্টোবর", "নভেম্বর", "ডিসেম্বর"
    )

    private val englishMonths = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    fun getMonthName(monthIndexZeroBased: Int, language: AppLanguage): String {
        val safeIndex = monthIndexZeroBased.coerceIn(0, 11)
        return if (language == AppLanguage.BN) {
            banglaMonths[safeIndex]
        } else {
            englishMonths[safeIndex]
        }
    }

    fun formatMonthYear(year: Int, monthIndexZeroBased: Int, language: AppLanguage): String {
        val monthName = getMonthName(monthIndexZeroBased, language)
        return if (language == AppLanguage.BN) {
            val yearBn = CurrencyFormatter.toBanglaDigits(year.toString())
            "$monthName $yearBn"
        } else {
            "$monthName $year"
        }
    }

    fun toMonthKey(year: Int, monthIndexZeroBased: Int): String {
        val monthOneBased = monthIndexZeroBased + 1
        return "%04d-%02d".format(year, monthOneBased)
    }

    fun parseMonthKey(monthKey: String): Pair<Int, Int> {
        val parts = monthKey.split("-")
        val year = parts.getOrNull(0)?.toIntOrNull() ?: Calendar.getInstance().get(Calendar.YEAR)
        val monthOneBased = parts.getOrNull(1)?.toIntOrNull() ?: (Calendar.getInstance().get(Calendar.MONTH) + 1)
        return Pair(year, monthOneBased - 1)
    }

    fun getMonthRangeMillis(year: Int, monthIndexZeroBased: Int): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, monthIndexZeroBased)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val startMillis = cal.timeInMillis

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        val endMillis = cal.timeInMillis

        return Pair(startMillis, endMillis)
    }

    fun formatDateGroup(millis: Long, language: AppLanguage): String {
        val now = Calendar.getInstance()
        val itemCal = Calendar.getInstance().apply { timeInMillis = millis }

        val isToday = now.get(Calendar.YEAR) == itemCal.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) == itemCal.get(Calendar.DAY_OF_YEAR)

        val yesterdayCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
        val isYesterday = yesterdayCal.get(Calendar.YEAR) == itemCal.get(Calendar.YEAR) &&
                yesterdayCal.get(Calendar.DAY_OF_YEAR) == itemCal.get(Calendar.DAY_OF_YEAR)

        if (isToday) {
            return if (language == AppLanguage.BN) "আজ" else "Today"
        }
        if (isYesterday) {
            return if (language == AppLanguage.BN) "গতকাল" else "Yesterday"
        }

        val day = itemCal.get(Calendar.DAY_OF_MONTH)
        val month = itemCal.get(Calendar.MONTH)
        val year = itemCal.get(Calendar.YEAR)

        return if (language == AppLanguage.BN) {
            val dayBn = CurrencyFormatter.toBanglaDigits(day.toString())
            val monthBn = banglaMonths[month]
            val yearBn = CurrencyFormatter.toBanglaDigits(year.toString())
            "$dayBn $monthBn, $yearBn"
        } else {
            val sdf = SimpleDateFormat("d MMM, yyyy", Locale.ENGLISH)
            sdf.format(Date(millis))
        }
    }

    fun formatDisplayDate(millis: Long, language: AppLanguage): String {
        val cal = Calendar.getInstance().apply { timeInMillis = millis }
        val day = cal.get(Calendar.DAY_OF_MONTH)
        val month = cal.get(Calendar.MONTH)
        val year = cal.get(Calendar.YEAR)

        return if (language == AppLanguage.BN) {
            val dayBn = CurrencyFormatter.toBanglaDigits(day.toString())
            val monthBn = banglaMonths[month]
            val yearBn = CurrencyFormatter.toBanglaDigits(year.toString())
            "$dayBn $monthBn, $yearBn"
        } else {
            val sdf = SimpleDateFormat("d MMM, yyyy", Locale.ENGLISH)
            sdf.format(Date(millis))
        }
    }

    fun formatShortDate(millis: Long, language: AppLanguage): String {
        val cal = Calendar.getInstance().apply { timeInMillis = millis }
        val day = cal.get(Calendar.DAY_OF_MONTH)
        val month = cal.get(Calendar.MONTH)

        return if (language == AppLanguage.BN) {
            val dayBn = CurrencyFormatter.toBanglaDigits(day.toString())
            "$dayBn ${banglaMonths[month]}"
        } else {
            val sdf = SimpleDateFormat("d MMM", Locale.ENGLISH)
            sdf.format(Date(millis))
        }
    }

    fun formatTime(millis: Long, language: AppLanguage): String {
        val cal = Calendar.getInstance().apply { timeInMillis = millis }
        val hour = cal.get(Calendar.HOUR)
        val displayHour = if (hour == 0) 12 else hour
        val minute = cal.get(Calendar.MINUTE)
        val isPm = cal.get(Calendar.AM_PM) == Calendar.PM

        val timeStr = "%02d:%02d".format(displayHour, minute)
        return if (language == AppLanguage.BN) {
            val bnDigitsTime = CurrencyFormatter.toBanglaDigits(timeStr)
            val amPm = if (isPm) "অপরাহ্ণ" else "পূর্বাহ্ণ"
            "$bnDigitsTime $amPm"
        } else {
            val amPm = if (isPm) "PM" else "AM"
            "$timeStr $amPm"
        }
    }
}
