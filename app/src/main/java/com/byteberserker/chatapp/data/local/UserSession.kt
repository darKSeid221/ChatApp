package com.byteberserker.chatapp.data.local

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserSession @Inject constructor() {
    val myUserId: Long = 100 // Hardcoded "Me"
    val myUserName: String = "Kamal" // Hardcoded Name
}
