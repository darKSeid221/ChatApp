package com.byteberserker.chatapp

import android.os.Bundle
import androidx.activity.compose.setContent
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
import javax.inject.Inject
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.byteberserker.chatapp.domain.repository.ChatRepository

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var repository: ChatRepository

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

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) {
            lifecycleScope.launch(Dispatchers.IO) {
                repository.clearAllData()
            }
        }
    }
}