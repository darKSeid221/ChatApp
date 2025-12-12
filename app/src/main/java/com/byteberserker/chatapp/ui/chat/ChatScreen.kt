package com.byteberserker.chatapp.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.byteberserker.chatapp.domain.model.ChatMessage
import com.byteberserker.chatapp.domain.model.MessageStatus


@Composable
fun ChatScreen(viewModel: ChatViewModel) {
    val messages by viewModel.messages.collectAsState()
    val isOnline by viewModel.networkStatusTracker.networkStatus.collectAsState(initial = false)
    val isOnlineText = if (isOnline) "Online" else "Offline"
    ChatScreenContent(
        messages = messages,
        connectionStatus = isOnlineText,
        onSendMessage = viewModel::sendMessage
    )
}

@Composable
fun ChatScreenContent(
    messages: List<ChatMessage>,
    connectionStatus: String,
    onSendMessage: (String) -> Unit
) {
    var inputText by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(verticalAlignment = Alignment.CenterVertically , modifier =  Modifier.padding(16.dp)) {
            Text(
                text = "ChatApp",
                style = MaterialTheme.typography.titleMedium
            )
            RedGreenIcon(connectionStatus)
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            reverseLayout = true
        ) {
            items(messages) { message ->
                ChatBubble(message)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type a message...") }
            )
            IconButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        onSendMessage(inputText)
                        inputText = ""
                    }
                }
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    val dummyMessages = listOf(
        ChatMessage(1, "Hello!", 0L, false, MessageStatus.RECEIVED),
        ChatMessage(2, "Hi there!", 0L, true, MessageStatus.SENT),
        ChatMessage(3, "How are you?", 0L, false, MessageStatus.RECEIVED)
    )
    MaterialTheme {
        ChatScreenContent(
            messages = dummyMessages,
            connectionStatus = "Online",
            onSendMessage = {}
        )
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val isMe = message.isSentByMe
    val alignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart
    val color =
        if (isMe) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer

    Box(modifier = Modifier.fillMaxWidth()) {
        Card(
            colors = CardDefaults.cardColors(containerColor = color),
            modifier = Modifier
                .align(alignment)
                .padding(4.dp)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(text = message.text)
                Text(
                    text = message.status.name,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

@Composable
fun RedGreenIcon(connectionStatus: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        val color: Color = if (connectionStatus == "Online") {
            Color.Green
        } else {
            Color.Red
        }
        Box(
            modifier = Modifier
                .padding(4.dp)
                .size(12.dp)
                .background(color, shape = CircleShape)
        )
    }
}
