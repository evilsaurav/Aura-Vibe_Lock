package com.vibelock.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vibelock.app.engine.UserState

@Entity(tableName = "user_state")
data class UserEntity(
    @PrimaryKey val id: Int = 1,
    val level: Int = 1,
    val xp: Int = 0,
    val currentStreak: Int = 0,
    val highestStreak: Int = 0,
    val lastCheckInTimestamp: Long = 0L,
    val auraCode: String = "",
    val shields: Int = 0,
    val lastMissionDate: String = "",
    val completedMissionIds: String = "",
    val avatarUrl: String = ""
) {
    fun toUserState() = UserState(
        level = level,
        xp = xp,
        currentStreak = currentStreak,
        highestStreak = highestStreak,
        lastCheckInTimestamp = lastCheckInTimestamp,
        auraCode = auraCode,
        shields = shields,
        lastMissionDate = lastMissionDate,
        completedMissionIds = completedMissionIds,
        avatarUrl = avatarUrl
    )

    companion object {
        fun fromUserState(state: UserState) = UserEntity(
            level = state.level,
            xp = state.xp,
            currentStreak = state.currentStreak,
            highestStreak = state.highestStreak,
            lastCheckInTimestamp = state.lastCheckInTimestamp,
            auraCode = state.auraCode,
            shields = state.shields,
            lastMissionDate = state.lastMissionDate,
            completedMissionIds = state.completedMissionIds,
            avatarUrl = state.avatarUrl
        )
    }
}
