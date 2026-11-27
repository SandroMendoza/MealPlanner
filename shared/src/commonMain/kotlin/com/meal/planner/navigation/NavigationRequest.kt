package com.meal.planner.navigation

/**
 * A request to open [route] while the app is already showing. Each instance is its own request,
 * so asking for the same route twice navigates both times.
 */
class NavigationRequest(val route: String)
