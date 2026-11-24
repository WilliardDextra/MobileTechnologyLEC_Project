package com.offroute.app.ui.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.offroute.app.R
import com.offroute.app.databinding.ActivityOnboardingBinding
import com.offroute.app.ui.auth.AuthGatewayActivity

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = OnboardingPagerAdapter(this)
        binding.viewPager.adapter = adapter

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateProgress(position)
            }
        })

        binding.btnSkipTop.setOnClickListener {
            navigateToAuth()
        }
    }

    fun goToStep(step: Int) {
        if (step in 0..2) {
            binding.viewPager.currentItem = step
        }
    }

    fun navigateToAuth() {
        val intent = Intent(this, AuthGatewayActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun updateProgress(position: Int) {
        binding.tvStepTitle.text = "OFFROUTE // ONBOARDING STEP ${position + 1}"

        when (position) {
            0 -> {
                binding.segment1.setBackgroundResource(R.drawable.bg_progress_bar_segment_active)
                binding.segment2.setBackgroundResource(R.drawable.bg_progress_bar_segment_inactive)
                binding.segment3.setBackgroundResource(R.drawable.bg_progress_bar_segment_inactive)
            }
            1 -> {
                binding.segment1.setBackgroundResource(R.drawable.bg_progress_bar_segment_active)
                binding.segment2.setBackgroundResource(R.drawable.bg_progress_bar_segment_active)
                binding.segment3.setBackgroundResource(R.drawable.bg_progress_bar_segment_inactive)
            }
            2 -> {
                binding.segment1.setBackgroundResource(R.drawable.bg_progress_bar_segment_active)
                binding.segment2.setBackgroundResource(R.drawable.bg_progress_bar_segment_active)
                binding.segment3.setBackgroundResource(R.drawable.bg_progress_bar_segment_active)
            }
        }
    }
}
