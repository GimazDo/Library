package ru.gimaz.library.components.login

import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.gimaz.library.Screen
import ru.gimaz.library.db.UserDao
import ru.gimaz.library.enums.ProcessState

class LoginViewModel(
    private val navController: NavController,
    private val userDao: UserDao
) {
    private val _login = MutableStateFlow("")
    val login = _login.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _error = MutableStateFlow("")
    val error = _error.asStateFlow()

    private val _loginState = MutableStateFlow(ProcessState.IDLE)
    val loginState = _loginState.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.IO)
    fun handleIntent(intent: Intent) {
        when (intent) {
            is Intent.LoginChanged -> _login.value = intent.login
            is Intent.PasswordChanged -> _password.value = intent.password
            is Intent.Login -> {
                scope.launch {
                   try {
                       _loginState.value = ProcessState.LOADING
                       val login = _login.value
                       val password = _password.value
                       val user = userDao.findByLoginAndPassword(login, password)
                       if (user != null) {
                           _loginState.value = ProcessState.SUCCESS
                       } else {
                           _loginState.value = ProcessState.ERROR
                           _error.value = "Неправильный логин или пароль"
                       }
                   } catch (e: Exception){
                       println(e)
                       _loginState.value = ProcessState.ERROR
                       _error.value = "Что-то пошло не так, попробуйте позже"
                   }
                }
            }
            is Intent.Register -> {
                navController.navigate(Screen.Register.route)
            }

            Intent.LoginSuccess -> navController.navigate(Screen.Books.route)
        }
    }


    sealed class Intent {
        data object Login : Intent()
        data object Register : Intent()
        data class LoginChanged(val login: String) : Intent()
        data class PasswordChanged(val password: String) : Intent()
        data object LoginSuccess: Intent()
    }

}