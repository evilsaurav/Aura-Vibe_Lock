package com.vibelock.app.data

import android.annotation.SuppressLint
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

data class GlobalCheckIn(
    val userId: String = "",
    val displayName: String = "",
    val vibe: String = "",
    val colorHex: Long = 0xFFFFFFFF,
    val lat: Double = 0.0,
    val lon: Double = 0.0,
    val timestamp: Long = 0L
)

class GlobalRepository(
    private val firestore: FirebaseFirestore,
    private val fusedLocationClient: FusedLocationProviderClient
) {
    @SuppressLint("MissingPermission")
    suspend fun fetchCurrentLocation(): Location? {
        return try {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null).await()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun broadcastCheckIn(
        userId: String,
        displayName: String,
        vibe: String,
        colorHex: Long,
        lat: Double,
        lon: Double
    ) {
        // Privacy Jitter: Add a random offset between -0.05 and +0.05 degrees (~5.5km radius)
        // This ensures the user's exact house/street location is never uploaded to Firestore
        val randomLatJitter = (Math.random() - 0.5) * 0.1
        val randomLonJitter = (Math.random() - 0.5) * 0.1
        
        val safeLat = lat + randomLatJitter
        val safeLon = lon + randomLonJitter

        val checkIn = GlobalCheckIn(
            userId = userId,
            displayName = displayName,
            vibe = vibe,
            colorHex = colorHex,
            lat = safeLat,
            lon = safeLon,
            timestamp = System.currentTimeMillis()
        )
        
        try {
            firestore.collection("global_checkins")
                .document(userId)
                .set(checkIn)
                .await()
        } catch (e: Exception) {
            // Silently fail if offline
        }
    }

    fun getRecentCheckInsFlow(): Flow<List<GlobalCheckIn>> = callbackFlow {
        val cutoffTime = System.currentTimeMillis() - (24 * 60 * 60 * 1000)
        
        val subscription = firestore.collection("global_checkins")
            .whereGreaterThan("timestamp", cutoffTime)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(100)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val checkIns = snapshot.documents.mapNotNull { it.toObject(GlobalCheckIn::class.java) }
                    trySend(checkIns)
                }
            }
            
        awaitClose { subscription.remove() }
    }
}
