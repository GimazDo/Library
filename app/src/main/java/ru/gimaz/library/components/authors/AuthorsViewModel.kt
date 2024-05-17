package ru.gimaz.library.components.authors

import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.gimaz.library.Screen
import ru.gimaz.library.db.Author
import ru.gimaz.library.db.AuthorDao

class AuthorsViewModel(
    val navController: NavController,
    private val authorDao: AuthorDao
) {
    private val _searchFlow = MutableStateFlow("")
    val searchFlow = _searchFlow.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.IO)

    val _authors = MutableStateFlow<List<Author>>(emptyList())

    val authorsFlow = _authors.asStateFlow()

    init {
        scope.launch {
            _authors.value = authorDao.getAll()
        }
    }

    fun handleIntent(intent: Intent) {
        when (intent) {
            is Intent.Search -> {
                _searchFlow.value = intent.search
            }

            Intent.AddAuthor -> {
                navController.navigate(Screen.AddAuthor.route)
            }

            is Intent.OpenAuthor -> {
                navController.navigate(String.format(Screen.Author.routeForFormat!!, intent.author.id))
            }
        }
    }

    sealed class Intent {
        data class Search(val search: String) : Intent()
        data object AddAuthor : Intent()
        data class OpenAuthor(val author: Author): Intent()
    }
}