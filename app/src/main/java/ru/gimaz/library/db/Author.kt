package ru.gimaz.library.db

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import java.time.LocalDate

@Entity
data class Author(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @ColumnInfo(name = "first_name") val firstName: String,
    @ColumnInfo(name = "last_name") val lastName: String,
    @ColumnInfo(name = "surname") val surname: String,
    @ColumnInfo(name = "date_of_birth") val dateOfBirth: LocalDate,
    @ColumnInfo(name = "biography") val biography: String,
    @ColumnInfo(name = "photo_path") val photoPath: String? = null
)


@Dao
interface AuthorDao{
    @Query("SELECT * FROM author")
    suspend fun getAll(): List<Author>

    @Insert
    suspend fun insert(author: Author)

    @Query("SELECT * FROM author WHERE first_name LIKE :text OR last_name LIKE :text OR surname LIKE :text")
    suspend fun search(text: String): List<Author>


    @Query("SELECT * FROM author WHERE id = :authorId")
    suspend fun getById(authorId: Int): Author?

    @Delete
    suspend fun delete(author: Author)

    @Update
    suspend fun update(author: Author)


}