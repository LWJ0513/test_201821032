package kr.ac.kku.cs.test_201821032.grouplist

import android.icu.text.SimpleDateFormat
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kr.ac.kku.cs.test_201821032.databinding.ItemGroupsBinding
import java.util.*

class GroupsAdapter (val onItemClicked: (GroupsModel) -> Unit) :
    ListAdapter<GroupsModel, GroupsAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val binding: ItemGroupsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(Build.VERSION_CODES.N)
        fun bind(groupModel: GroupsModel) {
            val format = SimpleDateFormat("MM월 dd일")
            val date = Date(groupModel.createAt)

            binding.titleTextView.text = groupModel.title
            binding.dateTextView.text = format.format(date).toString()
            binding.descriptionTextView.text = groupModel.description
            binding.hashTagTextView.text = groupModel.hashTag
            binding.locationTextView.text = groupModel.locationName
            if (binding.locationTextView.text != "") {
                binding.locationImageView.visibility = View.VISIBLE
            } else {
                binding.locationImageView.visibility = View.GONE
            }

            if (groupModel.imageUrl.isNotEmpty()) {
                Glide.with(binding.thumbnailImageView)
                    .load(groupModel.imageUrl)
                    .into(binding.thumbnailImageView)
            }

            binding.root.setOnClickListener {
                onItemClicked(groupModel)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemGroupsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<GroupsModel>() {
            override fun areItemsTheSame(oldItem: GroupsModel, newItem: GroupsModel): Boolean {
                return oldItem.createAt == newItem.createAt
            }

            override fun areContentsTheSame(oldItem: GroupsModel, newItem: GroupsModel): Boolean {
                return oldItem == newItem
            }

        }
    }
}
