package com.project.weather.home.view

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.view.drawToBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.project.weather.MainActivity
import com.project.weather.R
import com.project.weather.SharedViewModel
import com.project.weather.SharedViewModelFactory
import com.project.weather.constants.Constants
import com.project.weather.databinding.FragmentHomeBinding
import com.project.weather.home.viewmodel.HomeViewModel
import com.project.weather.home.viewmodel.HomeViewModelFactory
import com.project.weather.local.ConcreteLocalSource
import com.project.weather.model.State
import com.project.weather.model.WeatherResponse
import com.project.weather.network.WeatherClient
import com.project.weather.repo.PreferenceRepo
import com.project.weather.repo.Repo
import com.project.weather.utils.celsiusToFahrenheit
import com.project.weather.utils.celsiusToKelvin
import com.project.weather.utils.collectLatestFlowOnLifecycle
import com.project.weather.utils.getDateAndTime
import com.project.weather.utils.getIconLink
import com.project.weather.utils.metersPerSecondToMilesPerHour
import java.util.Locale

class HomeFragment : Fragment() {
    private val TAG = "TAG HomeFragment"
    private lateinit var binding: FragmentHomeBinding
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var hourlyAdapter: HourlyAdapter
    private lateinit var dailyAdapter: DailyAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(TAG, "onViewCreated: ")
        init()

        binding.homeSwipeRefresh.setOnRefreshListener {
            binding.homeSwipeRefresh.isRefreshing = false
            homeViewModel.getHomeWeatherData()
        }

        collectLatestFlowOnLifecycle(homeViewModel.weatherDataApiStateFlow) { state ->
            //Log.i(TAG, "weather state result $state")
            when (state) {
                is State.Failure -> {
                    setFailureState(state.error)
                }

                is State.Loading -> setLoadingState()

                is State.Success -> {
                    state.data?.let { weatherData -> setSuccessState(weatherData) }
                }
            }
        }

        collectLatestFlowOnLifecycle(homeViewModel.getHomeLocationSource()) { source ->
            when (source) {
                Constants.PREF_LOCATION_GPS -> {
                    binding.changeMapLocationBtn.visibility = View.GONE
                }

                Constants.PREF_LOCATION_MAP -> {
                    binding.changeMapLocationBtn.visibility = View.VISIBLE
                    binding.changeMapLocationBtn.startAnimation(
                        AnimationUtils.loadAnimation(
                            requireContext(),
                            R.anim.from_bottom
                        )
                    )
                }

                else -> {}
            }
        }

