package kr.ac.kku.cs.test_201821032

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kr.ac.kku.cs.test_201821032.DBKey.Companion.DB_EMAIL
import kr.ac.kku.cs.test_201821032.DBKey.Companion.DB_USERS
import kr.ac.kku.cs.test_201821032.DBKey.Companion.DB_USER_ID
import kr.ac.kku.cs.test_201821032.signup.HobbyActivity
import kr.ac.kku.cs.test_201821032.signup.SignUpActivity

class LoginActivity : AppCompatActivity() {

    //이메일과 password를 입력받아 firebase auth에다가 전달
    private lateinit var auth: FirebaseAuth
    private lateinit var callbackManager: CallbackManager
    private lateinit var userDB: DatabaseReference
    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        userDB = Firebase.database.reference.child("Users")
        auth = Firebase.auth    // = FirebaseAuth.getInstance()랑 똑같음
        callbackManager = CallbackManager.Factory.create()

        initSignUpButton()
        initLoginButton()
        initEmailAndPasswordEditText()
        initFacebookLoginButton()
    }

    private fun initSignUpButton() {
        val signUpButton = findViewById<Button>(R.id.signUpButton)
        signUpButton.setOnClickListener {
            val email = getInputEmail()
            val password = getInputPassword()

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid.orEmpty()
                        val currentUserDB =
                            Firebase.database.reference.child(DB_USERS).child(userId)
                        val user = mutableMapOf<String, Any>()
                        user[DB_USER_ID] = userId
                        user[DB_EMAIL] = email
                        currentUserDB.updateChildren(user)

                        Toast.makeText(
                            this,
                            "회원가입에 성공했습니다. 로그인 버튼을 눌러 로그인 해주세요",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(this, "이미 가입한 이메일 이거나 회원가입에 실패했습니다", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
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
        val signUpButton = findViewById<Button>(R.id.signUpButton)
        val loginButton: Button = findViewById<Button>(R.id.loginButton)

        emailEditText.addTextChangedListener {
            val enable = emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()
            loginButton.isEnabled = enable
            signUpButton.isEnabled = enable
        }

        passwordEditText.addTextChangedListener {
            val enable = emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()
            loginButton.isEnabled = enable
            signUpButton.isEnabled = enable
        }
    }

    private fun initFacebookLoginButton() {
        val facebookLoginButton = findViewById<LoginButton>(R.id.facebookLoginButton)

        facebookLoginButton.setPermissions("email", "public_profile")   //페북 계정에서 어떤 정보를 가져올건지
        facebookLoginButton.registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {

                override fun onSuccess(result: LoginResult) {  // 로그인 성공
                    val credential = FacebookAuthProvider.getCredential(result.accessToken.token)
                    auth.signInWithCredential(credential)
                        .addOnCompleteListener(this@LoginActivity) { task ->
                            if (task.isSuccessful) {
                                handleSuccessLogin()
                            } else {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "페이스북 로그인이 실패했습니다.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }

                override fun onCancel() {}      // 로그인 하다 취소


                override fun onError(error: FacebookException?) {       // 에러러
                    Toast.makeText(this@LoginActivity, "페이스북 로그인이 실패했습니다.", Toast.LENGTH_SHORT)
                        .show()
                }

            })
    }

    private fun getInputEmail(): String {
        return findViewById<EditText>(R.id.emailEditText).text.toString()
    }

    private fun getInputPassword(): String {
        return findViewById<EditText>(R.id.passwordEditText).text.toString()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun handleSuccessLogin() {
        if (auth.currentUser == null) {
            Toast.makeText(this, "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        finish()
    }


}