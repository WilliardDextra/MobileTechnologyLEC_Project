package com.offroute.app.ui.ranger

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.offroute.app.R
import com.offroute.app.data.OffRouteRepository
import com.offroute.app.databinding.FragmentRangerUnitsBinding

class RangerUnitsFragment : Fragment() {

    private var _binding: FragmentRangerUnitsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRangerUnitsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bravo = OffRouteRepository.units.find { it.callsign == "BRAVO-02" }
        if (bravo?.isAssisted == true) {
            updateAssistUI()
        }

        binding.btnAssistUnit.setOnClickListener {
            OffRouteRepository.assistUnit("U-02")
            updateAssistUI()
            Toast.makeText(
                requireContext(),
                "DISPATCH RESPON TERKIRIM // TIM MENUJU BRAVO-02",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun updateAssistUI() {
        binding.btnAssistUnit.text = "DISPATCHED [DALAM PERJALANAN]"
        binding.btnAssistUnit.setBackgroundResource(R.drawable.bg_btn_secondary)
        binding.btnAssistUnit.setTextColor(ContextCompat.getColor(requireContext(), R.color.accent_green))
        binding.tvBravoStatusText.text = "[ TIM BANTUAN DISPATCH ] Menuju Lokasi Unit (ETA 4m)"
        binding.tvBravoStatusText.setTextColor(ContextCompat.getColor(requireContext(), R.color.accent_green))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
