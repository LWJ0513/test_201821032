package kr.ac.kku.cs.test_201821032.mypage

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kr.ac.kku.cs.test_201821032.MainActivity
import kr.ac.kku.cs.test_201821032.R
import kr.ac.kku.cs.test_201821032.databinding.FragmentMypageBinding

class MyPageFragment : Fragment(R.layout.fragment_mypage) {

    private var binding: FragmentMypageBinding? = null
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentMypageBinding = FragmentMypageBinding.bind(view)
        binding = fragmentMypageBinding

        fragmentMypageBinding.signOutButton.setOnClickListener {        // 로그아웃

            auth.signOut()  // 이메일 계정 로그인일 경우
            LoginManager.getInstance().logOut()
            activity?.let {
                val intent = Intent(context, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }


    override fun onStart() {
        super.onStart()

        if (auth.currentUser == null) {      // 로그인 안되어있을 때
            activity?.let {
                val intent = Intent(context, MainActivity::class.java)
                startActivity(intent)
            }
        } else {
            binding?.let { binding ->
                binding.emailEditText.setText(auth.currentUser!!.email)
                binding.emailEditText.isEnabled = false

                binding.signOutButton.text = "로그아웃"
                binding.signOutButton.isEnabled = true
            }
        }
    }

}