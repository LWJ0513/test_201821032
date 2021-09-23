package kr.ac.kku.cs.test_201821032.grouplist

import android.content.Intent
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import kr.ac.kku.cs.test_201821032.LoginActivity
import kr.ac.kku.cs.test_201821032.R

class GroupListFragment: Fragment(R.layout.fragment_grouplist) {

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onResume() {
        super.onResume()

        if (auth.currentUser == null) { // 로그인이 안되어 있으면
            activity?.let {
                val intent = Intent(context, LoginActivity::class.java)// LoginActivity로 이동
                startActivity(intent)
            }
        }
    }
}