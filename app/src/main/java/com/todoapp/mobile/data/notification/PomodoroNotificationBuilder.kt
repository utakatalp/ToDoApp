package com.todoapp.mobile.data.notification

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.todoapp.mobile.MainActivity
import com.todoapp.mobile.R
import com.todoapp.mobile.domain.engine.PomodoroEngineState
import com.todoapp.mobile.domain.engine.PomodoroMode
import com.example.uikit.R as UikitR

object PomodoroNotificationBuilder {
    const val NOTIFICATION_ID: Int = 4242

    private const val REQUEST_CODE_OPEN: Int = 1001
    private const val REQUEST_CODE_PAUSE: Int = 1002
    private const val REQUEST_CODE_RESUME: Int = 1003
    private const val REQUEST_CODE_SKIP: Int = 1004

    private const val MILLIS_PER_SECOND: Long = 1_000L
    private const val SECONDS_PER_MINUTE: Long = 60L

    fun build(
        context: Context,
        state: PomodoroEngineState,
    ): Notification {
        val title = context.getString(modeTitleRes(state.mode))
        val sessionLabel = context.getString(
            R.string.pomodoro_notification_session_progress,
            (state.currentSessionIndex + 1).coerceAtLeast(1),
            state.totalSessions.coerceAtLeast(1),
        )
        val accent = ContextCompat.getColor(context, R.color.pomodoro_notification_bg)
        val endTime = System.currentTimeMillis() + state.remainingSeconds * MILLIS_PER_SECOND
        val total = state.currentSessionTotalSeconds.toInt().coerceAtLeast(1)
        val elapsed = (state.currentSessionTotalSeconds - state.remainingSeconds)
            .toInt()
            .coerceIn(0, total)
        val timeText = formatTime(context, state.remainingSeconds)
        val isPaused = !state.isRunning && !state.isOvertime
        val contentText = if (isPaused) {
            context.getString(R.string.pomodoro_notification_paused, timeText)
        } else {
            sessionLabel
        }
        val pauseResumeIntent = if (state.isRunning) {
            buildServicePendingIntent(context, PomodoroForegroundService.ACTION_PAUSE, REQUEST_CODE_PAUSE)
        } else {
            buildServicePendingIntent(context, PomodoroForegroundService.ACTION_RESUME, REQUEST_CODE_RESUME)
        }
        val pauseResumeLabel = context.getString(
            if (state.isRunning) R.string.pomodoro_notification_action_pause
            else R.string.pomodoro_notification_action_resume,
        )
        val pauseResumeIcon = if (state.isRunning) UikitR.drawable.ic_pause else android.R.drawable.ic_media_play
        val skipIntent = buildServicePendingIntent(
            context,
            PomodoroForegroundService.ACTION_SKIP,
            REQUEST_CODE_SKIP,
        )

        val builder = NotificationCompat
            .Builder(context, PomodoroNotificationChannels.LIVE_CHANNEL_ID)
            .setSmallIcon(UikitR.drawable.ic_sand_clock)
            .setContentTitle(title)
            .setContentText(contentText)
            .setSubText(sessionLabel)
            .setColor(accent)
            .setColorized(true)
            .setOnlyAlertOnce(true)
            .setShowWhen(true)
            .setProgress(total, elapsed, state.isOvertime)
            .setOngoing(true)
            .setSilent(true)
            .setCategory(NotificationCompat.CATEGORY_STOPWATCH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .setContentIntent(buildOpenAppIntent(context))
            .addAction(pauseResumeIcon, pauseResumeLabel, pauseResumeIntent)
            .addAction(
                UikitR.drawable.ic_fast_forward,
                context.getString(R.string.pomodoro_notification_action_skip),
                skipIntent,
            )

        if (isPaused) {
            // Disable chronometer when paused — otherwise the system keeps counting toward
            // the captured endTime and the notification "ignores" the pause.
            builder.setUsesChronometer(false).setWhen(System.currentTimeMillis())
        } else {
            builder.setUsesChronometer(true)
                .setChronometerCountDown(!state.isOvertime)
                .setWhen(endTime)
        }

        return builder.build()
    }

    private fun formatTime(context: Context, totalSeconds: Long): String {
        val safe = totalSeconds.coerceAtLeast(0L)
        val minutes = safe / SECONDS_PER_MINUTE
        val seconds = safe % SECONDS_PER_MINUTE
        return context.getString(
            R.string.pomodoro_notification_time_format,
            minutes.toInt(),
            seconds.toInt(),
        )
    }

    private fun modeTitleRes(mode: PomodoroMode): Int = when (mode) {
        PomodoroMode.Focus -> R.string.pomodoro_mode_focus
        PomodoroMode.ShortBreak -> R.string.pomodoro_mode_short_break
        PomodoroMode.LongBreak -> R.string.pomodoro_mode_long_break
        PomodoroMode.OverTime -> R.string.pomodoro_mode_overtime
    }

    private fun buildOpenAppIntent(context: Context): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        return PendingIntent.getActivity(
            context,
            REQUEST_CODE_OPEN,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun buildServicePendingIntent(
        context: Context,
        action: String,
        requestCode: Int,
    ): PendingIntent {
        val intent = Intent(context, PomodoroForegroundService::class.java).apply {
            this.action = action
        }
        return PendingIntent.getService(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }
}
