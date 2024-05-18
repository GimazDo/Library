package ru.gimaz.library.components.loading

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.gimaz.library.enums.LoadingState

@Composable
fun AppLoading(viewModel: AppLoadingViewModel){
    val loadingState by viewModel.loadingState.collectAsState()
    Scaffold(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(it)){
            when (loadingState) {
                LoadingState.LOADING -> {
                    CircularProgressIndicator(
                        Modifier
                            .fillMaxSize(0.5f)
                            .padding(top = 30.dp)
                            .align(Alignment.TopCenter))
                }
                LoadingState.LOADED -> {
                    LaunchedEffect(Unit) {
                        viewModel.handleIntent(AppLoadingViewModel.Intent.Navigate)
                    }
                }
                LoadingState.ERROR -> TODO()
            }
        }
    }
}