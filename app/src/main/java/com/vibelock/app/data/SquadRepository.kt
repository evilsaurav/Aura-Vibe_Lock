package com.vibelock.app.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

data class Squad(
    val squadId: String = "",
    val name: String = "",
    val createdBy: String = "",
    val members: List<String> = emptyList(), // list of UIDs
    val memberAuraCodes: List<String> = emptyList(), // list of AuraCodes for display
    val createdAt: Long = 0L
)

data class SquadVibe(
    val uid: String = "",
    val auraCode: String = "",
    val vibe: String = "",
    val timestamp: Long = 0L
)

class SquadRepository(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val uid get() = auth.currentUser?.uid ?: ""

    suspend fun createSquad(name: String, memberUids: List<String>, memberAuraCodes: List<String>): String {
        val squadRef = firestore.collection("squads").document()
        val squad = Squad(
            squadId = squadRef.id,
            name = name,
            createdBy = uid,
            members = memberUids,
            memberAuraCodes = memberAuraCodes,
            createdAt = System.currentTimeMillis()
        )
        squadRef.set(squad).await()
        return squadRef.id
    }

    fun getUserSquads(): Flow<List<Squad>> = callbackFlow {
        if (uid.isEmpty()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = firestore.collection("squads")
            .whereArrayContains("members", uid)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val squads = snapshot.documents.mapNotNull { it.toObject(Squad::class.java) }
                    trySend(squads)
                }
            }
        
        awaitClose { listener.remove() }
    }

    fun getSquadDailyVibes(squadId: String, dateString: String): Flow<Map<String, SquadVibe>> = callbackFlow {
        val listener = firestore.collection("squads").document(squadId)
            .collection("daily_vibes").document(dateString)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyMap())
                    return@addSnapshotListener
                }
                
                if (snapshot != null && snapshot.exists()) {
                    val vibesMap = mutableMapOf<String, SquadVibe>()
                    snapshot.data?.forEach { (memberUid, data) ->
                        val map = data as? Map<String, Any>
                        if (map != null) {
                            vibesMap[memberUid] = SquadVibe(
                                uid = memberUid,
                                auraCode = map["auraCode"] as? String ?: "",
                                vibe = map["vibe"] as? String ?: "",
                                timestamp = map["timestamp"] as? Long ?: 0L
                            )
                        }
                    }
                    trySend(vibesMap)
                } else {
                    trySend(emptyMap())
                }
            }
            
        awaitClose { listener.remove() }
    }

    suspend fun broadcastSquadVibe(squadIds: List<String>, auraCode: String, vibe: String, timestamp: Long) {
        if (uid.isEmpty()) return
        val dateString = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date(timestamp))
        
        val batch = firestore.batch()
        for (squadId in squadIds) {
            val docRef = firestore.collection("squads").document(squadId)
                .collection("daily_vibes").document(dateString)
            
            val vibeData = mapOf(
                uid to mapOf(
                    "auraCode" to auraCode,
                    "vibe" to vibe,
                    "timestamp" to timestamp
                )
            )
            batch.set(docRef, vibeData, com.google.firebase.firestore.SetOptions.merge())
        }
        
        try {
            batch.commit().await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
