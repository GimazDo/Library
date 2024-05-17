package ru.gimaz.library.ui.cards

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.gimaz.library.db.Publisher

@Composable
fun PublisherCard(publisher: Publisher, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .padding(8.dp)
            .height(300.dp)
            .clickable(
                onClick = onClick
            )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .padding(5.dp),
                contentAlignment = Alignment.Center
            ) {
                if (publisher.logoPath != null) {
                    AsyncImage(
                        model = publisher.logoPath,
                        contentDescription = "publisher logo"
                    )
                } else {
                    Text(text = "Фото отсутствует", style = MaterialTheme.typography.bodySmall)
                }
            }
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = publisher.name,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelLarge
                )
                Text(text =publisher.description, style = MaterialTheme.typography.bodySmall,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,)
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "${publisher.yearOfFoundation}", style = MaterialTheme.typography.bodySmall)

            }
        }
    }
}