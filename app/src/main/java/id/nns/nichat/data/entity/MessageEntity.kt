package id.nns.nichat.data.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "message_table")
data class MessageEntity(
  @PrimaryKey
  @NonNull
  @ColumnInfo(name = "message_id")
  val messageId: String,

  @ColumnInfo(name = "channel_id")
  val channelId: String,

  @ColumnInfo(name = "from_id")
  val fromId: String,

  @ColumnInfo(name = "to_id")
  val toId: String,

  @ColumnInfo(name = "text")
  val text: String,

  @ColumnInfo(name = "timestamp")
  val timeStamp: Long
)
