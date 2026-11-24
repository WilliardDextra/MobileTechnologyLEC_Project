package com.offroute.app.ui.auth

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.offroute.app.R
import com.offroute.app.data.OffRouteRepository
import com.offroute.app.data.UserRole
import com.offroute.app.databinding.ActivityAuthGatewayBinding
import com.offroute.app.ui.civil.CivilMainActivity
import com.offroute.app.ui.ranger.RangerMainActivity

class AuthGatewayActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthGatewayBinding
    private var selectedRole: UserRole = UserRole.RANGER

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthGatewayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRoleSelection()

        binding.btnEnterAuth.setOnClickListener {
            OffRouteRepository.activeRole = selectedRole
            if (selectedRole == UserRole.RANGER) {
                val intent = Intent(this, OperatorSignInActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, CivilMainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        binding.btnQuickEvac.setOnClickListener {
            OffRouteRepository.activeRole = UserRole.CIVIL
            val intent = Intent(this, CivilMainActivity::class.java).apply {
                putExtra("EMERGENCY_MODE", true)
            }
            startActivity(intent)
            finish()
        }
    }

    private fun setupRoleSelection() {
        updateRoleUI(UserRole.RANGER)

        binding.cardRoleRanger.setOnClickListener {
            updateRoleUI(UserRole.RANGER)
        }

        binding.cardRoleCivil.setOnClickListener {
            updateRoleUI(UserRole.CIVIL)
        }
    }

    private fun updateRoleUI(role: UserRole) {
        selectedRole = role
        OffRouteRepository.activeRole = role

        val greenColor = ContextCompat.getColor(this, R.color.accent_green)
        val borderSubtle = ContextCompat.getColor(this, R.color.border_subtle)
        val surfaceCard = ContextCompat.getColor(this, R.color.surface_card)
        val surfaceElevated = ContextCompat.getColor(this, R.color.surface_elevated)
        val textMuted = ContextCompat.getColor(this, R.color.text_muted)

        if (role == UserRole.RANGER) {
            // Highlight Ranger Card
            binding.cardRoleRanger.setCardBackgroundColor(surfaceElevated)
            binding.cardRoleRanger.strokeColor = greenColor
            binding.cardRoleRanger.strokeWidth = dpToPx(1.5f)
            binding.rbRanger.isChecked = true
            binding.ivRangerIcon.imageTintList = ColorStateList.valueOf(greenColor)

            // Dim Civil Card
            binding.cardRoleCivil.setCardBackgroundColor(surfaceCard)
            binding.cardRoleCivil.strokeColor = borderSubtle
            binding.cardRoleCivil.strokeWidth = dpToPx(1f)
            binding.rbCivil.isChecked = false
            binding.ivCivilIcon.imageTintList = ColorStateList.valueOf(textMuted)

            binding.btnEnterAuth.text = "MASUK DENGAN ID TAKTIS ->"
        } else {
            // Highlight Civil Card
            binding.cardRoleCivil.setCardBackgroundColor(surfaceElevated)
            binding.cardRoleCivil.strokeColor = greenColor
            binding.cardRoleCivil.strokeWidth = dpToPx(1.5f)
            binding.rbCivil.isChecked = true
            binding.ivCivilIcon.imageTintList = ColorStateList.valueOf(greenColor)

            // Dim Ranger Card
            binding.cardRoleRanger.setCardBackgroundColor(surfaceCard)
            binding.cardRoleRanger.strokeColor = borderSubtle
            binding.cardRoleRanger.strokeWidth = dpToPx(1f)
            binding.rbRanger.isChecked = false
            binding.ivRangerIcon.imageTintList = ColorStateList.valueOf(textMuted)

            binding.btnEnterAuth.text = "MASUK MODE WARGA ->"
        }
    }

    private fun dpToPx(dp: Float): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}
