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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.gms.location.*
import com.project.weather.constants.Constants
import com.project.weather.databinding.ActivityMainBinding
import com.project.weather.network.MyConnectivityManager
import com.project.weather.repo.PreferenceRepo
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import java.util.concurrent.TimeUnit


const val LOCATION_PERMISSION_ID = 74

class MainActivity : AppCompatActivity() {
    private val TAG = "TAG MainActivity"

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var myConnectivityManager: MyConnectivityManager
    lateinit var geocoder: Geocoder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Constants.cacheDirectory = this.cacheDir.toString()
        myConnectivityManager = MyConnectivityManager(this)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.mainNavHost) as NavHostFragment
        val navController = navHostFragment.navController
        NavigationUI.setupWithNavController(binding.bottomNav, navController)

        navController.addOnDestinationChangedListener { _, navDestination, _ ->
            if (navDestination.id == R.id.mapFragment) {
                binding.bottomNav.visibility = View.GONE
            } else {
                binding.bottomNav.visibility = View.VISIBLE
            }
        }

        sharedViewModel = ViewModelProvider(
            this,
            SharedViewModelFactory(PreferenceRepo.getInstance(this))
        )[SharedViewModel::class.java]
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedViewModel.homeLocationSource.collectLatest { source ->
                    when (source) {
                        Constants.PREF_LOCATION_GPS -> {
                            Log.i(TAG, "location source: GPS")
                            getLocation()
                        }

                        Constants.PREF_LOCATION_MAP -> {
                            Log.i(TAG, "location source: MAP")
                            sharedViewModel.setHomeLocation(null)
                        }

                        else -> {
                            Log.i(TAG, "no location source")
                        }
                    }
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                myConnectivityManager.isConnected.collectLatest {
                    when(it) {
                        true -> {
                            binding.apply {
                                noConnectionTxtV.visibility = View.GONE
                                noConnectionAnimation.pauseAnimation()
                                noConnectionAnimation.visibility = View.GONE
                            }
                        }
                        false -> {
                            binding.apply {
                                noConnectionTxtV.visibility = View.VISIBLE
                                noConnectionAnimation.playAnimation()
                                noConnectionAnimation.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }
        }
        if (ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestNotificationPermission()
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
                        TAG,
                        "onLocationResult: ${addressList[0].latitude}, ${addressList[0].longitude}"
                    )
                    sharedViewModel.setHomeLocation(
                        GeoPoint(
                            addressList[0].latitude,
                            addressList[0].longitude
                        )
                    )
                }
            }
        }
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

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    android.Manifest.permission.POST_NOTIFICATIONS
                ), Constants.NOTIFICATION_PERMISSION_ID
            )
        }
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
            } else {
                Toast.makeText(this, "Need Location Permission", Toast.LENGTH_SHORT).show()
            }
        } else if (requestCode == Constants.NOTIFICATION_PERMISSION_ID) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //getLocation()
            } else {
                Toast.makeText(this, "Need Notification Permission", Toast.LENGTH_SHORT).show()
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
}