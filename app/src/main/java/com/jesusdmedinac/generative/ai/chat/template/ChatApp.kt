package com.jesusdmedinac.generative.ai.chat.template

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

data class Message(val id: Int, val author: String, val body: String)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatApp() {
    val chatAPIImpl = remember {
        ChatAPIImpl()
    }
    var currentMessage by remember { mutableStateOf(TextFieldValue("")) }
    val messages = remember {
        mutableStateListOf<Message>()
    }
    val coroutineScope = rememberCoroutineScope()
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                reverseLayout = true
            ) {
                items(messages.reversed(), key = { it.id }) { message ->
                    MessageBox(
                        message,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .animateItemPlacement()
                    )
                }
            }
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                TextField(
                    value = currentMessage,
                    onValueChange = {
                        currentMessage = it
                    },
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text(text = "Message")
                    })
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    val newMessage = Message(messages.size + 1, "user", currentMessage.text)
                    messages += newMessage
                    currentMessage = TextFieldValue("")
                    coroutineScope.launch {
                        val response = chatAPIImpl.sendMessage(messages)
                        messages += response
                    }
                }) {
                    Text(text = "Send")
                }
            }
        }
    }
}

@Composable
fun MessageBox(
    message: Message,
    modifier: Modifier = Modifier
) {
    var isVisible by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(message) {
        isVisible = true
    }
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        if (message.author == "user") {
            UserMessageBox(
                message,
                modifier = modifier
            )
        } else {
            ModelMessageBox(
                message,
                modifier = modifier
            )
        }
    }
}

@Composable
fun UserMessageBox(
    message: Message,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = message.body,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .padding(start = 16.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.primary)
                .padding(8.dp)
        )
    }
}

@Composable
fun ModelMessageBox(
    message: Message,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
    ) {
        Text(
            text = message.body,
            color = MaterialTheme.colorScheme.onSecondary,
            modifier = Modifier
                .padding(end = 16.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.secondary)
                .padding(8.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}