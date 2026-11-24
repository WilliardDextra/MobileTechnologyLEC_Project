package com.offroute.app.ui.ranger

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.offroute.app.data.OffRouteRepository
import com.offroute.app.databinding.FragmentRangerTasksBinding

class RangerTasksFragment : Fragment() {

    private var _binding: FragmentRangerTasksBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRangerTasksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mission1 = OffRouteRepository.missions.firstOrNull()
        if (mission1?.isAccepted == true) {
            binding.btnAcceptMission.text = "MISI DITERIMA — NAVIGASI AKTIF"
        }

        binding.btnAcceptMission.setOnClickListener {
            OffRouteRepository.acceptMission("MISI-01")
            binding.btnAcceptMission.text = "MISI DITERIMA — NAVIGASI AKTIF"
            Toast.makeText(
                requireContext(),
                "MISI DITERIMA // MEMBUKA RADAR SEKTOR",
                Toast.LENGTH_SHORT
            ).show()

            // Jump to Sector Radar / Report Tab
            (activity as? RangerMainActivity)?.switchToTab(RangerTab.RADAR)
        }

        binding.btnNotificationBell.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "PEMBERITAHUAN: Sinyal SOS aktif terdeteksi dari BRAVO-02",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
