package ru.gimaz.library.components.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.gimaz.library.BottomBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Profile(viewModel: ProfileViewModel){
    Scaffold(
        bottomBar = { BottomBar(navController = viewModel.navController)},
        topBar = {
            TopAppBar(title = {
                Text(text = "Профиль")
            })
        }
    ) {paddingValues ->
        val user = viewModel.user!!
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)){
            Column(modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally){
                Row(modifier = Modifier.fillMaxWidth()){
                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .padding(5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (user.photoPath != null) {
                            AsyncImage(
                                model = user.photoPath,
                                contentDescription = "user photo"
                            )
                        } else {
                            Text(text = "Фото отсутствует", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    Column(modifier = Modifier.padding(5.dp)) {
                        Text(text = user.login, style = MaterialTheme.typography.headlineLarge)
                        Text(text = user.lastName,style = MaterialTheme.typography.headlineMedium)
                        Text(text = "${user.firstName} ${user.middleName}")
                    }
                }
                if(user.isAdmin){
                    Button(onClick = { viewModel.handleIntent(ProfileViewModel.Intent.Users)}) {
                        Text(text = "Управление пользователями")
                    }
                }
                Button(onClick = { viewModel.handleIntent(ProfileViewModel.Intent.Logout) }) {
                    Text(text = "Выйти")
                }
            }
        }

    }
}