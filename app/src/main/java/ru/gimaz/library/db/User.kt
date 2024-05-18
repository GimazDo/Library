package ru.gimaz.library.db

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query

@Entity
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @ColumnInfo(name = "first_name") val firstName: String,
    @ColumnInfo(name = "middle_name") val middleName: String? = null,
    @ColumnInfo(name = "last_name") val lastName: String,
    @ColumnInfo(name = "login") val login: String,
    @ColumnInfo(name = "password") val password: String,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "photo_path") val photoPath: String? = null,
    @ColumnInfo(name = "is_admin") val isAdmin: Boolean = false,
)


@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    suspend fun getAll(): List<User>

    @Insert
    suspend fun insert(user: User)

    @Query("SELECT * FROM user WHERE id = :id")
    suspend fun getById(id: Int): User

    @Query("SELECT * FROM user WHERE login = :login")
    suspend fun getByLogin(login: String): User

    @Query("SELECT * FROM user WHERE login = :login AND password = :password")
    suspend fun findByLoginAndPassword(login: String, password: String): User?


}


@Entity(primaryKeys = ["user_id", "book_id"])
data class UserBookRead(
    @ColumnInfo(name = "user_id") val userId: Int,
    @ColumnInfo(name = "book_id") val bookId: Int
)


@Dao
interface UserBookReadDao {
    @Insert
    suspend fun insert(userBookRead: UserBookRead)
    @Query("DELETE FROM userbookread WHERE user_id = :userId AND book_id = :bookId")
    suspend fun delete(userId: Int, bookId: Int)

    @Query("SELECT * FROM userbookread WHERE user_id = :userId")
    suspend fun getByUserId(userId: Int): List<UserBookRead>
}