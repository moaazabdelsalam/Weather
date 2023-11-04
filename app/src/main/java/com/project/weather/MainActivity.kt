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
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.*
import com.project.weather.home.viewmodel.HomeViewModel
import com.project.weather.network.WeatherClient
import com.project.weather.repo.Repo

const val LOCATION_PERMISSION_ID = 74

class MainActivity : AppCompatActivity() {
    lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var geocoder: Geocoder
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this)

        val viewModelFactory = ViewModelFactory(
            Repo.getInstance(
                WeatherClient
            )
        )
        homeViewModel = ViewModelProvider(this, viewModelFactory)[HomeViewModel::class.java]
        //homeViewModel.getWeatherData(33.44, -94.04)
        homeViewModel.weatherData.observe(this) {
            Log.i("TAG", "weather data: ${it}")
        }
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
        locationRequest.interval = 60 * 2 * 1000
        //locationRequest.fastestInterval = 60 * 1000

        if (checkPermission()) fusedLocationClient.requestLocationUpdates(
            locationRequest, locationCallback, Looper.myLooper()
        )
    }
}