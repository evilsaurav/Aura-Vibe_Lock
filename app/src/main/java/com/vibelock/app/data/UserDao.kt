package com.vibelock.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user_state WHERE id = 1")
    fun getUserStateFlow(): Flow<UserEntity?>

    @JvmSuppressWildcards
    @Query("SELECT * FROM user_state WHERE id = 1")
    suspend fun getUserState(): UserEntity?

    @JvmSuppressWildcards
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserState(userState: UserEntity): Long
}
