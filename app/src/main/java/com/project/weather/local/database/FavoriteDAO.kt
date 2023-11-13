package com.project.weather.local.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.project.weather.model.AlertItem
import com.project.weather.model.FavoriteLocation
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addToFavorite(location: FavoriteLocation): Long

    @Delete
    suspend fun deleteFromFavorite(location: FavoriteLocation): Int

    @Query("SELECT * FROM favorite_table")
    fun getAllFavoriteLocations(): Flow<List<FavoriteLocation>>

    @Update
    suspend fun updateAlert(location: FavoriteLocation)
}