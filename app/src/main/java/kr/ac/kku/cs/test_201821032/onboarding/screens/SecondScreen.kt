package kr.ac.kku.cs.test_201821032.onboarding.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.fragment_tutorial_second_screen.view.*
import kr.ac.kku.cs.test_201821032.R

class SecondScreen : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_tutorial_second_screen, container, false)

        val viewPager = activity?.findViewById<ViewPager2>(R.id.viewPager)

        view.previous.setOnClickListener {
            viewPager?.currentItem = 0
        }
        view.next.setOnClickListener {
            viewPager?.currentItem = 2
        }
        return view
    }
}