package kr.ac.kku.cs.test_201821032.memberslist

import android.icu.text.SimpleDateFormat
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kr.ac.kku.cs.test_201821032.databinding.ItemMembersBinding
import java.util.*

class MembersAdapter(val onItemClicked: (MembersModel) -> Unit) :
    ListAdapter<MembersModel, MembersAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val binding: ItemMembersBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(Build.VERSION_CODES.N)
        fun bind(membersModel: MembersModel) {
            val format = SimpleDateFormat("MM월 dd일")
            val date = Date(membersModel.createAt)

            binding.titleTextView.text = membersModel.title
            binding.dateTextView.text = format.format(date).toString()
            binding.descriptionTextView.text = membersModel.description

            if (membersModel.imageUrl.isNotEmpty()) {
                Glide.with(binding.thumbnailImageView)
                    .load(membersModel.imageUrl)
                    .into(binding.thumbnailImageView)

            }

            binding.root.setOnClickListener {
                onItemClicked(membersModel)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemMembersBinding.inflate(
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
        val diffUtil = object : DiffUtil.ItemCallback<MembersModel>() {
            override fun areItemsTheSame(oldItem: MembersModel, newItem: MembersModel): Boolean {
                return oldItem.createAt == newItem.createAt
            }

            override fun areContentsTheSame(oldItem: MembersModel, newItem: MembersModel): Boolean {
                return oldItem == newItem
            }

        }
    }

}