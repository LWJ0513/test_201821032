package kr.ac.kku.cs.test_201821032.editRooms

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
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
import kotlinx.android.synthetic.main.activity_add_members.*
import kotlinx.android.synthetic.main.activity_edit_groups_detail_online.*
import kotlinx.android.synthetic.main.activity_edit_members_detail.*
import kotlinx.android.synthetic.main.activity_edit_members_detail.backButton
import kotlinx.android.synthetic.main.activity_edit_members_detail.blackImageView
import kotlinx.android.synthetic.main.activity_edit_members_detail.cameraImageView
import kotlinx.android.synthetic.main.activity_edit_members_detail.saveButton
import kotlinx.android.synthetic.main.fragment_mypage.*
import kotlinx.android.synthetic.main.item_edit_members.*
import kr.ac.kku.cs.test_201821032.DBKey
import kr.ac.kku.cs.test_201821032.DBKey.Companion.DB_DESCRIPTION
import kr.ac.kku.cs.test_201821032.DBKey.Companion.DB_HASHTAG
import kr.ac.kku.cs.test_201821032.DBKey.Companion.DB_IMAGE_URL
import kr.ac.kku.cs.test_201821032.DBKey.Companion.DB_TITLE
import kr.ac.kku.cs.test_201821032.HomeActivity
import kr.ac.kku.cs.test_201821032.R
import kr.ac.kku.cs.test_201821032.databinding.ActivityEditMembersDetailBinding
import kr.ac.kku.cs.test_201821032.signIn.Hobbylist

class EditMembersDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditMembersDetailBinding
    private lateinit var roomNumber: String
    private lateinit var hobby: String
    private lateinit var membersDB: DatabaseReference
    private var selectedUri: Uri? = null
    private val storage: FirebaseStorage by lazy {
        Firebase.storage
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditMembersDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        membersDB = Firebase.database.reference.child(DBKey.DB_MEMBERS_LIST)
        roomNumber = intent.getStringExtra("roomNumber").toString()
        hobby = intent.getStringExtra("hobby").toString()


        initViews()
        initBackButton()
        initChangeImage()
        initSaveButton()
    }

    private fun initViews() {
        membersDB.child(hobby).child(roomNumber).addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                editMembersRoomTitleEditText.setText(snapshot.child(DB_TITLE).getValue(String::class.java))
                when (hobby) {
                    Hobbylist.SPORTS -> editMembersRoomTopicEditText.setText("운동/스포츠")
                    Hobbylist.FASHION -> editMembersRoomTopicEditText.setText("패션")
                    Hobbylist.FUND -> editMembersRoomTopicEditText.setText("금융")
                    Hobbylist.IT -> editMembersRoomTopicEditText.setText("IT")
                    Hobbylist.GAME -> editMembersRoomTopicEditText.setText("게임")
                    Hobbylist.STUDY -> editMembersRoomTopicEditText.setText("공부")
                    Hobbylist.READING -> editMembersRoomTopicEditText.setText("독서")
                    Hobbylist.TRAVEL -> editMembersRoomTopicEditText.setText("여행")
                    Hobbylist.ENTERTAINMENT -> editMembersRoomTopicEditText.setText("엔터테인먼트")
                    Hobbylist.PET -> editMembersRoomTopicEditText.setText("반려")
                    Hobbylist.FOOD -> editMembersRoomTopicEditText.setText("사교")
                    Hobbylist.BEAUTY -> editMembersRoomTopicEditText.setText("미용")
                    Hobbylist.ART -> editMembersRoomTopicEditText.setText("예술")
                    Hobbylist.DIY -> editMembersRoomTopicEditText.setText("DIY")
                    Hobbylist.COUNSELING -> editMembersRoomTopicEditText.setText("상담")
                    Hobbylist.RIDE -> editMembersRoomTopicEditText.setText("탈 것")
                }
                editMembersRoomHashTagEditText.setText(snapshot.child(DB_HASHTAG).getValue(String::class.java))
                editMembersRoomDescriptionEditText.setText(snapshot.child(DB_DESCRIPTION).getValue(String::class.java))
                if(!this@EditMembersDetailActivity.isDestroyed) {
                    Glide.with(this@EditMembersDetailActivity)
                        .load(snapshot.child(DB_IMAGE_URL).getValue(String::class.java))
                        .centerCrop()
                        .into(binding.editMembersRoomImageView2)
                }

            }
            override fun onCancelled(error: DatabaseError) { }
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
                        membersDB.child(hobby).child(roomNumber).child(DB_IMAGE_URL).setValue(it)
                        membersDB.child(hobby).child(roomNumber).child(DB_HASHTAG).setValue(editMembersRoomHashTagEditText.text.toString())
                        membersDB.child(hobby).child(roomNumber).child(DB_DESCRIPTION).setValue(editMembersRoomDescriptionEditText.text.toString())
                        hideProgress()
                        finish()
                    },
                    errorHandler = {
                        Toast.makeText(this, "사진 업로드에 실패했습니다", Toast.LENGTH_SHORT).show()
                        hideProgress()
                    }
                )
            } else  {
                membersDB.child(hobby).child(roomNumber).child(DB_HASHTAG).setValue(editMembersRoomHashTagEditText.text.toString())
                membersDB.child(hobby).child(roomNumber).child(DB_DESCRIPTION).setValue(editMembersRoomDescriptionEditText.text.toString())
                finish()
                hideProgress()
            }
        }
    }

    private fun uploadPhoto(uri: Uri, successHandler: (String) -> Unit, errorHandler: () -> Unit) {
        val fileName = "${System.currentTimeMillis()}.png"
        storage.reference.child("members/photo").child(fileName)
            .putFile(uri)
            .addOnCompleteListener {
                if (it.isSuccessful) {          // 업로드가 성공적
                    storage.reference.child("members/photo").child(fileName)
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
                    editMembersRoomImageView2.setImageURI(uri)
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
        editMembersProgressBar.isVisible = true
    }

    private fun hideProgress() {
        editMembersProgressBar.isVisible = false
    }

}