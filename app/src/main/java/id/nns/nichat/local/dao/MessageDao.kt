package id.nns.nichat.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import id.nns.nichat.data.entity.MessageEntity

@Dao
interface MessageDao {

    @Query("SELECT * FROM message_table WHERE channel_id = :channelId ORDER BY timestamp ASC")
    fun getMessagesByChannelId(channelId: String) : LiveData<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMessage(message: MessageEntity)

    @Query("DELETE FROM message_table WHERE channel_id = :channelId")
    fun deleteAllMessagesByChannelId(channelId: String)

}
