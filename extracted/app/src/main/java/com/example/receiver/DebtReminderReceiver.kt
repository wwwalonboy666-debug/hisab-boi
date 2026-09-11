package com.example.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.data.HisabBoiDatabase
import com.example.model.AppLanguage
import com.example.util.CurrencyFormatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DebtReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val debtId = intent.getLongExtra(DebtReminderScheduler.EXTRA_DEBT_ID, -1L)
        if (debtId == -1L) return

        val personName = intent.getStringExtra(DebtReminderScheduler.EXTRA_PERSON_NAME) ?: "স্মরণিকা"
        val type = intent.getStringExtra(DebtReminderScheduler.EXTRA_TYPE) ?: "LENT"
        val amount = intent.getDoubleExtra(DebtReminderScheduler.EXTRA_AMOUNT, 0.0)

        val channelId = "hisabboi_debt_reminders"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            ?: return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "ধার-দেনা অনুস্মারক (Debt Reminders)",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "ধার নেওয়া ও দেওয়ার পরিশোধের তারিখের অনুস্মারক"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Verify with Room DB if debt is still active and unpaid
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = HisabBoiDatabase.getInstance(context)
                val debt = db.debtDao().getDebtByIdDirect(debtId)
                if (debt != null && debt.status != "PAID") {
                    val payments = db.debtPaymentDao().getPaymentsForDebtDirect(debtId)
                    val remaining = (debt.originalAmount - payments.sumOf { it.amount }).coerceAtLeast(0.0)
                    if (remaining > 0.0) {
                        val openAppIntent = Intent(context, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        }
                        val contentPendingIntent = PendingIntent.getActivity(
                            context,
                            debtId.toInt(),
                            openAppIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )

                        val amountStr = CurrencyFormatter.format(remaining, AppLanguage.BN)
                        val title = "ধার পরিশোধের রিমাইন্ডার 🌿"
                        val content = if (type == "LENT") {
                            "$personName-এর কাছে $amountStr পাওনা রয়েছে।"
                        } else {
                            "$personName-কে $amountStr পরিশোধের তারিখ এগিয়ে আসছে।"
                        }

                        val notification = NotificationCompat.Builder(context, channelId)
                            .setSmallIcon(android.R.drawable.ic_dialog_info)
                            .setContentTitle(title)
                            .setContentText(content)
                            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
                            .setAutoCancel(true)
                            .setContentIntent(contentPendingIntent)
                            .build()

                        notificationManager.notify(debtId.toInt(), notification)
                    }
                }
            } catch (_: Exception) {
            } finally {
                pendingResult.finish()
            }
        }
    }
}
