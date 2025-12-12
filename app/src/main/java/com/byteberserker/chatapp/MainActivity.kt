package com.byteberserker.chatapp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.byteberserker.chatapp.ui.chat.ChatScreen
import com.byteberserker.chatapp.ui.chat.ChatViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: ChatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            androidx.compose.material3.MaterialTheme {
                ChatScreen(viewModel = viewModel )
            }
        }
    }
}