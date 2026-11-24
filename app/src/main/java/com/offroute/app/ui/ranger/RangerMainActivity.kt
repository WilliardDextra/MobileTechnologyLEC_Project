package com.offroute.app.ui.ranger

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.offroute.app.R
import com.offroute.app.data.OffRouteRepository
import com.offroute.app.data.UserRole
import com.offroute.app.databinding.ActivityRangerMainBinding

enum class RangerTab {
    TASKS,
    RADAR,
    UNITS,
    MESH
}

class RangerMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRangerMainBinding
    private var tasksFragment: RangerTasksFragment? = null
    private var radarFragment: RangerRadarFragment? = null
    private var unitsFragment: RangerUnitsFragment? = null
    private var meshFragment: RangerMeshFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        OffRouteRepository.activeRole = UserRole.RANGER
        binding = ActivityRangerMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()

        if (savedInstanceState == null) {
            switchToTab(RangerTab.TASKS)
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavRanger.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_map -> {
                    loadFragment(getOrCreateTasksFragment())
                    true
                }
                R.id.nav_report -> {
                    loadFragment(getOrCreateRadarFragment())
                    true
                }
                R.id.nav_sos -> {
                    loadFragment(getOrCreateUnitsFragment())
                    true
                }
                R.id.nav_settings -> {
                    loadFragment(getOrCreateMeshFragment())
                    true
                }
                else -> false
            }
        }
    }

    fun switchToTab(tab: RangerTab) {
        val itemId = when (tab) {
            RangerTab.TASKS -> R.id.nav_map
            RangerTab.RADAR -> R.id.nav_report
            RangerTab.UNITS -> R.id.nav_sos
            RangerTab.MESH -> R.id.nav_settings
        }
        binding.bottomNavRanger.selectedItemId = itemId
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerRanger, fragment)
            .commit()
    }

    private fun getOrCreateTasksFragment(): RangerTasksFragment {
        if (tasksFragment == null) {
            tasksFragment = RangerTasksFragment()
        }
        return tasksFragment!!
    }

    private fun getOrCreateRadarFragment(): RangerRadarFragment {
        if (radarFragment == null) {
            radarFragment = RangerRadarFragment()
        }
        return radarFragment!!
    }

    private fun getOrCreateUnitsFragment(): RangerUnitsFragment {
        if (unitsFragment == null) {
            unitsFragment = RangerUnitsFragment()
        }
        return unitsFragment!!
    }

    private fun getOrCreateMeshFragment(): RangerMeshFragment {
        if (meshFragment == null) {
            meshFragment = RangerMeshFragment()
        }
        return meshFragment!!
    }
}
