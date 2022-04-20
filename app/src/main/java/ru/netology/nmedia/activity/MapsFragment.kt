package ru.netology.nmedia.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.collections.MarkerManager
import com.google.maps.android.ktx.awaitAnimateCamera
import com.google.maps.android.ktx.awaitMap
import com.google.maps.android.ktx.model.cameraPosition
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentMapBinding
import ru.netology.nmedia.viewmodel.MapViewModel

class MapsFragment: Fragment() {
    lateinit var googleMap: GoogleMap

    @SuppressLint("MissingPermission")
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                googleMap.apply {
                    isMyLocationEnabled = true
                    uiSettings.isMyLocationButtonEnabled = true
                    uiSettings.setAllGesturesEnabled(true)
                }
            } else {
                // TODO: show sorry dialog
            }
        }

    lateinit var binding: FragmentMapBinding

    val viewModel: MapViewModel by viewModels(ownerProducer = ::requireParentFragment)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentMapBinding.inflate(inflater, container, false)
        println("this step done")
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        println("here is not go")
        super.onViewCreated(view, savedInstanceState)


        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        lifecycle.coroutineScope.launchWhenCreated {
            googleMap = mapFragment.awaitMap().apply {
                isTrafficEnabled = true
                isBuildingsEnabled = true

                uiSettings.apply {
                    isZoomControlsEnabled = true
                    setAllGesturesEnabled(true)
                }
            }

            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED -> {
                    googleMap.apply {
                        isMyLocationEnabled = true
                        uiSettings.isMyLocationButtonEnabled = true
                    }

                    val fusedLocationProviderClient = LocationServices
                        .getFusedLocationProviderClient(requireActivity())

                    fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                        println(" current location is $it")
                    }
                }
                // 2. Должны показать обоснование необходимости прав
                shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                    // TODO: show rationale dialog
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }

            val target = viewModel.coords.value
            val markerManager = MarkerManager(googleMap)
            val collection: MarkerManager.Collection =
                markerManager.newCollection().apply {
                    addMarker(
                        target?.let {
                            MarkerOptions()
                                .position(it)
                                .title("MyMarker")
                                .draggable(true)
                                .icon(BitmapDescriptorFactory.fromAsset("sber.bmp"))
                        }
                    ).apply {
                        tag = "Any additional data" // Any
                    }
                }
            collection.setOnMarkerClickListener { marker ->
                // TODO: work with marker
                Toast.makeText(
                    requireContext(),
                    (marker.tag as String),
                    Toast.LENGTH_LONG
                ).show()
                true
            }

            googleMap.setOnMapLongClickListener {
                markerManager.Collection().clear()
                markerManager.Collection().addMarker(
                    MarkerOptions()
                        .position(it)
                        .title("I pick it")
                        .draggable(false)
                )
                viewModel.coords.value = it
                binding.setButton.visibility = View.VISIBLE
            }

            binding.setButton.setOnClickListener {
                findNavController().navigateUp()
            }
            googleMap.awaitAnimateCamera(
                CameraUpdateFactory.newCameraPosition(
                    cameraPosition {
                        target(target ?: LatLng(55.751999, 37.617734))
                        zoom(13F)
                    }
                ))
        }
    }
}