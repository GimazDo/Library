package ru.gimaz.library.components.addpublisher

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.gimaz.library.enums.LoadingObjectError
import ru.gimaz.library.enums.LoadingState
import ru.gimaz.library.enums.ProcessState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPublisher(viewModel: AddPublisherViewModel) {
    val context = LocalContext.current
    val requiredFields by viewModel.requiredFields.collectAsState()
    val isEditMode by viewModel.isEditMode.collectAsState()
    val loadingState by viewModel.loadingState.collectAsState()
    val objectErrorState by viewModel.objectLoadingError.collectAsState()
    Scaffold(modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    if (isEditMode) {
                        Text(text = "Редактирование издательства")
                    } else {
                        Text(text = "Добавление издательства")
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.handleIntent(AddPublisherViewModel.Intent.Back) },

                        ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.handleIntent(AddPublisherViewModel.Intent.Save(context))
                    }, enabled = requiredFields.isEmpty()) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = ""
                        )
                    }
                })
        }
    )
    { paddingValues ->

        val savingState by viewModel.saveState.collectAsState()
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
                            ProcessState.IDLE, ProcessState.ERROR -> {
                                Form(viewModel = viewModel)
                            }

                            ProcessState.LOADING -> {
                                CircularProgressIndicator()
                            }

                            ProcessState.SUCCESS -> {
                                LaunchedEffect(key1 = Unit) {
                                    viewModel.handleIntent(AddPublisherViewModel.Intent.Back)
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
                                    Text(text = "Издательство не найдено")
                                }

                                LoadingObjectError.INTERNAL_ERROR, null -> {
                                    Text(text = "Что-то пошло не так")
                                }
                            }
                            OutlinedButton(onClick = { viewModel.handleIntent(AddPublisherViewModel.Intent.Back) }) {
                                Text(text = "Назад")
                            }
                        }
                    }
                }
            } else {
                when (savingState) {
                    ProcessState.IDLE, ProcessState.ERROR -> {
                        Form(viewModel = viewModel)
                    }

                    ProcessState.LOADING -> {
                        CircularProgressIndicator()
                    }

                    ProcessState.SUCCESS -> {
                        LaunchedEffect(key1 = Unit) {
                            viewModel.handleIntent(AddPublisherViewModel.Intent.Back)
                        }
                    }

                }
            }
        }
    }
}

@Composable
private fun Form(viewModel: AddPublisherViewModel) {
    val savingState by viewModel.saveState.collectAsState()
    val name by viewModel.name.collectAsState()
    val picker =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
            uri?.let { uri ->
                viewModel.handleIntent(AddPublisherViewModel.Intent.LogoChanged(uri))
            }
        }
    val description by viewModel.description.collectAsState()
    val address by viewModel.address.collectAsState()
    val email by viewModel.email.collectAsState()
    val website by viewModel.website.collectAsState()
    val logo by viewModel.logo.collectAsState()
    val logoBitmap by viewModel.logoPath.collectAsState()
    val yearOfFoundation by viewModel.yearOfFoundation.collectAsState()

    val requiredFields by viewModel.requiredFields.collectAsState()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(top = 50.dp, start = 20.dp, end = 20.dp)
            .fillMaxWidth()
    ) {
        if (savingState == ProcessState.ERROR) {
            Text(text = "Ошибка при сохранении")
            Spacer(modifier = Modifier.height(20.dp))
        }
        Box(modifier = Modifier.size(100.dp)) {
            if (logoBitmap != null) {
                AsyncImage(
                    model = logoBitmap,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center)
                )
            } else {
                logo?.let {
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
            Text(text = "Выбрать лого")
        }
        OutlinedTextField(
            value = name,
            onValueChange = {
                viewModel.handleIntent(AddPublisherViewModel.Intent.NameChanged(it))
            },
            label = { Text(text = "Название") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Next
            ),
            supportingText = {
                if (requiredFields.contains(AddPublisherViewModel.RequiredField.NAME)) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Поле обязательно для заполнения",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )
        OutlinedTextField(
            value = address,
            onValueChange = {
                viewModel.handleIntent(AddPublisherViewModel.Intent.AddressChanged(it))
            },
            label = { Text(text = "Адрес") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Next
            ),
        )
        OutlinedTextField(
            value = website,
            onValueChange = {
                viewModel.handleIntent(AddPublisherViewModel.Intent.WebsiteChanged(it))
            },
            label = { Text(text = "Сайт") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
        )
        OutlinedTextField(
            value = email,
            onValueChange = {
                viewModel.handleIntent(AddPublisherViewModel.Intent.EmailChanged(it))
            },
            label = { Text(text = "Электронная почта") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
        )
        OutlinedTextField(
            value = yearOfFoundation?.toString() ?: "",
            onValueChange = {
                viewModel.handleIntent(AddPublisherViewModel.Intent.YearOfFoundationChanged(it))
            },
            label = { Text(text = "Год основания") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Number
            ),
            supportingText = {
                if (requiredFields.contains(AddPublisherViewModel.RequiredField.DESCRIPTION)) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Поле обязательно для заполнения",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )
        OutlinedTextField(
            value = description,
            modifier = Modifier,
            onValueChange = {
                viewModel.handleIntent(AddPublisherViewModel.Intent.DescriptionChanged(it))
            },
            label = { Text(text = "Описание") },
            singleLine = false,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Done
            ),
            supportingText = {
                if (requiredFields.contains(AddPublisherViewModel.RequiredField.DESCRIPTION)) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Поле обязательно для заполнения",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )

    }
}
