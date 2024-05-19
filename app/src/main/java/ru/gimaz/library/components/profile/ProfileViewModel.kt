package ru.gimaz.library.components.profile

import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.gimaz.library.Screen
import ru.gimaz.library.db.Book
import ru.gimaz.library.db.BookDao
import ru.gimaz.library.db.UserBookReadDao
import ru.gimaz.library.storage.UserStorage

class ProfileViewModel(
    val navController: NavController,
    private val bookDao: BookDao,
    private val userBookReadDao: UserBookReadDao
) {
    val user = UserStorage.user!!
    private val _readBook = MutableStateFlow(emptyList<Book>())
    val readBook = _readBook.asStateFlow()
    private val scope = CoroutineScope(Dispatchers.IO)
    init {
        scope.launch {
            val read = userBookReadDao.getByUserId(user.id!!).map { it.bookId }
            _readBook.value = bookDao.getBooksByIds(read)
        }
    }

    fun handleIntent(intent: Intent){
        when(intent){
            is Intent.Logout -> {
                navController.navigate(Screen.Login.route)
            }
            is Intent.Users -> {
                navController.navigate(Screen.Users.route)
            }
            is Intent.OpenBook -> {
                navController.navigate(String.format(Screen.Book.routeForFormat!!, intent.id) )
            }
        }
    }


    sealed class Intent{
        data object Logout : Intent()
        data object Users: Intent()
        data class OpenBook(val id: Int): Intent()
    }
}