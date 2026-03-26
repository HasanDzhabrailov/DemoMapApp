package ru.tech.demomapapp.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.tech.demomapapp.root.api.RootComponent
import ru.tech.demomapapp.root.ui.RootContent

@Composable
fun App(component: RootComponent) {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
        ) {
            RootContent(
                component = component,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
