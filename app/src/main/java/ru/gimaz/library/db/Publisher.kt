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
data class Publisher(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "address") val address: String,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "website") val website: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "year_of_foundation") val yearOfFoundation: Int,
    @ColumnInfo(name = "logo_path") val logoPath: String? = null
)



@Dao
interface PublisherDao{

    @Query("SELECT * FROM publisher")
    suspend fun getAll(): List<Publisher>

    @Query("SELECT * FROM publisher WHERE id = :id")
    suspend fun getById(id: Int): Publisher?

    @Insert
    suspend fun insert(publisher: Publisher)

    @Update
    suspend fun update(publisher: Publisher)


    @Query("SELECT * FROM publisher WHERE name LIKE '%' || :text || '%'")
    suspend fun search(text: String): List<Publisher>

    @Delete
    suspend fun delete(publisher: Publisher)
}