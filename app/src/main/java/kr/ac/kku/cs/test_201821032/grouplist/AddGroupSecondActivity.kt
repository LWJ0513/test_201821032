package kr.ac.kku.cs.test_201821032.grouplist

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.activity_add_group_second.*
import kr.ac.kku.cs.test_201821032.databinding.ActivityAddGroupSecondBinding
import kr.ac.kku.cs.test_201821032.location.SearchRecyclerAdapter
import kr.ac.kku.cs.test_201821032.location.model.LocationLatLngEntity
import kr.ac.kku.cs.test_201821032.location.model.SearchResultEntity
import kr.ac.kku.cs.test_201821032.location.response.search.Poi
import kr.ac.kku.cs.test_201821032.location.response.search.Pois
import kr.ac.kku.cs.test_201821032.location.utillity.RetrofitUtil
import kotlinx.coroutines.*
import kr.ac.kku.cs.test_201821032.grouplist.AddGroupsThirdActivity.Companion.SEARCH_RESULT_EXTRA_KEY
import kotlin.coroutines.CoroutineContext

class AddGroupSecondActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var binding: ActivityAddGroupSecondBinding
    private lateinit var adapter: SearchRecyclerAdapter
    private lateinit var job: Job

    private lateinit var title: String
    private lateinit var description: String
    private lateinit var hashTag: String
    private lateinit var roomManager: String
    private lateinit var selectedHobby: String
    private var photoUri: Uri? = null

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityAddGroupSecondBinding.inflate(layoutInflater)
        setContentView(binding.root)

        job = Job()

        initAdapter()
        initViews()
        bindViews()
        initData()


        title = intent.getStringExtra("title").toString()
        description = intent.getStringExtra("description").toString()
        hashTag = intent.getStringExtra("hashTag").toString()
        roomManager = intent.getStringExtra("roomManager").toString()
        selectedHobby = intent.getStringExtra("selectedHobby").toString()
        photoUri = intent.data



        initBackButton()
    }
    private fun initBackButton() {
        secondBackButton.setOnClickListener {
            finish()
        }
    }
    private fun initAdapter() {
        adapter = SearchRecyclerAdapter()
    }

    private fun initViews() = with(binding) {    // with() ?????? scope function??? ???????????? ???????????? ?????? ?????? ??????
        emptyResultTextView.isVisible = false
        recyclerView.adapter = adapter
    }

    private fun bindViews() =
        with(binding) {       // ?????? ????????? ???????????? input??? ?????? ???????????? ???????????? api??? ???????????? ????????????
            searchButton.setOnClickListener {
                searchKeyword(searchBarInputView.text.toString())
            }
        }

    private fun initData() {
        adapter.notifyDataSetChanged()
    }

    private fun setData(pois: Pois) {
        val dataList = pois.poi.map {
            SearchResultEntity(
                locationName = it.name ?: "????????? ??????",
                fullAddress = makeMainAddress(it),
                locationLatLng = LocationLatLngEntity(
                    it.noorLat,
                    it.noorLon
                )
            )
        }

        adapter.setSearchResultList(dataList) {

            startActivity(Intent(this, AddGroupsThirdActivity::class.java).apply {
                putExtra(SEARCH_RESULT_EXTRA_KEY, it)
                putExtra("roomManager", roomManager)
                putExtra("title", title)
                putExtra("description", description)
                putExtra("hashTag", hashTag)
                data = photoUri
                putExtra("locationName", it.locationName)
                putExtra("address", it.fullAddress)
                putExtra("latitude", it.locationLatLng.latitude)
                putExtra("longitude", it.locationLatLng.longitude)
                putExtra("selectedHobby", selectedHobby)
            })
        }
    }

    // ??????????????? IO???????????? ???????????? ??????????????? ???????????? ???????????? ?????? ?????????????????? ?????????
    private fun searchKeyword(keywordString: String) {
        launch(coroutineContext) {
            try {
                withContext(Dispatchers.IO) {
                    val response = RetrofitUtil.apiService.getSearchLocation(
                        keyword = keywordString
                    )
                    if (response.isSuccessful) {
                        val body = response.body()
                        withContext(Dispatchers.Main) {
                            Log.e("response", body.toString())
                            body?.let { searchResponse ->
                                setData(searchResponse.searchPoiInfo.pois)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    this@AddGroupSecondActivity,
                    "???????????? ???????????? ????????? ??????????????????. : ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    private fun makeMainAddress(poi: Poi): String =
        if (poi.secondNo?.trim().isNullOrEmpty()) {
            (poi.upperAddrName?.trim() ?: "") + " " +
                    (poi.middleAddrName?.trim() ?: "") + " " +
                    (poi.lowerAddrName?.trim() ?: "") + " " +
                    (poi.detailAddrName?.trim() ?: "") + " " +
                    poi.firstNo?.trim()
        } else {
            (poi.upperAddrName?.trim() ?: "") + " " +
                    (poi.middleAddrName?.trim() ?: "") + " " +
                    (poi.lowerAddrName?.trim() ?: "") + " " +
                    (poi.detailAddrName?.trim() ?: "") + " " +
                    (poi.firstNo?.trim() ?: "") + " " +
                    poi.secondNo?.trim()
        }
}
