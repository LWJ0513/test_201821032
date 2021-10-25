package kr.ac.kku.cs.test_201821032.editRooms

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_edit_members.*
import kr.ac.kku.cs.test_201821032.DBKey
import kr.ac.kku.cs.test_201821032.databinding.ActivityEditMembersBinding

class EditMembersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditMembersBinding
    private lateinit var userDB: DatabaseReference
    private lateinit var editMemberAdapter: EditMembersAdapter
    private val editMemberList = mutableListOf<EditModel>()
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditMembersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userDB = Firebase.database.reference.child(DBKey.DB_USERS)

        editMemberAdapter = EditMembersAdapter(onItemClicked = {
            startActivity(Intent(this, EditMembersDetailActivity::class.java).apply {
                putExtra("roomNumber", it.roomNumber)
                putExtra("hobby", it.hobby)
            })
        })

        editMembersListRecyclerView.adapter = editMemberAdapter
        editMembersListRecyclerView.layoutManager = LinearLayoutManager(this)


        editMemberList.clear()
        userDB.child(auth.currentUser!!.uid).child(DBKey.DB_MADE).child(DBKey.DB_MEMBER)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        val model = it.getValue(EditModel::class.java)
                        model ?: return

                        editMemberList.add(model)
                    }
                    editMemberAdapter.submitList(editMemberList)
                    editMemberAdapter.notifyDataSetChanged()
                }
                override fun onCancelled(error: DatabaseError) {}
            })

        initBackButton()
    }

    private fun initBackButton() {
        editMemberBackButton.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()

        editMemberAdapter.notifyDataSetChanged()
    }
}