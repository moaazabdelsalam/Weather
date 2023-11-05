package com.project.weather


import android.content.Context
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
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.*
import com.project.weather.constants.Constants
import com.project.weather.databinding.ActivityMainBinding
import com.project.weather.home.viewmodel.HomeViewModel
import com.project.weather.model.ApiState
import com.project.weather.network.WeatherClient
import com.project.weather.repo.Repo
import com.project.weather.utils.collectLatestFlowOnLifecycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

const val LOCATION_PERMISSION_ID = 74

class MainActivity : AppCompatActivity() {
    val TAG = "TAG MainActivity"

    private lateinit var binding: ActivityMainBinding
    lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var geocoder: Geocoder
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Constants.cacheDirectory = this.cacheDir.toString()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this)

        val viewModelFactory = ViewModelFactory(
            Repo.getInstance(
                WeatherClient
            )
        )
        homeViewModel = ViewModelProvider(this, viewModelFactory)[HomeViewModel::class.java]
        //homeViewModel.getWeatherData(33.44, -94.04)
        collectLatestFlowOnLifecycle(homeViewModel.weatherDataStateFlow) { state ->
            when (state) {
                is ApiState.Failure -> {
                    setFailureState()
                    Toast.makeText(this, state.error, Toast.LENGTH_SHORT).show()
                }

                is ApiState.Loading -> setLoadingState()

                is ApiState.Successful -> {
                    lifecycleScope.launch {
                        setSuccessState()
                    }
                    lifecycleScope.launch {
                        state.data?.let {weatherData ->
                            binding.txtView.text = getDateTime(weatherData.current.dt)
                        }
                    }
                }
            }
        }
    }

    fun getDateTime(s: Long): String? {
        val sdf = SimpleDateFormat("dd EEE MMM hh:mm,aa", Locale.ENGLISH)
        val netDate = Date(s * 1000)
        return sdf.format(netDate)
    }

    private val locationCallback: LocationCallback = object : LocationCallback() {
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        override fun onLocationResult(p0: LocationResult?) {
            p0?.let {
                val lastLocation: Location = it.lastLocation
                geocoder.getFromLocation(
                    lastLocation.latitude,
                    lastLocation.longitude,
                    1
                ) { addressList ->
                    Log.i(
                        "TAG",
                        "onLocationResult: ${addressList[0].latitude}, ${addressList[0].longitude}"
                    )
                    /*locationNameTxtView.text =
                            addressList[0].countryName*/
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
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
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


    private fun setLoadingState() {
        binding.apply {
            progressBar.visibility = View.VISIBLE
            progressTxt.text = "updating..."
        }
    }

    private suspend fun setSuccessState() {
        Log.i("TAG", "update ui views: ")
        binding.apply {
            progressBar.visibility = View.GONE
            progressTxt.text = "updating success"
            delay(500)
            progressTxt.visibility = View.GONE
        }
    }

    private fun setFailureState() {
        binding.apply {
            progressBar.visibility = View.GONE
            progressTxt.text = "updating failed"
        }
    }
}