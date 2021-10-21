package kr.ac.kku.cs.test_201821032.signIn.signup

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.style.UnderlineSpan
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_add_members.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import kr.ac.kku.cs.test_201821032.DBKey.Companion.DB_HOBBY1
import kr.ac.kku.cs.test_201821032.DBKey.Companion.DB_HOBBY2
import kr.ac.kku.cs.test_201821032.DBKey.Companion.DB_HOBBY3
import kr.ac.kku.cs.test_201821032.DBKey.Companion.DB_HOBBY4
import kr.ac.kku.cs.test_201821032.DBKey.Companion.DB_HOBBY5
import kr.ac.kku.cs.test_201821032.MainActivity
import kr.ac.kku.cs.test_201821032.UserModel
import kr.ac.kku.cs.test_201821032.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var userDB: DatabaseReference
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var selectedUri: Uri? = null
    private lateinit var hobby1: String
    private lateinit var hobby2: String
    private lateinit var hobby3: String
    private lateinit var hobby4: String
    private lateinit var hobby5: String

    private val storage: FirebaseStorage by lazy {
        Firebase.storage
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userDB = Firebase.database.reference.child("Users")
        hobby1 = intent.getStringExtra("hobby1").toString()
        hobby2 = intent.getStringExtra("hobby2").toString()
        hobby3 = intent.getStringExtra("hobby3").toString()
        hobby4 = intent.getStringExtra("hobby4").toString()
        hobby5 = intent.getStringExtra("hobby5").toString()



        initUserProfileImageView()
        initSignUpButton()
        initBackButton()
        initUserNameEditText()
    }

    private fun initUserProfileImageView(){
        userProfileImageView.setOnClickListener {
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


    private fun initSignUpButton() {
        signUpTextView.setOnClickListener {
            val userId = getCurrentUserID()

            if (selectedUri != null) {            // 이미지가 있으면 업로드
                showProgress()
                val photoUri = selectedUri ?: return@setOnClickListener
                uploadPhoto(photoUri,
                    successHandler = { uri ->     // 비동기
                        val model = UserModel(userNameEditText.text.toString(), userId, auth.currentUser!!.email.toString(),uri)
                        userDB.child(userId).setValue(model)
                        userDB.child(auth.currentUser!!.uid).child(DB_HOBBY1).setValue(hobby1)
                        userDB.child(auth.currentUser!!.uid).child(DB_HOBBY2).setValue(hobby2)
                        userDB.child(auth.currentUser!!.uid).child(DB_HOBBY3).setValue(hobby3)
                        userDB.child(auth.currentUser!!.uid).child(DB_HOBBY4).setValue(hobby4)
                        userDB.child(auth.currentUser!!.uid).child(DB_HOBBY5).setValue(hobby5)

                        hideProgress()

                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    },
                    errorHandler = {
                        Toast.makeText(this, "사진 업로드에 실패했습니다", Toast.LENGTH_SHORT).show()
                    }
                )
            } else {
                Toast.makeText(this, "프로필 사진을 추가해주세요.", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun initBackButton() {
        backButton.setOnClickListener {
            startActivity(Intent(this, HobbyActivity::class.java))
            finish()
        }
    }

    private fun startContentProvider() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"     //
        startActivityForResult(intent, 2020)
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

    private fun uploadPhoto(uri: Uri, successHandler: (String) -> Unit, errorHandler: () -> Unit) {
        val fileName = "${System.currentTimeMillis()}.png"
        storage.reference.child("userProfile/photo").child(fileName)
            .putFile(uri)
            .addOnCompleteListener {
                if (it.isSuccessful) {          // 업로드가 성공적
                    storage.reference.child("userProfile/photo").child(fileName)
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

    override fun onBackPressed() {
        startActivity(Intent(this, HobbyActivity::class.java))
        finish()
    }

    private fun initUserNameEditText() {
        userNameEditText.addTextChangedListener {
            val enable = userNameEditText.text.isNotEmpty()
            signUpTextView.isEnabled = enable
        }
    }

    private fun getCurrentUserID(): String {
        if (auth.currentUser == null) {
            Toast.makeText(this, "로그인이 되어있지 않습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
        return auth.currentUser?.uid.orEmpty()
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
                    userProfileImageView.setImageURI(uri)               //  이미지 설정
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
        signUpProgressBar.isVisible = true
    }

    private fun hideProgress() {
        signUpProgressBar.isVisible = false
    }
}