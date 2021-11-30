package id.nns.nichat.ui.user

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import id.nns.nichat.R
import id.nns.nichat.databinding.ListUserBinding
import id.nns.nichat.domain.model.User
import id.nns.nichat.ui.image.ImageActivity
import id.nns.nichat.utils.OnSomethingClickListener

class UserAdapter : RecyclerView.Adapter<UserAdapter.UsersViewHolder>() {

    private lateinit var binding: ListUserBinding
    private var onUserClickListener: OnSomethingClickListener? = null

    var users = ArrayList<User>()
        @SuppressLint("NotifyDataSetChanged")
        set(data) {
            if (data.size > 0) {
                this.users.clear()
            }
            this.users.addAll(data)

            notifyDataSetChanged()
        }

    inner class UsersViewHolder(private val vhBinding: ListUserBinding) : RecyclerView.ViewHolder(vhBinding.root) {

        fun bind(user: User) {
            vhBinding.tvListUser.text = user.name
            Glide.with(itemView.context)
                .load(user.imgUrl)
                .placeholder(R.drawable.profile)
                .into(vhBinding.civListUser)

            vhBinding.civListUser.setOnClickListener {
                Intent(itemView.context, ImageActivity::class.java).apply {
                    putExtra(ImageActivity.OPEN_IMAGE, user.imgUrl)
                    itemView.context.startActivity(this)
                }
            }
        }

        init {
            binding.clListUser.setOnClickListener {
                onUserClickListener?.onSomethingClick(it, layoutPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        binding = ListUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UsersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount(): Int = users.size

    fun setOnUserClickListener(onClick: OnSomethingClickListener) {
        this.onUserClickListener = onClick
    }

}
