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
import kotlinx.android.synthetic.main.activity_edit_groups_detail_online.*
import kr.ac.kku.cs.test_201821032.DBKey
import kr.ac.kku.cs.test_201821032.databinding.ActivityEditGroupsDetailOnlineBinding
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

class EditGroupsDetailOnlineActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditGroupsDetailOnlineBinding
    private lateinit var roomNumber: String
    private lateinit var hobby: String
    private lateinit var groupsDB: DatabaseReference
    private var selectedUri: Uri? = null
    private val storage: FirebaseStorage by lazy {
        Firebase.storage
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditGroupsDetailOnlineBinding.inflate(layoutInflater)
        setContentView(binding.root)

        groupsDB = Firebase.database.reference.child(DBKey.DB_GROUPS_LIST).child(DBKey.DB_ONLINE)
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
                editGroupsOnlineRoomTitleEditText.setText(
                    snapshot.child(DBKey.DB_TITLE).getValue(String::class.java)
                )
                when (hobby) {
                    SPORTS -> editGroupsOnlineRoomTopicEditText.setText("운동/스포츠")
                    FASHION -> editGroupsOnlineRoomTopicEditText.setText("패션")
                    FUND -> editGroupsOnlineRoomTopicEditText.setText("금융")
                    IT -> editGroupsOnlineRoomTopicEditText.setText("IT")
                    GAME -> editGroupsOnlineRoomTopicEditText.setText("게임")
                    STUDY -> editGroupsOnlineRoomTopicEditText.setText("공부")
                    READING -> editGroupsOnlineRoomTopicEditText.setText("독서")
                    TRAVEL -> editGroupsOnlineRoomTopicEditText.setText("여행")
                    ENTERTAINMENT -> editGroupsOnlineRoomTopicEditText.setText("엔터테인먼트")
                    PET -> editGroupsOnlineRoomTopicEditText.setText("반려")
                    FOOD -> editGroupsOnlineRoomTopicEditText.setText("사교")
                    BEAUTY -> editGroupsOnlineRoomTopicEditText.setText("미용")
                    ART -> editGroupsOnlineRoomTopicEditText.setText("예술")
                    DIY -> editGroupsOnlineRoomTopicEditText.setText("DIY")
                    COUNSELING -> editGroupsOnlineRoomTopicEditText.setText("상담")
                    RIDE -> editGroupsOnlineRoomTopicEditText.setText("탈 것")
                }
                editGroupsOnlineRoomHashTagEditText.setText(
                    snapshot.child(DBKey.DB_HASHTAG).getValue(String::class.java)
                )
                editGroupsOnlineRoomDescriptionEditText.setText(
                    snapshot.child(DBKey.DB_DESCRIPTION).getValue(String::class.java)
                )
                if (!this@EditGroupsDetailOnlineActivity.isDestroyed) {
                    Glide.with(this@EditGroupsDetailOnlineActivity)
                        .load(snapshot.child(DBKey.DB_IMAGE_URL).getValue(String::class.java))
                        .centerCrop()
                        .into(binding.editGroupsOnlineRoomImageView2)
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
                ContextCompat.checkSelfPermission(          // 저장소 권한
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
                ContextCompat.checkSelfPermission(          // 저장소 권한
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
            if (selectedUri != null) {      // 사진을 바꿨으면
                val photoUri = selectedUri ?: return@setOnClickListener
                uploadPhoto(photoUri,
                    successHandler = {
                        groupsDB.child(hobby).child(roomNumber).child(DBKey.DB_IMAGE_URL)
                            .setValue(it)
                        groupsDB.child(hobby).child(roomNumber).child(DBKey.DB_HASHTAG)
                            .setValue(editGroupsOnlineRoomHashTagEditText.text.toString())
                        groupsDB.child(hobby).child(roomNumber).child(DBKey.DB_DESCRIPTION)
                            .setValue(editGroupsOnlineRoomDescriptionEditText.text.toString())
                        hideProgress()
                        finish()
                    },
                    errorHandler = {
                        Toast.makeText(this, "사진 업로드에 실패했습니다", Toast.LENGTH_SHORT).show()
                        hideProgress()
                    }
                )
            } else {
                groupsDB.child(hobby).child(roomNumber).child(DBKey.DB_HASHTAG)
                    .setValue(editGroupsOnlineRoomHashTagEditText.text.toString())
                groupsDB.child(hobby).child(roomNumber).child(DBKey.DB_DESCRIPTION)
                    .setValue(editGroupsOnlineRoomDescriptionEditText.text.toString())
                hideProgress()
                finish()
            }
        }
    }

    private fun uploadPhoto(uri: Uri, successHandler: (String) -> Unit, errorHandler: () -> Unit) {
        val fileName = "${System.currentTimeMillis()}.png"
        storage.reference.child("groups/online/photo").child(fileName)
            .putFile(uri)
            .addOnCompleteListener {
                if (it.isSuccessful) {          // 업로드가 성공적
                    storage.reference.child("groups/online/photo").child(fileName)
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
                    Toast.makeText(this, "권한을 거부하셨습니다.", Toast.LENGTH_SHORT).show()
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
                    editGroupsOnlineRoomImageView2.setImageURI(uri)
                    selectedUri = uri
                } else {
                    Toast.makeText(this, "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                Toast.makeText(this, "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showProgress() {
        editGroupsOnlineProgressBar.isVisible = true
    }

    private fun hideProgress() {
        editGroupsOnlineProgressBar.isVisible = false
    }
}