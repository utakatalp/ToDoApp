package com.todoapp.mobile.domain.observer

import kotlinx.coroutines.flow.StateFlow

interface ConnectivityObserver {
    val isConnected: StateFlow<Boolean>
}
