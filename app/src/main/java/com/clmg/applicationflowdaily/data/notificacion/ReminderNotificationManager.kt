package com.clmg.applicationflowdaily.data.notificacion

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.clmg.applicationflowdaily.R
import java.text.SimpleDateFormat
import java.util.*

class ReminderNotificationManager(private val context: Context) {

    companion object {
        private const val TAG = "ReminderNotification"
        private const val CHANNEL_ID = "reminder_channel"
        private const val CHANNEL_NAME = "Recordatorios"
    }

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    init {
        createNotificationChannel()
    }

    /**
     * Crea el canal de notificaciones (Android 8+)
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones de recordatorios"
                enableVibration(true)
                enableLights(true)
            }
            notificationManager.createNotificationChannel(channel)
            Log.d(TAG, "✅ Canal de notificación creado")
        }
    }

    /**
     * Programa un recordatorio
     */
    fun scheduleReminder(
        reminderId: String,
        title: String,
        description: String,
        dateStr: String,
        timeStr: String
    ) {
        try {
            val triggerTime = parseDateAndTime(dateStr, timeStr)

            if (triggerTime <= System.currentTimeMillis()) {
                Log.w(TAG, "⚠️ Fecha en el pasado, no se programa: $title")
                return
            }

            val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
                putExtra("reminderId", reminderId)
                putExtra("title", title)
                putExtra("description", description)
                action = "com.clmg.applicationflowdaily.REMINDER_NOTIFICATION"
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                reminderId.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                    )
                    Log.d(TAG, "✅ Notificación programada: $title - ${Date(triggerTime)}")
                } else {
                    Log.e(TAG, "❌ No hay permiso para alarmas exactas")
                }
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
                Log.d(TAG, "✅ Notificación programada: $title - ${Date(triggerTime)}")
            }

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error programando notificación: ${e.message}", e)
        }
    }

    /**
     * Actualiza un recordatorio (cancela el anterior y programa uno nuevo)
     */
    fun updateReminder(
        reminderId: String,
        title: String,
        description: String,
        dateStr: String,
        timeStr: String
    ) {
        cancelReminder(reminderId)
        scheduleReminder(reminderId, title, description, dateStr, timeStr)
    }

    /**
     * Cancela un recordatorio programado
     */
    fun cancelReminder(reminderId: String) {
        try {
            val intent = Intent(context, ReminderBroadcastReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                reminderId.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
            Log.d(TAG, "✅ Notificación cancelada: $reminderId")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error cancelando notificación: ${e.message}", e)
        }
    }

    /**
     * Convierte fecha y hora en timestamp
     */
    private fun parseDateAndTime(dateStr: String, timeStr: String): Long {
        return try {
            val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
            val timeFormatter = SimpleDateFormat("hh:mm a", Locale.ENGLISH)

            val parsedDate = dateFormatter.parse(dateStr)
            val parsedTime = timeFormatter.parse(timeStr)

            if (parsedDate != null && parsedTime != null) {
                val calendar = Calendar.getInstance().apply {
                    time = parsedDate
                    val timeCalendar = Calendar.getInstance().apply {
                        time = parsedTime
                    }
                    set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY))
                    set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE))
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                calendar.timeInMillis
            } else {
                Log.e(TAG, "❌ Error parseando fecha/hora")
                0L
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Excepción parseando fecha: ${e.message}")
            0L
        }
    }
}