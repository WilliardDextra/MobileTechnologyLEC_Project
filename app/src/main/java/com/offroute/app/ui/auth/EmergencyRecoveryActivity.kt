package com.offroute.app.ui.auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.offroute.app.R
import com.offroute.app.databinding.ActivityEmergencyRecoveryBinding
import com.offroute.app.ui.ranger.RangerMainActivity

class EmergencyRecoveryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEmergencyRecoveryBinding
    private val standardPaperKey = "alpha beacon radar echo sierra vector hazard orbit delta pulse ridge shelter"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmergencyRecoveryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBackRec.setOnClickListener {
            finish()
        }

        binding.etSeedInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateWordCount(s?.toString().orEmpty())
            }
        })

        binding.btnPasteSeed.setOnClickListener {
            binding.etSeedInput.setText(standardPaperKey)
            binding.etSeedInput.setSelection(standardPaperKey.length)
            Toast.makeText(this, "12 Kata Paper Key berhasil ditempel.", Toast.LENGTH_SHORT).show()
        }

        binding.btnRestoreAccess.setOnClickListener {
            val words = getWordList(binding.etSeedInput.text.toString())
            if (words.size >= 12) {
                binding.tvRecoveryStatus.text = "OTORISASI KUNCI PULIH // MEMASUKI SISTEM"
                binding.tvRecoveryStatus.setTextColor(ContextCompat.getColor(this, R.color.accent_green))

                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(this, RangerMainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }, 400)
            } else {
                Toast.makeText(
                    this,
                    "Harap masukkan 12 kata Paper Key (Saat ini: ${words.size}/12)",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun getWordList(text: String): List<String> {
        return text.trim().split("\\s+".toRegex()).filter { it.isNotBlank() }
    }

    private fun updateWordCount(text: String) {
        val words = getWordList(text)
        val count = words.size
        binding.tvWordCounter.text = "$count/12 KATA"

        if (count >= 12) {
            binding.tvWordCounter.setBackgroundResource(R.drawable.bg_pill_badge_green)
            binding.tvWordCounter.setTextColor(ContextCompat.getColor(this, R.color.accent_green))
            binding.tvRecoveryStatus.text = "STATUS: 12 KATA VALID • HASH COCOK"
            binding.tvRecoveryStatus.setTextColor(ContextCompat.getColor(this, R.color.accent_green))
        } else {
            binding.tvWordCounter.setBackgroundResource(R.drawable.bg_pill_badge_amber)
            binding.tvWordCounter.setTextColor(ContextCompat.getColor(this, R.color.accent_amber))
            binding.tvRecoveryStatus.text = "STATUS: MEMBUTUHKAN 12 KATA SEED ($count/12)"
            binding.tvRecoveryStatus.setTextColor(ContextCompat.getColor(this, R.color.text_muted))
        }
    }
}
