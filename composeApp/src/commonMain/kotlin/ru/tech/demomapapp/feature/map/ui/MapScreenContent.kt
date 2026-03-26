package ru.tech.demomapapp.feature.map.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import ru.tech.demomapapp.feature.map.api.MapRenderer
import ru.tech.demomapapp.feature.map.api.MapScreenComponent
import ru.tech.demomapapp.feature.map.impl.DefaultMapStateAdapter
import ru.tech.demomapapp.root.api.RootComponent

@Composable
fun MapScreenContent(
    component: RootComponent.Child.MapScreen,
    modifier: Modifier = Modifier,
) {
    val model by component.instance.model.subscribeAsState()
    val mapStateAdapter = remember { DefaultMapStateAdapter() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        MapRenderer(
            model = mapStateAdapter.adapt(model.mapState),
            modifier = Modifier.fillMaxSize(),
        )

    }
}

@Preview
@Composable
private fun MapScreenContentPreview() {
    MaterialTheme {
        MapScreenContent(component = RootComponent.Child.MapScreen(instance = PreviewMapScreenComponent()))
    }
}

private class PreviewMapScreenComponent : MapScreenComponent {
    override val model: Value<MapScreenComponent.Model> =
        MutableValue(MapScreenComponent.Model())

    override fun onPrimaryActionClick() = Unit
}
