package id.nns.nichat.data.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class UserEntity(
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "uid")
    val uid: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "img_url")
    val imgUrl: String? = null,

    @ColumnInfo(name = "status")
    val status: String = "",

    @ColumnInfo(name = "dob")
    val dob: String = "--/--/----"
)
