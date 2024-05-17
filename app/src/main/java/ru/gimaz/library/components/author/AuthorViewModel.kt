package ru.gimaz.library.components.author

import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.gimaz.library.Screen
import ru.gimaz.library.db.Author
import ru.gimaz.library.db.AuthorDao
import ru.gimaz.library.db.Book
import ru.gimaz.library.db.BookDao
import ru.gimaz.library.enums.LoadingObjectError
import ru.gimaz.library.enums.LoadingState

class AuthorViewModel(
    private val navController: NavController,
    private val authorId: Int,
    private val authorDao: AuthorDao,
    private val bookDao: BookDao
) {
    private val _author = MutableStateFlow<Author?>(null)
    val author = _author.asStateFlow()

    private val _books = MutableStateFlow<List<Book>>(emptyList())
    val books = _books.asStateFlow()

    private val _objectErrorState = MutableStateFlow<LoadingObjectError?>(null)
    val objectErrorState = _objectErrorState.asStateFlow()

    private val _loadingState = MutableStateFlow(LoadingState.LOADING)
    val loadingState = _loadingState.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.IO)
    init {
        scope.launch {
            try {
                _loadingState.value = LoadingState.LOADING
                val author = authorDao.getById(authorId)
                if (author != null) {
                    _author.value = author
                    _loadingState.value = LoadingState.LOADED
                    _books.value = bookDao.getByAuthorId(authorId)
                }
                else {
                    _objectErrorState.value = LoadingObjectError.NOT_FOUND
                    _loadingState.value = LoadingState.ERROR
                }
            } catch (e: Exception){
                _objectErrorState.value = LoadingObjectError.INTERNAL_ERROR
                _loadingState.value = LoadingState.ERROR
            }
        }
    }


    fun handleIntent(intent: Intent) {
        when (intent) {
            is Intent.Back -> navController.popBackStack()
            is Intent.Delete -> {
                scope.launch {
                    authorDao.delete(author.value!!)
                }
            }
            is Intent.Edit -> navController.navigate(String.format(Screen.EditAuthor.routeForFormat!!, author.value!!.id))
            is Intent.OpenBook -> navController.navigate(String.format(Screen.Book.routeForFormat!!, intent.bookId))
        }
    }


    sealed class Intent {
        data object Back : Intent()
        data object Delete : Intent()
        data object Edit : Intent()
        data class OpenBook(val bookId: Int) : Intent()
    }
}