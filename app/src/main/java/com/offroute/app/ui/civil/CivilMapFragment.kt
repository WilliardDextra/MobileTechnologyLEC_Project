package com.offroute.app.ui.civil

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.offroute.app.data.OffRouteRepository
import com.offroute.app.databinding.FragmentCivilMapBinding
import com.offroute.app.ui.custom.OsmDarkMapHelper
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Polyline

class CivilMapFragment : Fragment() {

    private var _binding: FragmentCivilMapBinding? = null
    private val binding get() = _binding!!

    private var activeRoutePolyline: Polyline? = null
    private val userLocation = GeoPoint(OffRouteRepository.defaultCenterLat, OffRouteRepository.defaultCenterLon)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCivilMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        OsmDarkMapHelper.initOsmConfig(requireContext())
        setupMap()
        setupListeners()
    }

    private fun setupMap() {
        val mapView = binding.mapView
        OsmDarkMapHelper.applyDarkTacticalTheme(mapView)

        mapView.controller.setZoom(15.2)
        mapView.controller.setCenter(userLocation)

        renderMapOverlays()
    }

    private fun renderMapOverlays() {
        val mapView = binding.mapView
        mapView.overlays.clear()

        // 1. Add Hazards from shared repository
        OffRouteRepository.hazards.forEach { hazard ->
            OsmDarkMapHelper.addHazardCircle(mapView, hazard)
        }

        // 2. Add Nearest Shelter Marker
        OsmDarkMapHelper.addShelterMarker(mapView, OffRouteRepository.nearestShelter)

        // 3. Add User GPS Marker
        OsmDarkMapHelper.addUserLocationMarker(mapView, userLocation)

        // If navigation was already active, redraw the route
        if (OffRouteRepository.isNavigationActive) {
            activeRoutePolyline = OsmDarkMapHelper.addSafeRoutePolyline(
                mapView,
                OffRouteRepository.safeRouteWaypoints
            )
            showActiveNavUI()
        } else {
            showMainMapUI()
        }

        mapView.invalidate()
    }

    private fun setupListeners() {
        // Start Navigation
        binding.btnFindEvacRoute.setOnClickListener {
            OffRouteRepository.isNavigationActive = true
            activeRoutePolyline = OsmDarkMapHelper.addSafeRoutePolyline(
                binding.mapView,
                OffRouteRepository.safeRouteWaypoints
            )
            binding.mapView.invalidate()
            showActiveNavUI()
            binding.mapView.controller.animateTo(userLocation)
        }

        // End Navigation
        binding.btnEndNavigation.setOnClickListener {
            OffRouteRepository.isNavigationActive = false
            activeRoutePolyline?.let {
                binding.mapView.overlays.remove(it)
                activeRoutePolyline = null
            }
            binding.mapView.invalidate()
            showMainMapUI()
        }

        // Report Hazard Buttons
        binding.fabReportHazard.setOnClickListener {
            (activity as? CivilMainActivity)?.switchToTab(CivilTab.REPORT)
        }

        binding.btnNavReportHazard.setOnClickListener {
            (activity as? CivilMainActivity)?.switchToTab(CivilTab.REPORT)
        }

        // Recenter GPS
        binding.fabRecenterGps.setOnClickListener {
            binding.mapView.controller.animateTo(userLocation)
            Toast.makeText(requireContext(), "Fokus ke posisi GPS Anda", Toast.LENGTH_SHORT).show()
        }

        // Map Layers Toggle
        binding.fabMapLayers.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Layer Taktis: Zona Bahaya & Shelter Aktif (3 Titik Terpantau)",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showActiveNavUI() {
        binding.layoutMainHeader.visibility = View.GONE
        binding.cardActiveTurnBanner.visibility = View.VISIBLE
        binding.cardMainShelter.visibility = View.GONE
        binding.cardActiveNavControls.visibility = View.VISIBLE
    }

    private fun showMainMapUI() {
        binding.layoutMainHeader.visibility = View.VISIBLE
        binding.cardActiveTurnBanner.visibility = View.GONE
        binding.cardMainShelter.visibility = View.VISIBLE
        binding.cardActiveNavControls.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
        // Re-render in case a new hazard was added from Report tab
        renderMapOverlays()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