        binding.changeMapLocationBtn.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_mapFragment)
        }
    }

    private fun init() {
        val viewModelFactory = HomeViewModelFactory(
            Repo.getInstance(
                WeatherClient,
                ConcreteLocalSource.getInstance(requireContext())
            ),
            ViewModelProvider(
                requireActivity(),
                SharedViewModelFactory(PreferenceRepo.getInstance(requireContext()))
            )[SharedViewModel::class.java]
        )
        homeViewModel = ViewModelProvider(this, viewModelFactory)[HomeViewModel::class.java]
        hourlyAdapter = HourlyAdapter(requireContext())
        dailyAdapter = DailyAdapter(requireContext())
        binding.hourlyRecyclerV.adapter = hourlyAdapter
        binding.dailyRecyclerV.adapter = dailyAdapter
        //binding.homeSwipeRefresh.isRefreshing = true
    }

    private fun setLoadingState() {
        binding.apply {
            progressTxt.visibility = View.VISIBLE
            progressTxt.text = getString(R.string.updating)
        }
    }

    private fun setFailureState(error: String) {
        binding.apply {
            progressTxt.text = getString(R.string.update_failed)
        }
        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
    }

    private fun setSuccessState(weatherData: WeatherResponse) {
        Log.i(TAG, "update ui views: ")
        binding.apply {
            progressTxt.text = getString(R.string.update_success)
            setDataOnViews(weatherData)
        }
    }

    private fun setDataOnViews(weatherData: WeatherResponse) {
        val currentDate = getDateAndTime(weatherData.current.dt)
        val sunrise = getDateAndTime(weatherData.current.sunrise)
        val sunset = getDateAndTime(weatherData.current.sunset)
        binding.apply {
            when (Locale.getDefault().language) {
                "ar" -> cityNameTxtV.text = weatherData.nameAr
                "en" -> cityNameTxtV.text = weatherData.nameEn
                else -> cityNameTxtV.text = weatherData.timezone
            }
            dateTxtV.text =
                "${currentDate[Constants.DAY_OF_WEEK_KEY]}, ${currentDate[Constants.MONTH_KEY]} ${currentDate[Constants.DAY_OF_MONTH_KEY]}, ${currentDate[Constants.YEAR_KEY]}"
            timeTxtV.text = "${currentDate[Constants.TIME_KEY]} ${currentDate[Constants.AM_PM_KEY]}"
            Glide.with(requireContext())
                .load(getIconLink(weatherData.current.weather[0].icon))
                .placeholder(R.drawable.weather_icon_placeholder)
                .into(currentWeatherIcon)
            currentWeatherDescriptionTxtV.text = weatherData.current.weather[0].description
            currentTempTxtV.text = weatherData.current.temp.toInt().toString()
            currentFeelsLikeValueTxt.text = weatherData.current.feelsLike.toInt().toString()
            humidityValueTxtV.text = weatherData.current.humidity.toString()
            windSpeedValueTxtV.text = weatherData.current.windSpeed.toString()
            pressureValueTxtV.text = weatherData.current.pressure.toString()
            cloudsValueTxtV.text = weatherData.current.clouds.toString()
            sunriseValueTxtV.text = "${sunrise[Constants.TIME_KEY]} ${sunrise[Constants.AM_PM_KEY]}"
            sunsetValueTxtV.text = "${sunset[Constants.TIME_KEY]} ${sunset[Constants.AM_PM_KEY]}"
            hourlyAdapter.submitList(weatherData.hourly.take(25))
            val layoutAnimationController =
                AnimationUtils.loadLayoutAnimation(requireContext(), R.anim.layout_animation_slide)
            binding.hourlyRecyclerV.apply {
                visibility = View.VISIBLE
                layoutAnimation = layoutAnimationController
                scheduleLayoutAnimation()
            }
            val dailyList = weatherData.daily.toMutableList()
            dailyList.removeAt(0)
            dailyAdapter.submitList(dailyList)
            checkUnits()
            homeScrollView.visibility = View.VISIBLE
            progressTxt.visibility = View.GONE
        }
    }

    private fun checkUnits() {
        collectLatestFlowOnLifecycle(homeViewModel.getTemperatureUnit()) { tempUnit ->
            when (tempUnit) {
                Constants.PREF_TEMP_C -> {
                    binding.apply {
                        currentTempUnitTxtV.text = resources.getString(R.string.celsius)
                        fellsLikeTempUnitTxtV.text = resources.getString(R.string.celsius)
                    }
                    hourlyAdapter.tempUnit.value = tempUnit
                    dailyAdapter.tempUnit.value = tempUnit
                }

                Constants.PREF_TEMP_K -> {
                    binding.apply {
                        currentTempUnitTxtV.text = resources.getString(R.string.kelvin)
                        fellsLikeTempUnitTxtV.text = resources.getString(R.string.kelvin)
                        currentTempTxtV.text =
                            celsiusToKelvin(currentTempTxtV.text.toString().toDouble()).toString()
                        currentFeelsLikeValueTxt.text =
                            celsiusToKelvin(
                                currentFeelsLikeValueTxt.text.toString().toDouble()
                            ).toString()
                    }
                    hourlyAdapter.tempUnit.value = tempUnit
                    dailyAdapter.tempUnit.value = tempUnit
                }

                Constants.PREF_TEMP_F -> {
                    binding.apply {
                        currentTempUnitTxtV.text = resources.getString(R.string.fahrenheit)
                        fellsLikeTempUnitTxtV.text = resources.getString(R.string.fahrenheit)
                        currentTempTxtV.text =
                            celsiusToFahrenheit(
                                currentTempTxtV.text.toString().toDouble()
                            ).toString()
                        currentFeelsLikeValueTxt.text =
                            celsiusToFahrenheit(
                                currentFeelsLikeValueTxt.text.toString().toDouble()
                            ).toString()
                    }
                    hourlyAdapter.tempUnit.value = tempUnit
                    dailyAdapter.tempUnit.value = tempUnit
                }
            }
        }

        collectLatestFlowOnLifecycle(homeViewModel.getSpeedUnit()) { speedUnit ->
            when (speedUnit) {
                Constants.PREF_SPEED_METER -> binding.windSpeedUnitTxtV.text =
                    resources.getString(R.string.wind_speed_unit_meter)

                Constants.PREF_SPEED_MILE -> {
                    binding.apply {
                        windSpeedUnitTxtV.text = resources.getString(R.string.wind_speed_unit_mile)
                        windSpeedValueTxtV.text = metersPerSecondToMilesPerHour(
                            windSpeedValueTxtV.text.toString().toDouble()
                        ).toString()
                    }
                }
            }
        }
    }
}