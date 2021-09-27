package kr.ac.kku.cs.test_201821032.signup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import kr.ac.kku.cs.test_201821032.LoginActivity
import kr.ac.kku.cs.test_201821032.MainActivity
import kr.ac.kku.cs.test_201821032.R

class HobbyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hobby)

        initNextButton()
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