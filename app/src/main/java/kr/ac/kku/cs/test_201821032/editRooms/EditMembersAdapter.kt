package kr.ac.kku.cs.test_201821032.editRooms

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.facebook.FacebookSdk.getApplicationContext
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kr.ac.kku.cs.test_201821032.DBKey
import kr.ac.kku.cs.test_201821032.databinding.ItemEditMembersBinding

class EditMembersAdapter(val onItemClicked: (EditModel) -> Unit) :
    ListAdapter<EditModel, EditMembersAdapter.ViewHolder>(diffUtil) {

    private lateinit var membersDB: DatabaseReference

    inner class ViewHolder(private val binding: ItemEditMembersBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(Build.VERSION_CODES.N)
        fun bind(editMembersModel: EditModel) {
            binding.root.setOnClickListener {
                onItemClicked(editMembersModel)
            }

            membersDB = Firebase.database.reference.child(DBKey.DB_MEMBERS_LIST)
            membersDB.child(editMembersModel.hobby).child(editMembersModel.roomNumber)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        binding.editTitleTextView.text =
                            snapshot.child(DBKey.DB_TITLE).getValue(String::class.java).toString()
                        val img = snapshot.child(DBKey.DB_IMAGE_URL).getValue(String::class.java)

                        Glide.with(getApplicationContext())
                            .load(img)
                            .centerCrop()
                            .into(binding.editMembersRoomImageView)

                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemEditMembersBinding.inflate(
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
        val diffUtil = object : DiffUtil.ItemCallback<EditModel>() {
            override fun areItemsTheSame(
                oldItem: EditModel,
                newItem: EditModel
            ): Boolean {
                return oldItem.roomNumber == newItem.roomNumber
            }

            override fun areContentsTheSame(
                oldItem: EditModel,
                newItem: EditModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
