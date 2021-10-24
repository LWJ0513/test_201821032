package kr.ac.kku.cs.test_201821032.chatlist

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kr.ac.kku.cs.test_201821032.DBKey
import kr.ac.kku.cs.test_201821032.databinding.ItemChatListGroupBinding

class GroupChatListAdapter(val onChatRoomClicked: (ChatListItem) -> Unit, val onCommunityClicked: (ChatListItem) -> Unit) :
    ListAdapter<ChatListItem, GroupChatListAdapter.ViewHolder>(diffUtil) {

    private val groupsDB: DatabaseReference by lazy {
        Firebase.database.reference.child(DBKey.DB_GROUPS_LIST)
    }

    inner class ViewHolder(private val binding: ItemChatListGroupBinding) :
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

            groupsDB.child(chatListItem.onOff).child(chatListItem.hobby).child(chatListItem.roomNumber).child("imageUrl").addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val photo = snapshot.getValue(String::class.java)
                    Glide.with(binding.groupsRoomImageView)
                        .load(photo!!.toUri())
                        .centerCrop()
                        .into(binding.groupsRoomImageView)
                }
                override fun onCancelled(error: DatabaseError) { }
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemChatListGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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