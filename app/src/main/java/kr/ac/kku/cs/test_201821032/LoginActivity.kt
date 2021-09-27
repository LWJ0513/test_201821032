package kr.ac.kku.cs.test_201821032

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
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

        initEmailLoginButton()
        initFacebookLoginButton()
    }

    private fun initEmailLoginButton() {
        val emailLoginButton: Button = findViewById<Button>(R.id.emailLoginButton)

        emailLoginButton.setOnClickListener {
            startActivity(Intent(this, EmailLoginActivity::class.java))
            finish()
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

    private fun handleSuccessLogin() {
        if (auth.currentUser == null) {
            Toast.makeText(this, "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        finish()
    }


}