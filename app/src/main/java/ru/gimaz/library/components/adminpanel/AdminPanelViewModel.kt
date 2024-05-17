package ru.gimaz.library.components.adminpanel

import androidx.navigation.NavController
import ru.gimaz.library.Screen

class AdminPanelViewModel(
    val navController: NavController,
) {


    fun handleIntent(intent: AdminPanelIntent){
        when(intent){
            AdminPanelIntent.AddAuthor -> navController.navigate(Screen.AddAuthor.route)
            AdminPanelIntent.AddBook -> navController.navigate(Screen.AddBook.route)
            AdminPanelIntent.AddPublisher -> navController.navigate(Screen.AddPublisher.route)
        }
    }

}


sealed class AdminPanelIntent{
    data object AddBook: AdminPanelIntent()
    data object AddAuthor: AdminPanelIntent()
    data object AddPublisher: AdminPanelIntent()
}

