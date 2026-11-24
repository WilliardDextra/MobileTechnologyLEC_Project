package com.offroute.app.ui.civil

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.offroute.app.R
import com.offroute.app.data.OffRouteRepository
import com.offroute.app.databinding.FragmentCivilSosBinding

class CivilSosFragment : Fragment() {

    private var _binding: FragmentCivilSosBinding? = null
    private val binding get() = _binding!!

    private var holdAnimator: ValueAnimator? = null
    private var isHolding = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCivilSosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateFlareUI(OffRouteRepository.isFlareActive)
        setupHoldToActivate()
        setupQuickTiles()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupHoldToActivate() {
        binding.btnFlareTrigger.setOnTouchListener { v, event ->
            if (OffRouteRepository.isFlareActive) {
                // If active, single tap deactivates
                if (event.action == MotionEvent.ACTION_UP) {
                    OffRouteRepository.isFlareActive = false
                    updateFlareUI(false)
                    Toast.makeText(requireContext(), "Suar Darurat Dinonaktifkan", Toast.LENGTH_SHORT).show()
                }
                return@setOnTouchListener true
            }

            // If inactive, requires 1.5s continuous hold
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    isHolding = true
                    startHoldProgress()
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    if (isHolding) {
                        isHolding = false
                        cancelHoldProgress()
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun startHoldProgress() {
        holdAnimator?.cancel()
        holdAnimator = ValueAnimator.ofInt(0, 100).apply {
            duration = 1500L
            addUpdateListener { anim ->
                val progress = anim.animatedValue as Int
                binding.progressFlareHold.progress = progress
            }
            setOnLongClickListenerDummy {
                triggerHaptic()
                OffRouteRepository.isFlareActive = true
                OffRouteRepository.broadcastSosBeacon()
                updateFlareUI(true)
                Toast.makeText(
                    requireContext(),
                    "SUAR DARURAT AKTIF // MEMANCARKAN LOKASI KE REGUR SAR",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        holdAnimator?.start()
    }

    private fun ValueAnimator.setOnLongClickListenerDummy(onComplete: () -> Unit) {
        addListener(object : android.animation.AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                if (isHolding) {
                    onComplete()
                }
            }
        })
    }

    private fun cancelHoldProgress() {
        holdAnimator?.cancel()
        binding.progressFlareHold.progress = 0
    }

    private fun updateFlareUI(isActive: Boolean) {
        if (isActive) {
            binding.pillFlareStatus.text = "• FLARE AKTIF // BROADCASTING 1s BLE BEACON"
            binding.pillFlareStatus.setBackgroundResource(R.drawable.bg_pill_badge_red)
            binding.pillFlareStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.accent_red))

            binding.tvFlareBtnText.text = "FLARE AKTIF"
            binding.tvFlareBtnSubtext.text = "KETUK UNTUK MEMATIKAN"
            binding.progressFlareHold.progress = 100

            binding.tvBleStatusDesc.text = "TX Interval: 1000ms • Daya Pancar Maksimal (+4dBm)"
            binding.tvBleBadge.text = "TX ACTIVE"
            binding.tvBleBadge.setBackgroundResource(R.drawable.bg_pill_badge_red)
            binding.tvBleBadge.setTextColor(ContextCompat.getColor(requireContext(), R.color.accent_red))
        } else {
            binding.pillFlareStatus.text = "• MODE SIAGA // SIAP DILUNCURKAN"
            binding.pillFlareStatus.setBackgroundResource(R.drawable.bg_pill_badge_amber)
            binding.pillFlareStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.accent_amber))

            binding.tvFlareBtnText.text = "AKTIFKAN FLARE"
            binding.tvFlareBtnSubtext.text = "TAHAN 1.5 DETIK"
            binding.progressFlareHold.progress = 0

            binding.tvBleStatusDesc.text = "TX Interval: Standby (1s saat flare aktif)"
            binding.tvBleBadge.text = "SIAP"
            binding.tvBleBadge.setBackgroundResource(R.drawable.bg_pill_badge_blue)
            binding.tvBleBadge.setTextColor(ContextCompat.getColor(requireContext(), R.color.accent_blue))
        }
    }

    private fun setupQuickTiles() {
        binding.tileSosCall.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "PANGGILAN DARURAT: Menghubungkan Saluran Posko Induk BPBD (112)",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.tileSmsAlert.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "SMS SIAGA: Format darurat & koordinat GPS siap dikirim via SMS seluler",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.tileResponders.setOnClickListener {
            showRespondersBottomSheet()
        }
    }

    private fun showRespondersBottomSheet() {
        val bottomSheet = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.bottom_sheet_responders, null)
        bottomSheet.setContentView(view)

        view.findViewById<View>(R.id.btnCloseResponders)?.setOnClickListener {
            bottomSheet.dismiss()
        }

        bottomSheet.show()
    }

    private fun triggerHaptic() {
        val vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(300)
        }
        view?.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        holdAnimator?.cancel()
        _binding = null
    }
}
