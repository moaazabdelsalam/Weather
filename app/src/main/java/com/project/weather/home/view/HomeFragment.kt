package com.project.weather.home.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.project.weather.R
import com.project.weather.SharedViewModel
import com.project.weather.constants.Constants
import com.project.weather.databinding.FragmentHomeBinding
import com.project.weather.home.viewmodel.HomeViewModel
import com.project.weather.local.ConcreteLocalSource
import com.project.weather.model.ApiState
import com.project.weather.model.WeatherResponse
import com.project.weather.network.WeatherClient
import com.project.weather.repo.Repo
import com.project.weather.utils.ViewModelFactory
import com.project.weather.utils.collectLatestFlowOnLifecycle
import com.project.weather.utils.getDateAndTime
import com.project.weather.utils.getIconDrawableId

class HomeFragment : Fragment() {
    private val TAG = "TAG HomeFragment"
    private lateinit var binding: FragmentHomeBinding
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var sharedViewModel: SharedViewModel
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

        init()

        binding.homeSwipeRefresh.setOnRefreshListener {
            binding.homeSwipeRefresh.isRefreshing = false
            homeViewModel.getHomeWeatherData()
        }

        collectLatestFlowOnLifecycle(homeViewModel.weatherDataStateFlow) { state ->
            //Log.i(TAG, "weather state result $state")
            when (state) {
                is ApiState.Failure -> {
                    setFailureState(state.error)
                }

                is ApiState.Loading -> setLoadingState()

                is ApiState.Successful -> {
                    state.data?.let { weatherData -> setSuccessState(weatherData) }
                }
            }
        }
    }

    private fun init() {
        val viewModelFactory = ViewModelFactory(
            Repo.getInstance(
                WeatherClient,
                ConcreteLocalSource.getInstance(requireContext())
            ),
            ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        )
        homeViewModel = ViewModelProvider(this, viewModelFactory)[HomeViewModel::class.java]
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        hourlyAdapter = HourlyAdapter()
        dailyAdapter = DailyAdapter()
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
            //progressBar.visibility = View.GONE
            progressTxt.text = getString(R.string.update_failed)
        }
        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
    }

    private fun setSuccessState(weatherData: WeatherResponse) {
        Log.i("TAG", "update ui views: ")
        binding.apply {
            //progressBar.visibility = View.GONE
            progressTxt.text = getString(R.string.update_success)
            setDataOnViews(weatherData)
        }
    }

    private fun setDataOnViews(weatherData: WeatherResponse) {
        val currentDate = getDateAndTime(weatherData.current.dt)
        val sunrise = getDateAndTime(weatherData.current.sunrise)
        val sunset = getDateAndTime(weatherData.current.sunset)
        binding.apply {
            cityNameTxtV.text = weatherData.timezone.split("/")[1]
            dateTxtV.text =
                "${currentDate[Constants.DAY_OF_WEEK_KEY]}, ${currentDate[Constants.MONTH_KEY]} ${currentDate[Constants.DAY_OF_MONTH_KEY]}, ${currentDate[Constants.YEAR_KEY]}"
            timeTxtV.text = "${currentDate[Constants.TIME_KEY]} ${currentDate[Constants.AM_PM_KEY]}"
            currentWeatherIcon.setImageResource(getIconDrawableId(weatherData.current.weather[0].icon))
            currentWeatherDescriptionTxtV.text = weatherData.current.weather[0].description
            currentTempTxtV.text = weatherData.current.temp.toInt().toString()
            currentFeelsLikeTxt.text =
                "Feels like ${weatherData.current.feelsLike.toInt().toString()}"
            humidityValueTxtV.text = weatherData.current.humidity.toString() + "%"
            windSpeedValueTxtV.text = weatherData.current.windSpeed.toString() + " m/s"
            pressureValueTxtV.text = weatherData.current.pressure.toString() + " hPa"
            cloudsValueTxtV.text = weatherData.current.clouds.toString() + "%"
            sunriseValueTxtV.text = "${sunrise[Constants.TIME_KEY]} ${sunrise[Constants.AM_PM_KEY]}"
            sunsetValueTxtV.text = "${sunset[Constants.TIME_KEY]} ${sunset[Constants.AM_PM_KEY]}"
            hourlyAdapter.submitList(weatherData.hourly)
            dailyAdapter.submitList(weatherData.daily)

            //homeSwipeRefresh.isRefreshing = false
            homeScrollView.visibility = View.VISIBLE
            progressTxt.visibility = View.GONE
        }
    }
}