package id.nns.nichat.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import id.nns.nichat.domain.model.Channel
import id.nns.nichat.domain.model.Message
import id.nns.nichat.domain.model.User
import id.nns.nichat.domain.repository.ILocalRepository
import id.nns.nichat.local.dao.ChannelDao
import id.nns.nichat.local.dao.MessageDao
import id.nns.nichat.local.dao.UserDao
import id.nns.nichat.utils.converters.DataMapper
import id.nns.nichat.utils.converters.DataMapper.toDomain
import id.nns.nichat.utils.converters.DataMapper.toEntity

class LocalRepository(
    private val userDao: UserDao,
    private val channelDao: ChannelDao,
    private val messageDao: MessageDao
) : ILocalRepository {

    companion object {
        @Volatile
        private var INSTANCE: LocalRepository? = null

        fun getInstance(usrDao: UserDao, chnDao: ChannelDao, msgDao: MessageDao): LocalRepository =
            INSTANCE ?: LocalRepository(usrDao, chnDao, msgDao)
    }

    override fun getAllChannels(): LiveData<List<Channel>> {
        return Transformations.map(channelDao.getAllChannels()) {
            DataMapper.mapChannelEntityToDomain(it)
        }
    }

    override fun getChannelById(channelId: String): Channel? {
        return channelDao.getChannelById(channelId)?.toDomain()
    }

    override fun getChannelByFirstUserId(firstUserId: String): Channel? {
        return channelDao.getChannelByFirstUserId(firstUserId)?.toDomain()
    }

    override fun getChannelBySecondUserId(secondUserId: String): Channel? {
        return channelDao.getChannelBySecondUserId(secondUserId)?.toDomain()
    }

    override fun insertChannel(channel: Channel) {
        channelDao.insertChannel(channel.toEntity())
    }

    override fun deleteChannel(partnerId: String) {
        channelDao.deleteChannel(partnerId)
    }

    override fun getMessagesByChannelId(partnerId: String): LiveData<List<Message>> {
        return Transformations.map(messageDao.getMessagesByChannelId(partnerId)) {
            DataMapper.mapMessageEntityToDomain(it)
        }
    }

    override fun getChannelCount(): LiveData<Int> {
        return channelDao.channelCount()
    }

    override fun insertMessage(message: Message) {
        messageDao.insertMessage(message.toEntity())
    }

    override fun deleteAllMessagesByChannelId(channelId: String) {
        messageDao.deleteAllMessagesByChannelId(channelId)
    }

    override fun getAllUsers(): List<User> {
        return DataMapper.mapUserEntityToDomain(userDao.getAllUsers())
    }

    override fun getUserById(uid: String): User? {
        return userDao.getUserById(uid)?.toDomain()
    }

    override fun insertUser(user: User) {
        userDao.insertUser(user.toEntity())
    }

    override fun deleteUser(uid: String) {
        userDao.deleteChannel(uid)
    }

}
