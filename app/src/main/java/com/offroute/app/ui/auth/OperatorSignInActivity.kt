package com.offroute.app.ui.auth

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.offroute.app.R
import com.offroute.app.data.OffRouteRepository
import com.offroute.app.databinding.ActivityOperatorSignInBinding

class OperatorSignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOperatorSignInBinding
    private var isPasswordVisible: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOperatorSignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBackOp.setOnClickListener {
            finish()
        }

        setupQuickChips()
        setupPasswordVisibility()
        setupQrScan()
        setupNfcScan()

        binding.switchOfflineKey.setOnCheckedChangeListener { _, isChecked ->
            OffRouteRepository.offlineKeySaved = isChecked
        }

        binding.btnSignInSystem.setOnClickListener {
            val callsign = binding.etCallsign.text.toString().trim()
            if (callsign.isEmpty()) {
                Toast.makeText(this, "Masukkan ID Callsign taktis", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            OffRouteRepository.currentCallsign = callsign
            val intent = Intent(this, PinVerificationActivity::class.java)
            startActivity(intent)
        }

        binding.btnPaperKeyRecovery.setOnClickListener {
            val intent = Intent(this, EmergencyRecoveryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupQuickChips() {
        val green = ContextCompat.getColor(this, R.color.accent_green)
        val muted = ContextCompat.getColor(this, R.color.text_secondary)

        binding.chipEcho.setOnClickListener {
            binding.etCallsign.setText("ECHO-04")
            binding.chipEcho.setTextColor(green)
            binding.chipBravo.setTextColor(muted)
            binding.chipTango.setTextColor(muted)
        }

        binding.chipBravo.setOnClickListener {
            binding.etCallsign.setText("BRAVO-02")
            binding.chipBravo.setTextColor(green)
            binding.chipEcho.setTextColor(muted)
            binding.chipTango.setTextColor(muted)
        }

        binding.chipTango.setOnClickListener {
            binding.etCallsign.setText("TANGO-09")
            binding.chipTango.setTextColor(green)
            binding.chipEcho.setTextColor(muted)
            binding.chipBravo.setTextColor(muted)
        }
    }

    private fun setupPasswordVisibility() {
        binding.btnToggleVisibility.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                binding.etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding.btnToggleVisibility.setImageResource(R.drawable.ic_visibility_off)
            } else {
                binding.etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.btnToggleVisibility.setImageResource(R.drawable.ic_visibility)
            }
            binding.etPassword.setSelection(binding.etPassword.text.length)
        }
    }

    private fun setupQrScan() {
        binding.btnQrCodeScan.setOnClickListener {
            binding.etPassword.setText("TAC-KEY-SECURED-992")
            Toast.makeText(
                this,
                "QR TAKTIS TERPINDAI // KUNCI MISI TERVERIFIKASI",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setupNfcScan() {
        binding.btnScanNfc.setOnClickListener {
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            val view = layoutInflater.inflate(R.layout.dialog_nfc_scan, null)
            dialog.setContentView(view)
            dialog.setCancelable(false)
            dialog.show()

            Handler(Looper.getMainLooper()).postDelayed({
                dialog.dismiss()
                Toast.makeText(this, "NFC RESCUER ID DITERIMA // OTORISASI", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, PinVerificationActivity::class.java)
                startActivity(intent)
            }, 1200)
        }
    }
}
