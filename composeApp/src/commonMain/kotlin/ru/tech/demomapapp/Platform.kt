package ru.tech.demomapapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
