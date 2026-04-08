package ru.tech.demomapapp.feature.map.location.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import ru.tech.demomapapp.feature.map.api.LocationUiContract
import ru.tech.demomapapp.feature.map.api.MapLocationRequest
import ru.tech.demomapapp.feature.map.api.MyLocationMode
import ru.tech.demomapapp.feature.map.location.LocationModel
import ru.tech.demomapapp.feature.map.ui.CurrentLocationFocusButton
import ru.tech.demomapapp.feature.map.ui.MyLocationButton

@Composable
internal fun LocationControls(component: LocationUiContract, modifier: Modifier = Modifier) {
    val model by component.model.subscribeAsState()

    Row(
        modifier = modifier
            .navigationBarsPadding()
            .padding(start = 66.dp, top = 16.dp, end = 16.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        MyLocationButton(
            onClick = component::onMyLocationClick,
            isEnabled = model.isManualLocationEnabled(),
        )
        CurrentLocationFocusButton(
            onClick = component::onCurrentLocationFocusClick,
            isEnabled = model.isCurrentLocationFocusEnabled(),
        )
    }

    MapLocationEffectBinder(
        request = model.pendingRequest,
        onRequestConsumed = component::onLocationRequestConsumed,
        onLocationResult = component::onLocationResult,
    )
}

internal fun LocationModel.isGpsToggleChecked(): Boolean = mode == MyLocationMode.GPS ||
    pendingRequest == MapLocationRequest.EnableGpsLocationRequest

internal fun LocationModel.isCurrentLocationFocusEnabled(): Boolean =
    currentMarker != null || mode == MyLocationMode.GPS

internal fun LocationModel.isManualLocationEnabled(): Boolean = mode != MyLocationMode.GPS
