package id.nns.nichat.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val dob: String = "--/--/----",
    val email: String,
    val imgUrl: String? = null,
    val name: String,
    val status: String = "",
    val uid: String,
) : Parcelable
