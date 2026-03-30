package ru.tech.demomapapp.feature.map.impl.store.handler

import ru.tech.demomapapp.feature.map.api.MapPoint
import ru.tech.demomapapp.feature.map.impl.store.MapStore
import ru.tech.demomapapp.feature.map.impl.store.MapStoreMessage
import ru.tech.demomapapp.feature.map.impl.CreateMapPointInput
import ru.tech.demomapapp.feature.map.impl.CreateMapPointUseCase
import ru.tech.demomapapp.feature.map.impl.FeatureIdProvider
import ru.tech.demomapapp.feature.map.impl.TimeProvider

internal class CreatePointHandler(
    private val createMapPointUseCase: CreateMapPointUseCase,
    private val timeProvider: TimeProvider,
    private val featureIdProvider: FeatureIdProvider,
) {
    fun handleConfirm(
        state: MapStore.State,
        onCreated: (MapStoreMessage) -> Unit,
    ) {
        val draft = state.createPointDraft ?: return
        val point = createMapPointUseCase.create(
            CreateMapPointInput(
                id = featureIdProvider.nextId(),
                latitudeInput = draft.latitudeInput,
                longitudeInput = draft.longitudeInput,
                titleInput = draft.titleInput,
                createdAtEpochMillis = timeProvider.currentTimeMillis(),
            ),
        ) ?: return
        onCreated(MapStoreMessage.CreatePointCreated(point))
    }
}