package com.project.weather.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.project.weather.model.FavoriteLocation

@Database(entities = [FavoriteLocation::class], version = 1, exportSchema = false)
abstract class FavoriteDatabase : RoomDatabase(){
    abstract fun getFavoriteDao(): FavoriteDAO

    companion object {
        @Volatile
        private var _instance: FavoriteDatabase? = null
        fun getInstance(context: Context): FavoriteDatabase {
            return _instance ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    FavoriteDatabase::class.java,
                    "Product_database"
                ).build()
                _instance = instance
                instance
            }
        }
    }
}