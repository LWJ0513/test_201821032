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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kr.ac.kku.cs.test_201821032.DBKey
import kr.ac.kku.cs.test_201821032.databinding.ItemChatListMembersBinding

class ChatListAdapter(val onItemClicked: (ChatListItem) -> Unit) :
    ListAdapter<ChatListItem, ChatListAdapter.ViewHolder>(diffUtil) {

    private lateinit var userDB: DatabaseReference
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    inner class ViewHolder(private val binding: ItemChatListMembersBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(Build.VERSION_CODES.N)
        fun bind(chatListItem: ChatListItem) {
            binding.root.setOnClickListener {
                onItemClicked(chatListItem)
            }

            userDB = Firebase.database.reference.child("Users")

            binding.chatRoomTitleTextView.text = chatListItem.roomName
            if(chatListItem.managerId == auth.currentUser!!.uid) {      // 내가 개설자면 입장자 이름

                userDB.child(chatListItem.entryId).addValueEventListener(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val photo = snapshot.child(DBKey.DB_USER_PROFILE_IMAGE).getValue(String::class.java)!!.toUri()

                        binding.lastChatTextView.text =snapshot.child(DBKey.DB_USER_NAME).getValue(String::class.java)
                        Glide.with(binding.membersRoomImageView)
                            .load(photo)
                            .centerCrop()
                            .into(binding.membersRoomImageView)
                    }
                    override fun onCancelled(error: DatabaseError) { }
                })
            } else {        //  내가 참여자면

                userDB.child(chatListItem.managerId).addValueEventListener(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val photo = snapshot.child(DBKey.DB_USER_PROFILE_IMAGE).getValue(String::class.java)!!.toUri()

                        binding.lastChatTextView.text =snapshot.child(DBKey.DB_USER_NAME).getValue(String::class.java)
                        Glide.with(binding.membersRoomImageView)
                            .load(photo)
                            .centerCrop()
                            .into(binding.membersRoomImageView)
                    }
                    override fun onCancelled(error: DatabaseError) { }
                })
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemChatListMembersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

            override fun areContentsTheSame(oldItem:ChatListItem, newItem:ChatListItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}