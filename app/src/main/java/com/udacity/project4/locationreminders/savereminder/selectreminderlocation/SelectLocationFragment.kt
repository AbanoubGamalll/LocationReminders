package com.udacity.project4.locationreminders.savereminder.selectreminderlocation


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PointOfInterest
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.BuildConfig
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject

class SelectLocationFragment : BaseFragment(), OnMapReadyCallback {
    //Done
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var map: GoogleMap
    private lateinit var binding: FragmentSelectLocationBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    //Done
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)
        binding.btnSelectLocation.setOnClickListener {
            if (_viewModel.latitude.value != null && _viewModel.longitude.value != null)
                activity?.onBackPressed()
            else
                Toast.makeText(requireActivity(), "Select Location!", Toast.LENGTH_SHORT).show()
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
//       zoom to the user location after taking his permission
//       add style to the map
//       put a marker to location that the user selected


        return binding.root
    }
    ///MAP
    //Done
    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        setMapPOI()
        setLongClick()
        setMapStyle()

        if (PackageManager.PERMISSION_GRANTED
            == ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            map.isMyLocationEnabled = true
        }
    }

    //Done
    @SuppressLint("MissingPermission")
    private fun setMyLocation() {
        val zoomLevel = 15f

        fusedLocationProviderClient.lastLocation.addOnSuccessListener(requireActivity())
        {
            it?.let {
                map.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            it.latitude,
                            it.longitude
                        ), zoomLevel
                    )
                )
            }
        }

    }

    //Done
    private fun setMapStyle() {
        try {
            map.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style))
        } catch (e: Resources.NotFoundException) {
        }
    }

    //Done
    private fun setMapPOI() {
        map.setOnPoiClickListener {
            map.clear()
            it?.let {
                map.addMarker(MarkerOptions().position(it.latLng).title(it.name)).showInfoWindow()
                onLocationSelected(it)
            }
        }
    }

    //Done
    private fun setLongClick() {
        map.setOnMapLongClickListener {
            val loc = LatLng(it.latitude, it.longitude)
            map.clear()
            map.addMarker(MarkerOptions().position(loc))
                .showInfoWindow()
            onLocationSelected(
                PointOfInterest(
                    loc,
                    "${loc.latitude},${loc.longitude}",
                    "${loc.latitude},${loc.longitude}"
                )
            )
        }
    }
    //////////
    // Permissions
    //
    private fun enableLocationPermission() {
        if (PackageManager.PERMISSION_GRANTED
            == ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            if (::map.isInitialized) map.isMyLocationEnabled = true
            checkDeviceLocationSettingsAndSetMyLocation()
        } else {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                10
            )
        }
    }

    //

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 60 && requestCode == Activity.RESULT_OK) {
            checkDeviceLocationSettingsAndSetMyLocation()
        } else {
            Snackbar.make(
                this.requireView(),
                R.string.location_required_error, Snackbar.LENGTH_SHORT
            ).setAction(android.R.string.ok) {
                checkDeviceLocationSettingsAndSetMyLocation()
            }.show()
        }
    }

    //Done
    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 10) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableLocationPermission()
            } else {
                Snackbar.make(
                    this.requireView(),
                    R.string.permission_denied_explanation,
                    Snackbar.LENGTH_SHORT
                ).setAction(R.string.settings) {
                    startActivity(Intent().apply {
                        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    })
                }.show()
            }
        }
    }


    //
     private fun checkDeviceLocationSettingsAndSetMyLocation() {
         val locationRequest = LocationRequest.create().apply {
             priority = LocationRequest.PRIORITY_LOW_POWER
         }
         val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

         val settingsClient = LocationServices.getSettingsClient(requireContext())
         val locationSettingsResponseTask =
             settingsClient.checkLocationSettings(builder.build())

         locationSettingsResponseTask.addOnFailureListener { exception ->
             if (exception is ResolvableApiException) {
                 try {
                     startIntentSenderForResult(
                         exception.resolution.intentSender,
                         60,
                         null,
                         0, 0, 0, null
                     )
                 } catch (sendEx: IntentSender.SendIntentException) {
                 }
             } else {
                 Snackbar.make(
                     this.requireView(),
                     R.string.location_required_error, Snackbar.LENGTH_SHORT
                 ).setAction(android.R.string.ok) {
                     checkDeviceLocationSettingsAndSetMyLocation()
                 }.show()
             }
         }

         locationSettingsResponseTask.addOnCompleteListener {
             if (it.isSuccessful) setMyLocation()
         }
     }

  
    override fun onStart() {
        super.onStart()
        enableLocationPermission()
    }
//////////
    //Menu
    //Done
    private fun onLocationSelected(p: PointOfInterest) {
        _viewModel.apply {
            reminderSelectedLocationStr.value = p.name
            latitude.value = p.latLng.latitude
            longitude.value = p.latLng.longitude
            selectedPOI.value = p
        }
    }

    //Done
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    //Done
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        //Change the map type based on the user's selection.
        R.id.normal_map -> {
            map.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            map.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            map.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            map.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
    
}