package kr.ac.kku.cs.test_201821032.editRooms

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_edit_groups_detail_offline.*
import kr.ac.kku.cs.test_201821032.DBKey
import kr.ac.kku.cs.test_201821032.databinding.ActivityEditGroupsDetailOfflineBinding
import kr.ac.kku.cs.test_201821032.signIn.Hobbylist

class EditGroupsDetailOfflineActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditGroupsDetailOfflineBinding
    private lateinit var roomNumber: String
    private lateinit var hobby: String
    private lateinit var groupsDB: DatabaseReference
    private var selectedUri: Uri? = null
    private val storage: FirebaseStorage by lazy {
        Firebase.storage
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditGroupsDetailOfflineBinding.inflate(layoutInflater)
        setContentView(binding.root)

        groupsDB = Firebase.database.reference.child(DBKey.DB_GROUPS_LIST).child(DBKey.DB_OFFLINE)
        roomNumber = intent.getStringExtra("roomNumber").toString()
        hobby = intent.getStringExtra("hobby").toString()


        initViews()
        initBackButton()
        initChangeImage()
        initSaveButton()
    }

    private fun initViews() {
        groupsDB.child(hobby).child(roomNumber).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                editGroupsOfflineRoomTitleEditText.setText(
                    snapshot.child(DBKey.DB_TITLE).getValue(String::class.java)
                )
                when (hobby) {
                    Hobbylist.SPORTS -> editGroupsOfflineRoomTopicEditText.setText("??????/?????????")
                    Hobbylist.FASHION -> editGroupsOfflineRoomTopicEditText.setText("??????")
                    Hobbylist.FUND -> editGroupsOfflineRoomTopicEditText.setText("??????")
                    Hobbylist.IT -> editGroupsOfflineRoomTopicEditText.setText("IT")
                    Hobbylist.GAME -> editGroupsOfflineRoomTopicEditText.setText("??????")
                    Hobbylist.STUDY -> editGroupsOfflineRoomTopicEditText.setText("??????")
                    Hobbylist.READING -> editGroupsOfflineRoomTopicEditText.setText("??????")
                    Hobbylist.TRAVEL -> editGroupsOfflineRoomTopicEditText.setText("??????")
                    Hobbylist.ENTERTAINMENT -> editGroupsOfflineRoomTopicEditText.setText("??????????????????")
                    Hobbylist.PET -> editGroupsOfflineRoomTopicEditText.setText("??????")
                    Hobbylist.FOOD -> editGroupsOfflineRoomTopicEditText.setText("??????")
                    Hobbylist.BEAUTY -> editGroupsOfflineRoomTopicEditText.setText("??????")
                    Hobbylist.ART -> editGroupsOfflineRoomTopicEditText.setText("??????")
                    Hobbylist.DIY -> editGroupsOfflineRoomTopicEditText.setText("DIY")
                    Hobbylist.COUNSELING -> editGroupsOfflineRoomTopicEditText.setText("??????")
                    Hobbylist.RIDE -> editGroupsOfflineRoomTopicEditText.setText("??? ???")
                }
                editGroupsOfflinePlaceEditText.setText(snapshot.child("locationName").getValue(String::class.java))
                editGroupsOfflineRoomHashTagEditText.setText(
                    snapshot.child(DBKey.DB_HASHTAG).getValue(String::class.java)
                )
                editGroupsOfflineRoomDescriptionEditText.setText(
                    snapshot.child(DBKey.DB_DESCRIPTION).getValue(String::class.java)
                )
                if (!this@EditGroupsDetailOfflineActivity.isDestroyed) {
                    Glide.with(this@EditGroupsDetailOfflineActivity)
                        .load(snapshot.child(DBKey.DB_IMAGE_URL).getValue(String::class.java))
                        .centerCrop()
                        .into(binding.editGroupsOfflineRoomImageView2)
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun initBackButton() {
        backButton.setOnClickListener {
            finish()
        }
    }

    private fun initChangeImage() {
        blackImageView.setOnClickListener {
            when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(          // ????????? ??????
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) -> {
                    startContentProvider()
                }
                else -> {
                    requestPermissions(
                        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                        1010
                    )
                }
            }
        }
        cameraImageView.setOnClickListener {
            when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(          // ????????? ??????
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) -> {
                    startContentProvider()
                }
                else -> {
                    requestPermissions(
                        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                        1010
                    )
                }
            }
        }
    }

    private fun initSaveButton() {
        saveButton.setOnClickListener {
            showProgress()
            if (selectedUri != null) {      // ????????? ????????????
                val photoUri = selectedUri ?: return@setOnClickListener
                uploadPhoto(photoUri,
                    successHandler = {
                        groupsDB.child(hobby).child(roomNumber).child(DBKey.DB_IMAGE_URL)
                            .setValue(it)
                        groupsDB.child(hobby).child(roomNumber).child(DBKey.DB_HASHTAG)
                            .setValue(editGroupsOfflineRoomHashTagEditText.text.toString())
                        groupsDB.child(hobby).child(roomNumber).child(DBKey.DB_DESCRIPTION)
                            .setValue(editGroupsOfflineRoomDescriptionEditText.text.toString())
                        hideProgress()
                        finish()
                    },
                    errorHandler = {
                        Toast.makeText(this, "?????? ???????????? ??????????????????", Toast.LENGTH_SHORT).show()
                        hideProgress()
                    }
                )
            } else {
                groupsDB.child(hobby).child(roomNumber).child(DBKey.DB_HASHTAG)
                    .setValue(editGroupsOfflineRoomHashTagEditText.text.toString())
                groupsDB.child(hobby).child(roomNumber).child(DBKey.DB_DESCRIPTION)
                    .setValue(editGroupsOfflineRoomDescriptionEditText.text.toString())
                hideProgress()
                finish()
            }
        }
    }

    private fun uploadPhoto(uri: Uri, successHandler: (String) -> Unit, errorHandler: () -> Unit) {
        val fileName = "${System.currentTimeMillis()}.png"
        storage.reference.child("groups/offline/photo").child(fileName)
            .putFile(uri)
            .addOnCompleteListener {
                if (it.isSuccessful) {          // ???????????? ?????????
                    storage.reference.child("groups/offline/photo").child(fileName)
                        .downloadUrl
                        .addOnSuccessListener { uri ->
                            successHandler(uri.toString())
                        }.addOnFailureListener {
                            errorHandler()
                        }
                } else {
                    errorHandler()
                }
            }
    }

    private fun startContentProvider() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"     //
        startActivityForResult(intent, 2020)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            1010 ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startContentProvider()
                } else {
                    Toast.makeText(this, "????????? ?????????????????????.", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }
        when (requestCode) {
            2020 -> {
                val uri = data?.data
                if (uri != null) {
                    editGroupsOfflineRoomImageView2.setImageURI(uri)
                    selectedUri = uri
                } else {
                    Toast.makeText(this, "????????? ???????????? ???????????????.", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                Toast.makeText(this, "????????? ???????????? ???????????????.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showProgress() {
        editGroupsOfflineProgressBar.isVisible = true
    }

    private fun hideProgress() {
        editGroupsOfflineProgressBar.isVisible = false
    }
}