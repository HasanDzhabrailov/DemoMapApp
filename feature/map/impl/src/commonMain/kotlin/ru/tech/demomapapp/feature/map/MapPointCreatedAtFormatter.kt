package ru.tech.demomapapp.feature.map

internal fun interface MapPointCreatedAtFormatter {
    fun format(epochMillis: Long): String
}

internal class DefaultMapPointCreatedAtFormatter : MapPointCreatedAtFormatter {
    override fun format(epochMillis: Long): String = formatMapPointCreatedAt(epochMillis)
}

internal expect fun formatMapPointCreatedAt(epochMillis: Long): String
