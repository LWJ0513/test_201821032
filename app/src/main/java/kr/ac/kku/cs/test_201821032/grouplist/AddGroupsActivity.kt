package kr.ac.kku.cs.test_201821032.grouplist

import android.app.Activity
import android.app.AlertDialog
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
import kotlinx.android.synthetic.main.activity_add_group.*
import kotlinx.android.synthetic.main.activity_groups_online_detail.*
import kr.ac.kku.cs.test_201821032.DBKey
import kr.ac.kku.cs.test_201821032.R
import kr.ac.kku.cs.test_201821032.databinding.ActivityAddGroupBinding
import kr.ac.kku.cs.test_201821032.editRooms.EditModel
import kr.ac.kku.cs.test_201821032.signIn.Hobbylist

class AddGroupsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddGroupBinding
    private lateinit var userDB: DatabaseReference
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

        userDB = Firebase.database.reference.child(DBKey.DB_USERS)

        val hobby = resources.getStringArray(R.array.hobby)
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, hobby)
        hobbyDropDownMenu.setAdapter(arrayAdapter)
        hobbyDropDownMenu.setOnItemClickListener { adapterView, view, i, l ->
            selectedHobby = hobby[i]
            when (selectedHobby) {
                "??????/?????????" -> hobbyDB = Hobbylist.SPORTS
                "??????" -> hobbyDB = Hobbylist.FASHION
                "??????" -> hobbyDB = Hobbylist.FUND
                "IT" -> hobbyDB = Hobbylist.IT
                "??????" -> hobbyDB = Hobbylist.GAME
                "??????" -> hobbyDB = Hobbylist.STUDY
                "??????" -> hobbyDB = Hobbylist.READING
                "??????" -> hobbyDB = Hobbylist.TRAVEL
                "??????????????????" -> hobbyDB = Hobbylist.ENTERTAINMENT
                "??????" -> hobbyDB = Hobbylist.PET
                "??????" -> hobbyDB = Hobbylist.FOOD
                "??????" -> hobbyDB = Hobbylist.BEAUTY
                "??????" -> hobbyDB = Hobbylist.ART
                "DIY" -> hobbyDB = Hobbylist.DIY
                "????????????" -> hobbyDB = Hobbylist.COUNSELING
                "??? ???" -> hobbyDB = Hobbylist.RIDE
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
        blackImageView.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(          // ????????? ??????
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
        cameraImageView.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(          // ????????? ??????
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
                meetTextView.text = "???????????? ??????"
                submitGroupsButton.text = "?????? ?????????"
            } else {
                meetTextView.text = "????????? ??????"
                submitGroupsButton.text = "????????????"
            }
        }
    }

    private fun initSubmitGroupsButton() {
        submitGroupsButton.setOnClickListener {
            val title = titleGroupsEditText.text.toString()
            val description = descriptionGroupsEditText.text.toString()
            val hashTag = hashtagGroupsEditText.text.toString()
            val roomManager = auth.currentUser?.uid.orEmpty()


            if (!meetToggleButton.isOn) {   // ???????????? ?????????
                if (selectedUri == null) {
                    Toast.makeText(this, "???????????? ??????????????????.", Toast.LENGTH_SHORT).show()
                } else if (hobbyDB == "") {
                    hideProgress()
                    Toast.makeText(this, "????????? ??????????????????", Toast.LENGTH_SHORT).show()
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
            } else {            // ????????? ?????????
                if (selectedUri != null) {            // ???????????? ?????????
                    if (hobbyDB == "") {
                        Toast.makeText(this, "????????? ??????????????????", Toast.LENGTH_SHORT).show()
                    } else {
                        showProgress()
                        val photoUri = selectedUri ?: return@setOnClickListener
                        uploadPhoto(photoUri,
                            successHandler = { uri ->     // ?????????
                                uploadGroup(roomManager, title, description, hashTag, uri)
                            },
                            errorHandler = {
                                hideProgress()
                                Toast.makeText(this, "?????? ???????????? ??????????????????", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                } else {
                    Toast.makeText(this, "???????????? ??????????????????.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun uploadPhoto(uri: Uri, successHandler: (String) -> Unit, errorHandler: () -> Unit) {
        val fileName = "${System.currentTimeMillis()}.png"
        storage.reference.child("groups/online/photo").child(fileName)
            .putFile(uri)
            .addOnCompleteListener {
                if (it.isSuccessful) {          // ???????????? ?????????
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
        val roomNumber = groupsDB.child(hobbyDB).push().key!!
        val qnAKey = "${System.currentTimeMillis()}" + roomManager
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
                roomNumber,
                hobbyDB,
                roomManager + System.currentTimeMillis(),
                qnAKey
            )
        groupsDB.child(hobbyDB).child(roomNumber).setValue(model)

        val madeRoom = EditModel(roomNumber, hobbyDB, DBKey.DB_ONLINE)
        userDB.child(auth.currentUser!!.uid).child(DBKey.DB_MADE).child(DBKey.DB_GROUP)
            .child(roomNumber)
            .setValue(madeRoom)
        val chatItem = QnAChatItem(
            message = "$title ??? QnA ??????????????????. ????????? ??????????????????",
            senderUid = roomManager,
            roomManager = roomManager
        )
        Firebase.database.reference.child(DBKey.DB_CHATS).child(qnAKey).push().setValue(chatItem)

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
                    Toast.makeText(this, "????????? ?????????????????????.", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(this, "????????? ???????????? ???????????????.", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                Toast.makeText(this, "????????? ???????????? ???????????????.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showPermissionContextPopup() {
        AlertDialog.Builder(this)
            .setTitle("????????? ???????????????.")
            .setMessage("????????? ???????????? ?????? ???????????????.")
            .setPositiveButton("??????", { _, _ ->
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1010)
            })
            .create()
            .show()
    }
}