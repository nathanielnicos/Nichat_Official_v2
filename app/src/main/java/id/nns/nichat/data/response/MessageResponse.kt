package id.nns.nichat.data.response

data class MessageResponse(
  val messageId: String? = null,
  val channelId: String? = null,
  val fromId: String? = null,
  val toId: String? = null,
  val text: String? = null,
  val timeStamp: Long? = null
)
