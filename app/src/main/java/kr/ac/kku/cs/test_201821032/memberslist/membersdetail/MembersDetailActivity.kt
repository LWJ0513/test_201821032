package kr.ac.kku.cs.test_201821032.memberslist.membersdetail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
import kr.ac.kku.cs.test_201821032.chatlist.ChatListItem
import kr.ac.kku.cs.test_201821032.databinding.ActivityMembersDetailBinding
import kr.ac.kku.cs.test_201821032.memberslist.MembersModel

class MembersDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMembersDetailBinding
    private lateinit var userDB: DatabaseReference
    private lateinit var memberDB: DatabaseReference
    private val auth: FirebaseAuth by lazy { Firebase.auth }
    private val membersModel by lazy { MembersModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMembersDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

            /*if (auth.currentUser != null ) {
                if (auth.currentUser!!.uid != membersModel.roomManager)
            }*/

            memberDB.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(dataSnapshot: DataSnapshot) {


                    val roomDetail = dataSnapshot

                    val chatRoom = ChatListItem(
                        entryId = auth.currentUser!!.uid,
                        managerId = membersModel.roomManager,
                        roomName = membersModel.title,
                        key = System.currentTimeMillis()
                    )

                    userDB.child(auth.currentUser!!.uid)
                        .child(CHILD_CHAT)
                        .push()
                        .setValue(chatRoom)

                    userDB.child(membersModel.roomManager)
                        .child(CHILD_CHAT)
                        .push()
                        .setValue(chatRoom)

                    // TODO 채팅화면으로 바로 이동
                    finish()
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })



        }
    }
}