package ru.tech.demomapapp.feature.map.impl

import ru.tech.demomapapp.feature.map.api.MapRenderModel
import ru.tech.demomapapp.feature.map.api.MapState

class DefaultMapStateAdapter : MapStateAdapter {
    override fun adapt(state: MapState): MapRenderModel =
        MapRenderModel(styleUrl = state.styleUrl)
}
