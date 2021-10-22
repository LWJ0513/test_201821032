package kr.ac.kku.cs.test_201821032.mypage

import android.annotation.SuppressLint
import android.app.ActionBar
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
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
import kr.ac.kku.cs.test_201821032.DBKey.Companion.DB_USER_NAME
import kr.ac.kku.cs.test_201821032.DBKey.Companion.DB_USER_PROFILE_IMAGE
import kr.ac.kku.cs.test_201821032.HomeActivity
import kr.ac.kku.cs.test_201821032.MainActivity
import kr.ac.kku.cs.test_201821032.R
import kr.ac.kku.cs.test_201821032.databinding.FragmentMypageBinding
import kr.ac.kku.cs.test_201821032.signIn.LoginActivity


class MyPageFragment : Fragment(R.layout.fragment_mypage) {

    private lateinit var userDB: DatabaseReference
    private var binding: FragmentMypageBinding? = null
    private var changeMode: Boolean = false
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    @SuppressLint("WrongConstant")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentMyPageBinding = FragmentMypageBinding.bind(view)
        binding = fragmentMyPageBinding

        userDB = Firebase.database.reference.child(DB_USERS)
        val userId = auth.currentUser?.uid.orEmpty()

        // (activity as HomeActivity?)!!.supportActionBar!!.hide()
        setHasOptionsMenu(true)
        val actionBar = (activity as HomeActivity?)!!.supportActionBar
        actionBar!!.setDisplayShowTitleEnabled(false)
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM)
        actionBar.setCustomView(R.layout.title_mypage)


        initChangeUserName()
        initWithdrawalButton(userId)
        initLogoutButton()
    }

    private fun initChangeUserName(){
        changeNameButton.setOnClickListener {
            if (!changeMode) {
                userNameEditText.isEnabled = true
                changeMode = true
            } else {
                val userName = userNameEditText.text.toString()
                userDB.child(auth.currentUser!!.uid).child(DB_USER_NAME).setValue(userName)
                userNameEditText.isEnabled = false
                changeMode = false
            }
        }
    }

    private fun initWithdrawalButton(uid: String) {
        withdrawalButton.setOnClickListener {
            startActivity(Intent(context, WithdrawalActivity::class.java))
        }
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

                emailEditText.setText(auth.currentUser!!.email)
                emailEditText.isEnabled = false

                signOutButton.text = "로그아웃"
                signOutButton.isEnabled = true

                userNameEditText.isEnabled = false
                userDB.child(auth.currentUser!!.uid).child(DB_USER_NAME)
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            userNameEditText.setText(dataSnapshot.getValue(String::class.java))
                        }
                        override fun onCancelled(error: DatabaseError) {}
                    })

                userDB.child(auth.currentUser!!.uid).child(DB_USER_PROFILE_IMAGE)
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {

                            val photoImageUrl = dataSnapshot.getValue(String::class.java)

                            Glide.with(this@MyPageFragment)
                                .load(photoImageUrl)
                                .centerCrop()
                                .into(userProfileImageView)
                        }

                        override fun onCancelled(databaseError: DatabaseError) {        // 에러문 출력
                            Toast.makeText(context, "사진을 불러오는 과정에서 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    })
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        menu.findItem(R.id.action_toggle).isVisible = false
        menu.findItem(R.id.action_search).isVisible = false
        menu.findItem(R.id.action_edit_hobby).isVisible = false

        super.onCreateOptionsMenu(menu, inflater)
    }
}