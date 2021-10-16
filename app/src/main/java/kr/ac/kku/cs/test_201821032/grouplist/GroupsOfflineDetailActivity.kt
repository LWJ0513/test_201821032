package kr.ac.kku.cs.test_201821032.grouplist

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_groups_offline_detail.*
import kr.ac.kku.cs.test_201821032.DBKey
import kr.ac.kku.cs.test_201821032.R
import kr.ac.kku.cs.test_201821032.chatdetail.ChatRoomActivity
import kr.ac.kku.cs.test_201821032.chatlist.ChatListItem
import kr.ac.kku.cs.test_201821032.databinding.ActivityGroupsOfflineDetailBinding

class GroupsOfflineDetailActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityGroupsOfflineDetailBinding
    private lateinit var userDB: DatabaseReference
    private lateinit var groupDB: DatabaseReference
    private lateinit var chatRoom: ChatListItem
    private lateinit var roomManager: String
    private lateinit var title: String
    private lateinit var description: String
    private lateinit var locationName: String
    private lateinit var address: String
    private var latitude: Float = 0F
    private var longitude: Float = 0F
    private lateinit var selectedHobby: String
    private var currentSelectMarker: Marker? = null

    private val auth: FirebaseAuth by lazy { Firebase.auth }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupsOfflineDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userDB = Firebase.database.reference.child(DBKey.DB_USERS)



        title = intent.getStringExtra("title").toString()
        roomManager = intent.getStringExtra("roomManager").toString()
        description = intent.getStringExtra("description").toString()
        locationName = intent.getStringExtra("locationName").toString()
        address = intent.getStringExtra("locationAddress").toString()
        latitude = intent.getFloatExtra("latitude", 0F)
        longitude = intent.getFloatExtra("longitude", 0F)
        selectedHobby = intent.getStringExtra("selectedHobby").toString()

        groupsOfflineRoomTitleTextView.text = title
        groupsOfflineRoomDescriptionTextView.text = description

        Glide.with(this)
            .load(intent.data)
            .centerCrop()
            .into(groupsRoomImageView)

        locationNameTextView.text = locationName
        setupGoogleMap()



        initBackButton()
        initSubmitOfflineGroupButton()
    }

    private fun initBackButton() {
        backButton.setOnClickListener {
            finish()
        }
    }

    private fun initSubmitOfflineGroupButton() {
        groupDB = Firebase.database.reference.child(DBKey.DB_GROUPS_LIST)

        binding.participateButton.setOnClickListener {
            chatRoom = ChatListItem(
                entryId = auth.currentUser!!.uid,
                managerId = roomManager,
                roomName = title,
                key = System.currentTimeMillis()
            )
            Toast.makeText(this, "$chatRoom", Toast.LENGTH_SHORT).show()

            userDB.child(auth.currentUser!!.uid)      // 사용자 유저디비에 채팅방 추가
                .child(DBKey.CHILD_CHAT)
                .child(DBKey.DB_GROUP)
                .push()
                .setValue(chatRoom)

            userDB.child(roomManager)      // 개설자 유저디비에 채팅방 추가
                .child(DBKey.CHILD_CHAT)
                .child(DBKey.DB_GROUP)
                .push()
                .setValue(chatRoom)

            // TODO 채팅화면으로 바로 이동
            val intent = Intent(this, ChatRoomActivity::class.java)
            intent.putExtra("chatKey", chatRoom.key)
            startActivity(Intent(intent))
            finish()

        }
    }

    private fun setupGoogleMap() {
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment

        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map

        currentSelectMarker = setupMarker()

        currentSelectMarker?.showInfoWindow()
    }

    private fun setupMarker(): Marker {
        val positionLatLng = LatLng(
            latitude.toDouble(),
            longitude.toDouble()
        )
        val markerOptions = MarkerOptions().apply {
            position(positionLatLng)
            title(locationName)
            snippet(address)
            icon(BitmapDescriptorFactory.defaultMarker(220F))
        }
        map.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                positionLatLng,
                17f       // 지도 확대 사이즈
            )
        )         // 지도 확대 사이즈 설정

        return map.addMarker(markerOptions)
    }
}