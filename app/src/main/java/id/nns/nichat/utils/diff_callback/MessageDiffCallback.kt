package id.nns.nichat.utils.diff_callback

import androidx.recyclerview.widget.DiffUtil
import id.nns.nichat.domain.model.Message

class MessageDiffCallback(
    private val oldList: ArrayList<Message>,
    private val newList: ArrayList<Message>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition].messageId == newList[newItemPosition].messageId

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition].text ==
            newList[newItemPosition].text &&
                oldList[oldItemPosition].timeStamp ==
                    newList[newItemPosition].timeStamp

}
