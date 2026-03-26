package ru.tech.demomapapp.feature.map.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import ru.tech.demomapapp.feature.map.api.MapScreenComponent
import ru.tech.demomapapp.feature.map.impl.toRenderModel
import ru.tech.demomapapp.feature.map.render.MapRenderer

@Composable
fun MapScreenContent(
    component: MapScreenComponent,
    modifier: Modifier = Modifier,
) {
    val model by component.model.subscribeAsState()
    val renderModel = model.mapState.toRenderModel()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        MapRenderer(
            model = renderModel,
            modifier = Modifier.fillMaxSize(),
        )

    }
}

@Preview
@Composable
private fun MapScreenContentPreview() {
    MaterialTheme {
        MapScreenContent(component = PreviewMapScreenComponent())
    }
}

private class PreviewMapScreenComponent : MapScreenComponent {
    override val model: Value<MapScreenComponent.Model> =
        MutableValue(MapScreenComponent.Model())
}
