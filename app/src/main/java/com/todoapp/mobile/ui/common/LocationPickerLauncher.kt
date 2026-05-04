package com.todoapp.mobile.ui.common

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import timber.log.Timber

/**
 * Wraps the Google Places Autocomplete Activity in a Compose-friendly launcher. Returns a
 * `() -> Unit` you can call from a click handler. The picker handles its own UI, location
 * permission prompts, and search keyboard — we only consume the result here and forward it
 * to [onPicked].
 *
 * If the Places SDK isn't initialized (no `MAPS_API_KEY`), the launcher logs and no-ops so
 * the rest of the form still works in CI / local-no-key dev.
 *
 * @param onPicked called with (name, address, lat, lng) on a successful pick. Latitude /
 *  longitude may be null when the chosen result lacks coordinates.
 */
@Composable
fun rememberLocationPickerLauncher(
    onPicked: (name: String, address: String, lat: Double?, lng: Double?) -> Unit,
): () -> Unit {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        val data = result.data
        if (result.resultCode == Activity.RESULT_OK && data != null) {
            runCatching { Autocomplete.getPlaceFromIntent(data) }
                .onSuccess { place ->
                    onPicked(
                        place.name.orEmpty(),
                        place.address.orEmpty(),
                        place.latLng?.latitude,
                        place.latLng?.longitude,
                    )
                }
                .onFailure { Timber.tag("LocationPicker").w(it, "place parse failed") }
        }
        // RESULT_CANCELED (user backed out) → no-op; existing form state stays.
    }

    return remember(launcher, context) {
        {
            if (!Places.isInitialized()) {
                Timber.tag("LocationPicker").w("Places SDK not initialized — MAPS_API_KEY missing.")
            } else {
                val fields = listOf(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)
                val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                    .build(context)
                launcher.launch(intent)
            }
        }
    }
}
