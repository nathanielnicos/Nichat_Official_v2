package id.nns.nichat.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import id.nns.nichat.data.entity.ChannelEntity
import id.nns.nichat.data.entity.MessageEntity
import id.nns.nichat.data.entity.UserEntity
import id.nns.nichat.local.dao.ChannelDao
import id.nns.nichat.local.dao.MessageDao
import id.nns.nichat.local.dao.UserDao

@Database(
    entities = [ChannelEntity::class, MessageEntity::class, UserEntity::class],
    version = 1,
    exportSchema = false
)
abstract class NichatDatabase : RoomDatabase() {

    abstract fun channelDao() : ChannelDao
    abstract fun messageDao() : MessageDao
    abstract fun userDao() : UserDao

    companion object {
        @Volatile
        private var INSTANCE: NichatDatabase? = null

        fun getInstance(context: Context) : NichatDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    NichatDatabase::class.java,
                    "nichat_database"
                ).build().apply {
                    INSTANCE = this
                }
            }
    }

}
