package kr.ac.kku.cs.test_201821032.chatlist

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
    private lateinit var chatListAdapter: ChatListAdapter

    private val chatRoomList = mutableListOf<ChatListItem>()
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (auth.currentUser == null) { // 로그인이 안되어 있으면
            activity?.let {
                val intent = Intent(context, LoginActivity::class.java)// LoginActivity로 이동
                startActivity(intent)
            }
        }

        setHasOptionsMenu(true)
        //(activity as HomeActivity?)!!.supportActionBar!!.hide()
        val actionBar = (activity as HomeActivity?)!!.supportActionBar
        actionBar!!.title = "채팅방 목록"
        actionBar!!.show()




        val fragmentChatlistBinding = FragmentChatlistBinding.bind(view)
        binding = fragmentChatlistBinding

        chatListAdapter = ChatListAdapter(onItemClicked = { chatRoom ->
            // 채팅방으로 이동하는 코드
            context?.let {
                val intent = Intent(it, ChatRoomActivity::class.java)
                intent.putExtra("chatKey", chatRoom.key)
                startActivity(intent)
            }
        })

        chatRoomList.clear()

//        fragmentChatlistBinding.chatListRecyclerView.adapter = chatListAdapter
//        fragmentChatlistBinding.chatListRecyclerView.layoutManager = LinearLayoutManager(context)

        view_pager_2.isSaveEnabled=false

        val adapter = ViewPagerAdapter(getChildFragmentManager(), lifecycle)

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








        if (auth.currentUser == null) {
            return
        }

        val chatDB = Firebase.database.reference.child(DB_USERS).child(auth.currentUser!!.uid)
            .child(CHILD_CHAT)

        chatDB.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val model = it.getValue(ChatListItem::class.java)
                    model ?: return

                    chatRoomList.add(model)
                }

                chatListAdapter.submitList(chatRoomList)
                chatListAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    override fun onResume() {
        super.onResume()

        chatListAdapter.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        menu.findItem(R.id.action_toggle).isVisible = false
//        menu.findItem(R.id.nav_view).isVisible = false

        super.onCreateOptionsMenu(menu, inflater)
    }
}