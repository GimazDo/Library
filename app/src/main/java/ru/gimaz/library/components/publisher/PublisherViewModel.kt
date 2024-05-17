package ru.gimaz.library.components.publisher

import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.gimaz.library.Screen
import ru.gimaz.library.db.Book
import ru.gimaz.library.db.BookDao
import ru.gimaz.library.db.Publisher
import ru.gimaz.library.db.PublisherDao
import ru.gimaz.library.enums.LoadingObjectError
import ru.gimaz.library.enums.LoadingState

class PublisherViewModel(
    private val navigationController: NavController,
    publisherId: Int,
    private val publisherDao: PublisherDao,
    private val bookDao: BookDao
) {
    private val _publisher = MutableStateFlow<Publisher?>(null)
    val publisher = _publisher.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.IO)

    private val _objectErrorState = MutableStateFlow<LoadingObjectError?>(null)
    val objectErrorState = _objectErrorState.asStateFlow()

    private val _loadingState = MutableStateFlow(LoadingState.LOADING)
    val loadingState = _loadingState.asStateFlow()

    private val _books = MutableStateFlow<List<Book>>(emptyList())
    val books = _books.asStateFlow()

    init {
        scope.launch {
            try {
                val publisher = publisherDao.getById(publisherId)
                if (publisher == null) {
                    _objectErrorState.value = LoadingObjectError.NOT_FOUND
                    _loadingState.value = LoadingState.ERROR
                } else {
                    _publisher.value = publisher
                    _loadingState.value = LoadingState.LOADED
                }
            } catch (e: Exception){
                _objectErrorState.value = LoadingObjectError.INTERNAL_ERROR
                _loadingState.value = LoadingState.ERROR
            }
        }
        scope.launch {
            _books.value = bookDao.getBookByPublisherId(publisherId)
        }
    }

    fun handleIntent(intent: Intent) {
        when (intent) {
            is Intent.Back -> navigationController.popBackStack()
            is Intent.Edit -> navigationController.navigate(
                String.format(
                    Screen.EditPublisher.routeForFormat!!,
                    publisher.value!!.id
                )
            )

            is Intent.Delete -> {
                scope.launch {
                    publisherDao.delete(publisher.value!!)
                }
                navigationController.popBackStack()
            }
            is Intent.OpenBook -> navigationController.navigate(String.format(Screen.Book.routeForFormat!!, intent.bookId))
        }
    }

    sealed class Intent {
        data object Back : Intent()
        data object Edit : Intent()
        data object Delete : Intent()
        data class OpenBook(val bookId: Int) : Intent()
    }

}