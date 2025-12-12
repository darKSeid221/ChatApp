package com.byteberserker.chatapp.data.remote

import okhttp3.*

class WebSocketClient {
    private var webSocket: WebSocket? = null
    private val client = OkHttpClient()
    
    private var listener: WebSocketListener? = null

    fun connect(url: String, listener: WebSocketListener) {
        close()
        this.listener = listener
        val request = Request.Builder().url(url).build()
        webSocket = client.newWebSocket(request, listener)
    }

    fun sendMessage(text: String): Boolean {
        return webSocket?.send(text) ?: false
    }

    fun close() {
        webSocket?.close(1000, "App closed")
        webSocket = null
    }
}
