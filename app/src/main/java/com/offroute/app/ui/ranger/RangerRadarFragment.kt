package com.offroute.app.ui.ranger

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.offroute.app.databinding.FragmentRangerRadarBinding

class RangerRadarFragment : Fragment() {

    private var _binding: FragmentRangerRadarBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRangerRadarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup blip touch selection listener
        binding.tacticalRadarView.onBlipSelectedListener = { blip ->
            binding.tvTelemetryTargetName.text = "TARGET: ${blip.name}"
            binding.tvTelemetryRssi.text = "RSSI: ${blip.rssiText}"
            binding.tvTelemetryDetail.text = "Jarak: ${blip.distanceText} • ${blip.info}"
        }

        // Fast radar ping sweep
        binding.btnScanSectorPing.setOnClickListener {
            binding.tacticalRadarView.triggerFastPing()
            Toast.makeText(
                requireContext(),
                "MEMINDAI SEKTOR // PING MESH TERKIRIM KE 4 SIMPUL",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Set waypoint
        binding.btnSetWaypoint.setOnClickListener {
            val target = binding.tacticalRadarView.selectedBlip?.name ?: "TARGET"
            Toast.makeText(
                requireContext(),
                "TITIK ARAH DIKUNCI KE: $target",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
