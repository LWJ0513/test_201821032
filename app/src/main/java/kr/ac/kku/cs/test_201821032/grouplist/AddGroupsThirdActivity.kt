package kr.ac.kku.cs.test_201821032.grouplist

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_add_group.*
import kotlinx.android.synthetic.main.activity_add_groups_third.*
import kotlinx.coroutines.*
import kr.ac.kku.cs.test_201821032.DBKey
import kr.ac.kku.cs.test_201821032.HomeActivity
import kr.ac.kku.cs.test_201821032.R
import kr.ac.kku.cs.test_201821032.databinding.ActivityAddGroupsThirdBinding
import kr.ac.kku.cs.test_201821032.location.model.LocationLatLngEntity
import kr.ac.kku.cs.test_201821032.location.model.SearchResultEntity
import kr.ac.kku.cs.test_201821032.location.utillity.RetrofitUtil
import kotlin.coroutines.CoroutineContext
import kr.ac.kku.cs.test_201821032.grouplist.GroupsFragment

class AddGroupsThirdActivity : AppCompatActivity(), OnMapReadyCallback, CoroutineScope {

    private lateinit var job: Job
    private lateinit var binding: ActivityAddGroupsThirdBinding
    private lateinit var map: GoogleMap
    private lateinit var searchResult: SearchResultEntity
    private lateinit var locationManager: LocationManager
    private lateinit var myLocationListener: MyLocationListener

