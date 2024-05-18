package ru.gimaz.library.components.loading

import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.gimaz.library.Screen
import ru.gimaz.library.db.UserDao
import ru.gimaz.library.enums.LoadingState

class AppLoadingViewModel(
    private val navController: NavController,
    private val userDao: UserDao
) {
    private val _loadingState = MutableStateFlow(LoadingState.LOADING)
    val loadingState = _loadingState.asStateFlow()
    private val scope = CoroutineScope(Dispatchers.IO)
    private val _nextScreen = MutableStateFlow<Screen?>(null)
    init {
        scope.launch {
            val users = userDao.getAll()
            println("Users: $users")
            if(users.isEmpty()){
                _nextScreen.value = Screen.Register
                _loadingState.value = LoadingState.LOADED
            }else{
                _nextScreen.value = Screen.Login
                _loadingState.value = LoadingState.LOADED
            }
        }
    }


    fun handleIntent(intent: Intent){
        when(intent){
            is Intent.Navigate -> navController.navigate(_nextScreen.value!!.route)
        }
    }
    sealed class Intent(){
        data object Navigate: Intent()
    }
}