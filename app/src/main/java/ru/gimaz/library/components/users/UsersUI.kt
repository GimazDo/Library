package ru.gimaz.library.components.users

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Users(viewModel: UsersViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Пользователи")
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.handleIntent(UsersViewModel.Intent.Back) }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                    }
                })
        }
    ) { paddingValues ->
        val users by viewModel.users.collectAsState()
        val admins by viewModel.admins.collectAsState()
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                item {
                    Text(text = "Администраторы")
                    Spacer(modifier = Modifier.height(5.dp))
                    Divider(modifier = Modifier.fillMaxWidth())
                }
                items(admins, key = { it.id!! }) { admin ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .background(Color.Gray)
                        ) {
                            admin.photoPath?.let {
                                AsyncImage(
                                    model = it,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "${admin.lastName} ${admin.firstName} ${admin.middleName}")
                        Spacer(modifier = Modifier.weight(1f))
                        if (admin.id != viewModel.userId) {
                            IconButton(onClick = {
                                viewModel.handleIntent(
                                    UsersViewModel.Intent.UnsetAdmin(
                                        admin.id!!
                                    )
                                )
                            }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "")
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    Divider(modifier = Modifier.fillMaxWidth())
                }
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
                item {
                    Text(text = "Пользователи")
                    Spacer(modifier = Modifier.height(5.dp))
                    Divider(modifier = Modifier.fillMaxWidth())
                }
                items(users, key = { it.id!! }) { user ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .background(Color.Gray)
                        ) {
                            user.photoPath?.let {
                                AsyncImage(
                                    model = it,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "${user.lastName} ${user.firstName} ${user.middleName}")
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(onClick = {
                            viewModel.handleIntent(
                                UsersViewModel.Intent.SetAdmin(
                                    user.id!!
                                )
                            )
                        }) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "")
                        }
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    Divider(modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}