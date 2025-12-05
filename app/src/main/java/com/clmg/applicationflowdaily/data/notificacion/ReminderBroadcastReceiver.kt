package com.clmg.applicationflowdaily.data.notificacion

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.clmg.applicationflowdaily.R
import com.clmg.applicationflowdaily.MainActivity
import java.text.SimpleDateFormat
import java.util.*

class ReminderBroadcastReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "ReminderBroadcast"
        private const val CHANNEL_ID = "reminder_channel"
        const val ACTION_POSTPONE = "com.clmg.applicationflowdaily.ACTION_POSTPONE"
        const val ACTION_ACCEPT = "com.clmg.applicationflowdaily.ACTION_ACCEPT"
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "Notificacion recibida - Action: ${intent.action}")

        val reminderId = intent.getStringExtra("reminderId") ?: return
        val title = intent.getStringExtra("title") ?: "Recordatorio"
        val description = intent.getStringExtra("description") ?: ""

        when (intent.action) {
            ACTION_POSTPONE -> {
                Log.d(TAG, "Posponiendo recordatorio: $reminderId")
                postponeReminder(context, reminderId, title, description)
            }
            ACTION_ACCEPT -> {
                Log.d(TAG, "Aceptando recordatorio: $reminderId")
                dismissNotification(context, reminderId)
            }
            else -> {
                // Primera notificacion - mostrar con botones
                showNotificationWithActions(context, reminderId, title, description)
            }
        }
    }

    /**
     * Muestra la notificacion con botones Posponer/Aceptar
     */
    private fun showNotificationWithActions(
        context: Context,
        reminderId: String,
        title: String,
        description: String
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Intent para abrir la app al tocar la notificacion
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            context,
            reminderId.hashCode(),
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Intent para POSPONER
        val postponeIntent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
            action = ACTION_POSTPONE
            putExtra("reminderId", reminderId)
            putExtra("title", title)
            putExtra("description", description)
        }
        val postponePendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId.hashCode() + 1,
            postponeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Intent para ACEPTAR
        val acceptIntent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
            action = ACTION_ACCEPT
            putExtra("reminderId", reminderId)
        }
        val acceptPendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId.hashCode() + 2,
            acceptIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Construir notificacion
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(openAppPendingIntent)
            .addAction(android.R.drawable.ic_menu_recent_history, "Posponer 5 min", postponePendingIntent)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Aceptar", acceptPendingIntent)
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .build()

        notificationManager.notify(reminderId.hashCode(), notification)
        Log.d(TAG, "Notificacion mostrada: $title")
    }

    /**
     * Pospone el recordatorio 5 minutos
     */
    private fun postponeReminder(
        context: Context,
        reminderId: String,
        title: String,
        description: String
    ) {
        // Calcular nueva fecha/hora (+5 minutos)
        val newCalendar = Calendar.getInstance().apply {
            add(Calendar.MINUTE, 5)
        }

        val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
        val timeFormatter = SimpleDateFormat("hh:mm a", Locale.ENGLISH)

        val newDate = dateFormatter.format(newCalendar.time)
        val newTime = timeFormatter.format(newCalendar.time)

        Log.d(TAG, "Nueva fecha/hora: $newDate - $newTime")

        // Programar nueva notificacion
        val notificationManager = ReminderNotificationManager(context)
        notificationManager.scheduleReminder(reminderId, title, description, newDate, newTime)

        // Cancelar notificacion actual
        dismissNotification(context, reminderId)

        Log.d(TAG, "Recordatorio pospuesto 5 minutos")
    }

    /**
     * Cancela/cierra la notificacion
     */
    private fun dismissNotification(context: Context, reminderId: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(reminderId.hashCode())
        Log.d(TAG, "Notificacion cerrada: $reminderId")
    }
}