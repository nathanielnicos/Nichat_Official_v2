package id.nns.nichat.data.response

data class UserResponse(
  val dob: String = "--/--/----",
  val email: String? = null,
  val imgUrl: String? = null,
  val name: String? = null,
  val status: String = "",
  val uid: String? = null,
)
