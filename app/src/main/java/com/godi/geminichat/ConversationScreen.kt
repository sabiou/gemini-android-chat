package com.godi.geminichat

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ConversationScreen(
    chatViewModel: ChatViewModel,
    conversations: SnapshotStateList<Pair<String, String>>,
) {
    @Composable
    fun ComposerBar(
        onSendClick: () -> Unit,
        text: String,
        onTextChange: (String) -> Unit,
        modifier: Modifier = Modifier
    ) {
        val isSendingAllowed = text.isNotBlank()

        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(shape = RoundedCornerShape(100),
                modifier = Modifier.weight(1f),
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text, imeAction = ImeAction.Send
                ),
                keyboardActions = KeyboardActions(onSend = {
                    if (isSendingAllowed) {
                        onSendClick()
                    }
                }),
                value = text,
                onValueChange = onTextChange,
                placeholder = { Text("Type your message here...") })
            IconButton(
                onClick = onSendClick, enabled = isSendingAllowed
            ) {
                Icon(
                    Icons.Rounded.Send, contentDescription = "Send", tint = if (isSendingAllowed) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.33f)
                    }
                )
            }
        }
    }

    var promtText by remember { mutableStateOf("") }

    Scaffold(containerColor = MaterialTheme.colorScheme.surface) { padding ->
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val state = rememberLazyListState()
                LaunchedEffect(Unit) {
                    // scroll to the most recent item when you start the screen
                    state.scrollToItem(Int.MAX_VALUE)
                }
                TopAppBar(
                    title = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(Modifier.size(40.dp)) {
                                AsyncImage(
                                    model = "https://avatars.githubusercontent.com/u/8596759?s=200&v=4",
                                    modifier = Modifier
                                        .matchParentSize()
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop,
                                    contentDescription = null
                                )
                                Box(
                                    Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .border(
                                            2.dp,
                                            MaterialTheme.colorScheme.surface,
                                            CircleShape
                                        )
                                        .background(Color(0xFF00C853))
                                        .align(Alignment.BottomEnd)
                                )
                            }
                            Column {
                                Text(
                                    text = "Gemini Bot",
                                    style = MaterialTheme.typography.titleMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "Powered by Gemini pro",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                    },
                )
                LazyColumn(
                    contentPadding = padding,
                    modifier = Modifier
                        .widthIn(max = 800.dp)
                        .fillMaxWidth()
                        .weight(1f),
                    state = state,
                ) {
                    items(conversations.size) { index ->
                        val conversation = conversations[index]
                        ChatBubble(
                            isFromMe = conversation.first == "sent",
                            content = conversation.second,
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateItemPlacement()
                        )
                    }
                }
                ComposerBar(
                    onSendClick = {
                        if (promtText.isNotBlank()) {
                            chatViewModel.sendMessage(promtText)
                            promtText = ""
                        }
                    },
                    text = promtText,
                    onTextChange = {
                        promtText = it
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun ChatBubble(
    isFromMe: Boolean,
    content: String,
    modifier: Modifier
) {
    if (isFromMe) {
        OutgoingMessage(text = content)
    } else {
        IncomingMessage(text = content)
    }
}

@Composable
fun OutgoingMessage(
    text: String,
) {
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(), contentAlignment = Alignment.TopEnd
    ) {
        val bubbleColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
        Surface(
            shape = MaterialTheme.shapes.medium.copy(bottomEnd = CornerSize(4.dp)),
            color = bubbleColor
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(12.dp),
                color = contentColorFor(backgroundColor = bubbleColor)
            )
        }
    }
}

@Composable
fun IncomingMessage(text: String) {
    Row(
        modifier = Modifier
            .padding(
                horizontal = 16.dp, vertical = 8.dp
            )
            .fillMaxWidth(0.9f), horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val bubbleColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f)
        Surface(
            shape = MaterialTheme.shapes.medium.copy(bottomStart = CornerSize(4.dp)),
            color = bubbleColor
        ) {
            Text(
                text,
                modifier = Modifier.padding(12.dp),
                color = contentColorFor(backgroundColor = bubbleColor)
            )
        }
    }
}