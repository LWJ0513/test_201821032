package kr.ac.kku.cs.test_201821032.chatlist

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import kr.ac.kku.cs.test_201821032.chatlist.fragments.OneToOneFragment
import kr.ac.kku.cs.test_201821032.chatlist.fragments.GroupFragment

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fragmentManager, lifecycle){
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position){
            0->{
                OneToOneFragment()
            }
            1->{
                GroupFragment()
            }
            else-> {
                Fragment()
            }
        }

    }
}