package kr.ac.kku.cs.test_201821032.memberslist

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_add_members.*
import kotlinx.android.synthetic.main.activity_add_members.view.*
import kotlinx.android.synthetic.main.activity_members_detail.*
import kr.ac.kku.cs.test_201821032.DBKey.Companion.DB_MEMBERS_LIST
import kr.ac.kku.cs.test_201821032.HomeActivity
import kr.ac.kku.cs.test_201821032.R
import kr.ac.kku.cs.test_201821032.databinding.ActivityAddMembersBinding
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

class AddMembersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddMembersBinding
    private var selectedUri: Uri? = null
    private var selectedHobby: String = ""
    private var hobbyDB: String = ""
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    private val storage: FirebaseStorage by lazy {
        Firebase.storage
    }

    private val membersDB: DatabaseReference by lazy {
        Firebase.database.reference.child(DB_MEMBERS_LIST)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMembersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val hobby = resources.getStringArray(R.array.hobby)
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, hobby)
        hobbyDropDownMenu.setAdapter(arrayAdapter)
        hobbyDropDownMenu.setOnItemClickListener { adapterView, view, i, l ->
            selectedHobby = hobby[i]
            when (selectedHobby) {
                "운동/스포츠" -> hobbyDB = SPORTS
                "패션" -> hobbyDB = FASHION
                "금융" -> hobbyDB = FUND
                "IT" -> hobbyDB = IT
                "게임" -> hobbyDB = GAME
                "공부" -> hobbyDB = STUDY
                "독서" -> hobbyDB = READING
                "여행" -> hobbyDB = TRAVEL
                "엔터테인먼트" -> hobbyDB = ENTERTAINMENT
                "애완" -> hobbyDB = PET
                "사교" -> hobbyDB = FOOD
                "미용" -> hobbyDB = BEAUTY
                "예술" -> hobbyDB = ART
                "DIY" -> hobbyDB = DIY
                "고민상담" -> hobbyDB = COUNSELING
                "탈 것" -> hobbyDB = RIDE
            }
        }


        initBackButton()
        initImageAddMembersButton()
        initSubmitMembersButton()
    }

    private fun initBackButton() {
        membersBackButton.setOnClickListener {
            finish()
        }
    }

    private fun initImageAddMembersButton() {
        imageAddMembersButton.setOnClickListener {
            when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(          // 저장소 권한
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) -> {
                    startContentProvider()
                }
                /*   shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                       showPermissionContextPopup()
                   }*/
                else -> {
                    requestPermissions(
                        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                        1010
                    )
                }
            }
        }
    }

    private fun initSubmitMembersButton() {
        submitMembersButton.setOnClickListener {
            val title = titleMembersEditText.text.toString().orEmpty()
            val description = descriptionMembersEditText.text.toString().orEmpty()
            val roomManager = auth.currentUser?.uid.orEmpty()
            val hashTag = hashtagMembersEditText.text.toString().orEmpty()


            if (selectedUri != null) {            // 이미지가 있으면 업로드
                showProgress()
                val photoUri = selectedUri ?: return@setOnClickListener
                uploadPhoto(photoUri,
                    successHandler = { uri ->     // 비동기
                        uploadMember(roomManager, title, description, hashTag,uri)
                    },
                    errorHandler = {
                        Toast.makeText(this, "사진 업로드에 실패했습니다", Toast.LENGTH_SHORT).show()
                        hideProgress()
                    }
                )
            } else {
                //uploadMember(roomManager, title, description, "")
                Toast.makeText(this, "이미지를 추가해주세요.", Toast.LENGTH_SHORT).show()
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

    private fun uploadMember(
        roomManager: String,
        title: String,
        description: String,
        hashTag: String,
        imageUrl: String
    ) {
        if (hobbyDB=="") {
            hideProgress()
            Toast.makeText(this, "주제를 선택해주세요", Toast.LENGTH_SHORT).show()
        } else {
            val roomNumber = membersDB.child(hobbyDB).push().key!!
            val model =
                MembersModel(roomManager, title, System.currentTimeMillis(), description, hashTag, imageUrl, roomNumber)
            membersDB.child(hobbyDB).child(roomNumber).setValue(model)
            hideProgress()
            finish()
            overridePendingTransition(0, 0)
            startActivity(Intent(this, HomeActivity::class.java))
            overridePendingTransition(0, 0)
        }
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
        membersProgressBar.isVisible = true
    }

    private fun hideProgress() {
        membersProgressBar.isVisible = false
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
                    findViewById<ImageView>(R.id.photoMembersImageView).setImageURI(uri)
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

    /* private fun showPermissionContextPopup() {
         AlertDialog.Builder(this)
             .setTitle("권한이 필요합니다.")
             .setMessage("사진을 가져오기 위해 필요합니다.")
             .setPositiveButton("동의", { _, _ ->
                 requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1010)
             })
             .create()
             .show()
     }*/
}