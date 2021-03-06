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
import kotlinx.android.synthetic.main.item_members.*
import kr.ac.kku.cs.test_201821032.DBKey
import kr.ac.kku.cs.test_201821032.DBKey.Companion.CHILD_CHAT
import kr.ac.kku.cs.test_201821032.DBKey.Companion.DB_MEMBER
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
    private lateinit var roomNumber: String
    private var createAt: Long = 0
    private var imgUri: Uri? = null

    private val auth: FirebaseAuth by lazy { Firebase.auth }

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMembersDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userDB = Firebase.database.reference.child(DB_USERS)

        roomNumber = intent.getStringExtra("key").toString()
        title =  intent.getStringExtra("title").toString()
        roomManager = intent.getStringExtra("roomManager").toString()

        membersRoomTitleTextView.text = title
        membersRoomDescriptionTextView.text = intent.getStringExtra("description").toString()
        membersRoomHashTagTextView.text = intent.getStringExtra("hashTag").toString()
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

//todo description ??????

//        val simpleDateFormat = SimpleDateFormat("yyyy-M-D kk:mm:ss", Locale("ko","KR"))
//        membersRoomCreateAtTextView.text = simpleDateFormat.format(createAt)
        // ?????? 2021-10-288 06:20:22 ??? ??? ??????


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
            if (auth.currentUser!!.uid != roomManager) {

                userDB.child(auth.currentUser!!.uid).child(DBKey.DB_USER_PROFILE_IMAGE).addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val profileImageUrl = snapshot.getValue(String::class.java)!!
                        chatRoom = ChatListItem(
                            entryId = auth.currentUser!!.uid,
                            managerId = roomManager,
                            roomName = title,
                            key = System.currentTimeMillis().toString(),
                            roomNumber = roomNumber,
                            hobby = "",
                            onOff = "members"
                        )

                        userDB.child(auth.currentUser!!.uid)      // ????????? ??????????????? ????????? ??????
                            .child(CHILD_CHAT)
                            .child(DB_MEMBER)
                            .push()
                            .setValue(chatRoom)

                        userDB.child(roomManager)      // ????????? ??????????????? ????????? ??????
                            .child(CHILD_CHAT)
                            .child(DB_MEMBER)
                            .push()
                            .setValue(chatRoom)


                        val intent = Intent(this@MembersDetailActivity, ChatRoomActivity::class.java)
                        intent.putExtra("chatKey", chatRoom.key)
                        startActivity(Intent(intent))
                        finish()

                    }
                    override fun onCancelled(error: DatabaseError) { }
                })
            } else {     // ?????? ?????? ?????????
                Toast.makeText(this, "?????? ?????? ??????????????????.", Toast.LENGTH_LONG).show()
            }
        }
    }
}