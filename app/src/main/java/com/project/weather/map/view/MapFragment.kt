package com.project.weather.map.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.project.weather.R
import com.project.weather.databinding.FragmentMapBinding
import com.project.weather.local.ConcreteLocalSource
import com.project.weather.map.viewmodel.MapViewModel
import com.project.weather.map.viewmodel.MapViewModelFactory
import com.project.weather.model.State
import com.project.weather.network.WeatherClient
import com.project.weather.repo.Repo
import com.project.weather.utils.collectLatestFlowOnLifecycle
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class MapFragment : Fragment() {
    private val TAG = "TAG MapFragment"

    private lateinit var binding: FragmentMapBinding
    private lateinit var mapController: IMapController
    private lateinit var mMyLocationOverlay: MyLocationNewOverlay
    private lateinit var mapViewModel: MapViewModel
    val args: MapFragmentArgs by navArgs()
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var cityName = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        Configuration.getInstance()
            .load(requireContext(), activity?.getPreferences(AppCompatActivity.MODE_PRIVATE))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        val source = args.source

        val mapEventsReceiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                latitude = p.latitude
                longitude = p.longitude
                mapViewModel.getCityName(latitude, longitude)
                return true
            }

            override fun longPressHelper(p: GeoPoint): Boolean {
                return false
            }
        }
        val eventOverlay = MapEventsOverlay(mapEventsReceiver)
        binding.osmMap.overlays.add(eventOverlay)

        collectLatestFlowOnLifecycle(mapViewModel.cityNameApiState) { state ->
            when (state) {
                is State.Failure -> {
                    binding.progressBar.visibility = View.GONE
                    showErrorToast()
                }

                State.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is State.Success -> {
                    binding.progressBar.visibility = View.GONE
                    cityName = state.data?.namedetails?.nameEn ?: "City"
                    if (source != "NAN") {
                        showAddToFavDialog()
                    } else {
                        showChangeHomeLocationDialog()
                    }
                }

                else -> {}
            }
        }

        collectLatestFlowOnLifecycle(mapViewModel.weatherDataApiState) { state ->
            Log.i(TAG, "map weather state")
            when (state) {
                is State.Failure -> {
                    binding.progressBar.visibility = View.GONE
                    showErrorToast()
                }

                State.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is State.Success -> {
                    binding.progressBar.visibility = View.GONE
                    state.data?.let {
                        if (source != "NAN") {
                            if (mapViewModel.addToFavorite(it, cityName) > -1) {
                                showAddFavSuccess()
                                Navigation.findNavController(view).navigateUp()
                            } else {
                                Log.i(TAG, "add to fav error: $it")
                                showErrorToast()
                            }
                        } else {
                            showHomeChangedSuccess()
                            Navigation.findNavController(view).navigateUp()
                        }
                    }
                }

                else -> {}
            }
        }
    }

    private fun init() {
        binding.osmMap.setTileSource(TileSourceFactory.MAPNIK)
        binding.osmMap.setMultiTouchControls(true)
        mapController = binding.osmMap.controller

        mapController.setZoom(8.0)
        mMyLocationOverlay =
            MyLocationNewOverlay(GpsMyLocationProvider(requireContext()), binding.osmMap)
        mMyLocationOverlay.enableMyLocation()
        mMyLocationOverlay.setPersonIcon(
            AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.ic_triangle
            )?.toBitmap(100, 100)
        )
        mMyLocationOverlay.isDrawAccuracyEnabled = true
        mMyLocationOverlay.runOnFirstFix {
            requireActivity().runOnUiThread {
                mapController.setCenter(mMyLocationOverlay.myLocation)
                mapController.animateTo(mMyLocationOverlay.myLocation)
            }
        }

        val compassOverlay =
            CompassOverlay(
                requireContext(),
                InternalCompassOrientationProvider(requireContext()),
                binding.osmMap
            )
        compassOverlay.enableCompass()
        val rotationGestureOverlay = RotationGestureOverlay(binding.osmMap)
        rotationGestureOverlay.isEnabled

        binding.osmMap.overlays.add(compassOverlay)
        binding.osmMap.overlays.add(mMyLocationOverlay)
        binding.osmMap.overlays.add(rotationGestureOverlay)
        binding.osmMap.invalidate()

        val viewModelFactory = MapViewModelFactory(
            Repo.getInstance(
                WeatherClient,
                ConcreteLocalSource.getInstance(requireContext())
            )
        )
        mapViewModel = ViewModelProvider(this, viewModelFactory)[MapViewModel::class.java]
    }

    private fun showErrorToast() {
        Toast.makeText(
            requireContext(),
            "something went wrong please try again later",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun showAddFavSuccess() {
        Toast.makeText(
            requireContext(),
            "successfully added to favorite",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun showHomeChangedSuccess() {
        Toast.makeText(
            requireContext(),
            "Home location changed",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun showAddToFavDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add city to favorite")
            .setMessage("Add $cityName to favorite?")
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton("Ok") { dialog, which ->
                mapViewModel.getWeatherData(latitude, longitude)
                dialog.dismiss()
            }
            .show()
    }

    private fun showChangeHomeLocationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Home Location")
            .setMessage("set home location to $cityName?")
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton("Ok") { dialog, which ->
                mapViewModel.setHomeLocation(latitude, longitude)
                dialog.dismiss()
            }
            .show()
    }
}