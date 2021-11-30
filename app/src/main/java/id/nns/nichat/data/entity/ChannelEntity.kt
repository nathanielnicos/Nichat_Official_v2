package id.nns.nichat.data.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "channel_table")
data class ChannelEntity(
  @PrimaryKey
  @NonNull
  @ColumnInfo(name = "channel_id")
  val channelId: String,

  @ColumnInfo(name = "first_user_uid")
  val firstUserUid: String,

  @ColumnInfo(name = "second_user_uid")
  val secondUserUid: String
)
