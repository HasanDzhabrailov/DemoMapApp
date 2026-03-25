package ru.tech.demomapapp.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import ru.tech.demomapapp.root.api.RootComponent
import ru.tech.demomapapp.root.ui.RootContent

@Composable
fun App(component: RootComponent) {
    MaterialTheme {
        RootContent(component = component)
    }
}
