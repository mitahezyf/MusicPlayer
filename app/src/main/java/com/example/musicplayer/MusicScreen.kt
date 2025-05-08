@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.musicplayer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicScreen(navController: NavController, viewModel: MusicViewModel) {
    val musicFiles by viewModel.musicFiles.collectAsState()
    val sortedMusicFiles = remember(musicFiles) {
        musicFiles.sortedBy { it.title.lowercase().trim() }
    }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lista odtwarzania") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            MiniPlayerContainer(
                viewModel = viewModel,
                onExpand = { navController.navigate("player") }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            when {
                musicFiles.isEmpty() -> {
                    Text(
                        text = "Brak uprawnień do odczytu plików!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.Center)
                    )
                }
                else -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Row(modifier = Modifier.fillMaxSize()) {
                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                state = listState
                            ) {
                                items(sortedMusicFiles) { musicFile ->
                                    MusicItem(musicFile, viewModel)
                                    HorizontalDivider(
                                        thickness = 0.5.dp,
                                        color = MaterialTheme.colorScheme.outlineVariant
                                    )
                                }
                            }
                            ScrollbarWithIndex(
                                listState = listState,
                                titles = sortedMusicFiles.map { it.title },
                                onScroll = { index ->
                                    coroutineScope.launch {
                                        listState.animateScrollToItem(index)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScrollbarWithIndex(
    listState: LazyListState,
    titles: List<String>,
    onScroll: (Int) -> Unit
) {
    val indices = remember(titles) {
        titles.mapNotNull { it.firstOrNull()?.uppercaseChar()?.toString() }.distinct()
    }

    val firstVisibleItemIndex = listState.firstVisibleItemIndex
    val currentLetter = titles.getOrNull(firstVisibleItemIndex)?.firstOrNull()?.uppercaseChar()?.toString()

    var boxSize by remember { mutableIntStateOf(0) }
    var showTooltip by remember { mutableStateOf(false) }
    var tooltipLetter by remember { mutableStateOf("") }
    var touchY by remember { mutableFloatStateOf(0f) }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxHeight()
            .padding(end = 8.dp)
            .onSizeChanged { size ->
                boxSize = size.height
            }
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragStart = { offset ->
                        touchY = offset.y
                        showTooltip = true
                    },
                    onDragEnd = {
                        showTooltip = false
                    },
                    onVerticalDrag = { change, _ ->
                        touchY = change.position.y
                        val scrollHeight = boxSize.toFloat()
                        val index = ((touchY / scrollHeight) * indices.size).toInt()
                            .coerceIn(0, indices.lastIndex)
                        tooltipLetter = indices.getOrNull(index) ?: ""
                        val scrollTo = titles.indexOfFirst { it.startsWith(indices[index], ignoreCase = true) }
                        if (scrollTo != -1) onScroll(scrollTo)
                    }
                )
                detectTapGestures { offset ->
                    touchY = offset.y
                    val scrollHeight = boxSize.toFloat()
                    val index = ((touchY / scrollHeight) * indices.size).toInt()
                        .coerceIn(0, indices.lastIndex)
                    tooltipLetter = indices.getOrNull(index) ?: ""
                    showTooltip = true
                    val scrollTo = titles.indexOfFirst { it.startsWith(indices[index], ignoreCase = true) }
                    if (scrollTo != -1) onScroll(scrollTo)

                    coroutineScope.launch {
                        kotlinx.coroutines.delay(500)
                        showTooltip = false
                    }
                }
            }
    ) {
        Column(
            modifier = Modifier.align(Alignment.CenterEnd),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            indices.forEach { letter ->
                Text(
                    text = letter,
                    modifier = Modifier.padding(2.dp),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = if (letter == currentLetter) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        }

        if (showTooltip && tooltipLetter.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(x = (-50).dp) // Poprawiony offset
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.small // Zmiana na "small" jeśli "medium" nie działa
                    )
                    .padding(8.dp)
            ) {
                Text(
                    text = tooltipLetter,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun MusicItem(musicFile: MusicFile, viewModel: MusicViewModel) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                viewModel.playMusic(musicFile.filePath)
            },
        color = MaterialTheme.colorScheme.surface,
        shape = RectangleShape
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = musicFile.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                musicFile.artist?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Odtwórz",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}