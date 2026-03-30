package ru.tech.demomapapp.feature.map.impl

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val mapPointCreatedAtFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")

internal actual fun formatMapPointCreatedAt(epochMillis: Long): String = Instant.ofEpochMilli(epochMillis)
    .atZone(ZoneId.systemDefault())
    .format(mapPointCreatedAtFormatter)
