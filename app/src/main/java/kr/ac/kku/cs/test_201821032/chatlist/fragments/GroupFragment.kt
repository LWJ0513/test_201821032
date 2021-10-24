package kr.ac.kku.cs.test_201821032.chatlist.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kr.ac.kku.cs.test_201821032.DBKey
import kr.ac.kku.cs.test_201821032.R
import kr.ac.kku.cs.test_201821032.chatdetail.ChatRoomActivity
import kr.ac.kku.cs.test_201821032.chatlist.ChatListItem
import kr.ac.kku.cs.test_201821032.chatlist.GroupChatListAdapter
import kr.ac.kku.cs.test_201821032.databinding.FragmentGroupBinding
import kr.ac.kku.cs.test_201821032.grouplist.GroupsOfflineDetailActivity
import kr.ac.kku.cs.test_201821032.grouplist.GroupsOnlineDetailActivity


class GroupFragment : Fragment(R.layout.fragment_group) {

    private var binding: FragmentGroupBinding? = null
    private lateinit var groupChatListAdapter: GroupChatListAdapter
    private val chatRoomList = mutableListOf<ChatListItem>()
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }
    private val groupsOnlineDB: DatabaseReference by lazy {
        Firebase.database.reference.child(DBKey.DB_ONLINE_GROUPS_LIST)
    }
    private val groupsOfflineDB: DatabaseReference by lazy {
        Firebase.database.reference.child(DBKey.DB_OFFLINE_GROUPS_LIST)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentGroupBinding = FragmentGroupBinding.bind(view)
        binding = fragmentGroupBinding

        groupChatListAdapter = GroupChatListAdapter(onChatRoomClicked = { chatRoom ->
            // 채팅방으로 이동하는 코드
            context?.let {
                if (chatRoom.onOff == "Online") {
                    groupsOnlineDB.child(chatRoom.hobby).child(chatRoom.roomNumber).child("title").addValueEventListener(object :ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val title = snapshot.getValue(String::class.java).toString()

                            val intent = Intent(it, ChatRoomActivity::class.java)
                            intent.putExtra("chatKey", chatRoom.key)
                            intent.putExtra("title", title)
                            startActivity(intent)
                        }
                        override fun onCancelled(error: DatabaseError) { }
                    })
                } else{
                    groupsOfflineDB.child(chatRoom.hobby).child(chatRoom.roomNumber).child("title").addValueEventListener(object :ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val title = snapshot.getValue(String::class.java).toString()

                            val intent = Intent(it, ChatRoomActivity::class.java)
                            intent.putExtra("chatKey", chatRoom.key)
                            intent.putExtra("title", title)
                            startActivity(intent)
                        }
                        override fun onCancelled(error: DatabaseError) { }
                    })
                }
            }
        }, onCommunityClicked = { chatRoom ->
            context?.let {
                if (chatRoom.onOff == "Online") {    // 온라인 모임일 때
                    groupsOnlineDB.child(chatRoom.hobby).child(chatRoom.roomNumber)
                        .addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {     // #############################
                                startActivity(
                                    Intent(
                                        context,
                                        GroupsOnlineDetailActivity::class.java
                                    ).apply {
                                        putExtra("roomNumber", dataSnapshot.child("roomNumber").getValue(String::class.java))
                                        putExtra("roomManager", dataSnapshot.child("roomManager").getValue(String::class.java))
                                        putExtra("title", dataSnapshot.child("title").getValue(String::class.java))
                                        putExtra("description", dataSnapshot.child("description").getValue(String::class.java))
                                        putExtra("hashTag", dataSnapshot.child("hashTag").getValue(String::class.java))
                                        putExtra("selectedHobby", dataSnapshot.child("hobby").getValue(String::class.java))
                                        data = dataSnapshot.child("imageUrl").getValue(String::class.java)!!.toUri()
                                    })
                            }
                            override fun onCancelled(error: DatabaseError) { }
                        })
                } else {    // 오프라인 모임일 때
                    groupsOfflineDB.child(chatRoom.hobby).child(chatRoom.roomNumber)
                        .addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                startActivity(
                                    Intent(
                                        context,
                                        GroupsOfflineDetailActivity::class.java
                                    ).apply {
                                        putExtra("roomNumber", dataSnapshot.child("roomNumber").getValue(String::class.java))
                                        putExtra("roomManager", dataSnapshot.child("roomManager").getValue(String::class.java))
                                        putExtra("title", dataSnapshot.child("title").getValue(String::class.java))
                                        putExtra("description", dataSnapshot.child("description").getValue(String::class.java))
                                        putExtra("hashTag", dataSnapshot.child("hashTag").getValue(String::class.java))
                                        putExtra("latitude", dataSnapshot.child("latitude").getValue(Float::class.java))
                                        putExtra("longitude", dataSnapshot.child("longitude").getValue(Float::class.java))
                                        putExtra("locationAddress", dataSnapshot.child("locationAddress").getValue(String::class.java))
                                        putExtra("locationName", dataSnapshot.child("locationName").getValue(String::class.java))
                                        putExtra("selectedHobby", dataSnapshot.child("hobby").getValue(String::class.java))
                                        data = dataSnapshot.child("imageUrl").getValue(String::class.java)!!.toUri()
                                    })
                            }
                            override fun onCancelled(error: DatabaseError) { }
                        })
                }
            }
        })

        fragmentGroupBinding.groupChatListRecyclerView.adapter = groupChatListAdapter
        fragmentGroupBinding.groupChatListRecyclerView.layoutManager = LinearLayoutManager(context)


        chatRoomList.clear()
        val chatDB = Firebase.database.reference.child(DBKey.DB_USERS).child(auth.currentUser!!.uid)
            .child(DBKey.CHILD_CHAT).child(DBKey.DB_GROUP)

        chatDB.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val model = it.getValue(ChatListItem::class.java)
                    model ?: return

                    chatRoomList.add(model)
                }
                groupChatListAdapter.submitList(chatRoomList)
                groupChatListAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) { }
        })
    }

    override fun onResume() {
        super.onResume()

        groupChatListAdapter.notifyDataSetChanged()
    }
}