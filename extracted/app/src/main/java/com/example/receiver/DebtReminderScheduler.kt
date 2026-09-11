package com.example.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.data.entity.DebtEntity

object DebtReminderScheduler {

    const val EXTRA_DEBT_ID = "extra_debt_id"
    const val EXTRA_PERSON_NAME = "extra_person_name"
    const val EXTRA_TYPE = "extra_type"
    const val EXTRA_AMOUNT = "extra_amount"
    const val EXTRA_DUE_DATE = "extra_due_date"

    fun scheduleReminder(
        context: Context,
        debt: DebtEntity,
        daysBefore: Int = 1,
        isEnabled: Boolean = true
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, DebtReminderReceiver::class.java).apply {
            putExtra(EXTRA_DEBT_ID, debt.id)
            putExtra(EXTRA_PERSON_NAME, debt.personName)
            putExtra(EXTRA_TYPE, debt.type)
            putExtra(EXTRA_AMOUNT, debt.originalAmount)
            putExtra(EXTRA_DUE_DATE, debt.dueDate ?: 0L)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            debt.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (!isEnabled || debt.dueDate == null || debt.status == "PAID") {
            alarmManager.cancel(pendingIntent)
            return
        }

        // Calculate trigger timestamp: e.g. 9:00 AM on (dueDate - daysBefore)
        val offsetMillis = daysBefore * 24L * 60L * 60L * 1000L
        val triggerTime = debt.dueDate - offsetMillis

        if (triggerTime > System.currentTimeMillis()) {
            try {
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            } catch (_: SecurityException) {
                // If exact alarm is restricted on newer OS versions, fall back gracefully
                try {
                    alarmManager.set(AlarmManager.RTC, triggerTime, pendingIntent)
                } catch (_: Exception) {}
            }
        }
    }

    fun cancelReminder(context: Context, debtId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, DebtReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            debtId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}
