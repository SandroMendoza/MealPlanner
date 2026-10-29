package com.meal.planner

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform