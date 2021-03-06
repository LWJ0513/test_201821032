package kr.ac.kku.cs.test_201821032.signIn

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import kr.ac.kku.cs.test_201821032.DBKey
import kr.ac.kku.cs.test_201821032.DBKey.Companion.DB_USERS
import kr.ac.kku.cs.test_201821032.MainActivity
import kr.ac.kku.cs.test_201821032.databinding.ActivityLoginBinding
import java.util.*

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var callbackManager: CallbackManager
    private lateinit var loginManager: LoginManager
    private lateinit var userDB: DatabaseReference


    private var googleSignInClient: GoogleSignInClient? = null
    private var RC_SIGN_IN = 9001


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userDB = Firebase.database.reference.child("Users")
        auth = Firebase.auth    // = FirebaseAuth.getInstance()??? ?????????
        callbackManager = CallbackManager.Factory.create()


        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("762777289795-7e223bhg95lsbrao3qcnrss9mg6afgl0.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)



        initEmailLoginButton()
        initGoogleLoginButton()
        //initFacebookLoginButton()
        initFacebookCustomLoginButton()
    }

    private fun initEmailLoginButton() {
        emailLoginButton.setOnClickListener {
            startActivity(Intent(this, EmailLoginActivity::class.java))

        }
    }

    private fun initGoogleLoginButton() {
        googleLoginButton.setOnClickListener {
            signIn()
        }
    }

 /*   private fun initFacebookLoginButton() {
        facebookLoginButton.setPermissions("email", "public_profile")   //?????? ???????????? ?????? ????????? ???????????????
        facebookLoginButton.registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {

                override fun onSuccess(result: LoginResult) {  // ????????? ??????
                    val credential = FacebookAuthProvider.getCredential(result.accessToken.token)
                    auth.signInWithCredential(credential)
                        .addOnCompleteListener(this@LoginActivity) { task ->
                            if (task.isSuccessful) {
                                handleSuccessLogin()
                            } else {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "???????????? ???????????? ??????????????????.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }

                override fun onCancel() {}      // ????????? ?????? ??????

                override fun onError(error: FacebookException?) {       // ?????????
                    Toast.makeText(this@LoginActivity, "???????????? ???????????? ??????????????????.", Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }
*/
    private fun initFacebookCustomLoginButton() {
        facebookLoginButton.setOnClickListener {
            loginManager = LoginManager.getInstance()
            loginManager.logInWithReadPermissions(this, listOf("public_profile", "email"))
            loginManager.registerCallback(
                callbackManager,
                object : FacebookCallback<LoginResult> {

                    override fun onSuccess(result: LoginResult) {  // ????????? ??????
                        val credential = FacebookAuthProvider.getCredential(result.accessToken.token)
                        auth.signInWithCredential(credential)
                            .addOnCompleteListener(this@LoginActivity) { task ->
                                if (task.isSuccessful) {
                                    handleSuccessLogin()
                                } else {
                                    Toast.makeText(
                                        this@LoginActivity,
                                        "???????????? ???????????? ??????????????????.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    }

                    override fun onCancel() {}      // ????????? ?????? ??????

                    override fun onError(error: FacebookException?) {       // ?????????
                        Toast.makeText(this@LoginActivity, "???????????? ???????????? ??????????????????.", Toast.LENGTH_SHORT)
                            .show()
                    }
                })
        }
    }

    private fun handleSuccessLogin() {
        if (auth.currentUser == null) {
            Toast.makeText(this, "???????????? ??????????????????.", Toast.LENGTH_SHORT).show()
            return
        }
        finish()
    }


    private fun signIn() {
        val signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            var result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)!!
            // ??????API??? ???????????? ??? ?????????

            if (result.isSuccess) {
                var accout = result.signInAccount
                firebaseAuthWithGoogle(accout)
                Toast.makeText(this, "????????? ??????", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "????????? ??????", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        var credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // ?????????, ???????????? ?????? ???
                    if (auth.currentUser != null) {
                        val userId = auth.currentUser?.uid.orEmpty()
                        val currentUserDB =
                            Firebase.database.reference.child(DBKey.DB_USERS).child(userId)
                        val user = mutableMapOf<String, Any>()
                        user[DBKey.DB_USER_ID] = userId
                        user[DBKey.DB_EMAIL] = auth.currentUser!!.email.toString()
                        currentUserDB.updateChildren(user)

                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                } else {
                    // ????????? ??? //todo
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onBackPressed() {

        ActivityCompat.finishAffinity(this)

    }
}