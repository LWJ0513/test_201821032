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
import kotlinx.android.synthetic.main.activity_edit_groups.*
import kr.ac.kku.cs.test_201821032.DBKey
import kr.ac.kku.cs.test_201821032.databinding.ActivityEditGroupsBinding

class EditGroupsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditGroupsBinding
    private lateinit var userDB: DatabaseReference
    private lateinit var editGroupsAdapter: EditGroupsAdapter
    private val editGroupsList = mutableListOf<EditModel>()
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditGroupsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userDB = Firebase.database.reference.child(DBKey.DB_USERS)

        editGroupsAdapter = EditGroupsAdapter(onItemClicked = {
            if (it.onOff == DBKey.DB_ONLINE) {      //  온라인이면
                startActivity(Intent(this, EditGroupsDetailOnlineActivity::class.java).apply {
                    putExtra("roomNumber", it.roomNumber)
                    putExtra("hobby", it.hobby)
                })
            } else {        // 오프라인이면
                startActivity(Intent(this, EditGroupsDetailOfflineActivity::class.java).apply {
                    putExtra("roomNumber", it.roomNumber)
                    putExtra("hobby", it.hobby)
                })
            }
        })

        editGroupsListRecyclerView.adapter = editGroupsAdapter
        editGroupsListRecyclerView.layoutManager = LinearLayoutManager(this)


        editGroupsList.clear()
        userDB.child(auth.currentUser!!.uid).child(DBKey.DB_MADE).child(DBKey.DB_GROUP)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        val model = it.getValue(EditModel::class.java)
                        model ?: return

                        editGroupsList.add(model)
                    }
                    editGroupsAdapter.submitList(editGroupsList)
                    editGroupsAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {}
            })

        initBackButton()
    }

    private fun initBackButton() {
        editGroupBackButton.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()

        editGroupsAdapter.notifyDataSetChanged()
    }
}