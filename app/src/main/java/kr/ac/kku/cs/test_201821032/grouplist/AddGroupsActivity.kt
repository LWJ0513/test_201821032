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
import kr.ac.kku.cs.test_201821032.DBKey
import kr.ac.kku.cs.test_201821032.R
import kr.ac.kku.cs.test_201821032.memberslist.MembersModel

class AddGroupsActivity : AppCompatActivity() {

    private var selectedUri: Uri? = null
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    private val storage: FirebaseStorage by lazy {
        Firebase.storage
    }

    private val groupsDB: DatabaseReference by lazy {
        Firebase.database.reference.child(DBKey.DB_GROUPS_LIST)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_group)

        findViewById<Button>(R.id.imageAddButton).setOnClickListener {
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

        findViewById<Button>(R.id.submitButton).setOnClickListener {
            val title = findViewById<EditText>(R.id.titleEditText).text.toString().orEmpty()
            val description = findViewById<EditText>(R.id.descriptionEditText).text.toString().orEmpty()
            val roomManager = auth.currentUser?.uid.orEmpty()

            showProgress()
            if (selectedUri != null) {            // 이미지가 있으면 업로드
                val photoUri = selectedUri ?: return@setOnClickListener
                uploadPhoto(photoUri,
                    successHandler = { uri ->     // 비동기
                        uploadMember(roomManager, title, description, uri)
                    },
                    errorHandler = {
                        Toast.makeText(this, "사진 업로드에 실패했습니다", Toast.LENGTH_SHORT).show()
                        hideProgress()
                    }
                )
            } else {    // 동기
                uploadMember(roomManager, title, description, "")
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

    private fun uploadMember(roomManager: String, title: String, description: String, imageUrl: String) {
        val model = MembersModel(roomManager, title, System.currentTimeMillis(), description, imageUrl)
        groupsDB.push().setValue(model)
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
        findViewById<ProgressBar>(R.id.progressBar).isVisible = true
    }

    private fun hideProgress() {
        findViewById<ProgressBar>(R.id.progressBar).isVisible = false
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
                    findViewById<ImageView>(R.id.photoImageView).setImageURI(uri)
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