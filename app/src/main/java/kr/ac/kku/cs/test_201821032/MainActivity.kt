package kr.ac.kku.cs.test_201821032

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kr.ac.kku.cs.test_201821032.signIn.LoginActivity
import kr.ac.kku.cs.test_201821032.signIn.signup.HobbyActivity

class MainActivity : AppCompatActivity() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var userDB: DatabaseReference
    private var userName: DataSnapshot? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userDB = Firebase.database.reference.child("Users")


    }

    override fun onStart() {
        super.onStart()

        // 로그인 정보가 firebase currentUser 에 저장됨
        if (auth.currentUser == null) { // 로그인이 안되어 있으면
            startActivity(Intent(this, LoginActivity::class.java))      // LoginActivity 로 이동
            finish()
        }/* else if (userDB.child("user_name").get() == null){
            startActivity(Intent(this, HobbyActivity::class.java))
            finish()
        }*/ else {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }

    private fun initUserInformation() {
        val currentUserDB = userDB.child(getCurrentUserID())
        currentUserDB.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child("user_name").value == null) {
                    startActivity(Intent(this@MainActivity, HobbyActivity::class.java))
                    finish()
                }
                // todo 유저 정보 갱신
                //getUnSelectedUsers()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun getCurrentUserID(): String {
        if (auth.currentUser == null) {
            Toast.makeText(this, "로그인이 되어있지 않습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
        return auth.currentUser?.uid.orEmpty()
    }
}