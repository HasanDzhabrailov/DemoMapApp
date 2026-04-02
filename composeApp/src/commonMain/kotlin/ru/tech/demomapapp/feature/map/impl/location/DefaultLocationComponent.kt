package ru.tech.demomapapp.feature.map.impl.location

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.getOrCreate
import ru.tech.demomapapp.feature.map.api.LocationRequestResult
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot

internal class DefaultLocationComponent(
    componentContext: ComponentContext,
    private val locationStoreFactory: LocationStoreFactory,
    private val output: LocationComponent.Output,
) : LocationComponent, ComponentContext by componentContext {

    private val holder = instanceKeeper.getOrCreate(key = STORE_HOLDER_KEY) {
        LocationStoreHolder(locationStoreFactory)
    }

    override val model: Value<LocationModel> = holder.model

    override fun onGpsToggle() = holder.accept(LocationStore.Intent.GpsToggled)

    override fun onMyLocationClick() = holder.accept(LocationStore.Intent.MyLocationClicked)

    override fun onCurrentLocationFocusClick() = holder.accept(LocationStore.Intent.CurrentLocationFocusClicked)

    override fun onLocationResult(result: LocationRequestResult) =
        holder.accept(LocationStore.Intent.LocationResultReceived(result))

    override fun onLocationRequestConsumed() = holder.accept(LocationStore.Intent.LocationRequestConsumed)

    override fun onCameraSnapshotReceived(snapshot: MapCameraSnapshot) =
        holder.accept(LocationStore.Intent.CameraSnapshotReceived(snapshot))

    init {
        holder.labels { label ->
            when (label) {
                is LocationStore.Label.LocationRequestIssued -> output.onLocationRequestIssued(label.request)
                is LocationStore.Label.LocationUpdated -> output.onLocationUpdated(label.location)
                is LocationStore.Label.ViewportCommandRequested -> output.onViewportCommandRequested(label.command)
            }
        }
    }

    private companion object {
        const val STORE_HOLDER_KEY = "DefaultLocationComponent.locationStoreHolder"
    }
}
