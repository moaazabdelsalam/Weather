package com.project.weather.favorite.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.project.weather.Cache
import com.project.weather.MapActivity
import com.project.weather.ViewModelFactory
import com.project.weather.databinding.FragmentFavoriteBinding
import com.project.weather.favorite.viewmodel.FavoriteViewModel
import com.project.weather.local.database.ConcreteLocalSource
import com.project.weather.model.ApiState
import com.project.weather.model.FavoriteLocation
import com.project.weather.network.WeatherClient
import com.project.weather.repo.Repo
import com.project.weather.utils.collectLatestFlowOnLifecycle

class FavoriteFragment : Fragment() {
    private val TAG = "TAG FavoriteFragment"

    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var favoriteAdapter: FavoriteAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModelFactory = ViewModelFactory(
            Repo.getInstance(
                WeatherClient,
                ConcreteLocalSource.getInstance(requireContext())
            )
        )
        favoriteViewModel = ViewModelProvider(this, viewModelFactory)[FavoriteViewModel::class.java]
        favoriteAdapter = FavoriteAdapter()
        binding.favoriteRecyclerV.adapter = favoriteAdapter

        binding.openMapBtn.setOnClickListener {
            val intent = Intent(activity, MapActivity::class.java)
            startActivity(intent)
        }

        collectLatestFlowOnLifecycle(Cache.FavoriteLocationPoint) { favoriteLocationPoint ->
            favoriteLocationPoint?.let {
                Log.i(TAG, "adding to favorite location: ${it.altitude}")
                favoriteViewModel.getWeatherData(
                    favoriteLocationPoint.latitude,
                    favoriteLocationPoint.longitude
                )
            }
            Cache.FavoriteLocationPoint.value = null
        }
        collectLatestFlowOnLifecycle(favoriteViewModel.weatherDataStateFlow) { resultState ->
            when (resultState) {
                is ApiState.Failure -> Toast.makeText(
                    requireContext(),
                    "cann't refresh Data",
                    Toast.LENGTH_SHORT
                ).show()

                ApiState.Loading -> Log.i(TAG, "getting fav data")
                is ApiState.Successful -> {
                    resultState.data?.let {
                        val favLocation = FavoriteLocation(
                            it.lat,
                            it.lon,
                            it.timezone,
                            it.current.weather[0].main,
                            it.current.weather[0].description,
                            it.current.weather[0].icon,
                            it.daily[0].temp.min,
                            it.daily[0].temp.max
                        )
                        favoriteViewModel.addLocationToFavorite(favLocation)
                    }
                }
            }
        }
        collectLatestFlowOnLifecycle(favoriteViewModel.favoriteList) {
            favoriteAdapter.submitList(it)
        }
    }
}