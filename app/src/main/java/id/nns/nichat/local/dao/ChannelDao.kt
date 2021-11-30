package id.nns.nichat.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import id.nns.nichat.data.entity.ChannelEntity

@Dao
interface ChannelDao {

    @Query("SELECT * FROM channel_table")
    fun getAllChannels() : LiveData<List<ChannelEntity>>

    @Query("SELECT * FROM channel_table WHERE channel_id = :channelId")
    fun getChannelById(channelId: String) : ChannelEntity?

    @Query("SELECT * FROM channel_table WHERE first_user_uid = :firstUserId")
    fun getChannelByFirstUserId(firstUserId: String) : ChannelEntity?

    @Query("SELECT * FROM channel_table WHERE second_user_uid = :secondUserId")
    fun getChannelBySecondUserId(secondUserId: String) : ChannelEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertChannel(channel: ChannelEntity)

    @Query("DELETE FROM channel_table WHERE channel_id = :channelId")
    fun deleteChannel(channelId: String)

    @Query("SELECT COUNT() FROM channel_table")
    fun channelCount() : LiveData<Int>

}
