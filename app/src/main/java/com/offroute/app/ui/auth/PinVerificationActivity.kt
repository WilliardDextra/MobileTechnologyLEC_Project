package com.offroute.app.ui.auth

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.offroute.app.R
import com.offroute.app.databinding.ActivityPinVerificationBinding
import com.offroute.app.ui.ranger.RangerMainActivity

class PinVerificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPinVerificationBinding
    private val enteredPin = StringBuilder()
    private var countDownTimer: CountDownTimer? = null
    private lateinit var pinBoxes: List<TextView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPinVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pinBoxes = listOf(
            binding.pinBox1,
            binding.pinBox2,
            binding.pinBox3,
            binding.pinBox4,
            binding.pinBox5,
            binding.pinBox6
        )

        binding.btnBackPin.setOnClickListener {
            finish()
        }

        setupNumericKeypad()
        startCountdownTimer()

        binding.btnResendCode.setOnClickListener {
            enteredPin.clear()
            updatePinUI()
            startCountdownTimer()
            Toast.makeText(this, "Kode otorisasi diperbarui ke terminal.", Toast.LENGTH_SHORT).show()
        }

        binding.btnMeshBypass.setOnClickListener {
            binding.tvPinFeedback.text = "BYPASS VIA MESH DIIZINKAN // LOG DIAWASI"
            binding.tvPinFeedback.setTextColor(ContextCompat.getColor(this, R.color.accent_green))
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, RangerMainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }, 300)
        }
    }

    private fun setupNumericKeypad() {
        val digitButtons = mapOf(
            binding.key0 to "0",
            binding.key1 to "1",
            binding.key2 to "2",
            binding.key3 to "3",
            binding.key4 to "4",
            binding.key5 to "5",
            binding.key6 to "6",
            binding.key7 to "7",
            binding.key8 to "8",
            binding.key9 to "9"
        )

        digitButtons.forEach { (button, digit) ->
            button.setOnClickListener {
                if (enteredPin.length < 6) {
                    enteredPin.append(digit)
                    updatePinUI()
                    checkPinComplete()
                }
            }
        }

        binding.keyBackspace.setOnClickListener {
            if (enteredPin.isNotEmpty()) {
                enteredPin.deleteCharAt(enteredPin.length - 1)
                updatePinUI()
            }
        }

        binding.keyMeshClear.setOnClickListener {
            enteredPin.clear()
            updatePinUI()
            binding.tvPinFeedback.text = "INPUT DIRESET // MASUKKAN 6 DIGIT"
            binding.tvPinFeedback.setTextColor(ContextCompat.getColor(this, R.color.text_muted))
        }
    }

    private fun updatePinUI() {
        val green = ContextCompat.getColor(this, R.color.accent_green)
        val muted = ContextCompat.getColor(this, R.color.text_muted)

        for (i in pinBoxes.indices) {
            if (i < enteredPin.length) {
                pinBoxes[i].text = "•"
                pinBoxes[i].setTextColor(green)
                pinBoxes[i].setBackgroundResource(R.drawable.bg_pin_box_filled)
            } else {
                pinBoxes[i].text = "—"
                pinBoxes[i].setTextColor(muted)
                pinBoxes[i].setBackgroundResource(R.drawable.bg_pin_box_empty)
            }
        }

        if (enteredPin.length < 6) {
            binding.tvPinFeedback.text = "INPUT TAKTIS: ${enteredPin.length}/6 DIGIT"
            binding.tvPinFeedback.setTextColor(ContextCompat.getColor(this, R.color.text_muted))
        }
    }

    private fun checkPinComplete() {
        if (enteredPin.length == 6) {
            binding.tvPinFeedback.text = "OTORISASI TERVERIFIKASI // CLEARANCE DIBERIKAN"
            binding.tvPinFeedback.setTextColor(ContextCompat.getColor(this, R.color.accent_green))

            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, RangerMainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }, 350)
        }
    }

    private fun startCountdownTimer() {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(165000L, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = (millisUntilFinished / 1000) / 60
                val seconds = (millisUntilFinished / 1000) % 60
                binding.pillTimer.text = String.format("• Berlaku %02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                binding.pillTimer.text = "• KODE KEDALUWARSA"
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}
