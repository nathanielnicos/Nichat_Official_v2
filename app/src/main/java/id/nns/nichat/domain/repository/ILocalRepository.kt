package id.nns.nichat.domain.repository

import androidx.lifecycle.LiveData
import id.nns.nichat.domain.model.Channel
import id.nns.nichat.domain.model.Message
import id.nns.nichat.domain.model.User

interface ILocalRepository {

    fun getAllChannels() : LiveData<List<Channel>>

    fun getChannelById(channelId: String) : Channel?

    fun getChannelByFirstUserId(firstUserId: String) : Channel?

    fun getChannelBySecondUserId(secondUserId: String) : Channel?

    fun insertChannel(channel: Channel)

    fun deleteChannel(partnerId: String)

    fun getMessagesByChannelId(partnerId: String) : LiveData<List<Message>>

    fun getChannelCount() : LiveData<Int>

    fun insertMessage(message: Message)

    fun deleteAllMessagesByChannelId(channelId: String)

    fun getAllUsers() : List<User>

    fun getUserById(uid: String) : User?

    fun insertUser(user: User)

    fun deleteUser(uid: String)

}
