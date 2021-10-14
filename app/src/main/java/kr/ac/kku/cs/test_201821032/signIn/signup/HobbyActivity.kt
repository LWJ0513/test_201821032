package kr.ac.kku.cs.test_201821032.signIn.signup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_hobby.*
import kr.ac.kku.cs.test_201821032.MainActivity
import kr.ac.kku.cs.test_201821032.R
import kr.ac.kku.cs.test_201821032.databinding.ActivityHobbyBinding
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


class HobbyActivity : AppCompatActivity() {

    private lateinit var userDB: DatabaseReference
    private lateinit var binding: ActivityHobbyBinding
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
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
    var sangdamClicked: Boolean = false
    var rideClicked: Boolean = false

    private var selected = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHobbyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userDB = Firebase.database.reference.child("Users")


        if (auth.currentUser != null) {
            Toast.makeText(this, auth.currentUser!!.email.toString(), Toast.LENGTH_SHORT).show()
            initHobby()
            initNextButton()
        } else {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }


    private fun initHobby() {
        sportsImageView.setOnClickListener {
            if (!sportsClicked) {
                if (selected < 5) {
                    sportsImageView.setImageResource(R.drawable.select)
                    sportsClicked = true
                    selected++
                } else {
                    Toast.makeText(this, "더 이상 선택할 수 없습니다", Toast.LENGTH_SHORT).show()
                }
            } else {
                sportsImageView.setImageResource(R.drawable.sports)
                sportsClicked = false
                selected--
            }
        }
        fashionImageView.setOnClickListener {
            if (!fashionClicked) {
                if (selected < 5) {
                    fashionImageView.setImageResource(R.drawable.select)
                    fashionClicked = true
                    selected++
                } else {
                    Toast.makeText(this, "더 이상 선택할 수 없습니다", Toast.LENGTH_SHORT).show()
                }
            } else {
                fashionImageView.setImageResource(R.drawable.fashion)
                fashionClicked = false
                selected--
            }
        }
        fundImageView.setOnClickListener {
            if (!fundClicked) {
                if (selected < 5) {
                    fundImageView.setImageResource(R.drawable.select)
                    fundClicked = true
                    selected++
                } else {
                    Toast.makeText(this, "더 이상 선택할 수 없습니다", Toast.LENGTH_SHORT).show()
                }
            } else {
                fundImageView.setImageResource(R.drawable.fund)
                fundClicked = false
                selected--
            }
        }

        itImageView.setOnClickListener {
            if (!itClicked) {
                if (selected < 5) {
                    itImageView.setImageResource(R.drawable.select)
                    itClicked = true
                    selected++
                } else {
                    Toast.makeText(this, "더 이상 선택할 수 없습니다", Toast.LENGTH_SHORT).show()
                }
            } else {
                itImageView.setImageResource(R.drawable.it)
                itClicked = false
                selected--
            }
        }
        gameImageView.setOnClickListener {
            if (!gameClicked) {
                if (selected < 5) {
                    gameImageView.setImageResource(R.drawable.select)
                    gameClicked = true
                    selected++
                } else {
                    Toast.makeText(this, "더 이상 선택할 수 없습니다", Toast.LENGTH_SHORT).show()
                }
            } else {
                gameImageView.setImageResource(R.drawable.game)
                gameClicked = false
                selected--
            }
        }
        studyImageView.setOnClickListener {
            if (!studyClicked) {
                if (selected < 5) {
                    studyImageView.setImageResource(R.drawable.select)
                    studyClicked = true
                    selected++
                } else {
                    Toast.makeText(this, "더 이상 선택할 수 없습니다", Toast.LENGTH_SHORT).show()
                }
            } else {
                studyImageView.setImageResource(R.drawable.study)
                studyClicked = false
                selected--
            }
        }

        readImageView.setOnClickListener {
            if (!readClicked) {
                if (selected < 5) {
                    readImageView.setImageResource(R.drawable.select)
                    readClicked = true
                    selected++
                } else {
                    Toast.makeText(this, "더 이상 선택할 수 없습니다", Toast.LENGTH_SHORT).show()
                }
            } else {
                readImageView.setImageResource(R.drawable.read)
                readClicked = false
                selected--
            }
        }
        travelImageView.setOnClickListener {
            if (!travelClicked) {
                if (selected < 5) {
                    travelImageView.setImageResource(R.drawable.select)
                    travelClicked = true
                    selected++
                } else {
                    Toast.makeText(this, "더 이상 선택할 수 없습니다", Toast.LENGTH_SHORT).show()
                }
            } else {
                travelImageView.setImageResource(R.drawable.travel)
                travelClicked = false
                selected--
            }
        }
        entertainmentImageView.setOnClickListener {
            if (!entertainmentClicked) {
                if (selected < 5) {
                    entertainmentImageView.setImageResource(R.drawable.select)
                    entertainmentClicked = true
                    selected++
                } else {
                    Toast.makeText(this, "더 이상 선택할 수 없습니다", Toast.LENGTH_SHORT).show()
                }
            } else {
                entertainmentImageView.setImageResource(R.drawable.enter)
                entertainmentClicked = false
                selected--
            }
        }

        companionImageView.setOnClickListener {
            if (!companionClicked) {
                if (selected < 5) {
                    companionImageView.setImageResource(R.drawable.select)
                    companionClicked = true
                    selected++
                } else {
                    Toast.makeText(this, "더 이상 선택할 수 없습니다", Toast.LENGTH_SHORT).show()
                }
            } else {
                companionImageView.setImageResource(R.drawable.companion)
                companionClicked = false
                selected--
            }
        }
        foodImageView.setOnClickListener {
            if (!foodClicked) {
                if (selected < 5) {
                    foodImageView.setImageResource(R.drawable.select)
                    foodClicked = true
                    selected++
                } else {
                    Toast.makeText(this, "더 이상 선택할 수 없습니다", Toast.LENGTH_SHORT).show()
                }
            } else {
                foodImageView.setImageResource(R.drawable.food)
                foodClicked = false
                selected--
            }
        }
        beautyImageView.setOnClickListener {
            if (!beautyClicked) {
                if (selected < 5) {
                    beautyImageView.setImageResource(R.drawable.select)
                    beautyClicked = true
                    selected++
                } else {
                    Toast.makeText(this, "더 이상 선택할 수 없습니다", Toast.LENGTH_SHORT).show()
                }
            } else {
                beautyImageView.setImageResource(R.drawable.beauty)
                beautyClicked = false
                selected--
            }
        }

        artImageView.setOnClickListener {
            if (!artClicked) {
                if (selected < 5) {
                    artImageView.setImageResource(R.drawable.select)
                    artClicked = true
                    selected++
                } else {
                    Toast.makeText(this, "더 이상 선택할 수 없습니다", Toast.LENGTH_SHORT).show()
                }
            } else {
                artImageView.setImageResource(R.drawable.art)
                artClicked = false
                selected--
            }
        }
        diyImageView.setOnClickListener {
            if (!diyClicked) {
                if (selected < 5) {
                    diyImageView.setImageResource(R.drawable.select)
                    diyClicked = true
                    selected++
                } else {
                    Toast.makeText(this, "더 이상 선택할 수 없습니다", Toast.LENGTH_SHORT).show()
                }
            } else {
                diyImageView.setImageResource(R.drawable.diy)
                diyClicked = false
                selected--
            }
        }
        sangdamImageView.setOnClickListener {
            if (!sangdamClicked) {
                if (selected < 5) {
                    sangdamImageView.setImageResource(R.drawable.select)
                    sangdamClicked = true
                    selected++
                } else {
                    Toast.makeText(this, "더 이상 선택할 수 없습니다", Toast.LENGTH_SHORT).show()
                }
            } else {
                sangdamImageView.setImageResource(R.drawable.sangdam)
                sangdamClicked = false
                selected--
            }
        }

        rideImageView.setOnClickListener {
            if (!rideClicked) {
                if (selected < 5) {
                    rideImageView.setImageResource(R.drawable.select)
                    rideClicked = true
                    selected++
                } else {
                    Toast.makeText(this, "더 이상 선택할 수 없습니다", Toast.LENGTH_SHORT).show()
                }
            } else {
                rideImageView.setImageResource(R.drawable.companion)
                rideClicked = false
                selected--
            }
        }
    }
/*
    private fun initArt() {

        var artClicked: Boolean = false
        artImageView.setOnClickListener {
            if (!artClicked) {
                sportsImageView.setImageResource(R.drawable.select)
                artClicked = true
                Toast.makeText(this, "선택되었습니다", Toast.LENGTH_SHORT).show()
            } else {
                sportsImageView.setImageResource(R.drawable.art)
                artClicked = false
                Toast.makeText(this, "취소되었습니다", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initBeauty() {
        var beautyClicked: Boolean = false
        beautyImageView.setOnClickListener {
            if (!beautyClicked) {
                sportsImageView.setImageResource(R.drawable.select)
                beautyClicked = true
                Toast.makeText(this, "선택되었습니다", Toast.LENGTH_SHORT).show()
            } else {
                sportsImageView.setImageResource(R.drawable.beauty)
                beautyClicked = false
                Toast.makeText(this, "취소되었습니다", Toast.LENGTH_SHORT).show()
            }
        }
    }*/

    private fun initNextButton() {
        nextButton.setOnClickListener {
            //todo db에 취미 저장

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
                if (sangdamClicked) {
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

                startActivity(Intent(this, SignUpActivity::class.java))
                finish()
            } else Toast.makeText(this, "취미를 한 개 이상 선택해주세요", Toast.LENGTH_SHORT).show()


            // todo 취미 선택 안했을 경우 에러메세지

        }

    }

}