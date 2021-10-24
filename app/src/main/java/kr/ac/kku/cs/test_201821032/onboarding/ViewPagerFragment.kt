package kr.ac.kku.cs.test_201821032.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_view_pager.view.*
import kr.ac.kku.cs.test_201821032.*
import kr.ac.kku.cs.test_201821032.onboarding.screens.FirstScreen
import kr.ac.kku.cs.test_201821032.onboarding.screens.FourthScreen
import kr.ac.kku.cs.test_201821032.onboarding.screens.SecondScreen
import kr.ac.kku.cs.test_201821032.onboarding.screens.ThirdScreen

class ViewPagerFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_view_pager, container, false)

        val fragmentList = arrayListOf<Fragment>(
            FirstScreen(),
            SecondScreen(),
            ThirdScreen(),
            FourthScreen()
        )

        val adapter = ViewPagerAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )

        view.viewPager.adapter = adapter

        return view
    }

}