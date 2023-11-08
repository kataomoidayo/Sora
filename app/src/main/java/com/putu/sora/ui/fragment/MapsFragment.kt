package com.putu.sora.ui.fragment

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.putu.sora.viewmodel.MapsViewModel
import com.putu.sora.R
import com.putu.sora.data.database.StoryEntity
import com.putu.sora.databinding.FragmentMapsBinding
import com.putu.sora.extra.Helper
import com.putu.sora.extra.ResultResponse
import com.putu.sora.extra.UserPreferences
import com.putu.sora.viewmodel.ViewModelFactory

class MapsFragment : Fragment(), OnMapReadyCallback, MenuProvider {

    private var _mapsBind: FragmentMapsBinding? = null
    private val mapsBind get() = _mapsBind

    private lateinit var mapsViewModel: MapsViewModel

    private val helper = Helper()

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    private lateinit var mMap: GoogleMap

    private val boundsBuilder = LatLngBounds.builder()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _mapsBind = FragmentMapsBinding.inflate(inflater, container, false)
        return mapsBind?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.maps_menu)

        setMapStyle()
        getMyLocation()
        setUpViewModel()

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.STARTED)

        mapsViewModel.isLoading.observe(this) {
            mapsBind?.let { it1 ->
                helper.isLoading(
                    it,
                    it1.mapsProgressBar
                )
            }
        }
    }

    private fun setMapStyle() {
        try {
            val success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireActivity(), R.raw.map_style))

            if (!success) {
                AlertDialog.Builder(requireActivity()).apply {
                    setTitle(R.string.failed_response_alert_title)
                    setMessage(R.string.failed_load_map_style_message)
                    setPositiveButton(R.string.back_alert_button) { _, _ ->
                        activity?.onBackPressedDispatcher?.onBackPressed()
                    }
                    create()
                    show()
                }
            }
        } catch (exception: Resources.NotFoundException) {
            AlertDialog.Builder(requireActivity()).apply {
                setTitle(R.string.failed_response_alert_title)
                setMessage(R.string.failed_load_map_style_message)
                setPositiveButton(R.string.back_alert_button) { _, _ ->
                    activity?.onBackPressedDispatcher?.onBackPressed()
                }
                create()
                show()
            }
        }
    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
        } else {
            requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private val requestLocationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            getMyLocation()
        }
    }

    private fun addManyMarker(stories: List<StoryEntity>) {
        stories.forEach { location->
            if (location.lat != null && location.lon != null) {
                val latLng = LatLng(location.lat, location.lon)
                mMap.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title(location.name)
                        .snippet(location.description)
                )

                boundsBuilder.include(latLng) }
        }

        val bounds: LatLngBounds = boundsBuilder.build()
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, resources.displayMetrics.widthPixels, resources.displayMetrics.heightPixels, 300))
    }

    private fun setUpViewModel() {
        val pref = UserPreferences.getInstance(requireContext().dataStore)
        mapsViewModel = ViewModelProvider(this, ViewModelFactory(pref, requireActivity()))[MapsViewModel::class.java]

        mapsViewModel.getUser().observe(viewLifecycleOwner) { user ->
            if (user.isLogin) {
                getData(user.token)
            }
        }
    }

    private fun getData(token: String) {
        mapsViewModel.getStoryLocation(token).observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when(result) {
                    is ResultResponse.Loading -> {

                    }

                    is ResultResponse.Success -> {
                        val stories = result.data
                        addManyMarker(stories)
                    }

                    is ResultResponse.Error -> {
                        AlertDialog.Builder(requireActivity()).apply {
                            setTitle(R.string.failed_response_alert_title)
                            setMessage(R.string.failed_load_map_message)
                            setPositiveButton(R.string.back_alert_button) { _, _ ->
                                activity?.onBackPressedDispatcher?.onBackPressed()
                            }
                            create()
                            show()
                        }
                    }
                }
            }
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        menuInflater.inflate(R.menu.map_menu_list, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            android.R.id.home -> {
                activity?.onBackPressedDispatcher?.onBackPressed()
                true
            }

            R.id.normal_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                true
            }

            R.id.satellite_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                true
            }

            R.id.terrain_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                true
            }

            R.id.hybrid_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                true
            }
            else -> false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _mapsBind = null
    }
}