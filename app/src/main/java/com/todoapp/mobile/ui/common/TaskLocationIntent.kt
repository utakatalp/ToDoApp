package com.todoapp.mobile.ui.common

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

/**
 * Returns a click handler that opens a location in the system Maps app.
 *
 * - With coordinates → `geo:lat,lng?q=lat,lng(label)` opens the precise pin.
 * - Without coordinates → `geo:0,0?q=label` lets the Maps app resolve the address itself.
 * - No `name` at all → returns null so the caller can suppress the pill click affordance.
 *
 * Takes primitives instead of `Task` so it works for personal tasks (`Task`), the Calendar
 * UI contract (`PersonalTaskCalendarItem`), and group tasks (`GroupTask` / `GroupTaskUiItem`)
 * without forcing each caller to construct a domain object.
 */
@Composable
fun rememberOpenLocation(
    name: String?,
    address: String?,
    lat: Double?,
    lng: Double?,
): (() -> Unit)? {
    val context = LocalContext.current
    val resolvedName = name?.takeIf { it.isNotBlank() } ?: return null
    val resolvedAddress = address?.takeIf { it.isNotBlank() }

    return remember(context, resolvedName, resolvedAddress, lat, lng) {
        {
            val label = if (!resolvedAddress.isNullOrBlank() && resolvedAddress != resolvedName) {
                "$resolvedName, $resolvedAddress"
            } else {
                resolvedName
            }
            val uri = if (lat != null && lng != null) {
                Uri.parse("geo:$lat,$lng?q=$lat,$lng(${Uri.encode(label)})")
            } else {
                Uri.parse("geo:0,0?q=${Uri.encode(label)}")
            }
            val intent = Intent(Intent.ACTION_VIEW, uri).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            runCatching { context.startActivity(intent) }
        }
    }
}
