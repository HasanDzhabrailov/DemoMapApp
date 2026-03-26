package ru.tech.demomapapp.feature.map.impl

import java.util.UUID

internal actual fun generateMapPointId(): String = UUID.randomUUID().toString()
