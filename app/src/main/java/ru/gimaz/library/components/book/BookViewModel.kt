package ru.gimaz.library.components.book

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
import ru.gimaz.library.db.Publisher
import ru.gimaz.library.db.PublisherDao
import ru.gimaz.library.enums.LoadingObjectError
import ru.gimaz.library.enums.LoadingState

class BookViewModel(
    private val navController: NavController,
    private val bookId: Int,
    private val bookDao: BookDao,
    private val authorDao: AuthorDao,
    private val publisherDao: PublisherDao
) {

    private val _book = MutableStateFlow<Book?>(null)
    val book = _book.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.IO)
    private val _objectErrorState = MutableStateFlow<LoadingObjectError?>(null)
    val objectErrorState = _objectErrorState.asStateFlow()

    private val _loadingState = MutableStateFlow(LoadingState.LOADING)
    val loadingState = _loadingState.asStateFlow()

    private val _publisher = MutableStateFlow<Publisher?>(null)
    val publisher = _publisher.asStateFlow()

    private val _author = MutableStateFlow<Author?>(null)
    val author = _author.asStateFlow()


    init {
        scope.launch {
            try {
                _loadingState.value = LoadingState.LOADING
                val book = bookDao.getBookById(bookId)
                if (book != null) {
                    _book.value = book
                    _author.value = authorDao.getById(book.authorId)
                    _publisher.value = publisherDao.getById(book.publisherId)
                    _loadingState.value = LoadingState.LOADED
                } else {
                    _objectErrorState.value = LoadingObjectError.NOT_FOUND
                    _loadingState.value = LoadingState.ERROR
                }
            } catch (e: Exception) {
                _loadingState.value = LoadingState.ERROR
                _objectErrorState.value = LoadingObjectError.INTERNAL_ERROR
            }
        }
    }

    fun handleIntent(intent: Intent) {
        when (intent) {
            is Intent.Back -> navController.popBackStack()
            is Intent.Edit -> navController.navigate(
                String.format(
                    Screen.EditBook.routeForFormat!!,
                    bookId
                )
            )

            is Intent.Delete -> {
                scope.launch {
                    bookDao.delete(book.value!!)
                }
                navController.popBackStack()
            }

            Intent.OpenAuthor -> {
                navController.navigate(
                    String.format(
                        Screen.Author.routeForFormat!!,
                        author.value!!.id
                    )
                )
            }
            Intent.OpenPublisher -> {
                navController.navigate(
                    String.format(
                        Screen.Publisher.routeForFormat!!,
                        publisher.value!!.id
                    )
                )
            }
        }
    }


    sealed class Intent {
        data object Back : Intent()
        data object Edit : Intent()
        data object Delete : Intent()
        data object OpenAuthor : Intent()
        data object OpenPublisher : Intent()
    }
}