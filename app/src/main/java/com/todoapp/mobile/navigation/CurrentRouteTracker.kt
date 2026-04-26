package com.todoapp.mobile.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Process-wide tracker of the currently visible NavGraph destination route. Updated from
 * NavHost via an OnDestinationChangedListener; read by [TDFireBaseMessagingService] to suppress
 * banners for events that match the screen the user is already looking at.
 */
@Singleton
class CurrentRouteTracker @Inject constructor() {
    private val _route = MutableStateFlow<String?>(null)
    val route: StateFlow<String?> = _route.asStateFlow()

    /** Optional structured args; consumed where the route alone isn't enough (e.g. taskId). */
    private val _args = MutableStateFlow<RouteArgs?>(null)
    val args: StateFlow<RouteArgs?> = _args.asStateFlow()

    fun update(route: String?, args: RouteArgs? = null) {
        _route.value = route
        _args.value = args
    }
}

sealed interface RouteArgs {
    data class GroupTaskDetail(val groupId: Long, val taskId: Long) : RouteArgs
    data class GroupDetail(val groupId: Long) : RouteArgs
}
