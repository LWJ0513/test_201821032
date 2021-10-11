package kr.ac.kku.cs.test_201821032.mypage

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_mypage.*
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

        userDB = Firebase.database.reference.child(DB_USERS)
        val userId = auth.currentUser?.uid.orEmpty()


        userDB.child(userId).child(DB_USER_PROFILE_IMAGE)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    var photoImageUrl = dataSnapshot.getValue(String::class.java)

                    Glide.with(this@MyPageFragment)
                        .load(photoImageUrl)
                        .centerCrop()
                        .into(userProfileImageView)

                    // Toast.makeText(context, photoImageUrl, Toast.LENGTH_LONG).show()
                    /*Glide.with(this@MyPageFragment)
                        .load("https://firebasestorage.googleapis.com/v0/b/kku-with-us.appspot.com/o/userProfile%2Fphoto%2F1633922878001.png?alt=media&token=d8180ec5-6e17-4aa1-bbb1-cd3490a6a53b")
                        .centerCrop()
                        .into(userProfileImageView)*/
                }
                override fun onCancelled(databaseError: DatabaseError) {        // 에러문 출력
                    Toast.makeText(context, "사진을 불러오는 과정에서 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            })

        initLogoutButton()


    }


    private fun initLogoutButton() {
        signOutButton.setOnClickListener {        // 로그아웃

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