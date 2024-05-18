package ru.gimaz.library.components.publishers

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.gimaz.library.Screen
import ru.gimaz.library.db.Publisher
import ru.gimaz.library.db.PublisherDao

class PublishersViewModel(
    val navController: NavController,
    private val publisherDao: PublisherDao
): ViewModel() {
    fun handleIntent(intent: Intent) {
        when(intent){
            is Intent.Search -> {
                _searchFlow.value = intent.query
                scope.launch {
                    _publishersFlow.value = publisherDao.search(intent.query)
                }
            }

            Intent.AddPublisher -> {
                navController.navigate(Screen.AddPublisher.route)
            }
            is Intent.Open -> {
                navController.navigate(String.format(Screen.Publisher.routeForFormat!!, intent.publisher.id))
            }
        }
    }

    private val _searchFlow = MutableStateFlow("")
    val searchFlow = _searchFlow.asStateFlow()

    private val _publishersFlow = MutableStateFlow(emptyList<Publisher>())
    val publishersFlow = _publishersFlow.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        scope.launch {
            _publishersFlow.value = publisherDao.getAll()
        }
    }

    sealed class Intent{
        data class Search(val query: String) : Intent()
        data object AddPublisher : Intent()
        data class Open(val publisher: Publisher) : Intent()
    }
}