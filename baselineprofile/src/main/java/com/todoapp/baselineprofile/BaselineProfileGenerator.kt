package com.todoapp.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Generates the baseline profile that AGP merges into `app/src/main/baseline-prof.txt`.
 *
 * Run on a connected physical device (Android 13+ recommended):
 *   ./gradlew :app:generateBaselineProfile
 *
 * The profile captures the cold-start path + Home scroll. After generation, commit
 * `app/src/main/baseline-prof.txt`. Cold start improves 15–30 % on first install.
 */
@RunWith(AndroidJUnit4::class)
class BaselineProfileGenerator {
    @get:Rule
    val rule = BaselineProfileRule()

    @Test
    fun generate() {
        rule.collect(
            packageName = "com.todoapp.mobile",
            includeInStartupProfile = true,
        ) {
            // Cold start
            pressHome()
            startActivityAndWait()

            // Let the splash + first frame settle.
            device.waitForIdle()

            // Best-effort scroll. Re-resolve the scrollable each iteration since the UI
            // may swap nodes between flings (drawer, sheet, list reflow), and wrap in
            // try/catch so a StaleObjectException doesn't fail the whole run — the
            // cold-start path is already captured and is the biggest win.
            device.wait(Until.findObject(By.scrollable(true)), 5_000)
            repeat(5) {
                runCatching {
                    val scrollable = device.findObject(By.scrollable(true)) ?: return@runCatching
                    scrollable.fling(androidx.test.uiautomator.Direction.DOWN)
                    device.waitForIdle()
                }
            }
        }
    }
}
