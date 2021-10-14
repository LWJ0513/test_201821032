package kr.ac.kku.cs.test_201821032.memberslist

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kr.ac.kku.cs.test_201821032.DBKey.Companion.CHILD_CHAT
import kr.ac.kku.cs.test_201821032.DBKey.Companion.DB_MEMBERS_LIST
import kr.ac.kku.cs.test_201821032.DBKey.Companion.DB_USERS
import kr.ac.kku.cs.test_201821032.R
import kr.ac.kku.cs.test_201821032.chatlist.ChatListItem
import kr.ac.kku.cs.test_201821032.databinding.FragmentMemberslistBinding
import kr.ac.kku.cs.test_201821032.memberslist.membersdetail.MembersDetailActivity

class MembersFragment : Fragment(R.layout.fragment_memberslist) {

    private lateinit var memberDB: DatabaseReference
    private lateinit var userDB: DatabaseReference
    private lateinit var memberAdapter: MembersAdapter

    private val memberList = mutableListOf<MembersModel>()
    private val listener = object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

            val membersModel = snapshot.getValue(MembersModel::class.java)
            membersModel ?: return

            memberList.add(membersModel)
            memberAdapter.submitList(memberList)
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

        override fun onChildRemoved(snapshot: DataSnapshot) {}

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

        override fun onCancelled(error: DatabaseError) {}

    }

    private var binding: FragmentMemberslistBinding? = null
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentMembersBinding = FragmentMemberslistBinding.bind(view)
        binding = fragmentMembersBinding

        memberList.clear()
        userDB = Firebase.database.reference.child(DB_USERS)
        memberDB = Firebase.database.reference.child(DB_MEMBERS_LIST)
        memberAdapter = MembersAdapter(onItemClicked = { membersModel ->
            if (auth.currentUser != null) {         // 로그인을 한 상태
                if (auth.currentUser!!.uid != membersModel.roomManager) {        // 다른사람이면 채팅방 열기

                    Toast.makeText(context, membersModel.toString(), Toast.LENGTH_SHORT).show()

                    // todo 데이터 넘겨주기 ***********************************


                    // todo 디테일 창 열기
                  /*  startActivity(Intent(context, MembersDetailActivity::class.java).apply {

                    })
*/



                    val chatRoom = ChatListItem(
                        entryId = auth.currentUser!!.uid,
                        managerId = membersModel.roomManager,
                        roomName = membersModel.title,
                        key = System.currentTimeMillis()
                    )
                       userDB.child(auth.currentUser!!.uid)   // 클릭한사람 채팅방 생성
                           .child(CHILD_CHAT)
                           .push()
                           .setValue(chatRoom)

                       userDB.child(membersModel.roomManager)   // 방개설자 채팅방 생성
                           .child(CHILD_CHAT)
                           .push()
                           .setValue(chatRoom)

                       Snackbar.make(view, "채팅방이 생성되었습니다. 채팅 탭에서 확인해주세요", Snackbar.LENGTH_LONG).show()
                } else {     // 내가 올린 아이템
                    Snackbar.make(view, "내가 올린 게시글입니다.", Snackbar.LENGTH_LONG).show()
                }
            } else {        //로그인 안한 상태
                Snackbar.make(view, "로그인 후 이용해주세요.", Snackbar.LENGTH_LONG).show()
            }
        })
//        articleAdapter.submitList(mutableListOf<ArticleModel>().apply {
//            add(ArticleModel("0", "aaa", 100000, "5000원", ""))
//            add(ArticleModel("0", "bbb", 200000, "35000원", ""))
//        })

        fragmentMembersBinding.articleRecyclerView.layoutManager = LinearLayoutManager(context)
        fragmentMembersBinding.articleRecyclerView.adapter = memberAdapter

        fragmentMembersBinding.addFloatingButton.setOnClickListener {
            context?.let {
                if (auth.currentUser != null) {         // 회원가입 한 사람만 글을 추가할 수 있게
                    val intent = Intent(it, AddMembersActivity::class.java)
                    startActivity(intent)
                } else {
                    Snackbar.make(view, "로그인 후 사용해주세요", Snackbar.LENGTH_LONG).show()
                }
            }
        }

        memberDB.addChildEventListener(listener)
    }

    override fun onResume() {
        super.onResume()

        memberAdapter.notifyDataSetChanged()


    }

    override fun onDestroyView() {
        super.onDestroyView()

        memberDB.removeEventListener(listener)
    }
}