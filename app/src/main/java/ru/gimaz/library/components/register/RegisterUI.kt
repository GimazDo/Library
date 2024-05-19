package ru.gimaz.library.components.register

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.gimaz.library.components.addbook.AddBookViewModel
import ru.gimaz.library.enums.ProcessState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Register(viewModel: RegisterViewModel) {
    val state by viewModel.registerState.collectAsState()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = {
                Text(text = "Регистрация")
            })
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (state) {
                ProcessState.IDLE, ProcessState.ERROR -> {
                    RegisterForm(viewModel)
                }

                ProcessState.LOADING -> {
                    CircularProgressIndicator(
                        Modifier
                            .fillMaxSize(0.5f)
                            .padding(top = 30.dp)
                            .align(Alignment.TopCenter)
                    )
                }

                ProcessState.SUCCESS -> {
                    LaunchedEffect(key1 = Unit) {
                        viewModel.handleIntent(RegisterViewModel.Intent.RegisterSuccess)
                    }
                }
            }
        }
    }
}

@Composable
private fun RegisterForm(viewModel: RegisterViewModel) {
    val context = LocalContext.current
    val firstName by viewModel.firstName.collectAsState()
    val middleName by viewModel.middleName.collectAsState()
    val lastName by viewModel.lastName.collectAsState()
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val confirmPassword by viewModel.confirmPassword.collectAsState()
    val login by viewModel.login.collectAsState()
    val photoUri by viewModel.photoUri.collectAsState()
    val isFirstRegister by viewModel.isFirstRegister.collectAsState()
    val requiredFields by viewModel.requiredFields.collectAsState()
    val picker =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
            uri?.let { uri ->
                viewModel.handleIntent(RegisterViewModel.Intent.PhotoUriChanged(uri))
            }
        }
    val isPasswordCorrect = remember(password, confirmPassword) {
        if (password.isNotEmpty() && confirmPassword.isNotEmpty()) {
            password == confirmPassword
        } else {
            true
        }
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isFirstRegister) {
            item {
                Text(
                    text = "Происходит регистрация первого аккаунта. Данному аккаунту автоматически будут выданые права администратора",
                    modifier = Modifier.padding(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall

                )
            }
        }
        item {
            Box(modifier = Modifier.size(100.dp)) {
                photoUri?.let {
                    AsyncImage(
                        model = it,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .align(Alignment.Center)
                    )
                }
            }
            TextButton(onClick = { picker.launch("image/*") }) {
                Text(text = "Выбрать аватар")
            }
        }
        item {
            OutlinedTextField(
                value = lastName, onValueChange = {
                    viewModel.handleIntent(RegisterViewModel.Intent.LastNameChanged(it))
                },
                singleLine = true,
                label = {
                    Text(text = "Фамилия")
                },
                supportingText = {
                    if (requiredFields.contains(RegisterViewModel.RequiredField.LAST_NAME)) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Поле обязательно для заполнения",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                )
            )
        }
        item {
            OutlinedTextField(
                value = firstName, onValueChange = {
                    viewModel.handleIntent(RegisterViewModel.Intent.FirstNameChanged(it))
                },
                singleLine = true,
                label = {
                    Text(text = "Имя")
                },
                supportingText = {
                    if (requiredFields.contains(RegisterViewModel.RequiredField.FIRST_NAME)) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Поле обязательно для заполнения",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                )
            )
        }
        item {
            OutlinedTextField(
                value = middleName, onValueChange = {
                    viewModel.handleIntent(RegisterViewModel.Intent.MiddleNameChanged(it))
                },
                singleLine = true,
                label = {
                    Text(text = "Отчество")
                },
                supportingText = {
                    if (requiredFields.contains(RegisterViewModel.RequiredField.MIDDLE_NAME)) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Поле обязательно для заполнения",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                )
            )
        }
        item {
            OutlinedTextField(
                value = email,
                onValueChange = {
                    viewModel.handleIntent(RegisterViewModel.Intent.EmailChanged(it))
                },
                singleLine = true,
                label = {
                    Text(text = "Электронная почта")
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                supportingText = {
                    if (requiredFields.contains(RegisterViewModel.RequiredField.EMAIL)) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Поле обязательно для заполнения",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
            )
        }
        item {
            OutlinedTextField(value = login, onValueChange =
            {
                viewModel.handleIntent(RegisterViewModel.Intent.LoginChanged(it))
            },
                singleLine = true,
                label = {
                    Text(text = "Логин")
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                supportingText = {
                    if (requiredFields.contains(RegisterViewModel.RequiredField.LOGIN)) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Поле обязательно для заполнения",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        }
        item {
            OutlinedTextField(
                value = password, onValueChange = {
                    viewModel.handleIntent(RegisterViewModel.Intent.PasswordChanged(it))
                },
                singleLine = true,
                label = {
                    Text(text = "Пароль")
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Password
                ),
                visualTransformation = PasswordVisualTransformation(),
                supportingText = {
                    if (requiredFields.contains(RegisterViewModel.RequiredField.PASSWORD)) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Поле обязательно для заполнения",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        }

        item {
            OutlinedTextField(
                value = confirmPassword, onValueChange = {
                    viewModel.handleIntent(RegisterViewModel.Intent.ConfirmPasswordChanged(it))
                },
                singleLine = true,
                label = {
                    Text(text = "Повторите пароль")
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Password
                ),
                visualTransformation = PasswordVisualTransformation(),
                supportingText = {
                    if (requiredFields.contains(RegisterViewModel.RequiredField.PASSWORD)) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Поле обязательно для заполнения",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    if (!isPasswordCorrect) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Пароли не совпадают",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        }
        item {
            OutlinedButton(
                onClick = {
                    viewModel.handleIntent(RegisterViewModel.Intent.Register(context))
                },
                enabled = isPasswordCorrect
            ) {
                Text(text = "Зарегистрироваться")
            }
        }
        item {
            Spacer(modifier = Modifier.height(300.dp))
        }


    }
}
