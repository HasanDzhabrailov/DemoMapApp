package ru.tech.demomapapp.feature.map.ui

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import ru.tech.demomapapp.feature.map.impl.ruler.RulerComponent

@Composable
internal fun BoxScope.RulerOverlay(component: RulerComponent, modifier: Modifier = Modifier) {
    val model by component.model.subscribeAsState()

    model.infoWindow?.let { infoWindow ->
        RulerInfoWindowOverlay(
            state = infoWindow,
            modifier = modifier
                .align(Alignment.Center)
                .offset(y = (-72).dp),
        )
    }
}
