package com.offroute.app.ui.ranger

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.offroute.app.R
import com.offroute.app.data.LogLevel
import com.offroute.app.data.OffRouteRepository
import com.offroute.app.databinding.FragmentRangerMeshBinding

class RangerMeshFragment : Fragment() {

    private var _binding: FragmentRangerMeshBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRangerMeshBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.meshTopologyView.onNodeSelectedListener = { node ->
            binding.tvMeshFooterSummary.text = "Terpilih: ${node.name} • Latensi: ${node.latencyMs}ms • Sinyal: ${node.signalQuality}%"
        }

        renderMeshLogs()

        binding.btnBroadcastEmergency.setOnClickListener {
            // Trigger animation on custom canvas
            binding.meshTopologyView.triggerEmergencyBroadcast()

            // Update shared repo
            OffRouteRepository.broadcastSosBeacon()

            // Re-render logs with new entry
            renderMeshLogs()

            Toast.makeText(
                requireContext(),
                "SUAR BEACON MESH TERTEMBAK KE SELURUH 4 SIMPUL!",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun renderMeshLogs() {
        val container = binding.layoutMeshLogsContainer
        container.removeAllViews()

        val logs = OffRouteRepository.meshLogs
        logs.forEach { log ->
            val row = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(0, 8, 0, 8)
                gravity = android.view.Gravity.CENTER_VERTICAL
            }

            // Colored dot indicator
            val dot = View(requireContext()).apply {
                val size = (8 * resources.displayMetrics.density).toInt()
                layoutParams = LinearLayout.LayoutParams(size, size).apply {
                    setMargins(0, 0, (8 * resources.displayMetrics.density).toInt(), 0)
                }
                val dotColorRes = when (log.level) {
                    LogLevel.DANGER -> R.drawable.bg_btn_danger
                    LogLevel.WARNING -> R.drawable.bg_pill_badge_amber
                    LogLevel.INFO -> R.drawable.bg_pill_badge_green
                }
                setBackgroundResource(dotColorRes)
            }

            // Timestamp
            val tvTime = TextView(requireContext()).apply {
                text = log.timestamp
                setTextAppearance(R.style.TacticalTelemetry)
                textSize = 10f
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, (8 * resources.displayMetrics.density).toInt(), 0)
                }
            }

            // Message text
            val tvMsg = TextView(requireContext()).apply {
                text = log.message
                setTextAppearance(R.style.TacticalTelemetry)
                setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary))
                textSize = 10.5f
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
            }

            row.addView(dot)
            row.addView(tvTime)
            row.addView(tvMsg)
            container.addView(row)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
