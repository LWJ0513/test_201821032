package kr.ac.kku.cs.test_201821032.signIn.signup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kr.ac.kku.cs.test_201821032.DBKey.Companion.DB_USER_NAME
import kr.ac.kku.cs.test_201821032.MainActivity
import kr.ac.kku.cs.test_201821032.R

class SignUpActivity : AppCompatActivity() {

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var userDB: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        userDB = Firebase.database.reference.child("Users")

        initSignUpButton()
        initBackButton()
        initUserNameEditText()
    }


    private fun initSignUpButton() {
        val signUpButton: Button = findViewById<Button>(R.id.signUpTextView)

        signUpButton.setOnClickListener {
            val nameEditText = findViewById<EditText>(R.id.userNameEditText)
            val userId = getCurrentUserID()
            val currentUserDB = userDB.child(userId)
            val user = mutableMapOf<String, Any>()
            user[DB_USER_NAME] = nameEditText.text.toString()
            currentUserDB.updateChildren(user)

            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun initBackButton() {
        val backButton: Button = findViewById<Button>(R.id.backButton)

        backButton.setOnClickListener {
            startActivity(Intent(this, HobbyActivity::class.java))
            finish()
        }
    }

    public override fun onBackPressed() {
        startActivity(Intent(this, HobbyActivity::class.java))
        finish()
    }

    private fun initUserNameEditText() {
        val nameEditText = findViewById<EditText>(R.id.userNameEditText)
        val checkUserNameButton = findViewById<Button>(R.id.checkUserNameButton)
        val signUpButton = findViewById<Button>(R.id.signUpTextView)

        nameEditText.addTextChangedListener {
            val enable = nameEditText.text.isNotEmpty()
            checkUserNameButton.isEnabled = enable
            signUpButton.isEnabled = enable
        }
    }

    private fun getCurrentUserID(): String {
        if (auth.currentUser == null) {
            Toast.makeText(this, "로그인이 되어있지 않습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
        return auth.currentUser?.uid.orEmpty()
    }
}