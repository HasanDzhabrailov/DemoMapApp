package ru.tech.demomapapp.root.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import ru.tech.demomapapp.feature.map.ui.MapScreenContent
import ru.tech.demomapapp.root.api.RootComponent

@Composable
fun RootContent(
    component: RootComponent,
    modifier: Modifier = Modifier,
) {
    Children(
        stack = component.stack,
        modifier = modifier,
    ) { component ->
        val child = component.instance

        when (child) {
            is RootComponent.Child.MapScreen -> MapScreenContent(component = child)
        }
    }
}
