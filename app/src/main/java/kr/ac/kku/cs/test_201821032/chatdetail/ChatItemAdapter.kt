package kr.ac.kku.cs.test_201821032.chatdetail

import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kr.ac.kku.cs.test_201821032.DBKey
import kr.ac.kku.cs.test_201821032.R
import kr.ac.kku.cs.test_201821032.databinding.ItemChatRowBinding

class ChatItemAdapter() : ListAdapter<ChatItem, ChatItemAdapter.ViewHolder>(diffUtil) {

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var userDB: DatabaseReference


    inner class ViewHolder(private val binding: ItemChatRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(chatItem: ChatItem) {

            userDB = Firebase.database.reference.child(DBKey.DB_USERS)

            if (chatItem.senderUid == auth.currentUser!!.uid) {      // 오른쪽
                binding.chatLayout.gravity = Gravity.END
                binding.senderTextView.visibility = View.GONE
                binding.messageTextView.text = chatItem.message
                binding.messageTextView.setTextColor(Color.WHITE)
                binding.messageTextView.setBackgroundResource(R.drawable.shape_chat_send_bubble)
                binding.messageTextView.setPadding(30, 30, 60, 30)
            } else {        // 왼쪽
                userDB.child(chatItem.senderUid).child(DBKey.DB_USER_NAME).addValueEventListener(object :ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val userName = snapshot.getValue(String::class.java)

                        binding.chatLayout.gravity = Gravity.START
                        binding.senderTextView.visibility = View.VISIBLE
                        binding.senderTextView.text = userName
                        binding.messageTextView.text = chatItem.message
                        binding.messageTextView.setTextColor(Color.BLACK)
                        binding.messageTextView.setBackgroundResource(R.drawable.shape_chat_receive_bubble)
                        binding.messageTextView.setPadding(30, 30, 60, 30)
                    }
                    override fun onCancelled(error: DatabaseError) { }
                })
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemChatRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ChatItem>() {
            override fun areItemsTheSame(oldItem: ChatItem, newItem: ChatItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ChatItem, newItem: ChatItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}