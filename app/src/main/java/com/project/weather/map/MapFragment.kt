package com.project.weather.map

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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.project.weather.R
import com.project.weather.SharedViewModel
import com.project.weather.databinding.FragmentMapBinding
import com.project.weather.local.ConcreteLocalSource
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
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var mapViewModel: MapViewModel

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

        val mapEventsReceiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                val latitude = String.format("%.2f", p.latitude).toDouble()
                val longitude = String.format("%.2f", p.longitude).toDouble()

                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Add location to favorite")
                    .setMessage("Add location on $latitude, $longitude to favorite?")
                    .setNegativeButton("Cancel") { dialog, which ->
                        dialog.dismiss()
                    }
                    .setPositiveButton("Ok") { dialog, which ->
                        mapViewModel.getWeatherData(latitude, longitude)
                        dialog.dismiss()
                    }
                    .show()
                return true
            }

            override fun longPressHelper(p: GeoPoint): Boolean {
                return false
            }
        }
        val eventOverlay = MapEventsOverlay(mapEventsReceiver)
        binding.osmMap.overlays.add(eventOverlay)


        collectLatestFlowOnLifecycle(mapViewModel.weatherDataStateFlow) { state ->
            when (state) {
                is State.Failure -> Toast.makeText(
                    requireContext(),
                    "something went wrong please try again later",
                    Toast.LENGTH_SHORT
                ).show()

                State.Loading -> {}

                is State.Successful -> {
                    state.data?.let {
                        if (mapViewModel.addToFavorite(it) > -1) {
                            Toast.makeText(
                                requireContext(),
                                "successfully added to favorite",
                                Toast.LENGTH_SHORT
                            ).show()
                            Navigation.findNavController(view).navigateUp()
                        } else {
                            Log.i(TAG, "add to fav error: $it")
                            Toast.makeText(
                                requireContext(),
                                "something went wrong please try again later",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                }

                else -> {}
            }
        }
    }

    private fun init() {
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
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
                R.drawable.location_pin
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
}