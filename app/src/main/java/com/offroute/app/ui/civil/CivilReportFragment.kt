package com.offroute.app.ui.civil

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.offroute.app.R
import com.offroute.app.data.HazardType
import com.offroute.app.data.HazardZone
import com.offroute.app.data.OffRouteRepository
import com.offroute.app.databinding.FragmentCivilReportBinding
import com.offroute.app.ui.custom.OsmDarkMapHelper
import org.osmdroid.util.GeoPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CivilReportFragment : Fragment() {

    private var _binding: FragmentCivilReportBinding? = null
    private val binding get() = _binding!!

    private var selectedHazardType: HazardType = HazardType.FIRE

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCivilReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMiniMap()
        setupTypeSelectors()
        setupPhotoActions()
        setupSubmitAction()
    }

    private fun setupMiniMap() {
        val miniMap = binding.miniMapView
        OsmDarkMapHelper.initOsmConfig(requireContext())
        OsmDarkMapHelper.applyDarkTacticalTheme(miniMap)

        val reportLocation = GeoPoint(OffRouteRepository.defaultCenterLat + 0.0012, OffRouteRepository.defaultCenterLon + 0.0015)
        miniMap.controller.setZoom(16.0)
        miniMap.controller.setCenter(reportLocation)
    }

    private fun setupTypeSelectors() {
        updateTypeSelectionUI(HazardType.FIRE)

        binding.cardTypeFire.setOnClickListener {
            updateTypeSelectionUI(HazardType.FIRE)
        }

        binding.cardTypeFlood.setOnClickListener {
            updateTypeSelectionUI(HazardType.FLOOD)
        }

        binding.cardTypeBlockage.setOnClickListener {
            updateTypeSelectionUI(HazardType.BLOCKAGE)
        }
    }

    private fun updateTypeSelectionUI(type: HazardType) {
        selectedHazardType = type

        val red = ContextCompat.getColor(requireContext(), R.color.accent_red)
        val blue = ContextCompat.getColor(requireContext(), R.color.accent_blue)
        val amber = ContextCompat.getColor(requireContext(), R.color.accent_amber)
        val borderSubtle = ContextCompat.getColor(requireContext(), R.color.border_subtle)
        val surfaceCard = ContextCompat.getColor(requireContext(), R.color.surface_card)
        val redSurface = ContextCompat.getColor(requireContext(), R.color.accent_red_surface)

        // Reset all 3
        binding.cardTypeFire.setCardBackgroundColor(surfaceCard)
        binding.cardTypeFire.strokeColor = borderSubtle
        binding.cardTypeFire.strokeWidth = dpToPx(1f)

        binding.cardTypeFlood.setCardBackgroundColor(surfaceCard)
        binding.cardTypeFlood.strokeColor = borderSubtle
        binding.cardTypeFlood.strokeWidth = dpToPx(1f)

        binding.cardTypeBlockage.setCardBackgroundColor(surfaceCard)
        binding.cardTypeBlockage.strokeColor = borderSubtle
        binding.cardTypeBlockage.strokeWidth = dpToPx(1f)

        // Highlight selected
        when (type) {
            HazardType.FIRE -> {
                binding.cardTypeFire.setCardBackgroundColor(redSurface)
                binding.cardTypeFire.strokeColor = red
                binding.cardTypeFire.strokeWidth = dpToPx(1.5f)
            }
            HazardType.FLOOD -> {
                binding.cardTypeFlood.setCardBackgroundColor(ColorStateListHelper.getColor(requireContext(), R.color.accent_blue_dark))
                binding.cardTypeFlood.strokeColor = blue
                binding.cardTypeFlood.strokeWidth = dpToPx(1.5f)
            }
            HazardType.BLOCKAGE, HazardType.LANDSLIDE -> {
                binding.cardTypeBlockage.setCardBackgroundColor(ColorStateListHelper.getColor(requireContext(), R.color.accent_amber_dark))
                binding.cardTypeBlockage.strokeColor = amber
                binding.cardTypeBlockage.strokeWidth = dpToPx(1.5f)
            }
        }
    }

    private fun setupPhotoActions() {
        binding.btnCapturePhoto.setOnClickListener {
            binding.cardPhotoPreview.visibility = View.VISIBLE
            Toast.makeText(requireContext(), "Foto bukti terlampir dalam laporan", Toast.LENGTH_SHORT).show()
        }

        binding.btnRemovePhoto.setOnClickListener {
            binding.cardPhotoPreview.visibility = View.GONE
        }
    }

    private fun setupSubmitAction() {
        binding.btnSubmitReport.setOnClickListener {
            val userText = binding.etSituationDesc.text.toString().trim()
            val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

            val (title, defaultDesc) = when (selectedHazardType) {
                HazardType.FIRE -> "Kebakaran Baru Terdeteksi" to "Titik api dilaporkan membesar di sekitar pemukiman"
                HazardType.FLOOD -> "Genangan Banjir Arus Cepat" to "Luapan air menutup akses jalan utama"
                HazardType.BLOCKAGE, HazardType.LANDSLIDE -> "Akses Tertutup Rintangan" to "Pohon tumbang / material longsor menghalangi jalur"
            }

            val finalDesc = if (userText.isNotEmpty()) userText else defaultDesc

            val newHazard = HazardZone(
                id = "HAZ-${System.currentTimeMillis() % 1000}",
                type = selectedHazardType,
                title = title,
                description = finalDesc,
                latitude = OffRouteRepository.defaultCenterLat + 0.0012,
                longitude = OffRouteRepository.defaultCenterLon + 0.0015,
                radiusMeters = 200.0,
                severity = "BAHAYA",
                reportedTime = time
            )

            // Add hazard to shared repository
            OffRouteRepository.addHazard(newHazard)

            Toast.makeText(
                requireContext(),
                "LAPORAN DITERUSKAN // MUNCUL DI PETA & POSKO RANGER",
                Toast.LENGTH_LONG
            ).show()

            // Return to MAP tab
            (activity as? CivilMainActivity)?.switchToTab(CivilTab.MAP)
        }
    }

    private fun dpToPx(dp: Float): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    override fun onResume() {
        super.onResume()
        binding.miniMapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.miniMapView.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

object ColorStateListHelper {
    fun getColor(context: android.content.Context, colorRes: Int): Int {
        return ContextCompat.getColor(context, colorRes)
    }
}
