package com.project.weather


import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.*
import com.project.weather.constants.Constants
import com.project.weather.databinding.ActivityMainBinding
import com.project.weather.home.view.DailyAdapter
import com.project.weather.home.view.HourlyAdapter
import com.project.weather.home.viewmodel.HomeViewModel
import com.project.weather.model.ApiState
import com.project.weather.model.WeatherResponse
import com.project.weather.network.WeatherClient
import com.project.weather.repo.Repo
import com.project.weather.utils.collectLatestFlowOnLifecycle
import com.project.weather.utils.getDateAndTime

const val LOCATION_PERMISSION_ID = 74

class MainActivity : AppCompatActivity() {
    val TAG = "TAG MainActivity"

    private lateinit var binding: ActivityMainBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var geocoder: Geocoder
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var hourlyAdapter: HourlyAdapter
    private lateinit var dailyAdapter: DailyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Constants.cacheDirectory = this.cacheDir.toString()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this)

        init()

        collectLatestFlowOnLifecycle(homeViewModel.weatherDataStateFlow) { state ->
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

    private val locationCallback: LocationCallback = object : LocationCallback() {
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        override fun onLocationResult(p0: LocationResult?) {
            p0?.let {
                val lastLocation: Location = it.lastLocation
                geocoder.getFromLocation(
                    lastLocation.latitude, lastLocation.longitude, 1
                ) { addressList ->
                    Log.i(
                        "TAG",
                        "onLocationResult: ${addressList[0].latitude}, ${addressList[0].longitude}"
                    )
                    homeViewModel.getWeatherData(addressList[0].latitude, addressList[0].longitude)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getLocation()
    }

    private fun getLocation() {
        if (checkPermission()) {
            if (isLocationEnabled()) {
                requestNewLocationData()
            } else {
                Toast.makeText(this, "Location should be enabled", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else requestPermission()
    }

    private fun checkPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this, android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
            this, android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ), LOCATION_PERMISSION_ID
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_ID) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation()
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun requestNewLocationData() {
        val locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 60 * 60 * 1000
        //locationRequest.fastestInterval = 60 * 1000

        if (checkPermission()) fusedLocationClient.requestLocationUpdates(
            locationRequest, locationCallback, Looper.myLooper()
        )
    }

    private fun init() {
        val viewModelFactory = ViewModelFactory(
            Repo.getInstance(
                WeatherClient
            )
        )
        homeViewModel = ViewModelProvider(this, viewModelFactory)[HomeViewModel::class.java]
        hourlyAdapter = HourlyAdapter()
        dailyAdapter = DailyAdapter()
        binding.hourlyRecyclerV.adapter = hourlyAdapter
        binding.dailyRecyclerV.adapter = dailyAdapter
    }

    private fun setLoadingState() {
        binding.apply {
            progressBar.visibility = View.VISIBLE
            progressTxt.text = "updating..."
        }
    }

    private fun setFailureState(error: String) {
        binding.apply {
            progressBar.visibility = View.GONE
            progressTxt.text = "updating failed"
        }
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }

    private fun setSuccessState(weatherData: WeatherResponse) {
        Log.i("TAG", "update ui views: ")
        binding.apply {
            progressBar.visibility = View.GONE
            progressTxt.text = "updating success"
            setDataOnViews(weatherData)
            progressTxt.visibility = View.GONE
        }
    }

    private fun setDataOnViews(weatherData: WeatherResponse) {
        val currentDate = getDateAndTime(weatherData.current.dt)
        Log.i(TAG, "setDataOnViews: $currentDate")
        val sunrise = getDateAndTime(weatherData.current.sunrise)
        val sunset = getDateAndTime(weatherData.current.sunset)
        binding.apply {
            cityNameTxtV.text = weatherData.timezone.split("/")[1]
            dateTxtV.text =
                "${currentDate[Constants.DAY_OF_WEEK_KEY]}, ${currentDate[Constants.MONTH_KEY]} ${currentDate[Constants.DAY_OF_MONTH_KEY]}, ${currentDate[Constants.YEAR_KEY]}"
            timeTxtV.text = "${currentDate[Constants.TIME_KEY]} ${currentDate[Constants.AM_PM_KEY]}"
            currentWeatherIcon.setImageResource(R.drawable.weather_icon_placeholder)
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
        }
    }
}