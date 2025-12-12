package com.byteberserker.chatapp.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.hilt.navigation.compose.hiltViewModel
import com.byteberserker.chatapp.domain.model.Chat
import com.byteberserker.chatapp.domain.model.ChatMessage
import com.byteberserker.chatapp.domain.model.MessageStatus
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.runtime.DisposableEffect

@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    onNavigateToChat: (Long) -> Unit
) {
    val messages by viewModel.messages.collectAsState(initial = emptyList())
    val chat by viewModel.chat.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()
    DisposableEffect(Unit) {
        viewModel.onResume()
        onDispose {
            viewModel.onPause()
        }
    }
    val snackbarHostState = remember { SnackbarHostState() }
    
    val chatListViewModel: ChatListViewModel = hiltViewModel()
    val chats by chatListViewModel.chats.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.errors.collect { error ->
            snackbarHostState.showSnackbar(error)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                chats?.let {
                    ChatListContent(
                        chats = it,
                        onChatClick = { chatId ->
                            scope.launch {
                                drawerState.close()
                                onNavigateToChat(chatId)
                            }
                        },
                        onDeleteClick = chatListViewModel::deleteChat
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                 ChatTopBar(
                     chatName = chat?.name ?: "Chat",
                     isOnline = isOnline,
                     onOpenDrawer = { scope.launch { drawerState.open() } }
                 )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { paddingValues ->
            ChatScreenContent(
                messages = messages,
                onSendMessage = viewModel::sendMessage,
                modifier = Modifier.padding(paddingValues),
                isOnline = isOnline
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTopBar(
    chatName: String,
    isOnline: Boolean,
    onOpenDrawer: () -> Unit
) {
    Column {
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.padding(8.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = chatName,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = if (isOnline) "Online" else "Offline",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isOnline) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
                        )
                    }
                }
            },
            navigationIcon = {
                IconButton(onClick = onOpenDrawer) {
                    Icon(Icons.Default.Menu, contentDescription = "Menu")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.onSurface
            )
        )
        HorizontalDivider()
        if (!isOnline) {
             Surface(
                color = MaterialTheme.colorScheme.errorContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "No internet connection",
                    modifier = Modifier.padding(8.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

@Composable
fun ChatScreenContent(
    messages: List<ChatMessage>,
    onSendMessage: (String) -> Unit,
    modifier: Modifier = Modifier,
    isOnline: Boolean
) {
    var inputText by remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxSize()) {
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
                placeholder = { Text("Type a message...") },
                enabled = isOnline || true
            )
            IconButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        onSendMessage(inputText)
                        inputText = ""
                    }
                }
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    val dummyMessages = listOf(
        ChatMessage(1L, "Hello!", 0L, false, MessageStatus.RECEIVED),
        ChatMessage(2L, "Hi there!", 0L, true, MessageStatus.SENT),
        ChatMessage(3L, "How are you?", 0L, false, MessageStatus.RECEIVED)
    )
    MaterialTheme {
        ChatScreenContent(
            messages = dummyMessages,
            onSendMessage = {},
            isOnline = true
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
                if (!isMe && !message.senderName.isNullOrEmpty()) {
                    Text(
                        text = message.senderName,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
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
