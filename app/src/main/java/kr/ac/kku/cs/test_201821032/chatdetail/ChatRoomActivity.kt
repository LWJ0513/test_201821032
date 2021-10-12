package kr.ac.kku.cs.test_201821032.chatdetail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_chat_room.*
import kr.ac.kku.cs.test_201821032.DBKey.Companion.DB_CHATS
import kr.ac.kku.cs.test_201821032.DBKey.Companion.DB_USERS
import kr.ac.kku.cs.test_201821032.DBKey.Companion.DB_USER_NAME
import kr.ac.kku.cs.test_201821032.databinding.ActivityChatRoomBinding

class ChatRoomActivity : AppCompatActivity() {

    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    private val chatList = mutableListOf<ChatItem>()
    private val adapter = ChatItemAdapter()
    private lateinit var chatDB: DatabaseReference
    private lateinit var userDB: DatabaseReference
    private lateinit var binding: ActivityChatRoomBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userDB = Firebase.database.reference.child(DB_USERS)

        val chatKey = intent.getLongExtra("chatKey", -1)
        chatDB = Firebase.database.reference.child(DB_CHATS).child("$chatKey")

        chatDB.addChildEventListener(object :ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatItem = snapshot.getValue(ChatItem::class.java)
                chatItem ?: return

                chatList.add(chatItem)
                adapter.submitList(chatList)
                adapter.notifyDataSetChanged()

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {  }

            override fun onChildRemoved(snapshot: DataSnapshot) { }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) { }

            override fun onCancelled(error: DatabaseError) {}
        })

        chatRecyclerView.adapter = adapter
        chatRecyclerView.layoutManager = LinearLayoutManager(this)

        sendButton.setOnClickListener {
            var userName: String
            var chatItem: ChatItem
            userDB.child(auth.currentUser!!.uid).child(DB_USER_NAME).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    userName = dataSnapshot.getValue(String::class.java)!!


                    chatItem = ChatItem(
                        senderName = userName,
                        message = messageEditText.text.toString()
                    )
                    chatDB.push().setValue(chatItem)
                    messageEditText.setText("")
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }
    }
}