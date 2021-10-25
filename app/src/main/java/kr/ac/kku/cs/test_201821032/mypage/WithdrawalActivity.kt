package kr.ac.kku.cs.test_201821032.mypage

import android.content.Intent
import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_withdrawal.*
import kr.ac.kku.cs.test_201821032.DBKey
import kr.ac.kku.cs.test_201821032.databinding.ActivityWithdrawalBinding
import kr.ac.kku.cs.test_201821032.signIn.LoginActivity

class WithdrawalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWithdrawalBinding
    private lateinit var userDB: DatabaseReference
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = ActivityWithdrawalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userDB = Firebase.database.reference.child(DBKey.DB_USERS)

        initYesButton()
        initNoButton()
    }

    private fun initYesButton() {
        yesButton.setOnClickListener {
            userDB.child(auth.currentUser!!.uid).removeValue()
            auth.currentUser!!.delete()

            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun initNoButton() {
        noButton.setOnClickListener {
            finish()
        }
    }
}