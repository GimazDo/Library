package ru.gimaz.library.components.adminpanel

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.gimaz.library.BottomBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanel(viewModel: AdminPanelViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "Панель управления") })
        },
        bottomBar = { BottomBar(navController = viewModel.navController) }
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)){
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(top = 20.dp)) {
                TextButton(onClick = { viewModel.handleIntent(AdminPanelIntent.AddBook) }) {
                    Text(text = "Добавить книгу")
                }
                TextButton(onClick = { viewModel.handleIntent(AdminPanelIntent.AddAuthor) }) {
                    Text(text = "Добавить автора")
                }
                TextButton(onClick = { viewModel.handleIntent(AdminPanelIntent.AddPublisher) }) {
                    Text(text = "Добавить издательство")
                }
                
            }
        }
    }
}