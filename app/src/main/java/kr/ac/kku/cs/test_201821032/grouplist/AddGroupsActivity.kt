package kr.ac.kku.cs.test_201821032.grouplist

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_add_group.*
import kotlinx.android.synthetic.main.activity_add_group.hobbyDropDownMenu
import kr.ac.kku.cs.test_201821032.DBKey
import kr.ac.kku.cs.test_201821032.R
import kr.ac.kku.cs.test_201821032.databinding.ActivityAddGroupBinding
import kr.ac.kku.cs.test_201821032.signIn.Hobbylist

class AddGroupsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddGroupBinding
    private var selectedUri: Uri? = null
    private var selectedHobby: String = ""
    private var hobbyDB: String = ""
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    private val storage: FirebaseStorage by lazy {
        Firebase.storage
    }

    private val groupsDB: DatabaseReference by lazy {
        Firebase.database.reference.child(DBKey.DB_ONLINE_GROUPS_LIST)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val hobby = resources.getStringArray(R.array.hobby)
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, hobby)
        hobbyDropDownMenu.setAdapter(arrayAdapter)
        hobbyDropDownMenu.setOnItemClickListener { adapterView, view, i, l ->
            selectedHobby = hobby[i]
            when (selectedHobby) {
                "운동/스포츠" -> hobbyDB = Hobbylist.SPORTS
                "패션" -> hobbyDB = Hobbylist.FASHION
                "금융" -> hobbyDB = Hobbylist.FUND
                "IT" -> hobbyDB = Hobbylist.IT
                "게임" -> hobbyDB = Hobbylist.GAME
                "공부" -> hobbyDB = Hobbylist.STUDY
                "독서" -> hobbyDB = Hobbylist.READING
                "여행" -> hobbyDB = Hobbylist.TRAVEL
                "엔터테인먼트" -> hobbyDB = Hobbylist.ENTERTAINMENT
                "반려" -> hobbyDB = Hobbylist.PET
                "사교" -> hobbyDB = Hobbylist.FOOD
                "미용" -> hobbyDB = Hobbylist.BEAUTY
                "예술" -> hobbyDB = Hobbylist.ART
                "DIY" -> hobbyDB = Hobbylist.DIY
                "고민상담" -> hobbyDB = Hobbylist.COUNSELING
                "탈 것" -> hobbyDB = Hobbylist.RIDE
            }
        }

        initBackButton()
        initImageAddGroupsButton()
        initMeetToggleButton()
        initSubmitGroupsButton()
    }

    private fun initBackButton() {
        firstBackButton.setOnClickListener {
            finish()
        }
    }

    private fun initImageAddGroupsButton() {
        imageAddGroupsButton.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(          // 저장소 권한
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    startContentProvider()
                }
                shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    showPermissionContextPopup()
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

    private fun initMeetToggleButton() {
        meetToggleButton.setOnToggledListener { toggleButton, isOn ->

            if (!isOn) {
                meetTextView.text = "오프라인 모임"
                submitGroupsButton.text = "장소 정하기"
            } else {
                meetTextView.text = "온라인 모임"
                submitGroupsButton.text = "등록하기"
            }
        }
    }

    private fun initSubmitGroupsButton() {
        submitGroupsButton.setOnClickListener {
            val title = titleGroupsEditText.text.toString()
            val description = descriptionGroupsEditText.text.toString()
            val hashTag = hashtagGroupsEditText.text.toString()
            val roomManager = auth.currentUser?.uid.orEmpty()


            if (!meetToggleButton.isOn) {   // 오프라인 선택시
                if (selectedUri == null) {
                    Toast.makeText(this, "이미지를 추가해주세요.", Toast.LENGTH_SHORT).show()
                } else if (hobbyDB == "") {
                    hideProgress()
                    Toast.makeText(this, "주제를 선택해주세요", Toast.LENGTH_SHORT).show()
                } else {
                    showProgress()
                    val intent = Intent(this, AddGroupSecondActivity::class.java)
                    intent.putExtra("title", title)
                    intent.putExtra("description", description)
                    intent.putExtra("hashTag", hashTag)
                    intent.putExtra("roomManager", roomManager)
                    intent.putExtra("selectedHobby", hobbyDB)
                    intent.data = selectedUri

                    hideProgress()
                    startActivity(intent)
                }

            } else {            // 온라인 선택시
                if (selectedUri != null) {            // 이미지가 있으면
                    if (hobbyDB == "") {
                        Toast.makeText(this, "주제를 선택해주세요", Toast.LENGTH_SHORT).show()
                    } else {
                        showProgress()
                        val photoUri = selectedUri ?: return@setOnClickListener
                        uploadPhoto(photoUri,
                            successHandler = { uri ->     // 비동기
                                uploadGroup(roomManager, title, description, hashTag, uri)
                            },
                            errorHandler = {
                                hideProgress()
                                Toast.makeText(this, "사진 업로드에 실패했습니다", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                } else {
                    Toast.makeText(this, "이미지를 추가해주세요.", Toast.LENGTH_SHORT).show()
                }
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

    private fun uploadGroup(
        roomManager: String,
        title: String,
        description: String,
        hashTag: String,
        imageUrl: String
    ) {
        val key = groupsDB.child(hobbyDB).push().key!!
        val model =
            GroupsModel(
                roomManager,
                title,
                System.currentTimeMillis(),
                description,
                hashTag,
                imageUrl,
                "",
                "",
                0F,
                0F,
                key,
                hobbyDB
            )
        groupsDB.child(hobbyDB).child(key).setValue(model)
        hideProgress()
        finish()
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

    private fun startContentProvider() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"     //
        startActivityForResult(intent, 2020)

    }

    private fun showProgress() {
        progressBar.isVisible = true
    }

    private fun hideProgress() {
        progressBar.isVisible = false
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
                    photoGroupsImageView.setImageURI(uri)
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

    private fun showPermissionContextPopup() {
        AlertDialog.Builder(this)
            .setTitle("권한이 필요합니다.")
            .setMessage("사진을 가져오기 위해 필요합니다.")
            .setPositiveButton("동의", { _, _ ->
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1010)
            })
            .create()
            .show()
    }
}