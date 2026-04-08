package ru.tech.demomapapp.feature.map

import java.util.UUID

internal actual fun generateMapPointId(): String = UUID.randomUUID().toString()
