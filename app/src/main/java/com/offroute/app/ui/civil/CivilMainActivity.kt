package com.offroute.app.ui.civil

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.offroute.app.R
import com.offroute.app.data.OffRouteRepository
import com.offroute.app.data.UserRole
import com.offroute.app.databinding.ActivityCivilMainBinding

enum class CivilTab {
    MAP,
    REPORT,
    SOS,
    SETTINGS
}

class CivilMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCivilMainBinding
    private var mapFragment: CivilMapFragment? = null
    private var reportFragment: CivilReportFragment? = null
    private var sosFragment: CivilSosFragment? = null
    private var settingsFragment: CivilSettingsFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        OffRouteRepository.activeRole = UserRole.CIVIL
        binding = ActivityCivilMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val isEmergency = intent.getBooleanExtra("EMERGENCY_MODE", false)
        if (isEmergency) {
            OffRouteRepository.isNavigationActive = true
        }

        setupBottomNavigation()

        // Default open Map Fragment
        if (savedInstanceState == null) {
            switchToTab(CivilTab.MAP)
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavCivil.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_map -> {
                    loadFragment(getOrCreateMapFragment())
                    true
                }
                R.id.nav_report -> {
                    loadFragment(getOrCreateReportFragment())
                    true
                }
                R.id.nav_sos -> {
                    loadFragment(getOrCreateSosFragment())
                    true
                }
                R.id.nav_settings -> {
                    loadFragment(getOrCreateSettingsFragment())
                    true
                }
                else -> false
            }
        }
    }

    fun switchToTab(tab: CivilTab) {
        val itemId = when (tab) {
            CivilTab.MAP -> R.id.nav_map
            CivilTab.REPORT -> R.id.nav_report
            CivilTab.SOS -> R.id.nav_sos
            CivilTab.SETTINGS -> R.id.nav_settings
        }
        binding.bottomNavCivil.selectedItemId = itemId
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerCivil, fragment)
            .commit()
    }

    private fun getOrCreateMapFragment(): CivilMapFragment {
        if (mapFragment == null) {
            mapFragment = CivilMapFragment()
        }
        return mapFragment!!
    }

    private fun getOrCreateReportFragment(): CivilReportFragment {
        if (reportFragment == null) {
            reportFragment = CivilReportFragment()
        }
        return reportFragment!!
    }

    private fun getOrCreateSosFragment(): CivilSosFragment {
        if (sosFragment == null) {
            sosFragment = CivilSosFragment()
        }
        return sosFragment!!
    }

    private fun getOrCreateSettingsFragment(): CivilSettingsFragment {
        if (settingsFragment == null) {
            settingsFragment = CivilSettingsFragment()
        }
        return settingsFragment!!
    }
}
