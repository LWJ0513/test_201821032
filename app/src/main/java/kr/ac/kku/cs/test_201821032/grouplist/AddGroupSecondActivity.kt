package kr.ac.kku.cs.test_201821032.grouplist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import kr.ac.kku.cs.test_201821032.databinding.ActivityAddGroupSecondBinding
import kr.ac.kku.cs.test_201821032.location.SearchRecyclerAdapter
import kr.ac.kku.cs.test_201821032.location.model.LocationLatLngEntity
import kr.ac.kku.cs.test_201821032.location.model.SearchResultEntity
import kr.ac.kku.cs.test_201821032.location.response.search.Poi
import kr.ac.kku.cs.test_201821032.location.response.search.Pois
import kr.ac.kku.cs.test_201821032.location.utillity.RetrofitUtil
import kotlinx.coroutines.*
import kotlinx.coroutines.GlobalScope.coroutineContext
import kotlin.coroutines.CoroutineContext

class AddGroupSecondActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var binding: ActivityAddGroupSecondBinding
    private lateinit var adapter: SearchRecyclerAdapter
    private lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        binding = ActivityAddGroupSecondBinding.inflate(layoutInflater)
        setContentView(binding.root)

        job = Job()

        initAdapter()
        initViews()
        bindViews()
        initData()
    }

    private fun initAdapter() {
        adapter = SearchRecyclerAdapter()
    }

    private fun initViews() = with(binding) {    // with() 라는 scope function을 사용하면 바인딩에 쉽게 접근 가능
        emptyResultTextView.isVisible = false
        recyclerView.adapter = adapter
    }

    private fun bindViews() =
        with(binding) {       // 검색 버튼을 클릭하면 input에 있는 키워드를 가져오고 api를 호출해서 보여주게
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
                name = it.name ?: "빌딩명 없음",
                fullAddress = makeMainAddress(it),
                locationLatLng = LocationLatLngEntity(
                    it.noorLat,
                    it.noorLon
                )
            )
        }

        adapter.setSearchResultList(dataList) {
            Toast.makeText(
                this,
                "빌딩 이름: ${it.name} , 주소 : ${it.fullAddress} , 위도/경도 : ${it.locationLatLng}",
                Toast.LENGTH_SHORT
            ).show()
           /*  startActivity(Intent(this, MapActivity::class.java).apply {
                 putExtra(SEARCH_RESULT_EXTRA_KEY, it)
        })*/
    }
}

// 코르틴으로 IO스레드로 바꿨다가 성공적으로 데이터를 받아오면 다시 메인스레드로 바꿔줌
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
                "검색하는 과정에서 에러가 발생했습니다. : ${e.message}",
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