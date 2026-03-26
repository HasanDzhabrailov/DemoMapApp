package ru.tech.demomapapp.feature.map.impl

import ru.tech.demomapapp.feature.map.api.MapRenderModel
import ru.tech.demomapapp.feature.map.api.MapState

interface MapStateAdapter {
    fun adapt(state: MapState): MapRenderModel
}
