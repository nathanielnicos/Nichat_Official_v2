package id.nns.nichat.ui.home

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import id.nns.nichat.R
import id.nns.nichat.databinding.ListChatBinding
import id.nns.nichat.domain.model.User
import id.nns.nichat.ui.chat.ChatActivity
import id.nns.nichat.ui.image.ImageActivity
import id.nns.nichat.utils.diff_callback.UserDiffCallback
import kotlin.collections.ArrayList

class HomeAdapter : RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {

    private lateinit var binding: ListChatBinding

    var partnerUsers = ArrayList<User>()
        set(data) {
            val diffCallback = UserDiffCallback(partnerUsers, data)
            val diffResult = DiffUtil.calculateDiff(diffCallback)

            this.partnerUsers.clear()
            this.partnerUsers.addAll(data)
            diffResult.dispatchUpdatesTo(this)
        }

    inner class HomeViewHolder(private val vhBinding: ListChatBinding) : RecyclerView.ViewHolder(vhBinding.root) {

        fun bind(user: User) {
            Glide.with(itemView.context)
                .load(user.imgUrl)
                .placeholder(R.drawable.profile)
                .into(vhBinding.civListChat)

            vhBinding.tvNameListChat.text = user.name

            vhBinding.civListChat.setOnClickListener {
                Intent(itemView.context, ImageActivity::class.java).apply {
                    putExtra(ImageActivity.OPEN_IMAGE, user.imgUrl)
                    itemView.context.startActivity(this)
                }
            }

            vhBinding.cvListChat.setOnClickListener {
                Intent(itemView.context, ChatActivity::class.java).apply {
                    putExtra(ChatActivity.KEY_USER, user)
                    itemView.context.startActivity(this)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        binding = ListChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.bind(partnerUsers[position])
    }

    override fun getItemCount(): Int = partnerUsers.size

}
