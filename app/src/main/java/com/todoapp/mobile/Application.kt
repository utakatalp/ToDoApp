package com.todoapp.mobile

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.todoapp.mobile.domain.repository.SecretPreferences
import com.todoapp.mobile.domain.security.SecretModeEndEvent
import com.todoapp.mobile.domain.usecase.security.OnSecretModeEventUseCase
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class Application : Application(), DefaultLifecycleObserver {
    @Inject
    lateinit var secretPreferences: SecretPreferences
    override fun onCreate() {
        super<Application>.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        owner.lifecycleScope.launch {
            OnSecretModeEventUseCase(secretPreferences).invoke(SecretModeEndEvent.APP_CLOSED)
        }
    }
}
