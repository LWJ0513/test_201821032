package kr.ac.kku.cs.test_201821032.memberslist

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_members_detail.*
import kr.ac.kku.cs.test_201821032.DBKey
import kr.ac.kku.cs.test_201821032.DBKey.Companion.CHILD_CHAT
import kr.ac.kku.cs.test_201821032.DBKey.Companion.DB_ONE
import kr.ac.kku.cs.test_201821032.DBKey.Companion.DB_USERS
import kr.ac.kku.cs.test_201821032.DBKey.Companion.DB_USER_NAME
import kr.ac.kku.cs.test_201821032.chatdetail.ChatRoomActivity
import kr.ac.kku.cs.test_201821032.chatlist.ChatListItem
import kr.ac.kku.cs.test_201821032.databinding.ActivityMembersDetailBinding

class MembersDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMembersDetailBinding
    private lateinit var userDB: DatabaseReference
    private lateinit var memberDB: DatabaseReference
    private lateinit var chatRoom: ChatListItem
    private lateinit var roomManager: String
    private lateinit var title: String
    private var createAt: Long = 0
    private var imgUri: Uri? = null

    private val auth: FirebaseAuth by lazy { Firebase.auth }

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMembersDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userDB = Firebase.database.reference.child(DB_USERS)

        title =  intent.getStringExtra("title").toString()
        roomManager = intent.getStringExtra("roomManager").toString()

        membersRoomTitleTextView.text = title
        membersRoomDescriptionTextView.text = intent.getStringExtra("description").toString()
        createAt = intent.getLongExtra("createAt", 0)
        imgUri = intent.data
        userDB.child(roomManager).child(DB_USER_NAME)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    membersRoomManagerTextView.text = dataSnapshot.getValue(String::class.java)
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        Glide.with(this)
            .load(intent.data)
            .centerCrop()
            .into(membersRoomImageView)

//todo description 삭제

//        val simpleDateFormat = SimpleDateFormat("yyyy-M-D kk:mm:ss", Locale("ko","KR"))
//        membersRoomCreateAtTextView.text = simpleDateFormat.format(createAt)
        // 자꾸 2021-10-288 06:20:22 로 뜸 ㅡㅡ


        initBackButton()
        initParticipateButton()
    }

    private fun initBackButton() {
        backButton.setOnClickListener {
            finish()
        }
    }

    private fun initParticipateButton() {
        memberDB = Firebase.database.reference.child(DBKey.DB_MEMBERS_LIST)

        participateButton.setOnClickListener {
            chatRoom = ChatListItem(
                entryId = auth.currentUser!!.uid,
                managerId = roomManager,
                roomName = title,
                key = System.currentTimeMillis()
            )
            Toast.makeText(this, "$chatRoom", Toast.LENGTH_SHORT).show()

            userDB.child(auth.currentUser!!.uid)      // 사용자 유저디비에 채팅방 추가
                .child(CHILD_CHAT)
                .child(DB_ONE)
                .push()
                .setValue(chatRoom)

            userDB.child(roomManager)      // 개설자 유저디비에 채팅방 추가
                .child(CHILD_CHAT)
                .child(DB_ONE)
                .push()
                .setValue(chatRoom)

            // TODO 채팅화면으로 바로 이동
            val intent = Intent(this@MembersDetailActivity, ChatRoomActivity::class.java)
            intent.putExtra("chatKey", chatRoom.key)
            startActivity(Intent(intent))
            finish()



        }






        /*
          memberDB.addValueEventListener(object : ValueEventListener {
              override fun onDataChange(dataSnapshot: DataSnapshot) {
              }
              override fun onCancelled(error: DatabaseError) {
              }
          })*/


    }
}