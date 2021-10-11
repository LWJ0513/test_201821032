package kr.ac.kku.cs.test_201821032.signIn.signup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kr.ac.kku.cs.test_201821032.MainActivity
import kr.ac.kku.cs.test_201821032.R

class HobbyActivity : AppCompatActivity() {

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hobby)

        if (auth.currentUser != null) {
            Toast.makeText(this, auth.currentUser!!.email.toString(), Toast.LENGTH_SHORT).show()
            initNextButton()
        } else {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }


    private fun initNextButton() {
        val nextButton: Button = findViewById<Button>(R.id.nextButton)

        nextButton.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }

    }


//    public override fun onBackPressed() {
//        startActivity(Intent(this, MainActivity::class.java))
//        finish()
//    }

}