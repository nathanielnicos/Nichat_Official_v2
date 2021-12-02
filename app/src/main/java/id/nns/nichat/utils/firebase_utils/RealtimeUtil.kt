package id.nns.nichat.utils.firebase_utils

import com.google.firebase.database.*
import id.nns.nichat.domain.model.Channel
import id.nns.nichat.domain.model.Message

object RealtimeUtil {

    private val realtimeInstance: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }

    fun getMessageReference(channelId: String) : DatabaseReference {
        return realtimeInstance
            .getReference("/all_channels/${channelId}")
    }

    fun pushMessageToReference(
        message: Message,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val reference = realtimeInstance
            .getReference("/all_channels/${message.channelId}")
            .child(message.messageId)

        reference.setValue(message)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onFailure(it.message.toString())
            }
    }

    fun getChannelReference(myId: String) : DatabaseReference {
        return realtimeInstance
            .getReference("/user_channels/${myId}")
    }

    fun pushChannelToReference(
        myId: String,
        partnerId: String,
        channel: Channel,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val myChannelReference = realtimeInstance
            .getReference("/user_channels/${myId}")
            .child(channel.channelId)
        val partnerChannelReference = realtimeInstance
            .getReference("/user_channels/${partnerId}")
            .child(channel.channelId)

        myChannelReference.setValue(channel)
            .addOnSuccessListener {
                partnerChannelReference.setValue(channel)
                    .addOnSuccessListener {
                        onSuccess()
                    }
                    .addOnFailureListener {
                        onFailure(it.message.toString())
                    }
            }
            .addOnFailureListener {
                onFailure(it.message.toString())
            }
    }

    fun deleteChannel(channelId: String, soWhat: (String?) -> Unit) {
        val reference = realtimeInstance
            .getReference("/all_channels/$channelId")

        reference.removeValue()
            .addOnSuccessListener {
                soWhat(null)
            }
            .addOnFailureListener {
                soWhat(it.message)
            }
    }

}
