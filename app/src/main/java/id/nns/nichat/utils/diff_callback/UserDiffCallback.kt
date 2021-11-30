package id.nns.nichat.utils.diff_callback

import androidx.recyclerview.widget.DiffUtil
import id.nns.nichat.domain.model.User

class UserDiffCallback(
    private val oldList: ArrayList<User>,
    private val newList: ArrayList<User>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition].uid == newList[newItemPosition].uid

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        true

}
