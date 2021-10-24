package kr.ac.kku.cs.test_201821032.onboarding.screens

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.fragment_tutorial_fourth_screen.*
import kotlinx.android.synthetic.main.fragment_tutorial_fourth_screen.view.*
import kotlinx.android.synthetic.main.fragment_tutorial_fourth_screen.view.previous
import kotlinx.android.synthetic.main.fragment_view_pager.*
import kr.ac.kku.cs.test_201821032.MainActivity
import kr.ac.kku.cs.test_201821032.R


class FourthScreen : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_tutorial_fourth_screen, container, false)
        val viewPager = activity?.findViewById<ViewPager2>(R.id.viewPager)
        view.previous.setOnClickListener {
            viewPager?.currentItem = 2
        }
        view.finish.setOnClickListener {
            onBoardingFinished()
            startActivity(Intent(context, MainActivity::class.java))
            finish
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.remove(this)
                ?.commit()
        }

        return view
    }

    private fun onBoardingFinished(){
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("Finished", true)
        editor.apply()
    }
}