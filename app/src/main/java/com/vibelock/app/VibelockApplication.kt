package com.vibelock.app

import android.app.Application
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.vibelock.app.workers.GeminiNotificationWorker
import java.util.concurrent.TimeUnit

class VibelockApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        
        // Enqueue the periodic notification worker
        val workRequest = PeriodicWorkRequestBuilder<GeminiNotificationWorker>(12, TimeUnit.HOURS)
            .build()
            
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "GeminiNotificationWorker",
            androidx.work.ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}
