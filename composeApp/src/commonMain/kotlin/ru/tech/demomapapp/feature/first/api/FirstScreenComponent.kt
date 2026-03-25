package ru.tech.demomapapp.feature.first.api

import com.arkivanov.decompose.value.Value

interface FirstScreenComponent {
    val model: Value<Model>

    fun onPrimaryActionClick()

    data class Model(
        val kicker: String = "Preview",
        val title: String = "First screen template",
        val description: String = "Preview stub for layout validation without a platform component context.",
        val status: String = "Ready for content, events, and navigation.",
        val primaryActionTitle: String = "Primary action",
    )
}
