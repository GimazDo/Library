package ru.gimaz.library.components.filters

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController

@Composable
fun Filters(filtersViewModel: FiltersViewModel){
    Column {
        Column(modifier = Modifier.fillMaxSize().background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Column(modifier = Modifier.weight(1f)) {

            }
            Button(onClick = {

            }) {
                Text(text = "Применить")
            }
        }
    }
}

@Composable
fun rememberFiltersViewModel(navController: NavController): FiltersViewModel = remember {
    FiltersViewModel()
}
