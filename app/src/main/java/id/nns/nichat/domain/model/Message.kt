package id.nns.nichat.domain.model

data class Message(
  val messageId: String,
  val channelId: String,
  val fromId: String,
  val toId: String,
  val text: String,
  val timeStamp: Long
)
