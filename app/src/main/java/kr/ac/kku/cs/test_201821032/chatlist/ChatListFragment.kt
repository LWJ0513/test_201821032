package kr.ac.kku.cs.test_201821032.chatlist

import android.annotation.SuppressLint
import android.app.ActionBar
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_chatlist.*
import kr.ac.kku.cs.test_201821032.DBKey.Companion.CHILD_CHAT
import kr.ac.kku.cs.test_201821032.DBKey.Companion.DB_USERS
import kr.ac.kku.cs.test_201821032.HomeActivity
import kr.ac.kku.cs.test_201821032.signIn.LoginActivity
import kr.ac.kku.cs.test_201821032.R
import kr.ac.kku.cs.test_201821032.chatdetail.ChatRoomActivity
import kr.ac.kku.cs.test_201821032.databinding.FragmentChatlistBinding

class ChatListFragment : Fragment(R.layout.fragment_chatlist) {

    private var binding: FragmentChatlistBinding? = null
    private val chatRoomList = mutableListOf<ChatListItem>()
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    @SuppressLint("WrongConstant")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (auth.currentUser == null) { // 로그인이 안되어 있으면
            activity?.let {
                val intent = Intent(context, LoginActivity::class.java)// LoginActivity로 이동
                startActivity(intent)
            }
        }

        setHasOptionsMenu(true)
        val actionBar = (activity as HomeActivity?)!!.supportActionBar
        actionBar!!.setDisplayShowTitleEnabled(false)
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM)
        actionBar.setCustomView(R.layout.title_chatlist)


        val fragmentChatlistBinding = FragmentChatlistBinding.bind(view)
        binding = fragmentChatlistBinding

        chatRoomList.clear()


        val adapter = ViewPagerAdapter(getChildFragmentManager(), lifecycle)
        view_pager_2.isSaveEnabled=false
        view_pager_2.adapter = adapter
        TabLayoutMediator(tab_layout, view_pager_2) { tab, position ->
            when (position) {
                0->{
                    tab.text="1:1"
                }
                1->{
                    tab.text="모임"
                }
            }
        }.attach()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        menu.findItem(R.id.action_toggle).isVisible = false
        menu.findItem(R.id.action_search).isVisible = false
        menu.findItem(R.id.action_edit_hobby).isVisible = false

        super.onCreateOptionsMenu(menu, inflater)
    }
}