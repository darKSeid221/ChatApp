package com.byteberserker.chatapp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.byteberserker.chatapp.ui.chat.ChatScreen
import com.byteberserker.chatapp.ui.chat.ChatViewModel
import dagger.hilt.android.AndroidEntryPoint

import androidx.compose.material3.MaterialTheme
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.hilt.navigation.compose.hiltViewModel

import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        installSplashScreen()
        
        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "chat_list") {
                    composable("chat_list") {
                        com.byteberserker.chatapp.ui.chat.ChatListScreen(
                            onChatClick = { chatId ->
                                navController.navigate("chat/$chatId")
                            }
                        )
                    }
                    composable(
                        "chat/{chatId}",
                        arguments = listOf(navArgument("chatId") { type = NavType.LongType })
                    ) {
                        val viewModel: ChatViewModel = hiltViewModel()
                        ChatScreen(
                            viewModel = viewModel,
                            onNavigateToChat = { chatId ->
                                navController.navigate("chat/$chatId")
                            }
                        )
                    }
                }
            }
        }
    }
}