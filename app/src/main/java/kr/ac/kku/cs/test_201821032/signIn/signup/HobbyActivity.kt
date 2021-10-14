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
                        hobby1 == "" -> hobby1 = "A"
                        hobby2 == "" -> hobby2 = "A"
                        hobby3 == "" -> hobby3 = "A"
                        hobby4 == "" -> hobby4 = "A"
                        hobby5 == "" -> hobby5 = "A"
                    }
                }
                if (fashionClicked) {
                    when {
                        hobby1 == "" -> hobby1 = "B"
                        hobby2 == "" -> hobby2 = "B"
                        hobby3 == "" -> hobby3 = "B"
                        hobby4 == "" -> hobby4 = "B"
                        hobby5 == "" -> hobby5 = "B"
                    }
                }
                if (fundClicked) {
                    when {
                        hobby1 == "" -> hobby1 = "C"
                        hobby2 == "" -> hobby2 = "C"
                        hobby3 == "" -> hobby3 = "C"
                        hobby4 == "" -> hobby4 = "C"
                        hobby5 == "" -> hobby5 = "C"
                    }
                }
                if (itClicked) {
                    when {
                        hobby1 == "" -> hobby1 = "D"
                        hobby2 == "" -> hobby2 = "D"
                        hobby3 == "" -> hobby3 = "D"
                        hobby4 == "" -> hobby4 = "D"
                        hobby5 == "" -> hobby5 = "D"
                    }
                }
                if (gameClicked) {
                    when {
                        hobby1 == "" -> hobby1 = "E"
                        hobby2 == "" -> hobby2 = "E"
                        hobby3 == "" -> hobby3 = "E"
                        hobby4 == "" -> hobby4 = "E"
                        hobby5 == "" -> hobby5 = "E"
                    }
                }
                if (studyClicked) {
                    when {
                        hobby1 == "" -> hobby1 = "F"
                        hobby2 == "" -> hobby2 = "F"
                        hobby3 == "" -> hobby3 = "F"
                        hobby4 == "" -> hobby4 = "F"
                        hobby5 == "" -> hobby5 = "F"
                    }
                }
                if (readClicked) {
                    when {
                        hobby1 == "" -> hobby1 = "G"
                        hobby2 == "" -> hobby2 = "G"
                        hobby3 == "" -> hobby3 = "G"
                        hobby4 == "" -> hobby4 = "G"
                        hobby5 == "" -> hobby5 = "G"
                    }
                }
                if (travelClicked) {
                    when {
                        hobby1 == "" -> hobby1 = "H"
                        hobby2 == "" -> hobby2 = "H"
                        hobby3 == "" -> hobby3 = "H"
                        hobby4 == "" -> hobby4 = "H"
                        hobby5 == "" -> hobby5 = "H"
                    }
                }
                if (entertainmentClicked) {
                    when {
                        hobby1 == "" -> hobby1 = "I"
                        hobby2 == "" -> hobby2 = "I"
                        hobby3 == "" -> hobby3 = "I"
                        hobby4 == "" -> hobby4 = "I"
                        hobby5 == "" -> hobby5 = "I"
                    }
                }
                if (companionClicked) {
                    when {
                        hobby1 == "" -> hobby1 = "J"
                        hobby2 == "" -> hobby2 = "J"
                        hobby3 == "" -> hobby3 = "J"
                        hobby4 == "" -> hobby4 = "J"
                        hobby5 == "" -> hobby5 = "J"
                    }
                }
                if (foodClicked) {
                    when {
                        hobby1 == "" -> hobby1 = "K"
                        hobby2 == "" -> hobby2 = "K"
                        hobby3 == "" -> hobby3 = "K"
                        hobby4 == "" -> hobby4 = "K"
                        hobby5 == "" -> hobby5 = "K"
                    }
                }
                if (beautyClicked) {
                    when {
                        hobby1 == "" -> hobby1 = "L"
                        hobby2 == "" -> hobby2 = "L"
                        hobby3 == "" -> hobby3 = "L"
                        hobby4 == "" -> hobby4 = "L"
                        hobby5 == "" -> hobby5 = "L"
                    }
                }
                if (artClicked) {
                    when {
                        hobby1 == "" -> hobby1 = "M"
                        hobby2 == "" -> hobby2 = "M"
                        hobby3 == "" -> hobby3 = "M"
                        hobby4 == "" -> hobby4 = "M"
                        hobby5 == "" -> hobby5 = "M"
                    }
                }
                if (diyClicked) {
                    when {
                        hobby1 == "" -> hobby1 = "N"
                        hobby2 == "" -> hobby2 = "N"
                        hobby3 == "" -> hobby3 = "N"
                        hobby4 == "" -> hobby4 = "N"
                        hobby5 == "" -> hobby5 = "N"
                    }
                }
                if (sangdamClicked) {
                    when {
                        hobby1 == "" -> hobby1 = "O"
                        hobby2 == "" -> hobby2 = "O"
                        hobby3 == "" -> hobby3 = "O"
                        hobby4 == "" -> hobby4 = "O"
                        hobby5 == "" -> hobby5 = "O"
                    }
                }
                if (rideClicked ) {
                    when {
                        hobby1 == "" -> hobby1 = "P"
                        hobby2 == "" -> hobby2 = "P"
                        hobby3 == "" -> hobby3 = "P"
                        hobby4 == "" -> hobby4 = "P"
                        hobby5 == "" -> hobby5 = "P"
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