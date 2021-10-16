package kr.ac.kku.cs.test_201821032.grouplist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_groups_online_detail.*
import kotlinx.android.synthetic.main.activity_groups_online_detail.groupsRoomImageView
import kr.ac.kku.cs.test_201821032.DBKey
import kr.ac.kku.cs.test_201821032.chatdetail.ChatRoomActivity
import kr.ac.kku.cs.test_201821032.chatlist.ChatListItem
import kr.ac.kku.cs.test_201821032.databinding.ActivityGroupsOnlineDetailBinding

class GroupsOnlineDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGroupsOnlineDetailBinding
    private lateinit var userDB: DatabaseReference
    private lateinit var groupDB: DatabaseReference
    private lateinit var chatRoom: ChatListItem
    private lateinit var roomManager: String
    private lateinit var title: String
    private lateinit var description: String
    private lateinit var selectedHobby: String

    private val auth: FirebaseAuth by lazy { Firebase.auth }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupsOnlineDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userDB = Firebase.database.reference.child(DBKey.DB_USERS)


        title = intent.getStringExtra("title").toString()
        roomManager = intent.getStringExtra("roomManager").toString()
        description = intent.getStringExtra("description").toString()
        selectedHobby = intent.getStringExtra("selectedHobby").toString()

        groupsOnlineRoomTitleTextView.text = title
        groupsOnlineRoomDescriptionTextView.text = description

        Glide.with(this)
            .load(intent.data)
            .centerCrop()
            .into(groupsRoomImageView)


        initBackButton()
        initSubmitOfflineGroupButton()
    }
    private fun initBackButton() {
        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun initSubmitOfflineGroupButton() {
        groupDB = Firebase.database.reference.child(DBKey.DB_GROUPS_LIST)

        binding.participateButton.setOnClickListener {
            chatRoom = ChatListItem(
                entryId = auth.currentUser!!.uid,
                managerId = roomManager,
                roomName = title,
                key = System.currentTimeMillis()
            )
            Toast.makeText(this, "$chatRoom", Toast.LENGTH_SHORT).show()

            userDB.child(auth.currentUser!!.uid)      // 사용자 유저디비에 채팅방 추가
                .child(DBKey.CHILD_CHAT)
                .child(DBKey.DB_GROUP)
                .push()
                .setValue(chatRoom)

            userDB.child(roomManager)      // 개설자 유저디비에 채팅방 추가
                .child(DBKey.CHILD_CHAT)
                .child(DBKey.DB_GROUP)
                .push()
                .setValue(chatRoom)

            // TODO 채팅화면으로 바로 이동
            val intent = Intent(this, ChatRoomActivity::class.java)
            intent.putExtra("chatKey", chatRoom.key)
            startActivity(Intent(intent))
            finish()

        }
    }
}