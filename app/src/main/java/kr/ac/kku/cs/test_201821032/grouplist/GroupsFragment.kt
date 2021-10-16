package kr.ac.kku.cs.test_201821032.grouplist

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_grouplist.*
import kr.ac.kku.cs.test_201821032.DBKey
import kr.ac.kku.cs.test_201821032.DBKey.Companion.DB_HOBBY1
import kr.ac.kku.cs.test_201821032.DBKey.Companion.DB_HOBBY2
import kr.ac.kku.cs.test_201821032.DBKey.Companion.DB_HOBBY3
import kr.ac.kku.cs.test_201821032.DBKey.Companion.DB_HOBBY4
import kr.ac.kku.cs.test_201821032.DBKey.Companion.DB_HOBBY5
import kr.ac.kku.cs.test_201821032.R
import kr.ac.kku.cs.test_201821032.databinding.FragmentGrouplistBinding
import kr.ac.kku.cs.test_201821032.signIn.Hobbylist
import kr.ac.kku.cs.test_201821032.signIn.LoginActivity

class GroupsFragment : Fragment(R.layout.fragment_grouplist) {

    private lateinit var groupOnlineDB: DatabaseReference
    private lateinit var groupOfflineDB: DatabaseReference
    private lateinit var userDB: DatabaseReference
    private lateinit var groupAdapter: GroupsAdapter
    private var online: Boolean = true

    private val groupList = mutableListOf<GroupsModel>()
    private val listener = object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

            val groupModel = snapshot.getValue(GroupsModel::class.java)
            groupModel ?: return

            groupList.add(groupModel)
            groupAdapter.submitList(groupList)
            groupAdapter.notifyDataSetChanged()
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
        groupOnlineDB = Firebase.database.reference.child(DBKey.DB_ONLINE_GROUPS_LIST)
        groupOfflineDB = Firebase.database.reference.child(DBKey.DB_OFFLINE_GROUPS_LIST)

        // todo 해당하는 취미 출력력

//        groupOnlineDB.addChildEventListener(listener)
        initOnlineGroupList()
        onOffToggleButton.setOnToggledListener { toggleButton, isOn ->
            when (isOn) {
                true -> {
                    online = true
                    groupList.clear()
//                    groupOfflineDB.removeEventListener(listener)
//                    groupOnlineDB.removeEventListener(listener)
//                    groupOnlineDB.addChildEventListener(listener)
                    initOnlineGroupList()
                    if (online) Toast.makeText(context, "online", Toast.LENGTH_SHORT).show()
                }
                false -> {
                    online = false
                    groupList.clear()
//                    groupOfflineDB.removeEventListener(listener)
//                    groupOnlineDB.removeEventListener(listener)
//                    groupOfflineDB.addChildEventListener(listener)
                    initOfflineGrouplist()
                    if (!online) Toast.makeText(context, "offline", Toast.LENGTH_SHORT).show()
                }
            }
        }


        groupAdapter = GroupsAdapter(onItemClicked = { groupsModel ->
            if (auth.currentUser != null) {         // 로그인을 한 상태
                if (auth.currentUser!!.uid != groupsModel.roomManager) {

                    when (online) {
                        true -> {
                            startActivity(
                                Intent(
                                    context,
                                    GroupsOnlineDetailActivity::class.java
                                ).apply {
                                    putExtra("roomManager", groupsModel.roomManager)
                                    putExtra("title", groupsModel.title)
                                    putExtra("createAt", groupsModel.createAt)
                                    putExtra("description", groupsModel.description)
                                    putExtra("online", online)
                                    data = groupsModel.imageUrl.toUri()
                                })
                        }
                        false -> {
                            startActivity(
                                Intent(
                                    context,
                                    GroupsOfflineDetailActivity::class.java
                                ).apply {
                                    putExtra("roomManager", groupsModel.roomManager)
                                    putExtra("title", groupsModel.title)
                                    putExtra("createAt", groupsModel.createAt)
                                    putExtra("description", groupsModel.description)
                                    putExtra("latitude", groupsModel.latitude)
                                    putExtra("longitude", groupsModel.longitude)
                                    putExtra("locationAddress", groupsModel.locationAddress)
                                    putExtra("locationName", groupsModel.locationName)
                                    putExtra("online", online)
                                    data = groupsModel.imageUrl.toUri()
                                })
                        }
                    }


                    /*
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
                        .setValue(chatRoom)*/

                } else {     // 내가 올린 아이템
                    Snackbar.make(view, "내가 올린 게시글입니다.", Snackbar.LENGTH_LONG).show()
                }
            } else {        //로그인 안한 상태
                Snackbar.make(view, "로그인 후 이용해주세요.", Snackbar.LENGTH_LONG).show()
            }
        })

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
    }

    // 화면 리스트 취미관련 출력
