package com.example.trail_tales_front_end_one

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform