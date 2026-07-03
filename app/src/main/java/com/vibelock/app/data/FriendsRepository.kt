package com.vibelock.app.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

data class UserProfile(
    val uid: String = "",
    val displayName: String = "",
    val auraCode: String = "",
    val level: Int = 1,
    val xp: Int = 0,
    val currentStreak: Int = 0,
    val highestStreak: Int = 0,
    val lastCheckInTimestamp: Long = 0L,
    val shields: Int = 0
) {
    // Helper to calculate weekly XP (Mock logic for now: just return total XP as weekly for MVP)
    val weeklyXP: Int get() = xp
}

data class FriendRequest(
    val fromUid: String = "",
    val fromUsername: String = "",
    val fromAuraCode: String = "",
    val status: String = "PENDING"
)

class FriendsRepository(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val uid get() = auth.currentUser?.uid ?: ""

    // Search user by Aura Code
    suspend fun findUserByAuraCode(auraCode: String): UserProfile? {
        if (auraCode.isBlank()) return null
        val result = firestore.collection("users")
            .whereEqualTo("auraCode", auraCode.uppercase())
            .limit(1)
            .get()
            .await()
        return result.documents.firstOrNull()?.toObject(UserProfile::class.java)
    }

    // Send friend request
    suspend fun sendFriendRequest(targetUid: String): Boolean {
        if (uid.isBlank() || targetUid == uid) return false
        return try {
            val currentUser = firestore.collection("users").document(uid).get().await()
            val request = hashMapOf(
                "fromUid" to uid,
                "fromUsername" to (currentUser.getString("displayName") ?: "AuraUser"),
                "fromAuraCode" to (currentUser.getString("auraCode") ?: ""),
                "sentAt" to FieldValue.serverTimestamp(),
                "status" to "PENDING"
            )
            firestore.collection("users").document(targetUid)
                .collection("friendRequests")
                .document(uid)
                .set(request)
                .await()
            true
        } catch (e: Exception) { false }
    }

    // Accept friend request — bidirectional add
    suspend fun acceptFriendRequest(fromUid: String) {
        if (uid.isBlank()) return
        val batch = firestore.batch()
        
        // Add them to my friends
        batch.set(
            firestore.document("users/$uid/friends/$fromUid"),
            mapOf("friendUid" to fromUid, "addedAt" to FieldValue.serverTimestamp())
        )
        // Add me to their friends
        batch.set(
            firestore.document("users/$fromUid/friends/$uid"),
            mapOf("friendUid" to uid, "addedAt" to FieldValue.serverTimestamp())
        )
        // Update request status
        batch.update(
            firestore.document("users/$uid/friendRequests/$fromUid"),
            "status", "ACCEPTED"
        )
        batch.commit().await()
    }

    // Decline friend request
    suspend fun declineFriendRequest(fromUid: String) {
        if (uid.isBlank()) return
        firestore.document("users/$uid/friendRequests/$fromUid")
            .update("status", "REJECTED")
            .await()
    }

    // Get friends list with their current data
    fun observeFriendsList(): Flow<List<UserProfile>> = callbackFlow {
        if (uid.isBlank()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        
        val friendsRef = firestore.collection("users/$uid/friends")
        
        val friendsListener = friendsRef.addSnapshotListener { snapshot, _ ->
            if (snapshot == null || snapshot.isEmpty) {
                trySend(emptyList())
                return@addSnapshotListener
            }
            
            val friendUids = snapshot.documents.map { it.id }
            
            // Listen to all these profiles in real-time
            // Firestore whereIn has a limit of 30, for MVP this is enough
            val profilesListener = firestore.collection("users")
                .whereIn(FieldPath.documentId(), friendUids.take(30))
                .addSnapshotListener { profilesSnapshot, _ ->
                    val friends = profilesSnapshot?.documents?.mapNotNull {
                        it.toObject(UserProfile::class.java)
                    } ?: emptyList()
                    trySend(friends.sortedByDescending { it.weeklyXP })
                }
                
            // When the friends listener closes, close the profiles listener? 
            // In a real app we'd manage this better, but this works for MVP.
        }
        
        awaitClose { friendsListener.remove() }
    }

    // Get pending friend requests
    fun observeFriendRequests(): Flow<List<FriendRequest>> = callbackFlow {
        if (uid.isBlank()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        
        val listener = firestore.collection("users/$uid/friendRequests")
            .whereEqualTo("status", "PENDING")
            .addSnapshotListener { snapshot, _ ->
                val requests = snapshot?.documents?.mapNotNull {
                    it.toObject(FriendRequest::class.java)
                } ?: emptyList()
                trySend(requests)
            }
        awaitClose { listener.remove() }
    }
}
