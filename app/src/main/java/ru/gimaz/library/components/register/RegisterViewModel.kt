package ru.gimaz.library.components.register

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.gimaz.library.Screen
import ru.gimaz.library.db.User
import ru.gimaz.library.db.UserDao
import ru.gimaz.library.enums.ProcessState
import ru.gimaz.library.storage.UserStorage
import ru.gimaz.library.util.loadBitmapToFile

class RegisterViewModel(
    private val navController: NavController,
    private val userDao: UserDao
) {
    private val _firstName = MutableStateFlow("")
    val firstName = _firstName.asStateFlow()
    private val _middleName = MutableStateFlow("")
    val middleName = _middleName.asStateFlow()
    private val _lastName = MutableStateFlow("")
    val lastName = _lastName.asStateFlow()
    private val _login = MutableStateFlow("")
    val login = _login.asStateFlow()
    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()
    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword = _confirmPassword.asStateFlow()
    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()
    private val _photoUri = MutableStateFlow<Uri?>(null)
    val photoUri = _photoUri.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.IO)

    private val _isFirstRegister = MutableStateFlow(false)
    val isFirstRegister = _isFirstRegister.asStateFlow()

    private val _registerState = MutableStateFlow(ProcessState.IDLE)
    val registerState = _registerState.asStateFlow()

    private val _requiredFields = MutableStateFlow<List<RequiredField>>(emptyList())
    val requiredFields = _requiredFields.asStateFlow()

    init {
        scope.launch {
            if (userDao.getAll().isEmpty()) {
                _isFirstRegister.value = true
            }
        }
    }


    fun handleIntent(intent: Intent) {
        when (intent) {
            is Intent.FirstNameChanged -> _firstName.value = intent.firstName
            is Intent.MiddleNameChanged -> _middleName.value = intent.middleName
            is Intent.LastNameChanged -> _lastName.value = intent.lastName
            is Intent.LoginChanged -> _login.value = intent.login
            is Intent.PasswordChanged -> _password.value = intent.password
            is Intent.ConfirmPasswordChanged -> _confirmPassword.value = intent.confirmPassword
            is Intent.EmailChanged -> _email.value = intent.email
            is Intent.PhotoUriChanged -> _photoUri.value = intent.photoUri
            is Intent.Register -> {
               scope.launch {
                   try {
                       saveUser(intent.context)
                   } catch (e: Exception){
                       Log.e("RegisterViewModel", "Error $e")
                       _registerState.value = ProcessState.ERROR
                   }
               }
            }

            Intent.RegisterSuccess -> {
                navController.navigate(Screen.Books.route)
            }
        }
    }
    private suspend fun saveUser(context: Context) {
            val firstName = _firstName.value
            val middleName = _middleName.value
            val lastName = _lastName.value
            val login = _login.value
            val password = _password.value
            val email = _email.value
            val photoUri = _photoUri.value
            if (photoUri != null) {
                val photoPath = loadBitmapToFile(photoUri, context)
                val user = User(
                    firstName = firstName,
                    middleName = middleName,
                    lastName = lastName,
                    login = login,
                    password = password,
                    email = email,
                    photoPath = photoPath,
                    isAdmin = isFirstRegister.value
                )
                userDao.insert(user)
                val newUser = userDao.getByLogin(login)
                UserStorage.user = newUser
                _registerState.value = ProcessState.SUCCESS
            } else {
                val user = User(
                    firstName = firstName,
                    middleName = middleName,
                    lastName = lastName,
                    login = login,
                    password = password,
                    email = email,
                    isAdmin = isFirstRegister.value
                )
                userDao.insert(user)
                val newUser = userDao.getByLogin(login)
                UserStorage.user = newUser
                _registerState.value = ProcessState.SUCCESS
        }
    }
    sealed class Intent {
        data class FirstNameChanged(val firstName: String) : Intent()
        data class MiddleNameChanged(val middleName: String) : Intent()
        data class LastNameChanged(val lastName: String) : Intent()
        data class LoginChanged(val login: String) : Intent()
        data class PasswordChanged(val password: String) : Intent()
        data class ConfirmPasswordChanged(val confirmPassword: String) : Intent()
        data class EmailChanged(val email: String) : Intent()
        data class PhotoUriChanged(val photoUri: Uri?) : Intent()
        data class Register(val context: Context) : Intent()
        data object RegisterSuccess : Intent()
    }


    enum class RequiredField {
        FIRST_NAME,
        MIDDLE_NAME,
        LAST_NAME,
        LOGIN,
        PASSWORD,
        EMAIL
    }
}