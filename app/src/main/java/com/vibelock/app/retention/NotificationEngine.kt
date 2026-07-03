package com.vibelock.app.retention

import android.util.Log

enum class NotificationPriority {
    P1_AURA_TIME,
    P2_STREAK_RISK,
    P3_SOCIAL_FOMO,
    P4_BOX_WAITING,
    P5_FLASH_EVENT,
    P6_WEEKLY_REPORT
}

data class AuraNotification(
    val priority: NotificationPriority,
    val title: String,
    val message: String
)

object NotificationEngine {

    // Simulating sending a notification
    fun scheduleNotification(priority: NotificationPriority, userName: String, streak: Int) {
        val notif = generateMessage(priority, userName, streak)
        // In reality, hook this up to WorkManager / AlarmManager
        Log.d("AURA_NOTIF", "Scheduled Priority ${priority.name}: ${notif.title} - ${notif.message}")
    }

    private fun generateMessage(priority: NotificationPriority, name: String, streak: Int): AuraNotification {
        return when (priority) {
            NotificationPriority.P1_AURA_TIME -> AuraNotification(
                priority = priority,
                title = "⚡ AURA TIME!",
                message = "your crew is vibing — don't miss it!"
            )
            NotificationPriority.P2_STREAK_RISK -> AuraNotification(
                priority = priority,
                title = "⚠️ your streak said 'help' 🙏",
                message = "ayo $name, your $streak-day streak ends in 4 hours. the check-in is RIGHT THERE bro."
            )
            NotificationPriority.P3_SOCIAL_FOMO -> AuraNotification(
                priority = priority,
                title = "💀 Rank Stolen",
                message = "someone just passed you on the leaderboard. embarrassing."
            )
            NotificationPriority.P4_BOX_WAITING -> AuraNotification(
                priority = priority,
                title = "🎁 Unopened Loot",
                message = "you literally have 3 Aura Boxes waiting to be opened. open them."
            )
            NotificationPriority.P5_FLASH_EVENT -> AuraNotification(
                priority = priority,
                title = "⚡ 2X XP FLASH EVENT",
                message = "ACTIVE NOW — 58 minutes remaining! Drop everything."
            )
            NotificationPriority.P6_WEEKLY_REPORT -> AuraNotification(
                priority = priority,
                title = "📊 Aura Report Drop",
                message = "your weekly aura report is ready to roast you. check it out."
            )
        }
    }
}