    private lateinit var title: String
    private lateinit var description: String
    private lateinit var roomManager: String
    private var photoUri: Uri? = null
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }
    private val storage: FirebaseStorage by lazy {
        Firebase.storage
    }
    private val groupsDB: DatabaseReference by lazy {
        Firebase.database.reference.child(DBKey.DB_GROUPS_LIST)
    }


    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private var currentSelectMarker: Marker? = null

    companion object {
        const val SEARCH_RESULT_EXTRA_KEY = "SEARCH_RESULT_EXTRA_KEY"
        const val CAMERA_ZOOM_LEVEL = 17f
        const val PERMISSION_REQUEST_CODE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddGroupsThirdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        job = Job()

        if (::searchResult.isInitialized.not()) {
            intent?.let {
                searchResult = it.getParcelableExtra<SearchResultEntity>(SEARCH_RESULT_EXTRA_KEY)
                    ?: throw Exception("데이터가 존재하지 않습니다.")
                setupGoogleMap()
            }
        }
        bindViews()
        initSubmitOfflineGroupButton()

        title = intent.getStringExtra("title").toString()
        description = intent.getStringExtra("description").toString()
        roomManager = intent.getStringExtra("roomManager").toString()
        photoUri = intent.data

    }

    private fun bindViews() = with(binding) {
        currentLocationButton.setOnClickListener {
            getMyLocation()
        }
    }

    private fun initSubmitOfflineGroupButton() {
        submitOfflineGroupButton.setOnClickListener {
            val roomManager = auth.currentUser?.uid.orEmpty()

            showProgress()
            if (photoUri != null) {            // 이미지가 있으면 업로드
                val photoUri = photoUri ?: return@setOnClickListener
                uploadPhoto(photoUri,
                    successHandler = { uri ->     // 비동기
                        uploadGroup(roomManager, title, description, uri)
                    }
                ) {
                    Toast.makeText(this, "사진 업로드에 실패했습니다", Toast.LENGTH_SHORT).show()
                    hideProgress()
                }
            } else {    // 동기
                uploadGroup(roomManager, title, description, "")
            }
            // TODO fdsdsfsdfsfdfsdsf
            val intent = Intent(application, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .apply {
                replace(R.id.fragmentContainer, fragment)
                commit()
            }
    }

    private fun uploadPhoto(uri: Uri, successHandler: (String) -> Unit, errorHandler: () -> Unit) {
        val fileName = "${System.currentTimeMillis()}.png"
        storage.reference.child("groups/offline/photo").child(fileName)
            .putFile(uri)
            .addOnCompleteListener {
                if (it.isSuccessful) {          // 업로드가 성공적
                    storage.reference.child("groups/offline/photo").child(fileName)
                        .downloadUrl
                        .addOnSuccessListener { uri ->
                            successHandler(uri.toString())
                        }.addOnFailureListener {
                            errorHandler()
                        }
                } else {
                    errorHandler()
                }
            }
    }


    private fun setupGoogleMap() {
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment

        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map
        currentSelectMarker = setupMarker(searchResult)

        currentSelectMarker?.showInfoWindow()       //마커 찍기
    }

    private fun setupMarker(searchResult: SearchResultEntity): Marker {
        val positionLatLng = LatLng(
            searchResult.locationLatLng.latitude.toDouble(),
            searchResult.locationLatLng.longitude.toDouble()
        )
        val markerOptions = MarkerOptions().apply {
            position(positionLatLng)
            title(searchResult.name)
            snippet(searchResult.fullAddress)
        }
        map.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                positionLatLng,
                CAMERA_ZOOM_LEVEL
            )
        )         // 지도 확대 사이즈 설정

        return map.addMarker(markerOptions)
    }

    private fun getMyLocation() {
        if (::locationManager.isInitialized.not()) {
            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }

        //권한 체크
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (isGpsEnabled) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ), PERMISSION_REQUEST_CODE
                )
            } else {
                setMyLocationListener()
            }
        }
    }

    @SuppressLint("MissingPermission")      // 권한 위에서 받았기 때문에 처리 안해도 됨
    private fun setMyLocationListener() {
        val minTime = 1500L
        val minDistance = 100f

        if (!::myLocationListener.isInitialized) {          // 초기화가 안되어있으면
            myLocationListener = MyLocationListener()
        }
        with(locationManager) {
            requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                minTime, minDistance, myLocationListener
            )
            requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                minTime, minDistance, myLocationListener
            )
        }
    }

    private fun onCurrentLocationChanged(locationLatLngEntity: LocationLatLngEntity) { //현재 내 위치
        map.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    locationLatLngEntity.latitude.toDouble(),
                    locationLatLngEntity.longitude.toDouble()
                ), CAMERA_ZOOM_LEVEL
            )
        )
        loadReverseGeoInformation(locationLatLngEntity)
        removeLocationListener()            // 내 현위치가 바뀌면 제거
    }

    // 아까 받은 위치 위도 경도 값을 통해 위치 정보 불러옴
    private fun loadReverseGeoInformation(locationLatLngEntity: LocationLatLngEntity) {
        launch(coroutineContext) {
            try {
                withContext(Dispatchers.IO) {
                    val response = RetrofitUtil.apiService.getReverseGeoCode(
                        lat = locationLatLngEntity.latitude.toDouble(),
                        lon = locationLatLngEntity.longitude.toDouble()
                    )
                    if (response.isSuccessful) {
                        val body = response.body()
                        withContext(Dispatchers.Main) {
                            Log.e("list", body.toString())  // 현재 내위치 찍기
                            body?.let {
                                currentSelectMarker = setupMarker(
                                    SearchResultEntity(
                                        fullAddress = it.addressInfo.fullAddress ?: "주소 정보 없음",
                                        name = "내 위치",
                                        locationLatLng = locationLatLngEntity
                                    )
                                )
                                currentSelectMarker?.showInfoWindow()       // 마커 지도에 뿌리기
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    this@AddGroupsThirdActivity,
                    "검색하는 과정에서 에러가 발생했습니다.", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun removeLocationListener() {
        if (::locationManager.isInitialized && ::myLocationListener.isInitialized) {
            locationManager.removeUpdates(myLocationListener)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {
                setMyLocationListener()     // 권한을 받으면
            } else {
                Toast.makeText(this, "권한을 받지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    inner class MyLocationListener : LocationListener {

        override fun onLocationChanged(location: Location) {
            val locationLatLngEntity = LocationLatLngEntity(
                location.latitude.toFloat(),
                location.longitude.toFloat()
            )
            onCurrentLocationChanged(locationLatLngEntity)
        }
    }


    private fun uploadGroup(
        roomManager: String,
        title: String,
        description: String,
        imageUrl: String
    ) {
        val model =
            GroupsModel(roomManager, title, System.currentTimeMillis(), description, imageUrl)
        groupsDB.push().setValue(model)
        hideProgress()
        finish()
    }

    private fun startContentProvider() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"     //
        startActivityForResult(intent, 2020)

    }

    private fun showProgress() {
        thirdProgressBar.isVisible = true
    }

    private fun hideProgress() {
        thirdProgressBar.isVisible = false
    }

}
