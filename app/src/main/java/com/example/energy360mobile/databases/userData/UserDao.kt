package com.example.energy360mobile.databases.userData

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for the user table.
 * Provides methods for interacting with the local user database using Room.
 */
@Dao
interface UserDao {

    /**
     * Inserts a user into the database. If the user already exists, it will be replaced.
     *
     * @param user The [UserEntity] to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity)

    /**
     * Retrieves the single user entry stored in the database.
     * Returns a [Flow] to observe changes to the user data reactively.
     *
     * @return A [Flow] emitting the [UserEntity], or null if no user is found.
     */
    @Query("SELECT * FROM userData LIMIT 1")
    fun getUser(): Flow<UserEntity?>

    /**
     * Deletes all entries from the user table.
     */
    @Query("DELETE FROM userData")
    suspend fun clearAll()
}
