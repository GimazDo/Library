package ru.gimaz.library.components.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.gimaz.library.enums.ProcessState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Login(viewModel: LoginViewModel){
        val login by viewModel.login.collectAsState()
        val password by viewModel.password.collectAsState()
        val loginState by viewModel.loginState.collectAsState()
        val error by viewModel.error.collectAsState()
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "Авторизация") }
                )
            },
        ) {paddingValues ->
            Box(modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()){
                when(loginState){
                    ProcessState.IDLE, ProcessState.ERROR -> {
                        Column(modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            if(error.isNotBlank()){
                                Text(
                                    text = error,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.height(30.dp))
                            }
                            OutlinedTextField(
                                value = login,
                                onValueChange = {viewModel.handleIntent(LoginViewModel.Intent.LoginChanged(it))},
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                label = { Text(text = "Логин") }
                            )
                            OutlinedTextField(
                                value = password,
                                onValueChange = {viewModel.handleIntent(LoginViewModel.Intent.PasswordChanged(it))},
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                label = { Text(text = "Пароль") }
                            )
                            OutlinedButton(onClick = { viewModel.handleIntent(LoginViewModel.Intent.Login) }) {
                                Text(text = "Войти")
                            }
                            TextButton(onClick = { viewModel.handleIntent(LoginViewModel.Intent.Register) }) {
                                Text(text = "Нет аккаунта?\n Нажмите, чтобы зарегистрироваться", textAlign = TextAlign.Center)
                            }
                        }

                    }

                    ProcessState.LOADING -> {
                        CircularProgressIndicator(modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(20.dp))
                    }
                    ProcessState.SUCCESS -> {
                        LaunchedEffect(Unit) {
                            viewModel.handleIntent(LoginViewModel.Intent.LoginSuccess)
                        }
                    }
                }

            }
        }
}