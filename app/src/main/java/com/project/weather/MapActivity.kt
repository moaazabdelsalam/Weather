package com.project.weather

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.project.weather.databinding.ActivityMapBinding
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


class MapActivity : AppCompatActivity() {
    lateinit var mMap: MapView
    private lateinit var mapController: IMapController
    private lateinit var mMyLocationOverlay: MyLocationNewOverlay

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(applicationContext, getPreferences(MODE_PRIVATE))
        val binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.osmMap.setTileSource(TileSourceFactory.MAPNIK)
        binding.osmMap.setMultiTouchControls(true)
        mapController = binding.osmMap.controller

        mapController.setZoom(10.0)
        mMyLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), binding.osmMap)
        mMyLocationOverlay.enableMyLocation()
        mMyLocationOverlay.setPersonIcon(
            AppCompatResources.getDrawable(
                this,
                R.drawable.location_pin
            )?.toBitmap(100, 100)
        )
        mMyLocationOverlay.isDrawAccuracyEnabled = true
        mMyLocationOverlay.runOnFirstFix {
            runOnUiThread {
                mapController.setCenter(mMyLocationOverlay.myLocation)
                mapController.animateTo(mMyLocationOverlay.myLocation)
            }
        }

        val compassOverlay =
            CompassOverlay(this, InternalCompassOrientationProvider(this), binding.osmMap)
        compassOverlay.enableCompass()
        val rotationGestureOverlay = RotationGestureOverlay(binding.osmMap)
        rotationGestureOverlay.isEnabled

        binding.osmMap.overlays.add(compassOverlay)
        binding.osmMap.overlays.add(mMyLocationOverlay)
        binding.osmMap.overlays.add(rotationGestureOverlay)
        binding.osmMap.invalidate()

        val mapEventsReceiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                val latitude = String.format("%.2f", p.latitude).toDouble()
                val longitude = String.format("%.2f", p.longitude).toDouble()

                MaterialAlertDialogBuilder(this@MapActivity)
                    .setTitle("Add location to favorite")
                    .setMessage("Add location on $latitude, $longitude to favorite?")
                    .setNegativeButton("Cancel") { dialog, which ->
                        dialog.dismiss()
                    }
                    .setPositiveButton("Ok") { dialog, which ->
                        Cache.FavoriteLocationPoint.value = GeoPoint(latitude, longitude)
                        dialog.dismiss()
                        finish()
                    }
                    .show()
                return true
            }

            override fun longPressHelper(p: GeoPoint): Boolean {
                return false
            }
        }

        val overlay = MapEventsOverlay(mapEventsReceiver)
        binding.osmMap.overlays.add(overlay)
    }
}