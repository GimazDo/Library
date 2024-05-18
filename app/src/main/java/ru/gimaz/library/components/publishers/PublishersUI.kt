package ru.gimaz.library.components.publishers

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.gimaz.library.BottomBar
import ru.gimaz.library.db.Publisher
import ru.gimaz.library.ui.cards.PublisherCard

@Composable
fun Publishers(viewModel: PublishersViewModel) {
    Scaffold(
        bottomBar = { BottomBar(navController = viewModel.navController) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val searchString by viewModel.searchFlow.collectAsState()
                val authors by viewModel.publishersFlow.collectAsState()
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(10.dp)
                ) {
                    OutlinedTextField(value = searchString,
                        onValueChange = {
                            viewModel.handleIntent(
                                PublishersViewModel.Intent.Search(
                                    it
                                )
                            )
                        },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text(text = "Начните вводить для поиска") }
                    )
                }
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    items(authors) {
                        PublisherCard(it,
                            onClick = {
                                viewModel.handleIntent(
                                    PublishersViewModel.Intent.Open(
                                        it
                                    )
                                )
                            })
                    }
                }
            }
            FloatingActionButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(15.dp),
                onClick = { viewModel.handleIntent(PublishersViewModel.Intent.AddPublisher) },
            ) {
                Icon(Icons.Filled.Add, "Floating action button.")
            }
        }
    }
}

