package kr.ac.kku.cs.test_201821032.signIn

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.facebook.CallbackManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kr.ac.kku.cs.test_201821032.MainActivity
import kr.ac.kku.cs.test_201821032.R

class EmailLoginActivity : AppCompatActivity() {

    //이메일과 password를 입력받아 firebase auth에다가 전달
    private lateinit var auth: FirebaseAuth
    private lateinit var callbackManager: CallbackManager
    private lateinit var userDB: DatabaseReference
    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_login)

        userDB = Firebase.database.reference.child("Users")
        auth = Firebase.auth    // = FirebaseAuth.getInstance()랑 똑같음
        callbackManager = CallbackManager.Factory.create()


        initSignUpTextView()
        initLoginButton()
        initEmailAndPasswordEditText()
    }

    private fun initSignUpTextView() {
        val signUpTextView: TextView = findViewById<TextView>(R.id.signUpTextView)
        signUpTextView.setOnClickListener {
            val intent = Intent(this, EmailSignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initLoginButton() {
        val loginButton: Button = findViewById<Button>(R.id.loginButton)
        loginButton.setOnClickListener {
            email = getInputEmail()
            val password = getInputPassword()

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {        // 로그인이 성공적으로 이루어졌으면 이 정보가 firebase auth에 저장이 됨
                        handleSuccessLogin()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "로그인에 실패했습니다. 이메일 또는 비밀번호를 확인해주세요", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
        }
    }

    private fun initEmailAndPasswordEditText() {
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginButton: Button = findViewById<Button>(R.id.loginButton)

        emailEditText.addTextChangedListener {
            val enable = emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()
            loginButton.isEnabled = enable
        }

        passwordEditText.addTextChangedListener {
            val enable = emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()
            loginButton.isEnabled = enable
        }
    }

    private fun getInputEmail(): String {
        return findViewById<EditText>(R.id.emailEditText).text.toString()
    }

    private fun getInputPassword(): String {
        return findViewById<EditText>(R.id.passwordEditText).text.toString()
    }


    private fun handleSuccessLogin() {
        if (auth.currentUser == null) {
            Toast.makeText(this, "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        finish()
    }

    override fun onBackPressed() {
        finish()
    }

}