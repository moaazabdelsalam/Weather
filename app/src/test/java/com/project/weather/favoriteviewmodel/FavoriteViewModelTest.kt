package com.project.weather.favoriteviewmodel

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.project.weather.SharedViewModel
import com.project.weather.datasource.FakeLocalDatasource
import com.project.weather.datasource.FakeRemoteDatasource
import com.project.weather.favorite.viewmodel.FavoriteViewModel
import com.project.weather.model.FavoriteLocation
import com.project.weather.model.State
import com.project.weather.repo.FakePrefRepo
import com.project.weather.repo.FakeRepo
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavoriteViewModelTest {
    private lateinit var repo: FakeRepo
    private lateinit var favoriteViewModel: FavoriteViewModel

    private val databaseWeather1 =
        FavoriteLocation(12.3, 32.1, "Mahalla", "Clear", "Clear sky", "src", 2.2, 32.2)
    private val databaseWeather2 =
        FavoriteLocation(12.3, 32.1, "Mahalla", "Clear", "Clear sky", "src", 2.2, 32.2)
    private val databaseWeather3 =
        FavoriteLocation(12.3, 32.1, "Mahalla", "Clear", "Clear sky", "src", 2.2, 32.2)
    private val databaseWeather4 =
        FavoriteLocation(12.3, 32.1, "El-Mahalla", "Clear", "Clear sky", "src", 2.2, 32.2)
    private val databaseWeather5 =
        FavoriteLocation(12.3, 32.1, "Mahalla", "Clear", "Clear sky", "src", 2.2, 32.2)
    private val fakeLocalData = mutableListOf(
        databaseWeather1,
        databaseWeather2,
        databaseWeather3,
        databaseWeather4,
        databaseWeather5
    )

    @Before
    fun setup() {
        repo =
            FakeRepo(FakeLocalDatasource(mutableListOf<FavoriteLocation>()), FakeRemoteDatasource())
        favoriteViewModel = FavoriteViewModel(repo, SharedViewModel(FakePrefRepo()))
    }

    @Test
    fun getAllFavWeather_Empty() = runBlocking {
        var weatherData: List<FavoriteLocation>? = emptyList()
        val job = launch {
            favoriteViewModel.favoriteList.collect {
                if (it is State.Success) {
                    weatherData = it.data
                }
            }
        }
        assertNotEquals(fakeLocalData, weatherData)
        job.cancel()
    }

    @Test
    fun deleteFavWeather_Success() = runBlocking {
        val job = launch {
            favoriteViewModel.deleteLocationFromFavorite(databaseWeather1)
        }
        assertEquals(1, repo.deleteFromFavorite(databaseWeather1))
        job.cancel()
    }
}