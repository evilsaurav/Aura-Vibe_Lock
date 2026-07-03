package com.vibelock.app.data

import com.vibelock.app.engine.UserState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val userDao: UserDao,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    // Expose flow for reactive Compose UI updates
    val userStateFlow: Flow<UserState> = userDao.getUserStateFlow().map { entity ->
        entity?.let {
            UserState(
                level = it.level,
                xp = it.xp,
                currentStreak = it.currentStreak,
                highestStreak = it.highestStreak,
                lastCheckInTimestamp = it.lastCheckInTimestamp,
                auraCode = it.auraCode,
                shields = it.shields,
                lastMissionDate = it.lastMissionDate,
                completedMissionIds = it.completedMissionIds,
                avatarUrl = it.avatarUrl
            )
        } ?: UserState()
    }

    suspend fun getCurrentState(): UserState {
        val entity = userDao.getUserState()
        return entity?.let {
            UserState(
                level = it.level,
                xp = it.xp,
                currentStreak = it.currentStreak,
                highestStreak = it.highestStreak,
                lastCheckInTimestamp = it.lastCheckInTimestamp,
                auraCode = it.auraCode,
                shields = it.shields,
                lastMissionDate = it.lastMissionDate,
                completedMissionIds = it.completedMissionIds,
                avatarUrl = it.avatarUrl
            )
        } ?: UserState()
    }

    suspend fun saveState(state: UserState) {
        val finalAuraCode = if (state.auraCode.isEmpty()) {
            val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
            val randomString = (1..6).map { chars.random() }.joinToString("")
            "AURA-$randomString"
        } else {
            state.auraCode
        }

        val entity = UserEntity(
            id = 1,
            level = state.level,
            xp = state.xp,
            currentStreak = state.currentStreak,
            highestStreak = state.highestStreak,
            lastCheckInTimestamp = state.lastCheckInTimestamp,
            auraCode = finalAuraCode,
            shields = state.shields,
            lastMissionDate = state.lastMissionDate,
            completedMissionIds = state.completedMissionIds,
            avatarUrl = state.avatarUrl
        )
        userDao.saveUserState(entity)
        
        // Phase B: Sync to Firestore if user is logged in
        val user = auth.currentUser
        if (user != null) {
            val userProfile = hashMapOf(
                "uid" to user.uid,
                "displayName" to (user.displayName ?: "AuraUser"),
                "level" to state.level,
                "xp" to state.xp,
                "currentStreak" to state.currentStreak,
                "highestStreak" to state.highestStreak,
                "lastCheckInTimestamp" to state.lastCheckInTimestamp,
                "auraCode" to finalAuraCode,
                "shields" to state.shields,
                "lastMissionDate" to state.lastMissionDate,
                "completedMissionIds" to state.completedMissionIds,
                "avatarUrl" to state.avatarUrl
            )
            try {
                firestore.collection("users").document(user.uid).set(userProfile).await()
            } catch (e: Exception) {
                // Silently fail if offline, Room will preserve state
            }
        }
    }
}
