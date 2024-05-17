package ru.gimaz.library.components.author

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.gimaz.library.components.book.BookViewModel
import ru.gimaz.library.enums.LoadingObjectError
import ru.gimaz.library.enums.LoadingState
import ru.gimaz.library.ui.cards.AuthorCard
import ru.gimaz.library.ui.cards.BookCard
import ru.gimaz.library.ui.cards.PublisherCard
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Author(viewModel: AuthorViewModel) {
    val author by viewModel.author.collectAsState()
    val loadingState by viewModel.loadingState.collectAsState()
    val objectErrorState by viewModel.objectErrorState.collectAsState()
    var showDeleteAlert by remember {
        mutableStateOf(false)
    }
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { viewModel.handleIntent(AuthorViewModel.Intent.Back) }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                title = {
                    Text(
                        text = author?.let { "${it.lastName} ${it.firstName} ${it.surname}" }
                            ?: "",
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                    )
                },
                actions = {
                    IconButton(onClick = { viewModel.handleIntent(AuthorViewModel.Intent.Edit) }) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = null)
                    }
                    IconButton(onClick = { showDeleteAlert = true }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (loadingState) {
                LoadingState.LOADING -> {
                    CircularProgressIndicator()
                }

                LoadingState.LOADED -> {
                    Content(viewModel)
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
                        OutlinedButton(onClick = { viewModel.handleIntent(AuthorViewModel.Intent.Back) }) {
                            Text(text = "Назад")
                        }
                    }
                }
            }
            if (showDeleteAlert) {
                AlertDialog(
                    onDismissRequest = {
                        showDeleteAlert = false
                    },
                    title = {
                        Text(text = "Удалить автора")
                    },
                    text = {
                        Text(text = "Вы действительно хотите удалить Автора? Также удаляться все его книги!")
                    },
                    confirmButton = {
                        TextButton(onClick = { viewModel.handleIntent(AuthorViewModel.Intent.Delete) }) {
                            Text(text = "Удалить")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteAlert = false }) {
                            Text(text = "Отмена")
                        }
                    }
                )
            }
        }

    }

}

@Composable
private fun Content(viewModel: AuthorViewModel) {
    val books by viewModel.books.collectAsState()
    val author by viewModel.author.collectAsState()
    author?.let {
        LazyVerticalGrid(columns = GridCells.Fixed(3), modifier = Modifier.fillMaxWidth()) {
            item(span = { GridItemSpan(3) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    if (it.photoPath != null) {
                        AsyncImage(
                            model = it.photoPath,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text(text = "Нет фотографии")
                    }
                }
            }
            item(span = { GridItemSpan(3) }) {
                Text(
                    text =  "${it.lastName} ${it.firstName} ${it.surname}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    style = MaterialTheme.typography.headlineMedium
                )
            }
            item(span = { GridItemSpan(3) }) {
                Text(
                    text = "Дата рождения: ${it.dateOfBirth.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            item(span = { GridItemSpan(3) }) {
                Text(
                    text = it.biography,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            items(books, key = {it.id!!}){
                BookCard(it){
                    viewModel.handleIntent(AuthorViewModel.Intent.OpenBook(it.id!!))
                }
            }

        }

    }
}