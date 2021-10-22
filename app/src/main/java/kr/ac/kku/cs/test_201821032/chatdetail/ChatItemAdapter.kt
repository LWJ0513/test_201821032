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
import kr.ac.kku.cs.test_201821032.R
import kr.ac.kku.cs.test_201821032.databinding.ItemChatRowBinding

class ChatItemAdapter() : ListAdapter<ChatItem, ChatItemAdapter.ViewHolder>(diffUtil) {

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()


    inner class ViewHolder(private val binding: ItemChatRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(chatItem: ChatItem) {
            if (chatItem.senderUid == auth.currentUser!!.uid) {      // 오른쪽
                binding.chatLayout.gravity = Gravity.END
                binding.senderTextView.visibility = View.GONE
                binding.senderTextView.text = chatItem.senderName
                binding.messageTextView.text = chatItem.message
                binding.messageTextView.setTextColor(Color.WHITE)
                binding.messageTextView.setBackgroundResource(R.drawable.shape_chat_send_bubble)
                binding.messageTextView.setPadding(30, 30, 60, 30)
            } else {        // 왼쪽
                binding.chatLayout.gravity = Gravity.START
                binding.senderTextView.visibility = View.VISIBLE
                binding.senderTextView.text = chatItem.senderName
                binding.messageTextView.text = chatItem.message
                binding.messageTextView.setTextColor(Color.BLACK)
                binding.messageTextView.setBackgroundResource(R.drawable.shape_chat_receive_bubble)
                binding.messageTextView.setPadding(30, 30, 60, 30)
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