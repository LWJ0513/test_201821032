package kr.ac.kku.cs.test_201821032.memberslist

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_memberslist.*
import kr.ac.kku.cs.test_201821032.DBKey.Companion.DB_HOBBY1
import kr.ac.kku.cs.test_201821032.DBKey.Companion.DB_HOBBY2
import kr.ac.kku.cs.test_201821032.DBKey.Companion.DB_HOBBY3
import kr.ac.kku.cs.test_201821032.DBKey.Companion.DB_HOBBY4
import kr.ac.kku.cs.test_201821032.DBKey.Companion.DB_HOBBY5
import kr.ac.kku.cs.test_201821032.DBKey.Companion.DB_MEMBERS_LIST
import kr.ac.kku.cs.test_201821032.DBKey.Companion.DB_USERS
import kr.ac.kku.cs.test_201821032.HomeActivity
import kr.ac.kku.cs.test_201821032.R
import kr.ac.kku.cs.test_201821032.databinding.FragmentMemberslistBinding
import kr.ac.kku.cs.test_201821032.signIn.Hobbylist.Companion.ART
import kr.ac.kku.cs.test_201821032.signIn.Hobbylist.Companion.BEAUTY
import kr.ac.kku.cs.test_201821032.signIn.Hobbylist.Companion.COUNSELING
import kr.ac.kku.cs.test_201821032.signIn.Hobbylist.Companion.DIY
import kr.ac.kku.cs.test_201821032.signIn.Hobbylist.Companion.ENTERTAINMENT
import kr.ac.kku.cs.test_201821032.signIn.Hobbylist.Companion.FASHION
import kr.ac.kku.cs.test_201821032.signIn.Hobbylist.Companion.FOOD
import kr.ac.kku.cs.test_201821032.signIn.Hobbylist.Companion.FUND
import kr.ac.kku.cs.test_201821032.signIn.Hobbylist.Companion.GAME
import kr.ac.kku.cs.test_201821032.signIn.Hobbylist.Companion.IT
import kr.ac.kku.cs.test_201821032.signIn.Hobbylist.Companion.PET
import kr.ac.kku.cs.test_201821032.signIn.Hobbylist.Companion.READING
import kr.ac.kku.cs.test_201821032.signIn.Hobbylist.Companion.RIDE
import kr.ac.kku.cs.test_201821032.signIn.Hobbylist.Companion.SPORTS
import kr.ac.kku.cs.test_201821032.signIn.Hobbylist.Companion.STUDY
import kr.ac.kku.cs.test_201821032.signIn.Hobbylist.Companion.TRAVEL

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
            memberList.shuffle()
            memberAdapter.submitList(memberList)
            memberAdapter.notifyDataSetChanged()
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


        setHasOptionsMenu(true)
        val actionBar = (activity as HomeActivity?)!!.supportActionBar
        actionBar!!.setDisplayShowTitleEnabled(true)
        actionBar.setDisplayShowCustomEnabled(false)
        actionBar.title = "1:1"
        actionBar.show()


        memberList.clear()
        userDB = Firebase.database.reference.child(DB_USERS)
        memberDB = Firebase.database.reference.child(DB_MEMBERS_LIST)


        memberAdapter = MembersAdapter(onItemClicked = { membersModel ->
            if (auth.currentUser != null) {         // 로그인을 한 상태
                startActivity(Intent(context, MembersDetailActivity::class.java).apply {
                    putExtra("key", membersModel.roomNumber)
                    putExtra("roomManager", membersModel.roomManager)
                    putExtra("title", membersModel.title)
                    putExtra("createAt", membersModel.createAt)
                    putExtra("description", membersModel.description)
                    putExtra("hashTag", membersModel.hashTag)
                    data = membersModel.imageUrl.toUri()
                })
            } else {        //로그인 안한 상태
                Snackbar.make(view, "로그인 후 이용해주세요.", Snackbar.LENGTH_LONG).show()
            }
        })


        fragmentMembersBinding.articleRecyclerView.layoutManager = LinearLayoutManager(context)
        fragmentMembersBinding.articleRecyclerView.adapter = memberAdapter


        swipeRefreshLayout.setOnRefreshListener {   // 당겨서 새로고침
            memberList.shuffle()
            memberAdapter.submitList(memberList)
            memberAdapter.notifyDataSetChanged()        // 새로고침
            swipeRefreshLayout.setRefreshing(false)
        }

        fragmentMembersBinding.addMembersButton.setOnClickListener {
            context?.let {
                if (auth.currentUser != null) {         // 회원가입 한 사람만 글을 추가할 수 있게
                    val intent = Intent(it, AddMembersActivity::class.java)
                    startActivity(intent)
                } else {
                    Snackbar.make(view, "로그인 후 사용해주세요", Snackbar.LENGTH_LONG).show()
                }
            }
        }

        userDB.child(auth.currentUser!!.uid).child(DB_HOBBY1).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                memberList.clear()                                  /////////////////////////
                when (dataSnapshot.getValue(String::class.java).toString()) {
                    SPORTS -> memberDB.child(SPORTS).addChildEventListener(listener)
                    FASHION -> memberDB.child(FASHION).addChildEventListener(listener)
                    FUND -> memberDB.child(FUND).addChildEventListener(listener)
                    IT -> memberDB.child(IT).addChildEventListener(listener)
                    GAME -> memberDB.child(GAME).addChildEventListener(listener)
                    STUDY -> memberDB.child(STUDY).addChildEventListener(listener)
                    READING -> memberDB.child(READING).addChildEventListener(listener)
                    TRAVEL -> memberDB.child(TRAVEL).addChildEventListener(listener)
                    ENTERTAINMENT -> memberDB.child(ENTERTAINMENT).addChildEventListener(listener)
                    PET -> memberDB.child(PET).addChildEventListener(listener)
                    FOOD -> memberDB.child(FOOD).addChildEventListener(listener)
                    BEAUTY -> memberDB.child(BEAUTY).addChildEventListener(listener)
                    ART -> memberDB.child(ART).addChildEventListener(listener)
                    DIY -> memberDB.child(DIY).addChildEventListener(listener)
                    COUNSELING -> memberDB.child(COUNSELING).addChildEventListener(listener)
                    RIDE -> memberDB.child(RIDE).addChildEventListener(listener)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        userDB.child(auth.currentUser!!.uid).child(DB_HOBBY2).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                memberList.clear()                                  /////////////////////////
                when (dataSnapshot.getValue(String::class.java).toString()) {
                    SPORTS -> memberDB.child(SPORTS).addChildEventListener(listener)
                    FASHION -> memberDB.child(FASHION).addChildEventListener(listener)
                    FUND -> memberDB.child(FUND).addChildEventListener(listener)
                    IT -> memberDB.child(IT).addChildEventListener(listener)
                    GAME -> memberDB.child(GAME).addChildEventListener(listener)
                    STUDY -> memberDB.child(STUDY).addChildEventListener(listener)
                    READING -> memberDB.child(READING).addChildEventListener(listener)
                    TRAVEL -> memberDB.child(TRAVEL).addChildEventListener(listener)
                    ENTERTAINMENT -> memberDB.child(ENTERTAINMENT).addChildEventListener(listener)
                    PET -> memberDB.child(PET).addChildEventListener(listener)
                    FOOD -> memberDB.child(FOOD).addChildEventListener(listener)
                    BEAUTY -> memberDB.child(BEAUTY).addChildEventListener(listener)
                    ART -> memberDB.child(ART).addChildEventListener(listener)
                    DIY -> memberDB.child(DIY).addChildEventListener(listener)
                    COUNSELING -> memberDB.child(COUNSELING).addChildEventListener(listener)
                    RIDE -> memberDB.child(RIDE).addChildEventListener(listener)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        userDB.child(auth.currentUser!!.uid).child(DB_HOBBY3).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                memberList.clear()                                  /////////////////////////
                when (dataSnapshot.getValue(String::class.java).toString()) {
                    SPORTS -> memberDB.child(SPORTS).addChildEventListener(listener)
                    FASHION -> memberDB.child(FASHION).addChildEventListener(listener)
                    FUND -> memberDB.child(FUND).addChildEventListener(listener)
                    IT -> memberDB.child(IT).addChildEventListener(listener)
                    GAME -> memberDB.child(GAME).addChildEventListener(listener)
                    STUDY -> memberDB.child(STUDY).addChildEventListener(listener)
                    READING -> memberDB.child(READING).addChildEventListener(listener)
                    TRAVEL -> memberDB.child(TRAVEL).addChildEventListener(listener)
                    ENTERTAINMENT -> memberDB.child(ENTERTAINMENT).addChildEventListener(listener)
                    PET -> memberDB.child(PET).addChildEventListener(listener)
                    FOOD -> memberDB.child(FOOD).addChildEventListener(listener)
                    BEAUTY -> memberDB.child(BEAUTY).addChildEventListener(listener)
                    ART -> memberDB.child(ART).addChildEventListener(listener)
                    DIY -> memberDB.child(DIY).addChildEventListener(listener)
                    COUNSELING -> memberDB.child(COUNSELING).addChildEventListener(listener)
                    RIDE -> memberDB.child(RIDE).addChildEventListener(listener)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        userDB.child(auth.currentUser!!.uid).child(DB_HOBBY4).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                memberList.clear()                                  /////////////////////////
                when (dataSnapshot.getValue(String::class.java).toString()) {
                    SPORTS -> memberDB.child(SPORTS).addChildEventListener(listener)
                    FASHION -> memberDB.child(FASHION).addChildEventListener(listener)
                    FUND -> memberDB.child(FUND).addChildEventListener(listener)
                    IT -> memberDB.child(IT).addChildEventListener(listener)
                    GAME -> memberDB.child(GAME).addChildEventListener(listener)
                    STUDY -> memberDB.child(STUDY).addChildEventListener(listener)
                    READING -> memberDB.child(READING).addChildEventListener(listener)
                    TRAVEL -> memberDB.child(TRAVEL).addChildEventListener(listener)
                    ENTERTAINMENT -> memberDB.child(ENTERTAINMENT).addChildEventListener(listener)
                    PET -> memberDB.child(PET).addChildEventListener(listener)
                    FOOD -> memberDB.child(FOOD).addChildEventListener(listener)
                    BEAUTY -> memberDB.child(BEAUTY).addChildEventListener(listener)
                    ART -> memberDB.child(ART).addChildEventListener(listener)
                    DIY -> memberDB.child(DIY).addChildEventListener(listener)
                    COUNSELING -> memberDB.child(COUNSELING).addChildEventListener(listener)
                    RIDE -> memberDB.child(RIDE).addChildEventListener(listener)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        userDB.child(auth.currentUser!!.uid).child(DB_HOBBY5).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                memberList.clear()                                  /////////////////////////
                when (dataSnapshot.getValue(String::class.java).toString()) {
                    SPORTS -> memberDB.child(SPORTS).addChildEventListener(listener)
                    FASHION -> memberDB.child(FASHION).addChildEventListener(listener)
                    FUND -> memberDB.child(FUND).addChildEventListener(listener)
                    IT -> memberDB.child(IT).addChildEventListener(listener)
                    GAME -> memberDB.child(GAME).addChildEventListener(listener)
                    STUDY -> memberDB.child(STUDY).addChildEventListener(listener)
                    READING -> memberDB.child(READING).addChildEventListener(listener)
                    TRAVEL -> memberDB.child(TRAVEL).addChildEventListener(listener)
                    ENTERTAINMENT -> memberDB.child(ENTERTAINMENT).addChildEventListener(listener)
                    PET -> memberDB.child(PET).addChildEventListener(listener)
                    FOOD -> memberDB.child(FOOD).addChildEventListener(listener)
                    BEAUTY -> memberDB.child(BEAUTY).addChildEventListener(listener)
                    ART -> memberDB.child(ART).addChildEventListener(listener)
                    DIY -> memberDB.child(DIY).addChildEventListener(listener)
                    COUNSELING -> memberDB.child(COUNSELING).addChildEventListener(listener)
                    RIDE -> memberDB.child(RIDE).addChildEventListener(listener)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })


        //memberDB.addChildEventListener(listener)
    }

    override fun onResume() {
        super.onResume()

        memberAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        memberDB.removeEventListener(listener)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // TODO Add your menu entries here
        menu.findItem(R.id.action_toggle).isVisible = false
        super.onCreateOptionsMenu(menu, inflater)
    }

}