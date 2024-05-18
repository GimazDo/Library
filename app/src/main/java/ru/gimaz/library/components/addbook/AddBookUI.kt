package ru.gimaz.library.components.addbook

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import ru.gimaz.library.enums.LoadingObjectError
import ru.gimaz.library.enums.LoadingState
import ru.gimaz.library.enums.ProcessState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBook(viewModel: AddBookViewModel) {
    val context = LocalContext.current
    val requiredFields by viewModel.requiredFields.collectAsState()
    val isEditMode by viewModel.isEditMode.collectAsState()
    val loadingState by viewModel.loadingState.collectAsState()
    val objectErrorState by viewModel.objectLoadingError.collectAsState()
    val savingState by viewModel.savingState.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (isEditMode) {
                        Text(text = "Редактирование книги")
                    } else {
                        Text(text = "Добавление книги")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.handleIntent(AddBookViewModel.Intent.Back) }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.handleIntent(AddBookViewModel.Intent.Save(context)) },
                        enabled = requiredFields.isEmpty()
                        ) {
                        Icon(imageVector = Icons.Filled.Check, contentDescription = "")
                    }
                })
        }
    ) { paddingValues ->
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
                                AddBookForm(viewModel = viewModel)
                            }

                            ProcessState.LOADING -> {
                                CircularProgressIndicator()
                            }

                            ProcessState.SUCCESS -> {
                                LaunchedEffect(key1 = Unit) {
                                    viewModel.handleIntent(AddBookViewModel.Intent.Back)
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
                            OutlinedButton(onClick = { viewModel.handleIntent(AddBookViewModel.Intent.Back) }) {
                                Text(text = "Назад")
                            }
                        }
                    }
                }
            } else {
                when (savingState) {
                    ProcessState.IDLE, ProcessState.ERROR -> {
                        AddBookForm(viewModel = viewModel)
                    }

                    ProcessState.LOADING -> {
                        CircularProgressIndicator()
                    }

                    ProcessState.SUCCESS -> {
                        LaunchedEffect(key1 = Unit) {
                            viewModel.handleIntent(AddBookViewModel.Intent.Back)
                        }
                    }

                }
            }
        }
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddBookForm(viewModel: AddBookViewModel) {
    val title by viewModel.title.collectAsState()
    val year by viewModel.publishYear.collectAsState()
    val description by viewModel.description.collectAsState()
    val author by viewModel.author.collectAsState()
    val publisher by viewModel.publisher.collectAsState()

    val authors by viewModel.authors.collectAsState()
    val publishers by viewModel.publishers.collectAsState()

    var showAuthorPicker by remember { mutableStateOf(false) }
    val authorModalState = rememberModalBottomSheetState()
    var showPublisherPicker by remember { mutableStateOf(false) }
    val publisherModalState = rememberModalBottomSheetState()

    val pages by viewModel.pages.collectAsState()
    val imageUri by viewModel.imageUri.collectAsState()
    val imageBitmap by viewModel.imagePath.collectAsState()

    val requiredFields by viewModel.requiredFields.collectAsState()


    val picker =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
            uri?.let { uri ->
                viewModel.handleIntent(AddBookViewModel.Intent.ChangedPhoto(uri))
            }
        }
    val scope = rememberCoroutineScope()

    val interactionSource = remember { MutableInteractionSource() }
    Column(modifier = Modifier.padding(10.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.size(100.dp)) {
            if (imageUri != null) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center)
                )
            } else {
                imageBitmap?.let {
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
            value = title,
            onValueChange = {
                viewModel.handleIntent(AddBookViewModel.Intent.ChangedTitle(it))
            },
            label = { Text(text = "Название") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            ),
            supportingText = {
                if (requiredFields.contains(AddBookViewModel.RequiredField.TITLE)) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Поле обязательно для заполнения",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )

        OutlinedTextField(
            value = year?.toString() ?: "",
            onValueChange = {
                viewModel.handleIntent(AddBookViewModel.Intent.ChangedPublishYear(it))
            },
            label = { Text(text = "Год издания") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            supportingText = {
                if (requiredFields.contains(AddBookViewModel.RequiredField.PUBLISH_YEAR)) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Поле обязательно для заполнения",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )
        OutlinedTextField(
            value = pages?.toString() ?: "",
            onValueChange = {
                viewModel.handleIntent(AddBookViewModel.Intent.ChangedPage(it))
            },
            label = { Text(text = "Кол-во страниц") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            ),
            supportingText = {
                if (requiredFields.contains(AddBookViewModel.RequiredField.PAGES)) {
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
            onValueChange = {
                viewModel.handleIntent(AddBookViewModel.Intent.ChangedDescription(it))
            },
            label = { Text(text = "Описание") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Done
            ),
            supportingText = {
                if (requiredFields.contains(AddBookViewModel.RequiredField.DESCRIPTION)) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Поле обязательно для заполнения",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )
        OutlinedTextField(
            value = if (author == null) "" else "${author!!.lastName} ${author!!.firstName} ${author!!.surname}",
            onValueChange = { },
            label = { Text(text = "Автор") },
            readOnly = true,
            enabled = false,
            modifier = Modifier.clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                showAuthorPicker = true
                scope.launch {
                    authorModalState.show()
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
            supportingText = { Text(text = "Нажмите для выбора") }
        )
        OutlinedTextField(
            value = if (publisher == null) "" else publisher!!.name,
            onValueChange = { },
            label = { Text(text = "Издательство") },
            readOnly = true,
            enabled = false,
            modifier = Modifier.clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                showPublisherPicker = true
                scope.launch {
                    publisherModalState.show()
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
            supportingText = { Text(text = "Нажмите для выбора") }
        )
    }

    if (showPublisherPicker) {
        Picker(header = "Выберите издательство",
            state = publisherModalState,
            onPicked = { viewModel.handleIntent(AddBookViewModel.Intent.ChangedPublisher(it)) },
            closeSheet = {
                showPublisherPicker = false
            }, items = publishers, key = { it.id!! }) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(5.dp))
            ) {
                AsyncImage(model = it.logoPath, contentDescription = "")
            }
            Text(text = it.name)
        }
    }
    if (showAuthorPicker) {
        Picker(header = "Выберите автора",
            state = authorModalState,
            onPicked = { viewModel.handleIntent(AddBookViewModel.Intent.ChangedAuthor(it)) },
            closeSheet = {
                showAuthorPicker = false
            }, items = authors, key = { it.id!! }) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(5.dp))
            ) {
                AsyncImage(model = it.photoPath, contentDescription = "")
            }
            Spacer(Modifier.width(10.dp))
            Text(text = " ${it.lastName} ${it.firstName} ${it.surname}")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> Picker(
    header: String,
    state: SheetState,
    onPicked: (T) -> Unit,
    closeSheet: () -> Unit,
    items: List<T>,
    key: ((T) -> Any),
    content: @Composable RowScope.(T) -> Unit,
) {
    val scope = rememberCoroutineScope()
    ModalBottomSheet(
        onDismissRequest = { closeSheet() },
        sheetState = state,
        dragHandle = {
            Text(text = header)
        },
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )
            {
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 15.dp)
                ) {
                    itemsIndexed(items = items, key = { _, item -> key(item) }) { index, item ->
                        Row(
                            modifier = Modifier
                                .height(50.dp)
                                .fillMaxWidth()
                                .clickable {
                                    onPicked(item)
                                    scope
                                        .launch {
                                            state.hide()
                                        }
                                        .invokeOnCompletion {
                                            closeSheet()
                                        }
                                },
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            content(item)
                        }
                        if (index < items.size - 1) {
                            Divider(modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
                Spacer(modifier = Modifier.height(15.dp))
            }
        }
    }
}