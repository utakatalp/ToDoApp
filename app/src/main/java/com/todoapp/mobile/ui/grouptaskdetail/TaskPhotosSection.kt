package com.todoapp.mobile.ui.grouptaskdetail

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.todoapp.mobile.BuildConfig
import com.todoapp.mobile.R
import com.todoapp.uikit.components.TDText
import com.todoapp.uikit.theme.TDTheme

@Composable
fun TaskPhotosSection(
    photoUrls: List<String>,
    onPick: (ByteArray, String) -> Unit,
    onDelete: (Long) -> Unit,
) {
    val context = LocalContext.current
    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        val cr = context.contentResolver
        val mime = cr.getType(uri) ?: "image/jpeg"
        val bytes = runCatching { cr.openInputStream(uri)?.use { it.readBytes() } }
            .getOrNull() ?: return@rememberLauncherForActivityResult
        onPick(bytes, mime)
    }
    var pendingDeleteId by remember { mutableStateOf<Long?>(null) }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TDText(
                text = stringResource(R.string.photos),
                style = TDTheme.typography.subheading2,
                color = TDTheme.colors.onBackground,
            )
            Spacer(modifier = Modifier.padding(horizontal = 4.dp))
            TDText(
                text = "(${photoUrls.size})",
                style = TDTheme.typography.subheading1,
                color = TDTheme.colors.gray,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                AddPhotoTile(onClick = {
                    picker.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                    )
                })
            }
            items(photoUrls, key = { it }) { relativeUrl ->
                PhotoTile(
                    url = absoluteUrl(relativeUrl),
                    onLongPress = {
                        val id = photoIdFromUrl(relativeUrl)
                        if (id != null) pendingDeleteId = id
                    },
                )
            }
        }
    }

    pendingDeleteId?.let { id ->
        AlertDialog(
            onDismissRequest = { pendingDeleteId = null },
            title = { TDText(text = stringResource(R.string.delete_photo_title)) },
            text = { TDText(text = stringResource(R.string.delete_photo_message)) },
            confirmButton = {
                TextButton(onClick = {
                    onDelete(id)
                    pendingDeleteId = null
                }) {
                    TDText(text = stringResource(R.string.delete), color = TDTheme.colors.crossRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteId = null }) {
                    TDText(text = stringResource(R.string.cancel))
                }
            },
            containerColor = TDTheme.colors.background,
        )
    }
}

@Composable
private fun AddPhotoTile(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(TDTheme.colors.lightPending)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(com.example.uikit.R.drawable.ic_plus),
            contentDescription = stringResource(R.string.add_photo),
            tint = TDTheme.colors.pendingGray,
        )
    }
}

@Composable
private fun PhotoTile(url: String, onLongPress: () -> Unit) {
    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(TDTheme.colors.lightPending)
            .clickable(onClick = onLongPress),
    ) {
        AsyncImage(
            model = url,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(80.dp),
        )
    }
}

private fun absoluteUrl(relative: String): String {
    val base = BuildConfig.BASE_URL.trimEnd('/')
    val path = relative.trimStart('/')
    return "$base/$path"
}

private fun photoIdFromUrl(url: String): Long? =
    url.trimEnd('/').substringAfterLast('/').toLongOrNull()
