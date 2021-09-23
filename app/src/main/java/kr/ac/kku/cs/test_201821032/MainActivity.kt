package kr.ac.kku.cs.test_201821032

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()

        // 로그인 정보가 firebase currentUser에 저장됨
        if (auth.currentUser == null) { // 로그인이 안되어 있으면
            startActivity(Intent(this, LoginActivity::class.java))      // LoginActivity로 이동
        } else {
            startActivity(Intent(this,HomeActivity::class.java))
            finish()
        }
    }
}