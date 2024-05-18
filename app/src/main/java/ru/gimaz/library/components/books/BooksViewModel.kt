package ru.gimaz.library.components.books

import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.gimaz.library.Screen
import ru.gimaz.library.db.Book
import ru.gimaz.library.db.BookDao

class BooksViewModel(
    val navController: NavController,
    private val bookDao: BookDao
) {

    private val _searchFlow = MutableStateFlow("")
    val searchFlow = _searchFlow.asStateFlow()

    private val _books = MutableStateFlow<List<Book>>(emptyList())
    val books = _books.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        scope.launch {
            _books.value = bookDao.getAll()
        }
    }

    fun handleIntent(intent: Intent ) {
        when(intent){
            Intent.AddBook -> {
                navController.navigate(Screen.AddBook.route)
            }
            is Intent.Search -> {
                _searchFlow.value = intent.query
                scope.launch {
                    _books.value = bookDao.search(intent.query)
                }
            }

            is Intent.OpenBook -> {
                navController.navigate(String.format(Screen.Book.routeForFormat!!, intent.book.id))
            }
        }
    }

    sealed class Intent{
        data object AddBook : Intent()
        data class Search(val query: String): Intent()
        data class OpenBook(val book: Book): Intent()
    }
}