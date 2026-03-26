package ru.tech.demomapapp.feature.map.render

import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import org.maplibre.android.maps.MapLibreMap

internal class MapLibreCameraAdapter(
    private val onCameraIdle: (MapCameraSnapshot) -> Unit,
) {
    private var map: MapLibreMap? = null
    private var listener: MapLibreMap.OnCameraIdleListener? = null
    private var lastSnapshot: MapCameraSnapshot? = null

    fun attach(map: MapLibreMap) {
        if (this.map === map && listener != null) {
            return
        }

        detach()

        val idleListener = MapLibreMap.OnCameraIdleListener {
            emitSnapshot(map)
        }

        this.map = map
        listener = idleListener
        map.addOnCameraIdleListener(idleListener)
    }

    fun detach() {
        val currentMap = map
        val currentListener = listener
        if (currentMap != null && currentListener != null) {
            currentMap.removeOnCameraIdleListener(currentListener)
        }

        map = null
        listener = null
        lastSnapshot = null
    }

    private fun emitSnapshot(map: MapLibreMap) {
        val snapshot = map.toMapCameraSnapshot() ?: return
        if (snapshot == lastSnapshot) {
            return
        }

        lastSnapshot = snapshot
        onCameraIdle(snapshot)
    }
}

private fun MapLibreMap.toMapCameraSnapshot(): MapCameraSnapshot? {
    val cameraPosition = cameraPosition
    val target = cameraPosition.target ?: return null

    return MapCameraSnapshot(
        latitude = target.latitude,
        longitude = target.longitude,
        zoom = cameraPosition.zoom,
        bearing = cameraPosition.bearing,
    )
}
