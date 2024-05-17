package ru.gimaz.library.components.addauthor

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.gimaz.library.enums.LoadingObjectError
import ru.gimaz.library.enums.LoadingState
import ru.gimaz.library.enums.SavingState
import java.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAuthor(viewModel: AddAuthorViewModel) {
    val context = LocalContext.current
    val requiredFields by viewModel.requiredFields.collectAsState()
    val isEditMode by viewModel.isEditMode.collectAsState()
    val loadingState by viewModel.loadingState.collectAsState()
    val objectErrorState by viewModel.objectLoadingError.collectAsState()
    val savingState by viewModel.saveState.collectAsState()
    Scaffold(modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = { Text(text = "Добавление автора") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.handleIntent(AddAuthorViewModel.Intent.Back) }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.handleIntent(
                                AddAuthorViewModel.Intent.Save(
                                    context
                                )
                            )
                        },
                        enabled = requiredFields.isEmpty()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = ""
                        )
                    }
                })
        }
    )
    { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isEditMode) {
                when (loadingState) {
                    LoadingState.LOADING -> {
                        CircularProgressIndicator()
                    }

                    LoadingState.LOADED -> {

                        when (savingState) {
                            SavingState.IDLE, SavingState.ERROR -> {
                                Form(viewModel = viewModel)
                            }

                            SavingState.LOADING -> {
                                CircularProgressIndicator()
                            }

                            SavingState.SUCCESS -> {
                                LaunchedEffect(key1 = Unit) {
                                    viewModel.handleIntent(AddAuthorViewModel.Intent.Back)
                                }
                            }

                        }
                    }

                    LoadingState.ERROR -> {
                        Column(
                            Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            when (objectErrorState) {
                                LoadingObjectError.NOT_FOUND -> {
                                    Text(text = "Автор не найден")
                                }

                                LoadingObjectError.INTERNAL_ERROR, null -> {
                                    Text(text = "Что-то пошло не так")
                                }
                            }
                            OutlinedButton(onClick = { viewModel.handleIntent(AddAuthorViewModel.Intent.Back) }) {
                                Text(text = "Назад")
                            }
                        }
                    }
                }
            } else {
                when (savingState) {
                    SavingState.IDLE, SavingState.ERROR -> {
                        Form(viewModel = viewModel)
                    }

                    SavingState.LOADING -> {
                        CircularProgressIndicator()
                    }

                    SavingState.SUCCESS -> {
                        LaunchedEffect(key1 = Unit) {
                            viewModel.handleIntent(AddAuthorViewModel.Intent.Back)
                        }
                    }

                }
            }

        }


    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Form(viewModel: AddAuthorViewModel) {
    val picker =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
            uri?.let { uri ->
                viewModel.handleIntent(AddAuthorViewModel.Intent.ChangePhoto(uri))
            }
        }
    val firstname by viewModel.firstname.collectAsState()
    val lastname by viewModel.lastname.collectAsState()
    val surname by viewModel.surname.collectAsState()
    val dateOfBirth by viewModel.dateOfBirthday.collectAsState()
    val biography by viewModel.biography.collectAsState()
    val imageBitmap by viewModel.imagePath.collectAsState()
    val imageUri by viewModel.imageUri.collectAsState()
    val dateState = rememberDatePickerState(yearRange = IntRange(0, 2100))
    val datePickerVisibility = remember {
        MutableTransitionState(false)
    }
    val selectedDate = dateState.selectedDateMillis?.let {
        Instant.ofEpochMilli(it).atZone(java.time.ZoneId.systemDefault()).toLocalDate()
    }
    val interactionSource = remember { MutableInteractionSource() }

    val requiredFields by viewModel.requiredFields.collectAsState()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(top = 50.dp, start = 20.dp, end = 20.dp)
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
        ) {
            if (imageBitmap != null) {
                AsyncImage(
                    model = imageBitmap,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center)
                )
            } else {
                imageUri?.let {
                    AsyncImage(
                        model = it,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .align(Alignment.Center)
                    )
                }
            }
        }
        TextButton(onClick = { picker.launch("image/*") }) {
            Text(text = "Выбрать фото")
        }
        OutlinedTextField(value = lastname,
            onValueChange = {
                viewModel.handleIntent(AddAuthorViewModel.Intent.ChangeLastname(it))
            },
            label = { Text(text = "Фамилия") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            ),
            supportingText = {
                if (requiredFields.contains(AddAuthorViewModel.RequiredField.LASTNAME)) {
                    Text(text = "Поле обязательно для заполнения")
                }
            }
        )
        OutlinedTextField(value = firstname,
            onValueChange = {
                viewModel.handleIntent(AddAuthorViewModel.Intent.ChangeFirstname(it))
            },
            label = { Text(text = "Имя") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            ),
            supportingText = {
                if (requiredFields.contains(AddAuthorViewModel.RequiredField.FIRSTNAME)) {
                    Text(text = "Поле обязательно для заполнения")
                }
            }
        )
        OutlinedTextField(
            value = surname,
            onValueChange = {
                viewModel.handleIntent(AddAuthorViewModel.Intent.ChangeSurname(it))
            },
            label = { Text(text = "Отчество") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Done
            ),
        )
        OutlinedTextField(
            value = dateOfBirth?.toString() ?: "",
            onValueChange = { },
            label = { Text(text = "Дата рождения") },
            readOnly = true,
            enabled = false,
            modifier = Modifier.clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                datePickerVisibility.apply {
                    targetState = true
                }
            },
            colors = TextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledSupportingTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledPrefixColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledSuffixColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
            supportingText = {
                if (requiredFields.contains(AddAuthorViewModel.RequiredField.DATE_OF_BIRTHDAY)) {
                    Text(text = "Поле обязательно для заполнения")
                }
                Text(text = "Нажмите для выбора")
            }
        )
        OutlinedTextField(
            value = biography,
            modifier = Modifier,
            onValueChange = {
                viewModel.handleIntent(AddAuthorViewModel.Intent.ChangeBiography(it))
            },
            label = { Text(text = "Биография") },
            singleLine = false,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Done
            )
        )
    }
    AnimatedVisibility(
        visibleState = datePickerVisibility,
    ) {
        DatePickerDialog(onDismissRequest = {
            datePickerVisibility.apply {
                targetState = false
            }
        },
            confirmButton = {
                Button(onClick = {
                    viewModel.handleIntent(AddAuthorViewModel.Intent.ChangeDateBirthday(selectedDate))
                    datePickerVisibility.apply {
                        targetState = false
                    }
                }
                ) {
                    Text(text = "OK")
                }
            },
            dismissButton = {
                Button(onClick = {
                    datePickerVisibility.apply {
                        targetState = false
                    }
                }) {
                    Text(text = "Отмена")
                }
            }) {
            DatePicker(state = dateState)
        }
    }
}
