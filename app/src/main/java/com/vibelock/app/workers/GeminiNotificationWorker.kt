package com.vibelock.app.workers

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.vibelock.app.MainActivity
import com.vibelock.app.R
import com.vibelock.app.data.AppDatabase
import com.vibelock.app.data.UserRepository
import kotlinx.coroutines.flow.firstOrNull

class GeminiNotificationWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val db = AppDatabase.getDatabase(context)
            val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
            val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()
            val userRepository = UserRepository(db.userDao(), auth, firestore)
            val userState = userRepository.userStateFlow.firstOrNull()
            
            // Only notify if user exists and hasn't checked in recently
            if (userState != null) {
                val timeSinceLastCheckIn = System.currentTimeMillis() - userState.lastCheckInTimestamp
                val twelveHoursInMillis = 12 * 60 * 60 * 1000L
                
                if (timeSinceLastCheckIn >= twelveHoursInMillis) {
                    showNotification(
                        title = "🔥 Streak at Risk!",
                        message = "AURA BOT: Your vibe is fading. Lock in now before you lose your ${userState.currentStreak}-day streak!"
                    )
                } else {
                    // Maybe show a hype message if they did check in
                    val randomHype = listOf(
                        "AURA BOT: The global matrix feels your presence.",
                        "AURA BOT: Your ${userState.currentVibe} vibe is radiating.",
                        "AURA BOT: Check your AI Insights for the week."
                    ).random()
                    showNotification(
                        title = "AURA Matrix Active ✨",
                        message = randomHype
                    )
                }
            }
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

    private fun showNotification(title: String, message: String) {
        val channelId = "aura_alerts"
        
        // Create Channel for Android O+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Aura Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Daily reminders and AI Insights"
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher) // Fallback icon
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setColor(0xFF8B5CF6.toInt()) // Neon Purple

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || 
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            with(NotificationManagerCompat.from(context)) {
                notify(System.currentTimeMillis().toInt(), builder.build())
            }
        }
    }
}
