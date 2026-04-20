package com.todoapp.mobile.ui.home

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.runtime.remember
import com.todoapp.mobile.R
import com.todoapp.uikit.components.TDText
import com.todoapp.uikit.theme.TDTheme

@Composable
fun PendingPhotosRow(
    pending: List<PendingPhoto>,
    onPick: (ByteArray, String) -> Unit,
    onRemoveAt: (Int) -> Unit,
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

    Column(Modifier.fillMaxWidth()) {
        TDText(
            text = stringResource(R.string.photos),
            style = TDTheme.typography.subheading2,
            color = TDTheme.colors.onBackground,
        )
        Spacer(Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(TDTheme.colors.lightPending)
                        .clickable {
                            picker.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly,
                                ),
                            )
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(com.example.uikit.R.drawable.ic_plus),
                        contentDescription = stringResource(R.string.add_photo),
                        tint = TDTheme.colors.pendingGray,
                    )
                }
            }
            itemsIndexed(pending) { index, photo ->
                val bitmap: ImageBitmap? = remember(photo.bytes) {
                    runCatching {
                        BitmapFactory.decodeByteArray(photo.bytes, 0, photo.bytes.size)?.asImageBitmap()
                    }.getOrNull()
                }
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(TDTheme.colors.lightPending)
                        .clickable { onRemoveAt(index) },
                ) {
                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(72.dp),
                        )
                    }
                }
            }
        }
    }
}
