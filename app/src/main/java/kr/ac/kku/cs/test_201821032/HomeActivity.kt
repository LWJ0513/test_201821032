package kr.ac.kku.cs.test_201821032

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_add_members.*
import kotlinx.android.synthetic.main.activity_hobby.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_mypage.*
import kotlinx.android.synthetic.main.nav_header.*
import kr.ac.kku.cs.test_201821032.chatlist.ChatListFragment
import kr.ac.kku.cs.test_201821032.databinding.ActivityHomeBinding
import kr.ac.kku.cs.test_201821032.grouplist.GroupsFragment
import kr.ac.kku.cs.test_201821032.memberslist.MembersFragment
import kr.ac.kku.cs.test_201821032.mypage.MyPageFragment
import kr.ac.kku.cs.test_201821032.signIn.Hobbylist.Companion.ART
import kr.ac.kku.cs.test_201821032.signIn.Hobbylist.Companion.BEAUTY
import kr.ac.kku.cs.test_201821032.signIn.Hobbylist.Companion.COUNSELING
import kr.ac.kku.cs.test_201821032.signIn.Hobbylist.Companion.DIY
import kr.ac.kku.cs.test_201821032.signIn.Hobbylist.Companion.ENTERTAINMENT
import kr.ac.kku.cs.test_201821032.signIn.Hobbylist.Companion.FASHION
import kr.ac.kku.cs.test_201821032.signIn.Hobbylist.Companion.FOOD
import kr.ac.kku.cs.test_201821032.signIn.Hobbylist.Companion.FUND
import kr.ac.kku.cs.test_201821032.signIn.Hobbylist.Companion.GAME
import kr.ac.kku.cs.test_201821032.signIn.Hobbylist.Companion.IT
import kr.ac.kku.cs.test_201821032.signIn.Hobbylist.Companion.PET
import kr.ac.kku.cs.test_201821032.signIn.Hobbylist.Companion.READING
import kr.ac.kku.cs.test_201821032.signIn.Hobbylist.Companion.RIDE
import kr.ac.kku.cs.test_201821032.signIn.Hobbylist.Companion.SPORTS
import kr.ac.kku.cs.test_201821032.signIn.Hobbylist.Companion.STUDY
import kr.ac.kku.cs.test_201821032.signIn.Hobbylist.Companion.TRAVEL
import kr.ac.kku.cs.test_201821032.signIn.LoginActivity
import kr.ac.kku.cs.test_201821032.signIn.signup.HobbyActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var userDB: DatabaseReference
    private lateinit var database: DatabaseReference
    private lateinit var binding: ActivityHomeBinding
    private lateinit var drawerToggle: ActionBarDrawerToggle

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var selected = 0
    private var hobby1: String = ""
    private var hobby2: String = ""
    private var hobby3: String = ""
    private var hobby4: String = ""
    private var hobby5: String = ""

    var sportsClicked: Boolean = false
    var fashionClicked: Boolean = false
    var fundClicked: Boolean = false
    var itClicked: Boolean = false
    var gameClicked: Boolean = false
    var studyClicked: Boolean = false
    var readClicked: Boolean = false
    var travelClicked: Boolean = false
    var entertainmentClicked: Boolean = false
    var companionClicked: Boolean = false
    var foodClicked: Boolean = false
    var beautyClicked: Boolean = false
    var artClicked: Boolean = false
    var diyClicked: Boolean = false
    var sangDamClicked: Boolean = false
    var rideClicked: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (auth.currentUser == null) { // 로그인이 안되어 있으면
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }


        database = Firebase.database.reference
        userDB = Firebase.database.reference.child("Users")

        val membersFragment = MembersFragment()
        val groupFragment = GroupsFragment()
        val chatListFragment = ChatListFragment()
        val myPageFragment = MyPageFragment()

        replaceFragment(membersFragment)


        drawerToggle =
            object : ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close) {
                override fun onDrawerClosed(drawerView: View) {     // Drawer closed
                    super.onDrawerClosed(drawerView)
                    finish()
                    overridePendingTransition(0, 0)
                    startActivity(Intent(this@HomeActivity, HomeActivity::class.java))
                    overridePendingTransition(0, 0)
                }
                override fun onDrawerOpened(drawerView: View) {     //Drawer opened
                    super.onDrawerOpened(drawerView)
                }
            }

        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()
        drawerLayout.setDrawerListener(drawerToggle)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)





        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.membersList -> {
                    replaceFragment(membersFragment)
                    drawerToggle.isDrawerIndicatorEnabled = true
                    supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                    supportActionBar!!.setHomeButtonEnabled(true)
                }
                R.id.groupList -> {
                    replaceFragment(groupFragment)
                    drawerToggle.isDrawerIndicatorEnabled = true
                    supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                    supportActionBar!!.setHomeButtonEnabled(true)
                }
                R.id.chatList -> {
                    replaceFragment(chatListFragment)
                    //drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                    drawerToggle.isDrawerIndicatorEnabled = false
                    supportActionBar!!.setDisplayHomeAsUpEnabled(false)
                    supportActionBar!!.setHomeButtonEnabled(false)
                    //drawerToggle.syncState()
                }
                R.id.myPage -> {
                    replaceFragment(myPageFragment)
                }
            }
            true
        }




        initUserInformation()
        initChangeHobby()
        initSaveButton()
    }

    private fun initUserInformation() {
        val currentUserDB = userDB.child(getCurrentUserID())
        currentUserDB.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child("user_name").value == null) {
                    startActivity(Intent(this@HomeActivity, HobbyActivity::class.java))
                    finish()
                }
                // todo 유저 정보 갱신
                //getUnSelectedUsers()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun initChangeHobby() {
        val headerView = nav_view.getHeaderView(0)
        val sportsImageView: ImageView = headerView.findViewById(R.id.sportsImageView)!!
        val fashionImageView: ImageView = headerView.findViewById(R.id.fashionImageView)!!
        val fundImageView: ImageView = headerView.findViewById(R.id.fundImageView)!!
        val itImageView: ImageView = headerView.findViewById(R.id.itImageView)!!
        val gameImageView: ImageView = headerView.findViewById(R.id.gameImageView)!!
        val studyImageView: ImageView = headerView.findViewById(R.id.studyImageView)!!
        val readImageView: ImageView = headerView.findViewById(R.id.readImageView)!!
        val travelImageView: ImageView = headerView.findViewById(R.id.travelImageView)!!
        val entertainmentImageView: ImageView =
            headerView.findViewById(R.id.entertainmentImageView)!!
        val companionImageView: ImageView = headerView.findViewById(R.id.companionImageView)!!
        val foodImageView: ImageView = headerView.findViewById(R.id.foodImageView)!!
        val beautyImageView: ImageView = headerView.findViewById(R.id.beautyImageView)!!
        val artImageView: ImageView = headerView.findViewById(R.id.artImageView)!!
        val diyImageView: ImageView = headerView.findViewById(R.id.diyImageView)!!
        val sangDamImageView: ImageView = headerView.findViewById(R.id.sangdamImageView)!!
        val rideImageView: ImageView = headerView.findViewById(R.id.rideImageView)!!

        sportsImageView.setOnClickListener {
            if (!sportsClicked) {    // 선택된 상태면
                if (selected < 5) {
                    sportsImageView.setImageResource(R.drawable.img_selected_hobby_sports)
                    selected++
                    sportsClicked = true
                } else {
                    Toast.makeText(this, "더 이상 선택할 수 없습니다", Toast.LENGTH_SHORT).show()
                }
            } else {
                sportsImageView.setImageResource(R.drawable.img_hobby_sports)
                selected--
                sportsClicked = false
            }
        }
        fashionImageView.setOnClickListener {
            if (!fashionClicked) {    // 선택된 상태면
                if (selected < 5) {
                    fashionImageView.setImageResource(R.drawable.img_selected_hobby_fashion)
                    selected++
                    fashionClicked = true
                } else {
                    Toast.makeText(this, "더 이상 선택할 수 없습니다", Toast.LENGTH_SHORT).show()
                }
            } else {
                fashionImageView.setImageResource(R.drawable.img_hobby_fashion)
                selected--
                fashionClicked = false
            }
        }
        fundImageView.setOnClickListener {
            if (!fundClicked) {    // 선택된 상태면
                if (selected < 5) {
                    fundImageView.setImageResource(R.drawable.img_selected_hobby_fund)
                    selected++
                    fundClicked = true
                } else {
                    Toast.makeText(this, "더 이상 선택할 수 없습니다", Toast.LENGTH_SHORT).show()
                }
            } else {
                fundImageView.setImageResource(R.drawable.img_hobby_fund)
                selected--
                fundClicked = false
            }
        }
        itImageView.setOnClickListener {
            if (!itClicked) {    // 선택된 상태면
                if (selected < 5) {
                    itImageView.setImageResource(R.drawable.img_selected_hobby_it)
                    selected++
                    itClicked = true
                } else {
                    Toast.makeText(this, "더 이상 선택할 수 없습니다", Toast.LENGTH_SHORT).show()
                }
            } else {
                itImageView.setImageResource(R.drawable.img_hobby_it)
                selected--
                itClicked = false
            }
        }
        gameImageView.setOnClickListener {
            if (!gameClicked) {    // 선택된 상태면
                if (selected < 5) {
                    gameImageView.setImageResource(R.drawable.img_selected_hobby_game)
                    selected++
                    gameClicked = true
                } else {
                    Toast.makeText(this, "더 이상 선택할 수 없습니다", Toast.LENGTH_SHORT).show()
                }
            } else {
                gameImageView.setImageResource(R.drawable.img_hobby_game)
                selected--
                gameClicked = false
            }
        }
        studyImageView.setOnClickListener {
            if (!studyClicked) {    // 선택된 상태면
                if (selected < 5) {
                    studyImageView.setImageResource(R.drawable.img_selected_hobby_study)
                    selected++
                    studyClicked = true
                } else {
                    Toast.makeText(this, "더 이상 선택할 수 없습니다", Toast.LENGTH_SHORT).show()
                }
            } else {
                studyImageView.setImageResource(R.drawable.img_hobby_study)
                selected--
                studyClicked = false
            }
        }
        readImageView.setOnClickListener {
            if (!readClicked) {    // 선택된 상태면
                if (selected < 5) {
                    readImageView.setImageResource(R.drawable.img_selected_hobby_read)
                    selected++
                    readClicked = true
                } else {
                    Toast.makeText(this, "더 이상 선택할 수 없습니다", Toast.LENGTH_SHORT).show()
                }
            } else {
                readImageView.setImageResource(R.drawable.img_hobby_read)
                selected--
                readClicked = false
            }
        }
        travelImageView.setOnClickListener {
            if (!travelClicked) {    // 선택된 상태면
                if (selected < 5) {
                    travelImageView.setImageResource(R.drawable.img_selected_hobby_travel)
                    selected++
                    travelClicked = true
                } else {
                    Toast.makeText(this, "더 이상 선택할 수 없습니다", Toast.LENGTH_SHORT).show()
                }
            } else {
                travelImageView.setImageResource(R.drawable.img_hobby_travel)
                selected--
                travelClicked = false
            }
        }
        entertainmentImageView.setOnClickListener {
            if (!entertainmentClicked) {    // 선택된 상태면
                if (selected < 5) {
                    entertainmentImageView.setImageResource(R.drawable.img_selected_hobby_enter)
                    selected++
                    entertainmentClicked = true
                } else {
                    Toast.makeText(this, "더 이상 선택할 수 없습니다", Toast.LENGTH_SHORT).show()
                }
            } else {
                entertainmentImageView.setImageResource(R.drawable.img_hobby_enter)
                selected--
                entertainmentClicked = false
            }
        }
        companionImageView.setOnClickListener {
            if (!companionClicked) {    // 선택된 상태면
                if (selected < 5) {
                    companionImageView.setImageResource(R.drawable.img_selected_hobby_companion)
                    selected++
                    companionClicked = true
                } else {
                    Toast.makeText(this, "더 이상 선택할 수 없습니다", Toast.LENGTH_SHORT).show()
                }
            } else {
                companionImageView.setImageResource(R.drawable.img_hobby_companion)
                selected--
                companionClicked = false
            }
        }
        foodImageView.setOnClickListener {
            if (!foodClicked) {    // 선택된 상태면
                if (selected < 5) {
                    foodImageView.setImageResource(R.drawable.img_selected_hobby_food)
                    selected++
                    foodClicked = true
                } else {
                    Toast.makeText(this, "더 이상 선택할 수 없습니다", Toast.LENGTH_SHORT).show()
                }
            } else {
                foodImageView.setImageResource(R.drawable.img_hobby_food)
                selected--
                foodClicked = false
            }
        }
        beautyImageView.setOnClickListener {
            if (!beautyClicked) {    // 선택안된 상태면
                if (selected < 5) {
                    beautyImageView.setImageResource(R.drawable.img_selected_hobby_beauty)
                    selected++
                    beautyClicked = true
                } else {
                    Toast.makeText(this, "더 이상 선택할 수 없습니다", Toast.LENGTH_SHORT).show()
                }
            } else {
                beautyImageView.setImageResource(R.drawable.img_hobby_beauty)
                selected--
                beautyClicked = false
            }
        }
        artImageView.setOnClickListener {
            if (!artClicked) {    // 선택된 상태면
                if (selected < 5) {
                    artImageView.setImageResource(R.drawable.img_selected_hobby_art)
                    selected++
                    artClicked = true
                } else {
                    Toast.makeText(this, "더 이상 선택할 수 없습니다", Toast.LENGTH_SHORT).show()
                }
            } else {
                artImageView.setImageResource(R.drawable.img_hobby_art)
                selected--
                artClicked = false
            }
        }
        diyImageView.setOnClickListener {
            if (!diyClicked) {    // 선택된 상태면
                if (selected < 5) {
                    diyImageView.setImageResource(R.drawable.img_selected_hobby_diy)
                    selected++
                    diyClicked = true
                } else {
                    Toast.makeText(this, "더 이상 선택할 수 없습니다", Toast.LENGTH_SHORT).show()
                }
            } else {
                diyImageView.setImageResource(R.drawable.img_hobby_diy)
                selected--
                diyClicked = false
            }
        }
        sangDamImageView.setOnClickListener {
            if (!sangDamClicked) {    // 선택된 상태면
                if (selected < 5) {
                    sangDamImageView.setImageResource(R.drawable.img_selected_hobby_sangdam)
                    selected++
                    sangDamClicked = true
                } else {
                    Toast.makeText(this, "더 이상 선택할 수 없습니다", Toast.LENGTH_SHORT).show()
                }
            } else {
                sangDamImageView.setImageResource(R.drawable.img_hobby_sangdam)
                selected--
                sangDamClicked = false
            }
        }
        rideImageView.setOnClickListener {
            if (!rideClicked) {    // 선택된 상태면
                if (selected < 5) {
                    rideImageView.setImageResource(R.drawable.img_selected_hobby_ride)
                    selected++
                    rideClicked = true
                } else {
                    Toast.makeText(this, "더 이상 선택할 수 없습니다", Toast.LENGTH_SHORT).show()
                }
            } else {
                rideImageView.setImageResource(R.drawable.img_hobby_ride)
                selected--
                rideClicked = false
            }
        }
    }

    private fun initSaveButton() {
        val headerView = nav_view.getHeaderView(0)
        val saveButton = headerView.findViewById<Button>(R.id.saveButton)

        saveButton.setOnClickListener {
            hobby1 = ""
            hobby2 = ""
            hobby3 = ""
            hobby4 = ""
            hobby5 = ""

            if (selected > 0) {
                if (sportsClicked) {
                    when {
                        hobby1 == "" -> hobby1 = SPORTS
                        hobby2 == "" -> hobby2 = SPORTS
                        hobby3 == "" -> hobby3 = SPORTS
                        hobby4 == "" -> hobby4 = SPORTS
                        hobby5 == "" -> hobby5 = SPORTS
                    }
                }
                if (fashionClicked) {
                    when {
                        hobby1 == "" -> hobby1 = FASHION
                        hobby2 == "" -> hobby2 = FASHION
                        hobby3 == "" -> hobby3 = FASHION
                        hobby4 == "" -> hobby4 = FASHION
                        hobby5 == "" -> hobby5 = FASHION
                    }
                }
                if (fundClicked) {
                    when {
                        hobby1 == "" -> hobby1 = FUND
                        hobby2 == "" -> hobby2 = FUND
                        hobby3 == "" -> hobby3 = FUND
                        hobby4 == "" -> hobby4 = FUND
                        hobby5 == "" -> hobby5 = FUND
                    }
                }
                if (itClicked) {
                    when {
                        hobby1 == "" -> hobby1 = IT
                        hobby2 == "" -> hobby2 = IT
                        hobby3 == "" -> hobby3 = IT
                        hobby4 == "" -> hobby4 = IT
                        hobby5 == "" -> hobby5 = IT
                    }
                }
                if (gameClicked) {
                    when {
                        hobby1 == "" -> hobby1 = GAME
                        hobby2 == "" -> hobby2 = GAME
                        hobby3 == "" -> hobby3 = GAME
                        hobby4 == "" -> hobby4 = GAME
                        hobby5 == "" -> hobby5 = GAME
                    }
                }
                if (studyClicked) {
                    when {
                        hobby1 == "" -> hobby1 = STUDY
                        hobby2 == "" -> hobby2 = STUDY
                        hobby3 == "" -> hobby3 = STUDY
                        hobby4 == "" -> hobby4 = STUDY
                        hobby5 == "" -> hobby5 = STUDY
                    }
                }
                if (readClicked) {
                    when {
                        hobby1 == "" -> hobby1 = READING
                        hobby2 == "" -> hobby2 = READING
                        hobby3 == "" -> hobby3 = READING
                        hobby4 == "" -> hobby4 = READING
                        hobby5 == "" -> hobby5 = READING
                    }
                }
                if (travelClicked) {
                    when {
                        hobby1 == "" -> hobby1 = TRAVEL
                        hobby2 == "" -> hobby2 = TRAVEL
                        hobby3 == "" -> hobby3 = TRAVEL
                        hobby4 == "" -> hobby4 = TRAVEL
                        hobby5 == "" -> hobby5 = TRAVEL
                    }
                }
                if (entertainmentClicked) {
                    when {
                        hobby1 == "" -> hobby1 = ENTERTAINMENT
                        hobby2 == "" -> hobby2 = ENTERTAINMENT
                        hobby3 == "" -> hobby3 = ENTERTAINMENT
                        hobby4 == "" -> hobby4 = ENTERTAINMENT
                        hobby5 == "" -> hobby5 = ENTERTAINMENT
                    }
                }
                if (companionClicked) {
                    when {
                        hobby1 == "" -> hobby1 = PET
                        hobby2 == "" -> hobby2 = PET
                        hobby3 == "" -> hobby3 = PET
                        hobby4 == "" -> hobby4 = PET
                        hobby5 == "" -> hobby5 = PET
                    }
                }
                if (foodClicked) {
                    when {
                        hobby1 == "" -> hobby1 = FOOD
                        hobby2 == "" -> hobby2 = FOOD
                        hobby3 == "" -> hobby3 = FOOD
                        hobby4 == "" -> hobby4 = FOOD
                        hobby5 == "" -> hobby5 = FOOD
                    }
                }
                if (beautyClicked) {
                    when {
                        hobby1 == "" -> hobby1 = BEAUTY
                        hobby2 == "" -> hobby2 = BEAUTY
                        hobby3 == "" -> hobby3 = BEAUTY
                        hobby4 == "" -> hobby4 = BEAUTY
                        hobby5 == "" -> hobby5 = BEAUTY
                    }
                }
                if (artClicked) {
                    when {
                        hobby1 == "" -> hobby1 = ART
                        hobby2 == "" -> hobby2 = ART
                        hobby3 == "" -> hobby3 = ART
                        hobby4 == "" -> hobby4 = ART
                        hobby5 == "" -> hobby5 = ART
                    }
                }
                if (diyClicked) {
                    when {
                        hobby1 == "" -> hobby1 = DIY
                        hobby2 == "" -> hobby2 = DIY
                        hobby3 == "" -> hobby3 = DIY
                        hobby4 == "" -> hobby4 = DIY
                        hobby5 == "" -> hobby5 = DIY
                    }
                }
                if (sangDamClicked) {
                    when {
                        hobby1 == "" -> hobby1 = COUNSELING
                        hobby2 == "" -> hobby2 = COUNSELING
                        hobby3 == "" -> hobby3 = COUNSELING
                        hobby4 == "" -> hobby4 = COUNSELING
                        hobby5 == "" -> hobby5 = COUNSELING
                    }
                }
                if (rideClicked) {
                    when {
                        hobby1 == "" -> hobby1 = RIDE
                        hobby2 == "" -> hobby2 = RIDE
                        hobby3 == "" -> hobby3 = RIDE
                        hobby4 == "" -> hobby4 = RIDE
                        hobby5 == "" -> hobby5 = RIDE
                    }
                }

                userDB.child(auth.currentUser!!.uid).child("hobby1").setValue(hobby1)
                userDB.child(auth.currentUser!!.uid).child("hobby2").setValue(hobby2)
                userDB.child(auth.currentUser!!.uid).child("hobby3").setValue(hobby3)
                userDB.child(auth.currentUser!!.uid).child("hobby4").setValue(hobby4)
                userDB.child(auth.currentUser!!.uid).child("hobby5").setValue(hobby5)
            } else {
                Toast.makeText(this, "취미를 한 개 이상 선택해주세요", Toast.LENGTH_SHORT).show()
            }
            drawerLayout.closeDrawers()
        }
    }

    private fun getHobbyList() {
        selected = 0
        userDB.child(auth.currentUser!!.uid).child(DBKey.DB_HOBBY1)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val headerView = nav_view.getHeaderView(0)
                    val sportsImageView: ImageView = headerView.findViewById(R.id.sportsImageView)!!
                    val fashionImageView: ImageView =
                        headerView.findViewById(R.id.fashionImageView)!!
                    val fundImageView: ImageView = headerView.findViewById(R.id.fundImageView)!!
                    val itImageView: ImageView = headerView.findViewById(R.id.itImageView)!!
                    val gameImageView: ImageView = headerView.findViewById(R.id.gameImageView)!!
                    val studyImageView: ImageView = headerView.findViewById(R.id.studyImageView)!!
                    val readImageView: ImageView = headerView.findViewById(R.id.readImageView)!!
                    val travelImageView: ImageView = headerView.findViewById(R.id.travelImageView)!!
                    val entertainmentImageView: ImageView =
                        headerView.findViewById(R.id.entertainmentImageView)!!
                    val companionImageView: ImageView =
                        headerView.findViewById(R.id.companionImageView)!!
                    val foodImageView: ImageView = headerView.findViewById(R.id.foodImageView)!!
                    val beautyImageView: ImageView = headerView.findViewById(R.id.beautyImageView)!!
                    val artImageView: ImageView = headerView.findViewById(R.id.artImageView)!!
                    val diyImageView: ImageView = headerView.findViewById(R.id.diyImageView)!!
                    val sangDamImageView: ImageView =
                        headerView.findViewById(R.id.sangdamImageView)!!
                    val rideImageView: ImageView = headerView.findViewById(R.id.rideImageView)!!

                    when (dataSnapshot.getValue(String::class.java)) {
                        SPORTS -> {
                            sportsImageView.setImageResource(R.drawable.img_selected_hobby_sports)
                            selected++
                            sportsClicked = true
                        }
                        FASHION -> {
                            fashionImageView.setImageResource(R.drawable.img_selected_hobby_fashion)
                            selected++
                            fashionClicked = true
                        }
                        FUND -> {
                            fundImageView.setImageResource(R.drawable.img_selected_hobby_fund)
                            selected++
                            fundClicked = true
                        }
                        IT -> {
                            itImageView.setImageResource(R.drawable.img_selected_hobby_it)
                            selected++
                            itClicked = true
                        }
                        GAME -> {
                            gameImageView.setImageResource(R.drawable.img_selected_hobby_game)
                            selected++
                            gameClicked = true
                        }
                        STUDY -> {
                            studyImageView.setImageResource(R.drawable.img_selected_hobby_study)
                            selected++
                            studyClicked = true
                        }
                        READING -> {
                            readImageView.setImageResource(R.drawable.img_selected_hobby_read)
                            selected++
                            readClicked = true
                        }
                        TRAVEL -> {
                            travelImageView.setImageResource(R.drawable.img_selected_hobby_travel)
                            selected++
                            travelClicked = true
                        }
                        ENTERTAINMENT -> {
                            entertainmentImageView.setImageResource(R.drawable.img_selected_hobby_enter)
                            selected++
                            entertainmentClicked = true
                        }
                        PET -> {
                            companionImageView.setImageResource(R.drawable.img_selected_hobby_companion)
                            selected++
                            companionClicked = true
                        }
                        FOOD -> {
                            foodImageView.setImageResource(R.drawable.img_selected_hobby_food)
                            selected++
                            foodClicked = true
                        }
                        BEAUTY -> {
                            beautyImageView.setImageResource(R.drawable.img_selected_hobby_beauty)
                            selected++
                            beautyClicked = true
                        }
                        ART -> {
                            artImageView.setImageResource(R.drawable.img_selected_hobby_art)
                            selected++
                            artClicked = true
                        }
                        DIY -> {
                            diyImageView.setImageResource(R.drawable.img_selected_hobby_diy)
                            selected++
                            diyClicked = true
                        }
                        COUNSELING -> {
                            sangDamImageView.setImageResource(R.drawable.img_selected_hobby_sangdam)
                            selected++
                            sangDamClicked = true
                        }
                        RIDE -> {
                            rideImageView.setImageResource(R.drawable.img_selected_hobby_ride)
                            selected++
                            rideClicked = true
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {        // 에러문 출력
                }
            })
        userDB.child(auth.currentUser!!.uid).child(DBKey.DB_HOBBY2)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val headerView = nav_view.getHeaderView(0)
                    val sportsImageView: ImageView = headerView.findViewById(R.id.sportsImageView)!!
                    val fashionImageView: ImageView =
                        headerView.findViewById(R.id.fashionImageView)!!
                    val fundImageView: ImageView = headerView.findViewById(R.id.fundImageView)!!
                    val itImageView: ImageView = headerView.findViewById(R.id.itImageView)!!
                    val gameImageView: ImageView = headerView.findViewById(R.id.gameImageView)!!
                    val studyImageView: ImageView = headerView.findViewById(R.id.studyImageView)!!
                    val readImageView: ImageView = headerView.findViewById(R.id.readImageView)!!
                    val travelImageView: ImageView = headerView.findViewById(R.id.travelImageView)!!
                    val entertainmentImageView: ImageView =
                        headerView.findViewById(R.id.entertainmentImageView)!!
                    val companionImageView: ImageView =
                        headerView.findViewById(R.id.companionImageView)!!
                    val foodImageView: ImageView = headerView.findViewById(R.id.foodImageView)!!
                    val beautyImageView: ImageView = headerView.findViewById(R.id.beautyImageView)!!
                    val artImageView: ImageView = headerView.findViewById(R.id.artImageView)!!
                    val diyImageView: ImageView = headerView.findViewById(R.id.diyImageView)!!
                    val sangDamImageView: ImageView =
                        headerView.findViewById(R.id.sangdamImageView)!!
                    val rideImageView: ImageView = headerView.findViewById(R.id.rideImageView)!!

                    when (dataSnapshot.getValue(String::class.java)) {
                        SPORTS -> {
                            sportsImageView.setImageResource(R.drawable.img_selected_hobby_sports)
                            selected++
                            sportsClicked = true
                        }
                        FASHION -> {
                            fashionImageView.setImageResource(R.drawable.img_selected_hobby_fashion)
                            selected++
                            fashionClicked = true
                        }
                        FUND -> {
                            fundImageView.setImageResource(R.drawable.img_selected_hobby_fund)
                            selected++
                            fundClicked = true
                        }
                        IT -> {
                            itImageView.setImageResource(R.drawable.img_selected_hobby_it)
                            selected++
                            itClicked = true
                        }
                        GAME -> {
                            gameImageView.setImageResource(R.drawable.img_selected_hobby_game)
                            selected++
                            gameClicked = true
                        }
                        STUDY -> {
                            studyImageView.setImageResource(R.drawable.img_selected_hobby_study)
                            selected++
                            studyClicked = true
                        }
                        READING -> {
                            readImageView.setImageResource(R.drawable.img_selected_hobby_read)
                            selected++
                            readClicked = true
                        }
                        TRAVEL -> {
                            travelImageView.setImageResource(R.drawable.img_selected_hobby_travel)
                            selected++
                            travelClicked = true
                        }
                        ENTERTAINMENT -> {
                            entertainmentImageView.setImageResource(R.drawable.img_selected_hobby_enter)
                            selected++
                            entertainmentClicked = true
                        }
                        PET -> {
                            companionImageView.setImageResource(R.drawable.img_selected_hobby_companion)
                            selected++
                            companionClicked = true
                        }
                        FOOD -> {
                            foodImageView.setImageResource(R.drawable.img_selected_hobby_food)
                            selected++
                            foodClicked = true
                        }
                        BEAUTY -> {
                            beautyImageView.setImageResource(R.drawable.img_selected_hobby_beauty)
                            selected++
                            beautyClicked = true
                        }
                        ART -> {
                            artImageView.setImageResource(R.drawable.img_selected_hobby_art)
                            selected++
                            artClicked = true
                        }
                        DIY -> {
                            diyImageView.setImageResource(R.drawable.img_selected_hobby_diy)
                            selected++
                            diyClicked = true
                        }
                        COUNSELING -> {
                            sangDamImageView.setImageResource(R.drawable.img_selected_hobby_sangdam)
                            selected++
                            sangDamClicked = true
                        }
                        RIDE -> {
                            rideImageView.setImageResource(R.drawable.img_selected_hobby_ride)
                            selected++
                            rideClicked = true
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {        // 에러문 출력
                }
            })
        userDB.child(auth.currentUser!!.uid).child(DBKey.DB_HOBBY3)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val headerView = nav_view.getHeaderView(0)
                    val sportsImageView: ImageView = headerView.findViewById(R.id.sportsImageView)!!
                    val fashionImageView: ImageView =
                        headerView.findViewById(R.id.fashionImageView)!!
                    val fundImageView: ImageView = headerView.findViewById(R.id.fundImageView)!!
                    val itImageView: ImageView = headerView.findViewById(R.id.itImageView)!!
                    val gameImageView: ImageView = headerView.findViewById(R.id.gameImageView)!!
                    val studyImageView: ImageView = headerView.findViewById(R.id.studyImageView)!!
                    val readImageView: ImageView = headerView.findViewById(R.id.readImageView)!!
                    val travelImageView: ImageView = headerView.findViewById(R.id.travelImageView)!!
                    val entertainmentImageView: ImageView =
                        headerView.findViewById(R.id.entertainmentImageView)!!
                    val companionImageView: ImageView =
                        headerView.findViewById(R.id.companionImageView)!!
                    val foodImageView: ImageView = headerView.findViewById(R.id.foodImageView)!!
                    val beautyImageView: ImageView = headerView.findViewById(R.id.beautyImageView)!!
                    val artImageView: ImageView = headerView.findViewById(R.id.artImageView)!!
                    val diyImageView: ImageView = headerView.findViewById(R.id.diyImageView)!!
                    val sangDamImageView: ImageView =
                        headerView.findViewById(R.id.sangdamImageView)!!
                    val rideImageView: ImageView = headerView.findViewById(R.id.rideImageView)!!

                    when (dataSnapshot.getValue(String::class.java)) {
                        SPORTS -> {
                            sportsImageView.setImageResource(R.drawable.img_selected_hobby_sports)
                            selected++
                            sportsClicked = true
                        }
                        FASHION -> {
                            fashionImageView.setImageResource(R.drawable.img_selected_hobby_fashion)
                            selected++
                            fashionClicked = true
                        }
                        FUND -> {
                            fundImageView.setImageResource(R.drawable.img_selected_hobby_fund)
                            selected++
                            fundClicked = true
                        }
                        IT -> {
                            itImageView.setImageResource(R.drawable.img_selected_hobby_it)
                            selected++
                            itClicked = true
                        }
                        GAME -> {
                            gameImageView.setImageResource(R.drawable.img_selected_hobby_game)
                            selected++
                            gameClicked = true
                        }
                        STUDY -> {
                            studyImageView.setImageResource(R.drawable.img_selected_hobby_study)
                            selected++
                            studyClicked = true
                        }
                        READING -> {
                            readImageView.setImageResource(R.drawable.img_selected_hobby_read)
                            selected++
                            readClicked = true
                        }
                        TRAVEL -> {
                            travelImageView.setImageResource(R.drawable.img_selected_hobby_travel)
                            selected++
                            travelClicked = true
                        }
                        ENTERTAINMENT -> {
                            entertainmentImageView.setImageResource(R.drawable.img_selected_hobby_enter)
                            selected++
                            entertainmentClicked = true
                        }
                        PET -> {
                            companionImageView.setImageResource(R.drawable.img_selected_hobby_companion)
                            selected++
                            companionClicked = true
                        }
                        FOOD -> {
                            foodImageView.setImageResource(R.drawable.img_selected_hobby_food)
                            selected++
                            foodClicked = true
                        }
                        BEAUTY -> {
                            beautyImageView.setImageResource(R.drawable.img_selected_hobby_beauty)
                            selected++
                            beautyClicked = true
                        }
                        ART -> {
                            artImageView.setImageResource(R.drawable.img_selected_hobby_art)
                            selected++
                            artClicked = true
                        }
                        DIY -> {
                            diyImageView.setImageResource(R.drawable.img_selected_hobby_diy)
                            selected++
                            diyClicked = true
                        }
                        COUNSELING -> {
                            sangDamImageView.setImageResource(R.drawable.img_selected_hobby_sangdam)
                            selected++
                            sangDamClicked = true
                        }
                        RIDE -> {
                            rideImageView.setImageResource(R.drawable.img_selected_hobby_ride)
                            selected++
                            rideClicked = true
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {        // 에러문 출력
                }
            })
        userDB.child(auth.currentUser!!.uid).child(DBKey.DB_HOBBY4)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val headerView = nav_view.getHeaderView(0)
                    val sportsImageView: ImageView = headerView.findViewById(R.id.sportsImageView)!!
                    val fashionImageView: ImageView =
                        headerView.findViewById(R.id.fashionImageView)!!
                    val fundImageView: ImageView = headerView.findViewById(R.id.fundImageView)!!
                    val itImageView: ImageView = headerView.findViewById(R.id.itImageView)!!
                    val gameImageView: ImageView = headerView.findViewById(R.id.gameImageView)!!
                    val studyImageView: ImageView = headerView.findViewById(R.id.studyImageView)!!
                    val readImageView: ImageView = headerView.findViewById(R.id.readImageView)!!
                    val travelImageView: ImageView = headerView.findViewById(R.id.travelImageView)!!
                    val entertainmentImageView: ImageView =
                        headerView.findViewById(R.id.entertainmentImageView)!!
                    val companionImageView: ImageView =
                        headerView.findViewById(R.id.companionImageView)!!
                    val foodImageView: ImageView = headerView.findViewById(R.id.foodImageView)!!
                    val beautyImageView: ImageView = headerView.findViewById(R.id.beautyImageView)!!
                    val artImageView: ImageView = headerView.findViewById(R.id.artImageView)!!
                    val diyImageView: ImageView = headerView.findViewById(R.id.diyImageView)!!
                    val sangDamImageView: ImageView =
                        headerView.findViewById(R.id.sangdamImageView)!!
                    val rideImageView: ImageView = headerView.findViewById(R.id.rideImageView)!!

                    when (dataSnapshot.getValue(String::class.java)) {
                        SPORTS -> {
                            sportsImageView.setImageResource(R.drawable.img_selected_hobby_sports)
                            selected++
                            sportsClicked = true
                        }
                        FASHION -> {
                            fashionImageView.setImageResource(R.drawable.img_selected_hobby_fashion)
                            selected++
                            fashionClicked = true
                        }
                        FUND -> {
                            fundImageView.setImageResource(R.drawable.img_selected_hobby_fund)
                            selected++
                            fundClicked = true
                        }
                        IT -> {
                            itImageView.setImageResource(R.drawable.img_selected_hobby_it)
                            selected++
                            itClicked = true
                        }
                        GAME -> {
                            gameImageView.setImageResource(R.drawable.img_selected_hobby_game)
                            selected++
                            gameClicked = true
                        }
                        STUDY -> {
                            studyImageView.setImageResource(R.drawable.img_selected_hobby_study)
                            selected++
                            studyClicked = true
                        }
                        READING -> {
                            readImageView.setImageResource(R.drawable.img_selected_hobby_read)
                            selected++
                            readClicked = true
                        }
                        TRAVEL -> {
                            travelImageView.setImageResource(R.drawable.img_selected_hobby_travel)
                            selected++
                            travelClicked = true
                        }
                        ENTERTAINMENT -> {
                            entertainmentImageView.setImageResource(R.drawable.img_selected_hobby_enter)
                            selected++
                            entertainmentClicked = true
                        }
                        PET -> {
                            companionImageView.setImageResource(R.drawable.img_selected_hobby_companion)
                            selected++
                            companionClicked = true
                        }
                        FOOD -> {
                            foodImageView.setImageResource(R.drawable.img_selected_hobby_food)
                            selected++
                            foodClicked = true
                        }
                        BEAUTY -> {
                            beautyImageView.setImageResource(R.drawable.img_selected_hobby_beauty)
                            selected++
                            beautyClicked = true
                        }
                        ART -> {
                            artImageView.setImageResource(R.drawable.img_selected_hobby_art)
                            selected++
                            artClicked = true
                        }
                        DIY -> {
                            diyImageView.setImageResource(R.drawable.img_selected_hobby_diy)
                            selected++
                            diyClicked = true
                        }
                        COUNSELING -> {
                            sangDamImageView.setImageResource(R.drawable.img_selected_hobby_sangdam)
                            selected++
                            sangDamClicked = true
                        }
                        RIDE -> {
                            rideImageView.setImageResource(R.drawable.img_selected_hobby_ride)
                            selected++
                            rideClicked = true
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {        // 에러문 출력
                }
            })
        userDB.child(auth.currentUser!!.uid).child(DBKey.DB_HOBBY5)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val headerView = nav_view.getHeaderView(0)
                    val sportsImageView: ImageView = headerView.findViewById(R.id.sportsImageView)!!
                    val fashionImageView: ImageView = headerView.findViewById(R.id.fashionImageView)!!
                    val fundImageView: ImageView = headerView.findViewById(R.id.fundImageView)!!
                    val itImageView: ImageView = headerView.findViewById(R.id.itImageView)!!
                    val gameImageView: ImageView = headerView.findViewById(R.id.gameImageView)!!
                    val studyImageView: ImageView = headerView.findViewById(R.id.studyImageView)!!
                    val readImageView: ImageView = headerView.findViewById(R.id.readImageView)!!
                    val travelImageView: ImageView = headerView.findViewById(R.id.travelImageView)!!
                    val entertainmentImageView: ImageView = headerView.findViewById(R.id.entertainmentImageView)!!
                    val companionImageView: ImageView = headerView.findViewById(R.id.companionImageView)!!
                    val foodImageView: ImageView = headerView.findViewById(R.id.foodImageView)!!
                    val beautyImageView: ImageView = headerView.findViewById(R.id.beautyImageView)!!
                    val artImageView: ImageView = headerView.findViewById(R.id.artImageView)!!
                    val diyImageView: ImageView = headerView.findViewById(R.id.diyImageView)!!
                    val sangDamImageView: ImageView = headerView.findViewById(R.id.sangdamImageView)!!
                    val rideImageView: ImageView = headerView.findViewById(R.id.rideImageView)!!

                    when (dataSnapshot.getValue(String::class.java)) {
                        SPORTS -> {
                            sportsImageView.setImageResource(R.drawable.img_selected_hobby_sports)
                            selected++
                            sportsClicked = true
                        }
                        FASHION -> {
                            fashionImageView.setImageResource(R.drawable.img_selected_hobby_fashion)
                            selected++
                            fashionClicked = true
                        }
                        FUND -> {
                            fundImageView.setImageResource(R.drawable.img_selected_hobby_fund)
                            selected++
                            fundClicked = true
                        }
                        IT -> {
                            itImageView.setImageResource(R.drawable.img_selected_hobby_it)
                            selected++
                            itClicked = true
                        }
                        GAME -> {
                            gameImageView.setImageResource(R.drawable.img_selected_hobby_game)
                            selected++
                            gameClicked = true
                        }
                        STUDY -> {
                            studyImageView.setImageResource(R.drawable.img_selected_hobby_study)
                            selected++
                            studyClicked = true
                        }
                        READING -> {
                            readImageView.setImageResource(R.drawable.img_selected_hobby_read)
                            selected++
                            readClicked = true
                        }
                        TRAVEL -> {
                            travelImageView.setImageResource(R.drawable.img_selected_hobby_travel)
                            selected++
                            travelClicked = true
                        }
                        ENTERTAINMENT -> {
                            entertainmentImageView.setImageResource(R.drawable.img_selected_hobby_enter)
                            selected++
                            entertainmentClicked = true
                        }
                        PET -> {
                            companionImageView.setImageResource(R.drawable.img_selected_hobby_companion)
                            selected++
                            companionClicked = true
                        }
                        FOOD -> {
                            foodImageView.setImageResource(R.drawable.img_selected_hobby_food)
                            selected++
                            foodClicked = true
                        }
                        BEAUTY -> {
                            beautyImageView.setImageResource(R.drawable.img_selected_hobby_beauty)
                            selected++
                            beautyClicked = true
                        }
                        ART -> {
                            artImageView.setImageResource(R.drawable.img_selected_hobby_art)
                            selected++
                            artClicked = true
                        }
                        DIY -> {
                            diyImageView.setImageResource(R.drawable.img_selected_hobby_diy)
                            selected++
                            diyClicked = true
                        }
                        COUNSELING -> {
                            sangDamImageView.setImageResource(R.drawable.img_selected_hobby_sangdam)
                            selected++
                            sangDamClicked = true
                        }
                        RIDE -> {
                            rideImageView.setImageResource(R.drawable.img_selected_hobby_ride)
                            selected++
                            rideClicked = true
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {        // 에러문 출력
                }
            })
    }

    private fun getCurrentUserID(): String {
        if (auth.currentUser == null) {
            Toast.makeText(this, "로그인이 되어있지 않습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
        return auth.currentUser?.uid.orEmpty()
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .apply {
                replace(R.id.fragmentContainer, fragment)
                commit()
            }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (drawerToggle.onOptionsItemSelected(item)) {
            resetHobby()
            getHobbyList()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun resetHobby() {
        selected = 0

        sportsClicked = false
        fashionClicked = false
        fundClicked = false
        itClicked = false
        gameClicked = false
        studyClicked = false
        readClicked = false
        travelClicked = false
        entertainmentClicked = false
        companionClicked = false
        foodClicked = false
        beautyClicked = false
        artClicked = false
        diyClicked = false
        sangDamClicked = false
        rideClicked = false

        val headerView = nav_view.getHeaderView(0)
        val sportsImageView: ImageView = headerView.findViewById(R.id.sportsImageView)!!
        val fashionImageView: ImageView = headerView.findViewById(R.id.fashionImageView)!!
        val fundImageView: ImageView = headerView.findViewById(R.id.fundImageView)!!
        val itImageView: ImageView = headerView.findViewById(R.id.itImageView)!!
        val gameImageView: ImageView = headerView.findViewById(R.id.gameImageView)!!
        val studyImageView: ImageView = headerView.findViewById(R.id.studyImageView)!!
        val readImageView: ImageView = headerView.findViewById(R.id.readImageView)!!
        val travelImageView: ImageView = headerView.findViewById(R.id.travelImageView)!!
        val entertainmentImageView: ImageView = headerView.findViewById(R.id.entertainmentImageView)!!
        val companionImageView: ImageView = headerView.findViewById(R.id.companionImageView)!!
        val foodImageView: ImageView = headerView.findViewById(R.id.foodImageView)!!
        val beautyImageView: ImageView = headerView.findViewById(R.id.beautyImageView)!!
        val artImageView: ImageView = headerView.findViewById(R.id.artImageView)!!
        val diyImageView: ImageView = headerView.findViewById(R.id.diyImageView)!!
        val sangDamImageView: ImageView = headerView.findViewById(R.id.sangdamImageView)!!
        val rideImageView: ImageView = headerView.findViewById(R.id.rideImageView)!!

        sportsImageView.setImageResource(R.drawable.img_hobby_sports)
        fashionImageView.setImageResource(R.drawable.img_hobby_fashion)
        fundImageView.setImageResource(R.drawable.img_hobby_fund)
        itImageView.setImageResource(R.drawable.img_hobby_it)
        gameImageView.setImageResource(R.drawable.img_hobby_game)
        studyImageView.setImageResource(R.drawable.img_hobby_study)
        readImageView.setImageResource(R.drawable.img_hobby_read)
        travelImageView.setImageResource(R.drawable.img_hobby_travel)
        entertainmentImageView.setImageResource(R.drawable.img_hobby_enter)
        companionImageView.setImageResource(R.drawable.img_hobby_companion)
        foodImageView.setImageResource(R.drawable.img_hobby_food)
        beautyImageView.setImageResource(R.drawable.img_hobby_beauty)
        artImageView.setImageResource(R.drawable.img_hobby_art)
        diyImageView.setImageResource(R.drawable.img_hobby_diy)
        sangDamImageView.setImageResource(R.drawable.img_hobby_sangdam)
        rideImageView.setImageResource(R.drawable.img_hobby_ride)
    }

    override fun onBackPressed() {

        ActivityCompat.finishAffinity(this)

    }
}