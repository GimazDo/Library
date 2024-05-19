package ru.gimaz.library.components.users

import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.gimaz.library.components.book.BookViewModel
import ru.gimaz.library.db.User
import ru.gimaz.library.db.UserDao
import ru.gimaz.library.storage.UserStorage

class UsersViewModel(
    private val navController: NavController,
    private val userDao: UserDao
) {
    private val _allUsers = MutableStateFlow<List<User>>(emptyList())
    private val _admins = MutableStateFlow<List<User>>(emptyList())
    val admins = _admins.asStateFlow()
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users = _users.asStateFlow()
    private val scope = CoroutineScope(Dispatchers.IO)
    val userId = UserStorage.user!!.id!!
    init {
        scope.launch {
            val allUsers = userDao.getAll()
            _allUsers.value = allUsers
            _admins.value = allUsers.filter { it.isAdmin }
            _users.value = allUsers.filter { !it.isAdmin }
        }
    }


    fun handleIntent(intent: Intent) {
        when (intent) {
            is Intent.SetAdmin -> {
                scope.launch {
                    val user = _allUsers.value.find { it.id == intent.userId }
                    if (user != null) {
                        _admins.value = _admins.value.plus(user)
                        _users.value = _users.value.minus(user)
                        userDao.update(user.copy(isAdmin = true))
                    }
                }
            }

            is Intent.UnsetAdmin -> {
                scope.launch {
                    val user = _allUsers.value.find { it.id == intent.userId }
                    if (user != null) {
                        _admins.value = _admins.value.minus(user)
                        _users.value = _users.value.plus(user)
                        userDao.update(user.copy(isAdmin = false))
                    }
                }
            }

            is Intent.Back -> {
                navController.popBackStack()
            }
        }
    }

    sealed class Intent {
        data class SetAdmin(val userId: Int) : Intent()
        data class UnsetAdmin(val userId: Int) : Intent()
        data object Back : Intent()
    }

}