package kr.ac.kku.cs.test_201821032

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kr.ac.kku.cs.test_201821032.databinding.ActivityTestBinding
import kr.ac.kku.cs.test_201821032.onboarding.ViewPagerFragment

class TestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewPagerFragment = ViewPagerFragment()
        if (onBoardingFinish()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            replaceFragment(viewPagerFragment)
        }
    }
    private fun onBoardingFinish(): Boolean {
        val sharedPref= getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("Finished", false)
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .apply {
                replace(R.id.fragmentContainerView, fragment)
                commit()
            }
    }
}