package com.project.weather.repo

import com.project.weather.datasource.FakeDatasource
import com.project.weather.model.FavoriteLocation
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class RepoTest {
    val databaseWeather1 =
        FavoriteLocation(12.3, 32.1, "Mahalla", "Clear", "Clear sky", "src", 2.2, 32.2)
    val databaseWeather2 =
        FavoriteLocation(12.3, 32.1, "Mahalla", "Clear", "Clear sky", "src", 2.2, 32.2)
    val databaseWeather3 =
        FavoriteLocation(12.3, 32.1, "Mahalla", "Clear", "Clear sky", "src", 2.2, 32.2)
    val databaseWeather4 =
        FavoriteLocation(12.3, 32.1, "El-Mahalla", "Clear", "Clear sky", "src", 2.2, 32.2)
    val databaseWeather5 =
        FavoriteLocation(12.3, 32.1, "Mahalla", "Clear", "Clear sky", "src", 2.2, 32.2)

    val localWeather = mutableListOf(
        databaseWeather1,
        databaseWeather2,
        databaseWeather3,
        databaseWeather4,
        databaseWeather5
    )
    val remoteSource = mutableListOf(
        databaseWeather1,
        databaseWeather2,
        databaseWeather3,
        databaseWeather4,
        databaseWeather5
    )
    lateinit var fakeDatasource: FakeDatasource
    lateinit var fakeRemoteDatasource: FakeDatasource
    lateinit var repo: Repo

    @Before
    fun setup() {
        fakeDatasource = FakeDatasource(localWeather)
        fakeRemoteDatasource = FakeDatasource(remoteSource)
        repo = Repo.getInstance(fakeRemoteDatasource, fakeDatasource)
    }

    @Test
    fun addWeather() = runBlocking {
        val testingWeather =
            FavoriteLocation(12.3, 32.00001, "Mahalla", "Clear", "Clear sky", "src", 2.2, 32.2)
        val result = repo.addToFavorite(testingWeather)
        assertEquals(result, 1L)
    }

    @Test
    fun delete() = runBlocking {
        val result = repo.deleteFromFavorite(databaseWeather4)
        assertEquals(result, 1)
    }

}