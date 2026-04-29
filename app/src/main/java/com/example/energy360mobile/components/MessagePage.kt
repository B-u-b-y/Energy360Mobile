package com.example.energy360mobile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.energy360mobile.R

data class Message(
    val sender: String,
    val content: String,
    val time: String,
    val isUnread: Boolean = false
)

//Message("Staff Energy360", "Il tuo abbonamento è in scadenza. Rinnova ora!", "10:30", true),
//Message("Marco Rossi", "Ciao! Ci vediamo per l'allenamento di domani?", "09:15", true),
//Message("Segreteria", "Confermato l'Open Day per sabato 15.", "Ieri"),
//Message("Coach Giulia", "Ottimi progressi nell'ultima sessione!", "Ieri"),
//Message("Sistema", "Aggiornamento termini e condizioni del servizio.", "2gg fa")

@Composable
fun MessagePage(rootNavController: NavHostController){

    val messages = listOf<Message>(
        Message("Staff Energy360", "Il tuo abbonamento è in scadenza. Rinnova ora!", "10:30", true),
        Message("Marco Rossi", "Ciao! Ci vediamo per l'allenamento di domani?", "09:15", true),
        Message("Segreteria", "Confermato l'Open Day per sabato 15.", "Ieri"),
        Message("Coach Giulia", "Ottimi progressi nell'ultima sessione!", "Ieri"),
        Message("Sistema", "Aggiornamento termini e condizioni del servizio.", "2gg fa")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        //top rounded box (big-one)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primaryContainer
                        )
                    ),
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                )
                .padding(24.dp)
        ) {
            Column(modifier = Modifier.align(Alignment.CenterStart)) {
                Text(
                    text = stringResource(id = R.string.message),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp
                    )
                )
            }
        }
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (messages.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.NotificationsNone,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            stringResource(id = R.string.no_new_messages),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                messages.forEach { message ->
                    ChatCard(message, onChatClick = {
                        rootNavController.navigate("chat_detail/${message.sender}")
                    })
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun ChatCard(message: Message, onChatClick: (String) -> Unit){
    Card(
        onClick = { onChatClick(message.sender) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(0.2f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .weight(1f)
            ) {
                Text(
                    text = message.sender,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = " " + message.content,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

