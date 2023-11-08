package com.project.weather.local.database

import android.content.Context
import com.project.weather.model.FavoriteLocation

class ConcreteLocalSource private constructor(context: Context) : LocalSource {
    private var favoriteDatabase: FavoriteDatabase = FavoriteDatabase.getInstance(context)
    private var favoriteDAO: FavoriteDAO = favoriteDatabase.getFavoriteDao()

    companion object {
        @Volatile
        private var _instance: ConcreteLocalSource? = null
        fun getInstance(context: Context): ConcreteLocalSource {
            return _instance ?: synchronized(this) {
                val instance = ConcreteLocalSource(context)
                _instance = instance
                instance
            }
        }
    }

    override suspend fun addToFavorite(location: FavoriteLocation) =
        favoriteDAO.addToFavorite(location)

    override suspend fun deleteFromFavorite(location: FavoriteLocation) =
        favoriteDAO.deleteFromFavorite(location)

    override fun getAllFavoriteLocations() = favoriteDAO.getAllFavoriteLocations()
}