package kr.ac.kku.cs.test_201821032.mypage

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kr.ac.kku.cs.test_201821032.DBKey.Companion.DB_USERS
import kr.ac.kku.cs.test_201821032.DBKey.Companion.DB_USER_PROFILE_IMAGE
import kr.ac.kku.cs.test_201821032.MainActivity
import kr.ac.kku.cs.test_201821032.R
import kr.ac.kku.cs.test_201821032.databinding.FragmentMypageBinding

class MyPageFragment : Fragment(R.layout.fragment_mypage) {

    private var binding: FragmentMypageBinding? = null
    private lateinit var userDB: DatabaseReference
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
                userDB = Firebase.database.reference.child(DB_USERS)

                val uri =
                    userDB.child(auth.currentUser!!.uid).child(DB_USER_PROFILE_IMAGE).toString()
                Toast.makeText(context, uri, Toast.LENGTH_SHORT).show()
                if (uri.isNotEmpty()) {
                    Glide.with(binding.userProfileImageView)
                        .load(uri)
                        .into(binding.userProfileImageView)
                }
                binding.emailEditText.setText(auth.currentUser!!.email)
                binding.emailEditText.isEnabled = false

                binding.signOutButton.text = "로그아웃"
                binding.signOutButton.isEnabled = true
            }
        }
    }

}