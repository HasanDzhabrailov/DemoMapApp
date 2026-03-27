package ru.tech.demomapapp.feature.map.render

import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import org.maplibre.android.maps.MapLibreMap

internal class MapLibreCameraAdapter(
    private val onCameraIdle: (MapCameraSnapshot) -> Unit,
) {
    private var map: MapLibreMap? = null
    private var idleListener: MapLibreMap.OnCameraIdleListener? = null
    private var moveListener: MapLibreMap.OnCameraMoveListener? = null
    private var lastSnapshot: MapCameraSnapshot? = null

    fun attach(map: MapLibreMap) {
        if (this.map === map && idleListener != null && moveListener != null) {
            return
        }

        detach()

        val localIdleListener = MapLibreMap.OnCameraIdleListener {
            emitSnapshot(map)
        }
        val localMoveListener = MapLibreMap.OnCameraMoveListener {
            emitSnapshot(map)
        }

        this.map = map
        idleListener = localIdleListener
        moveListener = localMoveListener
        map.addOnCameraIdleListener(localIdleListener)
        map.addOnCameraMoveListener(localMoveListener)
        emitSnapshot(map)
    }

    fun detach() {
        val currentMap = map
        val currentIdleListener = idleListener
        val currentMoveListener = moveListener
        if (currentMap != null && currentIdleListener != null) {
            currentMap.removeOnCameraIdleListener(currentIdleListener)
        }
        if (currentMap != null && currentMoveListener != null) {
            currentMap.removeOnCameraMoveListener(currentMoveListener)
        }

        map = null
        idleListener = null
        moveListener = null
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
