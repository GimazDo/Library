package ru.gimaz.library.components.profile

import androidx.navigation.NavController
import ru.gimaz.library.Screen
import ru.gimaz.library.storage.UserStorage

class ProfileViewModel(
    val navController: NavController,
) {
    val user = UserStorage.user


    fun handleIntent(intent: Intent){
        when(intent){
            is Intent.Logout -> {
                navController.navigate(Screen.Login.route)
            }
            is Intent.Users -> {

            }
        }
    }


    sealed class Intent{
        data object Logout : Intent()
        data object Users: Intent()
    }
}