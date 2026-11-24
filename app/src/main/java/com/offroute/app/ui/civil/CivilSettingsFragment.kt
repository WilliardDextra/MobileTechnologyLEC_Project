package com.offroute.app.ui.civil

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.offroute.app.R
import com.offroute.app.databinding.FragmentCivilSettingsBinding
import com.offroute.app.ui.auth.OperatorSignInActivity

class CivilSettingsFragment : Fragment() {

    private var _binding: FragmentCivilSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCivilSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnManageOfflineMap.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "CACHE PETA JAKARTA: 2.4 GB Tersimpan Lokal • 100% Air-Gap Ready",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.switchUdpSync.setOnCheckedChangeListener { _, isChecked ->
            Toast.makeText(
                requireContext(),
                "Local UDP Sync: ${if (isChecked) "Aktif (Ad-hoc P2P)" else "Nonaktif"}",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.switchSateliteRelay.setOnCheckedChangeListener { _, isChecked ->
            Toast.makeText(
                requireContext(),
                "Satelite Relay: ${if (isChecked) "Mencari downlink Iridium..." else "Nonaktif"}",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.switchBatterySaver.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.tvBatteryEstimate.text = "Estimasi daya: 72 Jam (Mode Ekstrem Aktif)"
                binding.tvBatteryEstimate.setTextColor(ContextCompat.getColor(requireContext(), R.color.accent_green))
                Toast.makeText(requireContext(), "Mode Ekstrem: Refresh rate & render latar diminimalkan", Toast.LENGTH_SHORT).show()
            } else {
                binding.tvBatteryEstimate.text = "Estimasi daya: 32 Jam operasional standar"
                binding.tvBatteryEstimate.setTextColor(ContextCompat.getColor(requireContext(), R.color.accent_amber))
            }
        }

        binding.switchBleBeacon.setOnCheckedChangeListener { _, isChecked ->
            Toast.makeText(
                requireContext(),
                "BLE Beacon Standby: ${if (isChecked) "Aktif Siaga" else "Nonaktif"}",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.btnSwitchToRanger.setOnClickListener {
            val intent = Intent(requireContext(), OperatorSignInActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
