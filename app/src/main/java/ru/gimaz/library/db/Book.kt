package ru.gimaz.library.db

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update

@Entity
data class Book(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "author_id") val authorId: Int,
    @ColumnInfo(name = "publisher_id") val publisherId: Int,
    @ColumnInfo(name = "pages") val pages: Int,
    @ColumnInfo(name = "publish_year") val publishYear: Int,
    @ColumnInfo(name = "photo_path") val photoPath: String? = null,
    @ColumnInfo(name = "document_preview_path") val documentPreviewPath: String? = null

)


@Dao
interface BookDao {
    @Query("SELECT * FROM book")
    suspend fun getAll(): List<Book>

    @Insert
    suspend fun insert(book: Book)

    @Query("SELECT * FROM book WHERE publisher_id = :publisherId")
    suspend fun getBookByPublisherId(publisherId: Int): List<Book>

    @Query("SELECT * FROM book WHERE author_id = :authorId")
    suspend fun getBookByAuthorId(authorId: Int): List<Book>

    @Query("SELECT * FROM book WHERE id = :bookId")
    suspend fun getBookById(bookId: Int): Book?

    @Query("SELECT * FROM book WHERE id IN (:bookIds)")
    suspend fun getBooksByIds(bookIds: List<Int>): List<Book>

    @Update
    suspend fun update(book: Book)

    @Delete
    suspend fun delete(book: Book)
    @Query("SELECT * FROM book WHERE author_id = :authorId")
    fun getByAuthorId(authorId: Int): List<Book>

    @Query("SELECT * FROM book WHERE title LIKE '%' || :query || '%'")
    fun search(query: String): List<Book>
}
