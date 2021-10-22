package kr.ac.kku.cs.test_201821032.chatlist

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_groups_offline_detail.*
import kr.ac.kku.cs.test_201821032.databinding.ItemGroupChatListBinding

class GroupChatListAdapter(val onChatRoomClicked: (ChatListItem) -> Unit, val onCommunityClicked: (ChatListItem) -> Unit) :
    ListAdapter<ChatListItem, GroupChatListAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val binding: ItemGroupChatListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(Build.VERSION_CODES.N)
        fun bind(chatListItem: ChatListItem) {
            binding.communityButton.setOnClickListener {
               onCommunityClicked(chatListItem)
            }
            binding.chatRoomButton.setOnClickListener {
                onChatRoomClicked(chatListItem)
            }

            binding.chatRoomTitleTextView.text = chatListItem.roomName
            Glide.with(binding.groupsRoomImageView)
                .load(chatListItem.roomImage.toUri())
                .centerCrop()
                .into(binding.groupsRoomImageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemGroupChatListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ChatListItem>() {
            override fun areItemsTheSame(oldItem: ChatListItem, newItem: ChatListItem): Boolean {
                return oldItem.key == newItem.key
            }

            override fun areContentsTheSame(oldItem: ChatListItem, newItem: ChatListItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}