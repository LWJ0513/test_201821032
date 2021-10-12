package kr.ac.kku.cs.test_201821032.location

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.ac.kku.cs.test_201821032.databinding.ViewholderSearchResultItemBinding
import kr.ac.kku.cs.test_201821032.location.model.SearchResultEntity

class SearchRecyclerAdapter :
    RecyclerView.Adapter<SearchRecyclerAdapter.SearchResultItemViewHolder>() {

    private var searchResultList: List<SearchResultEntity> = listOf()
    private lateinit var searchResultClickListener: (SearchResultEntity) -> Unit

    class SearchResultItemViewHolder(
        private val binding: ViewholderSearchResultItemBinding,
        val searchResultClickListener: (SearchResultEntity) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindData(data: SearchResultEntity) = with(binding) {      // 뷰 초기화
            textTextView.text = data.locationName
            subtextTextView.text = data.fullAddress
        }

        fun bindViews(data: SearchResultEntity) {
            binding.root.setOnClickListener {
                searchResultClickListener(data)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultItemViewHolder {
        val view = ViewholderSearchResultItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SearchResultItemViewHolder(view, searchResultClickListener)
    }

    override fun onBindViewHolder(holder: SearchResultItemViewHolder, position: Int) {
        holder.bindData(searchResultList[position])
        holder.bindViews(searchResultList[position])
    }

    override fun getItemCount(): Int = searchResultList.size

    fun setSearchResultList(
        searchResultList: List<SearchResultEntity>,
        searchResultClickListener: (SearchResultEntity) -> Unit
    ) {
        this.searchResultList = searchResultList
        this.searchResultClickListener = searchResultClickListener
        notifyDataSetChanged()
    }
}
