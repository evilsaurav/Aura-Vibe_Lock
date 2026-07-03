package com.vibelock.app.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.vibelock.app.ai.GeminiEngine
import com.vibelock.app.data.AppDatabase
import com.vibelock.app.data.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class GeminiNotificationWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // Retrieve User State from local Room DB
            val database = AppDatabase.getDatabase(appContext)
            // Firebase initialized, passing just to fulfill constructor though we read from local DAO
            val auth = FirebaseAuth.getInstance()
            val firestore = FirebaseFirestore.getInstance()
            
            val userRepository = UserRepository(database.userDao(), auth, firestore)
            val currentState = userRepository.getCurrentState()
            
            // Check if user already checked in today
            val isCheckedIn = (System.currentTimeMillis() - currentState.lastCheckInTimestamp) < 24 * 60 * 60 * 1000
            
            if (!isCheckedIn) {
                // User hasn't checked in, generate warning via Gemini
                val sharedPrefs = appContext.getSharedPreferences("aura_prefs", Context.MODE_PRIVATE)
                val isRoast = sharedPrefs.getBoolean("ai_tone_roast", false)
                
                val geminiEngine = GeminiEngine()
                val message = geminiEngine.generateDailyReminder(currentState.currentStreak, isRoast)
                showNotification("AURA Vibe Check", message)
            }
            
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun showNotification(title: String, message: String) {
        val channelId = "aura_alerts"
        val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create Android 8.0+ Notification Channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Aura Daily Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "AI generated daily reminders to lock your vibe"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(appContext, channelId)
            // Note: fallback icon if ic_launcher isn't valid as a small icon, Android requires transparent background icons usually
            // For now, using standard sym_def_app_icon to prevent crash
            .setSmallIcon(android.R.drawable.sym_def_app_icon)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        // ID 1001 for daily reminder
        notificationManager.notify(1001, notification)
    }
}