//    groupOfflineDB.addChildEventListener(listener)
    private fun initOnlineGroupList() {
        userDB.child(auth.currentUser!!.uid).child(DB_HOBBY1).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                when (dataSnapshot.getValue(String::class.java).toString()) {
                    Hobbylist.SPORTS -> groupOnlineDB.child(Hobbylist.SPORTS)
                        .addChildEventListener(listener)
                    Hobbylist.FASHION -> groupOnlineDB.child(Hobbylist.FASHION)
                        .addChildEventListener(listener)
                    Hobbylist.FUND -> groupOnlineDB.child(Hobbylist.FUND)
                        .addChildEventListener(listener)
                    Hobbylist.IT -> groupOnlineDB.child(Hobbylist.IT)
                        .addChildEventListener(listener)
                    Hobbylist.GAME -> groupOnlineDB.child(Hobbylist.GAME)
                        .addChildEventListener(listener)
                    Hobbylist.STUDY -> groupOnlineDB.child(Hobbylist.STUDY)
                        .addChildEventListener(listener)
                    Hobbylist.READING -> groupOnlineDB.child(Hobbylist.READING)
                        .addChildEventListener(listener)
                    Hobbylist.TRAVEL -> groupOnlineDB.child(Hobbylist.TRAVEL)
                        .addChildEventListener(listener)
                    Hobbylist.ENTERTAINMENT -> groupOnlineDB.child(Hobbylist.ENTERTAINMENT)
                        .addChildEventListener(listener)
                    Hobbylist.PET -> groupOnlineDB.child(Hobbylist.PET)
                        .addChildEventListener(listener)
                    Hobbylist.FOOD -> groupOnlineDB.child(Hobbylist.FOOD)
                        .addChildEventListener(listener)
                    Hobbylist.BEAUTY -> groupOnlineDB.child(Hobbylist.BEAUTY)
                        .addChildEventListener(listener)
                    Hobbylist.ART -> groupOnlineDB.child(Hobbylist.ART)
                        .addChildEventListener(listener)
                    Hobbylist.DIY -> groupOnlineDB.child(Hobbylist.DIY)
                        .addChildEventListener(listener)
                    Hobbylist.COUNSELING -> groupOnlineDB.child(Hobbylist.COUNSELING)
                        .addChildEventListener(listener)
                    Hobbylist.RIDE -> groupOnlineDB.child(Hobbylist.RIDE)
                        .addChildEventListener(listener)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        userDB.child(auth.currentUser!!.uid).child(DB_HOBBY2).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                when (dataSnapshot.getValue(String::class.java).toString()) {
                    Hobbylist.SPORTS -> groupOnlineDB.child(Hobbylist.SPORTS)
                        .addChildEventListener(listener)
                    Hobbylist.FASHION -> groupOnlineDB.child(Hobbylist.FASHION)
                        .addChildEventListener(listener)
                    Hobbylist.FUND -> groupOnlineDB.child(Hobbylist.FUND)
                        .addChildEventListener(listener)
                    Hobbylist.IT -> groupOnlineDB.child(Hobbylist.IT)
                        .addChildEventListener(listener)
                    Hobbylist.GAME -> groupOnlineDB.child(Hobbylist.GAME)
                        .addChildEventListener(listener)
                    Hobbylist.STUDY -> groupOnlineDB.child(Hobbylist.STUDY)
                        .addChildEventListener(listener)
                    Hobbylist.READING -> groupOnlineDB.child(Hobbylist.READING)
                        .addChildEventListener(listener)
                    Hobbylist.TRAVEL -> groupOnlineDB.child(Hobbylist.TRAVEL)
                        .addChildEventListener(listener)
                    Hobbylist.ENTERTAINMENT -> groupOnlineDB.child(Hobbylist.ENTERTAINMENT)
                        .addChildEventListener(listener)
                    Hobbylist.PET -> groupOnlineDB.child(Hobbylist.PET)
                        .addChildEventListener(listener)
                    Hobbylist.FOOD -> groupOnlineDB.child(Hobbylist.FOOD)
                        .addChildEventListener(listener)
                    Hobbylist.BEAUTY -> groupOnlineDB.child(Hobbylist.BEAUTY)
                        .addChildEventListener(listener)
                    Hobbylist.ART -> groupOnlineDB.child(Hobbylist.ART)
                        .addChildEventListener(listener)
                    Hobbylist.DIY -> groupOnlineDB.child(Hobbylist.DIY)
                        .addChildEventListener(listener)
                    Hobbylist.COUNSELING -> groupOnlineDB.child(Hobbylist.COUNSELING)
                        .addChildEventListener(listener)
                    Hobbylist.RIDE -> groupOnlineDB.child(Hobbylist.RIDE)
                        .addChildEventListener(listener)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        userDB.child(auth.currentUser!!.uid).child(DB_HOBBY3).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                when (dataSnapshot.getValue(String::class.java).toString()) {
                    Hobbylist.SPORTS -> groupOnlineDB.child(Hobbylist.SPORTS)
                        .addChildEventListener(listener)
                    Hobbylist.FASHION -> groupOnlineDB.child(Hobbylist.FASHION)
                        .addChildEventListener(listener)
                    Hobbylist.FUND -> groupOnlineDB.child(Hobbylist.FUND)
                        .addChildEventListener(listener)
                    Hobbylist.IT -> groupOnlineDB.child(Hobbylist.IT)
                        .addChildEventListener(listener)
                    Hobbylist.GAME -> groupOnlineDB.child(Hobbylist.GAME)
                        .addChildEventListener(listener)
                    Hobbylist.STUDY -> groupOnlineDB.child(Hobbylist.STUDY)
                        .addChildEventListener(listener)
                    Hobbylist.READING -> groupOnlineDB.child(Hobbylist.READING)
                        .addChildEventListener(listener)
                    Hobbylist.TRAVEL -> groupOnlineDB.child(Hobbylist.TRAVEL)
                        .addChildEventListener(listener)
                    Hobbylist.ENTERTAINMENT -> groupOnlineDB.child(Hobbylist.ENTERTAINMENT)
                        .addChildEventListener(listener)
                    Hobbylist.PET -> groupOnlineDB.child(Hobbylist.PET)
                        .addChildEventListener(listener)
                    Hobbylist.FOOD -> groupOnlineDB.child(Hobbylist.FOOD)
                        .addChildEventListener(listener)
                    Hobbylist.BEAUTY -> groupOnlineDB.child(Hobbylist.BEAUTY)
                        .addChildEventListener(listener)
                    Hobbylist.ART -> groupOnlineDB.child(Hobbylist.ART)
                        .addChildEventListener(listener)
                    Hobbylist.DIY -> groupOnlineDB.child(Hobbylist.DIY)
                        .addChildEventListener(listener)
                    Hobbylist.COUNSELING -> groupOnlineDB.child(Hobbylist.COUNSELING)
                        .addChildEventListener(listener)
                    Hobbylist.RIDE -> groupOnlineDB.child(Hobbylist.RIDE)
                        .addChildEventListener(listener)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        userDB.child(auth.currentUser!!.uid).child(DB_HOBBY4).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                when (dataSnapshot.getValue(String::class.java).toString()) {
                    Hobbylist.SPORTS -> groupOnlineDB.child(Hobbylist.SPORTS)
                        .addChildEventListener(listener)
                    Hobbylist.FASHION -> groupOnlineDB.child(Hobbylist.FASHION)
                        .addChildEventListener(listener)
                    Hobbylist.FUND -> groupOnlineDB.child(Hobbylist.FUND)
                        .addChildEventListener(listener)
                    Hobbylist.IT -> groupOnlineDB.child(Hobbylist.IT)
                        .addChildEventListener(listener)
                    Hobbylist.GAME -> groupOnlineDB.child(Hobbylist.GAME)
                        .addChildEventListener(listener)
                    Hobbylist.STUDY -> groupOnlineDB.child(Hobbylist.STUDY)
                        .addChildEventListener(listener)
                    Hobbylist.READING -> groupOnlineDB.child(Hobbylist.READING)
                        .addChildEventListener(listener)
                    Hobbylist.TRAVEL -> groupOnlineDB.child(Hobbylist.TRAVEL)
                        .addChildEventListener(listener)
                    Hobbylist.ENTERTAINMENT -> groupOnlineDB.child(Hobbylist.ENTERTAINMENT)
                        .addChildEventListener(listener)
                    Hobbylist.PET -> groupOnlineDB.child(Hobbylist.PET)
                        .addChildEventListener(listener)
                    Hobbylist.FOOD -> groupOnlineDB.child(Hobbylist.FOOD)
                        .addChildEventListener(listener)
                    Hobbylist.BEAUTY -> groupOnlineDB.child(Hobbylist.BEAUTY)
                        .addChildEventListener(listener)
                    Hobbylist.ART -> groupOnlineDB.child(Hobbylist.ART)
                        .addChildEventListener(listener)
                    Hobbylist.DIY -> groupOnlineDB.child(Hobbylist.DIY)
                        .addChildEventListener(listener)
                    Hobbylist.COUNSELING -> groupOnlineDB.child(Hobbylist.COUNSELING)
                        .addChildEventListener(listener)
                    Hobbylist.RIDE -> groupOnlineDB.child(Hobbylist.RIDE)
                        .addChildEventListener(listener)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        userDB.child(auth.currentUser!!.uid).child(DB_HOBBY5).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                when (dataSnapshot.getValue(String::class.java).toString()) {
                    Hobbylist.SPORTS -> groupOnlineDB.child(Hobbylist.SPORTS)
                        .addChildEventListener(listener)
                    Hobbylist.FASHION -> groupOnlineDB.child(Hobbylist.FASHION)
                        .addChildEventListener(listener)
                    Hobbylist.FUND -> groupOnlineDB.child(Hobbylist.FUND)
                        .addChildEventListener(listener)
                    Hobbylist.IT -> groupOnlineDB.child(Hobbylist.IT)
                        .addChildEventListener(listener)
                    Hobbylist.GAME -> groupOnlineDB.child(Hobbylist.GAME)
                        .addChildEventListener(listener)
                    Hobbylist.STUDY -> groupOnlineDB.child(Hobbylist.STUDY)
                        .addChildEventListener(listener)
                    Hobbylist.READING -> groupOnlineDB.child(Hobbylist.READING)
                        .addChildEventListener(listener)
                    Hobbylist.TRAVEL -> groupOnlineDB.child(Hobbylist.TRAVEL)
                        .addChildEventListener(listener)
                    Hobbylist.ENTERTAINMENT -> groupOnlineDB.child(Hobbylist.ENTERTAINMENT)
                        .addChildEventListener(listener)
                    Hobbylist.PET -> groupOnlineDB.child(Hobbylist.PET)
                        .addChildEventListener(listener)
                    Hobbylist.FOOD -> groupOnlineDB.child(Hobbylist.FOOD)
                        .addChildEventListener(listener)
                    Hobbylist.BEAUTY -> groupOnlineDB.child(Hobbylist.BEAUTY)
                        .addChildEventListener(listener)
                    Hobbylist.ART -> groupOnlineDB.child(Hobbylist.ART)
                        .addChildEventListener(listener)
                    Hobbylist.DIY -> groupOnlineDB.child(Hobbylist.DIY)
                        .addChildEventListener(listener)
                    Hobbylist.COUNSELING -> groupOnlineDB.child(Hobbylist.COUNSELING)
                        .addChildEventListener(listener)
                    Hobbylist.RIDE -> groupOnlineDB.child(Hobbylist.RIDE)
                        .addChildEventListener(listener)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

    }

    private fun initOfflineGrouplist() {
        userDB.child(auth.currentUser!!.uid).child(DB_HOBBY1).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                when (dataSnapshot.getValue(String::class.java).toString()) {
                    Hobbylist.SPORTS -> groupOfflineDB.child(Hobbylist.SPORTS)
                        .addChildEventListener(listener)
                    Hobbylist.FASHION -> groupOfflineDB.child(Hobbylist.FASHION)
                        .addChildEventListener(listener)
                    Hobbylist.FUND -> groupOfflineDB.child(Hobbylist.FUND)
                        .addChildEventListener(listener)
                    Hobbylist.IT -> groupOfflineDB.child(Hobbylist.IT)
                        .addChildEventListener(listener)
                    Hobbylist.GAME -> groupOfflineDB.child(Hobbylist.GAME)
                        .addChildEventListener(listener)
                    Hobbylist.STUDY -> groupOfflineDB.child(Hobbylist.STUDY)
                        .addChildEventListener(listener)
                    Hobbylist.READING -> groupOfflineDB.child(Hobbylist.READING)
                        .addChildEventListener(listener)
                    Hobbylist.TRAVEL -> groupOfflineDB.child(Hobbylist.TRAVEL)
                        .addChildEventListener(listener)
                    Hobbylist.ENTERTAINMENT -> groupOfflineDB.child(Hobbylist.ENTERTAINMENT)
                        .addChildEventListener(listener)
                    Hobbylist.PET -> groupOfflineDB.child(Hobbylist.PET)
                        .addChildEventListener(listener)
                    Hobbylist.FOOD -> groupOfflineDB.child(Hobbylist.FOOD)
                        .addChildEventListener(listener)
                    Hobbylist.BEAUTY -> groupOfflineDB.child(Hobbylist.BEAUTY)
                        .addChildEventListener(listener)
                    Hobbylist.ART -> groupOfflineDB.child(Hobbylist.ART)
                        .addChildEventListener(listener)
                    Hobbylist.DIY -> groupOfflineDB.child(Hobbylist.DIY)
                        .addChildEventListener(listener)
                    Hobbylist.COUNSELING -> groupOfflineDB.child(Hobbylist.COUNSELING)
                        .addChildEventListener(listener)
                    Hobbylist.RIDE -> groupOfflineDB.child(Hobbylist.RIDE)
                        .addChildEventListener(listener)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        userDB.child(auth.currentUser!!.uid).child(DB_HOBBY2).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                when (dataSnapshot.getValue(String::class.java).toString()) {
                    Hobbylist.SPORTS -> groupOfflineDB.child(Hobbylist.SPORTS)
                        .addChildEventListener(listener)
                    Hobbylist.FASHION -> groupOfflineDB.child(Hobbylist.FASHION)
                        .addChildEventListener(listener)
                    Hobbylist.FUND -> groupOfflineDB.child(Hobbylist.FUND)
                        .addChildEventListener(listener)
                    Hobbylist.IT -> groupOfflineDB.child(Hobbylist.IT)
                        .addChildEventListener(listener)
                    Hobbylist.GAME -> groupOfflineDB.child(Hobbylist.GAME)
                        .addChildEventListener(listener)
                    Hobbylist.STUDY -> groupOfflineDB.child(Hobbylist.STUDY)
                        .addChildEventListener(listener)
                    Hobbylist.READING -> groupOfflineDB.child(Hobbylist.READING)
                        .addChildEventListener(listener)
                    Hobbylist.TRAVEL -> groupOfflineDB.child(Hobbylist.TRAVEL)
                        .addChildEventListener(listener)
                    Hobbylist.ENTERTAINMENT -> groupOfflineDB.child(Hobbylist.ENTERTAINMENT)
                        .addChildEventListener(listener)
                    Hobbylist.PET -> groupOfflineDB.child(Hobbylist.PET)
                        .addChildEventListener(listener)
                    Hobbylist.FOOD -> groupOfflineDB.child(Hobbylist.FOOD)
                        .addChildEventListener(listener)
                    Hobbylist.BEAUTY -> groupOfflineDB.child(Hobbylist.BEAUTY)
                        .addChildEventListener(listener)
                    Hobbylist.ART -> groupOfflineDB.child(Hobbylist.ART)
                        .addChildEventListener(listener)
                    Hobbylist.DIY -> groupOfflineDB.child(Hobbylist.DIY)
                        .addChildEventListener(listener)
                    Hobbylist.COUNSELING -> groupOfflineDB.child(Hobbylist.COUNSELING)
                        .addChildEventListener(listener)
                    Hobbylist.RIDE -> groupOfflineDB.child(Hobbylist.RIDE)
                        .addChildEventListener(listener)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        userDB.child(auth.currentUser!!.uid).child(DB_HOBBY3).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                when (dataSnapshot.getValue(String::class.java).toString()) {
                    Hobbylist.SPORTS -> groupOfflineDB.child(Hobbylist.SPORTS)
                        .addChildEventListener(listener)
                    Hobbylist.FASHION -> groupOfflineDB.child(Hobbylist.FASHION)
                        .addChildEventListener(listener)
                    Hobbylist.FUND -> groupOfflineDB.child(Hobbylist.FUND)
                        .addChildEventListener(listener)
                    Hobbylist.IT -> groupOfflineDB.child(Hobbylist.IT)
                        .addChildEventListener(listener)
                    Hobbylist.GAME -> groupOfflineDB.child(Hobbylist.GAME)
                        .addChildEventListener(listener)
                    Hobbylist.STUDY -> groupOfflineDB.child(Hobbylist.STUDY)
                        .addChildEventListener(listener)
                    Hobbylist.READING -> groupOfflineDB.child(Hobbylist.READING)
                        .addChildEventListener(listener)
                    Hobbylist.TRAVEL -> groupOfflineDB.child(Hobbylist.TRAVEL)
                        .addChildEventListener(listener)
                    Hobbylist.ENTERTAINMENT -> groupOfflineDB.child(Hobbylist.ENTERTAINMENT)
                        .addChildEventListener(listener)
                    Hobbylist.PET -> groupOfflineDB.child(Hobbylist.PET)
                        .addChildEventListener(listener)
                    Hobbylist.FOOD -> groupOfflineDB.child(Hobbylist.FOOD)
                        .addChildEventListener(listener)
                    Hobbylist.BEAUTY -> groupOfflineDB.child(Hobbylist.BEAUTY)
                        .addChildEventListener(listener)
                    Hobbylist.ART -> groupOfflineDB.child(Hobbylist.ART)
                        .addChildEventListener(listener)
                    Hobbylist.DIY -> groupOfflineDB.child(Hobbylist.DIY)
                        .addChildEventListener(listener)
                    Hobbylist.COUNSELING -> groupOfflineDB.child(Hobbylist.COUNSELING)
                        .addChildEventListener(listener)
                    Hobbylist.RIDE -> groupOfflineDB.child(Hobbylist.RIDE)
                        .addChildEventListener(listener)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        userDB.child(auth.currentUser!!.uid).child(DB_HOBBY4).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                when (dataSnapshot.getValue(String::class.java).toString()) {
                    Hobbylist.SPORTS -> groupOfflineDB.child(Hobbylist.SPORTS)
                        .addChildEventListener(listener)
                    Hobbylist.FASHION -> groupOfflineDB.child(Hobbylist.FASHION)
                        .addChildEventListener(listener)
                    Hobbylist.FUND -> groupOfflineDB.child(Hobbylist.FUND)
                        .addChildEventListener(listener)
                    Hobbylist.IT -> groupOfflineDB.child(Hobbylist.IT)
                        .addChildEventListener(listener)
                    Hobbylist.GAME -> groupOfflineDB.child(Hobbylist.GAME)
                        .addChildEventListener(listener)
                    Hobbylist.STUDY -> groupOfflineDB.child(Hobbylist.STUDY)
                        .addChildEventListener(listener)
                    Hobbylist.READING -> groupOfflineDB.child(Hobbylist.READING)
                        .addChildEventListener(listener)
                    Hobbylist.TRAVEL -> groupOfflineDB.child(Hobbylist.TRAVEL)
                        .addChildEventListener(listener)
                    Hobbylist.ENTERTAINMENT -> groupOfflineDB.child(Hobbylist.ENTERTAINMENT)
                        .addChildEventListener(listener)
                    Hobbylist.PET -> groupOfflineDB.child(Hobbylist.PET)
                        .addChildEventListener(listener)
                    Hobbylist.FOOD -> groupOfflineDB.child(Hobbylist.FOOD)
                        .addChildEventListener(listener)
                    Hobbylist.BEAUTY -> groupOfflineDB.child(Hobbylist.BEAUTY)
                        .addChildEventListener(listener)
                    Hobbylist.ART -> groupOfflineDB.child(Hobbylist.ART)
                        .addChildEventListener(listener)
                    Hobbylist.DIY -> groupOfflineDB.child(Hobbylist.DIY)
                        .addChildEventListener(listener)
                    Hobbylist.COUNSELING -> groupOfflineDB.child(Hobbylist.COUNSELING)
                        .addChildEventListener(listener)
                    Hobbylist.RIDE -> groupOfflineDB.child(Hobbylist.RIDE)
                        .addChildEventListener(listener)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        userDB.child(auth.currentUser!!.uid).child(DB_HOBBY5).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                when (dataSnapshot.getValue(String::class.java).toString()) {
                    Hobbylist.SPORTS -> groupOfflineDB.child(Hobbylist.SPORTS)
                        .addChildEventListener(listener)
                    Hobbylist.FASHION -> groupOfflineDB.child(Hobbylist.FASHION)
                        .addChildEventListener(listener)
                    Hobbylist.FUND -> groupOfflineDB.child(Hobbylist.FUND)
                        .addChildEventListener(listener)
                    Hobbylist.IT -> groupOfflineDB.child(Hobbylist.IT)
                        .addChildEventListener(listener)
                    Hobbylist.GAME -> groupOfflineDB.child(Hobbylist.GAME)
                        .addChildEventListener(listener)
                    Hobbylist.STUDY -> groupOfflineDB.child(Hobbylist.STUDY)
                        .addChildEventListener(listener)
                    Hobbylist.READING -> groupOfflineDB.child(Hobbylist.READING)
                        .addChildEventListener(listener)
                    Hobbylist.TRAVEL -> groupOfflineDB.child(Hobbylist.TRAVEL)
                        .addChildEventListener(listener)
                    Hobbylist.ENTERTAINMENT -> groupOfflineDB.child(Hobbylist.ENTERTAINMENT)
                        .addChildEventListener(listener)
                    Hobbylist.PET -> groupOfflineDB.child(Hobbylist.PET)
                        .addChildEventListener(listener)
                    Hobbylist.FOOD -> groupOfflineDB.child(Hobbylist.FOOD)
                        .addChildEventListener(listener)
                    Hobbylist.BEAUTY -> groupOfflineDB.child(Hobbylist.BEAUTY)
                        .addChildEventListener(listener)
                    Hobbylist.ART -> groupOfflineDB.child(Hobbylist.ART)
                        .addChildEventListener(listener)
                    Hobbylist.DIY -> groupOfflineDB.child(Hobbylist.DIY)
                        .addChildEventListener(listener)
                    Hobbylist.COUNSELING -> groupOfflineDB.child(Hobbylist.COUNSELING)
                        .addChildEventListener(listener)
                    Hobbylist.RIDE -> groupOfflineDB.child(Hobbylist.RIDE)
                        .addChildEventListener(listener)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    override fun onResume() {
        super.onResume()

        groupAdapter.notifyDataSetChanged()

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

        groupOnlineDB.removeEventListener(listener)
        groupOfflineDB.removeEventListener(listener)
    }
}