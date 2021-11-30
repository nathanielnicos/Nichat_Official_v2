package id.nns.nichat.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import id.nns.nichat.data.entity.UserEntity

@Dao
interface UserDao {

    @Query("SELECT * FROM user_table")
    fun getAllUsers() : List<UserEntity>

    @Query("SELECT * FROM user_table WHERE uid = :uid")
    fun getUserById(uid: String) : UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: UserEntity)

    @Query("DELETE FROM user_table WHERE uid = :uid")
    fun deleteChannel(uid: String)

}
