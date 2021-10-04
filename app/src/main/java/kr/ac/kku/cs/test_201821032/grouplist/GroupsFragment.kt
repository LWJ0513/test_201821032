package kr.ac.kku.cs.test_201821032.grouplist

import android.content.Intent
import android.os.Bundle
import android.view.View
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
import kr.ac.kku.cs.test_201821032.DBKey
import kr.ac.kku.cs.test_201821032.LoginActivity
import kr.ac.kku.cs.test_201821032.R
import kr.ac.kku.cs.test_201821032.chatlist.ChatListItem
import kr.ac.kku.cs.test_201821032.databinding.FragmentGrouplistBinding
import kr.ac.kku.cs.test_201821032.databinding.FragmentMemberslistBinding
import kr.ac.kku.cs.test_201821032.memberslist.AddMembersActivity
import kr.ac.kku.cs.test_201821032.memberslist.MembersAdapter
import kr.ac.kku.cs.test_201821032.memberslist.MembersModel

class GroupsFragment: Fragment(R.layout.fragment_grouplist) {

    private lateinit var groupDB: DatabaseReference
    private lateinit var userDB: DatabaseReference
    private lateinit var groupAdapter: GroupsAdapter

    private val groupList = mutableListOf<GroupsModel>()
    private val listener = object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

            val groupModel = snapshot.getValue(GroupsModel::class.java)
            groupModel ?: return

            groupList.add(groupModel)
            groupAdapter.submitList(groupList)
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

        override fun onChildRemoved(snapshot: DataSnapshot) {}

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

        override fun onCancelled(error: DatabaseError) {}

    }

    private var binding: FragmentGrouplistBinding? = null
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentGroupsBinding = FragmentGrouplistBinding.bind(view)
        binding = fragmentGroupsBinding

        groupList.clear()
        userDB = Firebase.database.reference.child(DBKey.DB_USERS)
        groupDB = Firebase.database.reference.child(DBKey.DB_GROUPS_LIST)
        groupAdapter = GroupsAdapter(onItemClicked = { groupsModel ->
            if (auth.currentUser != null) {         // 로그인을 한 상태
                if (auth.currentUser!!.uid != groupsModel.roomManager) {        // 다른사람이면 채팅방 열기

                    val chatRoom = ChatListItem(
                        entryId = auth.currentUser!!.uid,
                        managerId = groupsModel.roomManager,
                        roomName = groupsModel.title,
                        key = System.currentTimeMillis()
                    )

                    userDB.child(auth.currentUser!!.uid)
                        .child(DBKey.CHILD_CHAT)
                        .push()
                        .setValue(chatRoom)

                    userDB.child(groupsModel.roomManager)
                        .child(DBKey.CHILD_CHAT)
                        .push()
                        .setValue(chatRoom)

                    Snackbar.make(view, "채팅방이 생성되었습니다. 채팅챕에서 확인해주세요", Snackbar.LENGTH_LONG).show()

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

        fragmentGroupsBinding.articleRecyclerView.layoutManager = LinearLayoutManager(context)
        fragmentGroupsBinding.articleRecyclerView.adapter = groupAdapter

        fragmentGroupsBinding.addFloatingButton.setOnClickListener {
            context?.let {
                if (auth.currentUser != null) {         // 회원가입 한 사람만 글을 추가할 수 있게
                    val intent = Intent(it, AddGroupsActivity::class.java)
                    startActivity(intent)
                } else {
                    Snackbar.make(view, "로그인 후 사용해주세요", Snackbar.LENGTH_LONG).show()
                }
            }
        }

        groupDB.addChildEventListener(listener)
    }
    override fun onResume() {
        super.onResume()

        if (auth.currentUser == null) { // 로그인이 안되어 있으면
            activity?.let {
                val intent = Intent(context, LoginActivity::class.java)// LoginActivity로 이동
                startActivity(intent)
                activity?.supportFragmentManager
                    ?.beginTransaction()
                    ?.remove(this)
                    ?.commit()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        groupDB.removeEventListener(listener)
    }
}