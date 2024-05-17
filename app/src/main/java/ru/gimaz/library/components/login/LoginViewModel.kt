package ru.gimaz.library.components.login

import androidx.navigation.NavController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.gimaz.library.db.UserDao
import ru.gimaz.library.enums.LoginState

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

    private val _loginState = MutableStateFlow(LoginState.IDLE)
    val loginState = _loginState.asStateFlow()


    fun handleIntent(intent: Intent) {
        when (intent) {
            is Intent.LoginChanged -> _login.value = intent.login
            is Intent.PasswordChanged -> _password.value = intent.password
            is Intent.Login -> {

            }
            is Intent.Register -> {

            }
        }
    }


    sealed class Intent{
        data object Login : Intent()
        data object Register : Intent()
        data class LoginChanged(val login: String) : Intent()
        data class PasswordChanged(val password: String) : Intent()
    }

}