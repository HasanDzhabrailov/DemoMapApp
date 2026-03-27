package ru.tech.demomapapp.feature.map.impl

import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import ru.tech.demomapapp.feature.map.api.LocationRequestResult
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapLocationRequest
import ru.tech.demomapapp.feature.map.api.MapScreenComponent
import ru.tech.demomapapp.feature.map.api.MapViewportCommand
import ru.tech.demomapapp.feature.map.api.MyLocationMode

class DefaultMapScreenComponentTest {

    @Test
    fun `map tools menu closes center marker menu when opened`() {
        val component = createComponent()

        component.onCenterMarkerClick()
        component.onMapToolsClick()

        assertTrue(component.model.value.isMapToolsMenuVisible)
        assertFalse(component.model.value.isCenterMarkerMenuVisible)
    }

    @Test
    fun `map tools menu clears info window when opened`() {
        val component = createComponent()

        component.onCameraIdle(defaultSnapshot())
        component.onCreatePointClick()
        component.onCreatePointTitleChange("Test point")
        component.onCreatePointConfirm()

        val point = component.model.value.mapState.points.single()
        component.onFeatureClick(
            featureKey = point.id,
            featureType = MapScreenComponent.FeatureType.POINT,
            anchor = MapScreenComponent.FeatureInfoWindowAnchor(screenX = 120, screenY = 240),
        )

        component.onMapToolsClick()

        assertTrue(component.model.value.isMapToolsMenuVisible)
        assertNull(component.model.value.selectedFeatureInfoWindow)
    }

    @Test
    fun `center marker click closes map tools menu`() {
        val component = createComponent()

        component.onMapToolsClick()
        component.onCenterMarkerClick()

        assertFalse(component.model.value.isMapToolsMenuVisible)
        assertTrue(component.model.value.isCenterMarkerMenuVisible)
    }

    @Test
    fun `gps enable emits one shot request and keeps menu open`() {
        val component = createComponent()

        component.onMapToolsClick()
        component.onGpsToggle()

        assertTrue(component.model.value.isMapToolsMenuVisible)
        assertEquals(MyLocationMode.OFF, component.model.value.myLocationMode)
        assertEquals(MapLocationRequest.EnableGpsLocationRequest, component.model.value.pendingLocationRequest)
        assertNull(component.model.value.pendingViewportCommand)
    }

    @Test
    fun `second gps toggle tap cancels pending enable request`() {
        val component = createComponent()

        component.onGpsToggle()
        component.onGpsToggle()

        assertEquals(MyLocationMode.OFF, component.model.value.myLocationMode)
        assertNull(component.model.value.currentLocationMarker)
        assertNull(component.model.value.pendingLocationRequest)
    }

    @Test
    fun `gps denied result keeps mode off and clears marker`() {
        val component = createComponent()

        component.onGpsToggle()
        component.onLocationRequestConsumed()
        component.onLocationResult(LocationRequestResult.PermissionDenied)

        assertEquals(MyLocationMode.OFF, component.model.value.myLocationMode)
        assertNull(component.model.value.currentLocationMarker)
        assertNull(component.model.value.pendingLocationRequest)
    }

    @Test
    fun `gps unavailable result keeps mode off and clears marker`() {
        val component = createComponent()

        component.onGpsToggle()
        component.onLocationRequestConsumed()
        component.onLocationResult(LocationRequestResult.LocationUnavailable)

        assertEquals(MyLocationMode.OFF, component.model.value.myLocationMode)
        assertNull(component.model.value.currentLocationMarker)
        assertNull(component.model.value.pendingLocationRequest)
    }

    @Test
    fun `successful gps result sets gps mode marker and move command`() {
        val component = createComponent()

        component.onGpsToggle()
        component.onLocationRequestConsumed()
        component.onLocationResult(LocationRequestResult.LocationResolved(latitude = 55.7, longitude = 37.6))

        assertEquals(MyLocationMode.GPS, component.model.value.myLocationMode)
        assertEquals(55.7, component.model.value.currentLocationMarker?.latitude)
        assertEquals(37.6, component.model.value.currentLocationMarker?.longitude)
        assertFalse(component.model.value.currentLocationMarker?.isPlaceholder ?: true)
        assertEquals(
            MapViewportCommand.MoveTo(latitude = 55.7, longitude = 37.6),
            component.model.value.pendingViewportCommand,
        )
    }

    @Test
    fun `gps toggle off clears gps marker and request`() {
        val component = createComponent()

        component.onLocationResult(LocationRequestResult.LocationResolved(latitude = 55.7, longitude = 37.6))
        component.onGpsToggle()

        assertEquals(MyLocationMode.OFF, component.model.value.myLocationMode)
        assertNull(component.model.value.currentLocationMarker)
        assertNull(component.model.value.pendingLocationRequest)
    }

    @Test
    fun `recenter failure keeps gps mode and current marker`() {
        val component = createComponent()

        component.onLocationResult(LocationRequestResult.LocationResolved(latitude = 55.7, longitude = 37.6))
        component.onViewportCommandConsumed()
        component.onCurrentLocationFocusClick()
        component.onLocationResult(LocationRequestResult.LocationUnavailable)

        assertEquals(MyLocationMode.GPS, component.model.value.myLocationMode)
        assertEquals(55.7, component.model.value.currentLocationMarker?.latitude)
        assertEquals(37.6, component.model.value.currentLocationMarker?.longitude)
        assertNull(component.model.value.pendingLocationRequest)
    }

    @Test
    fun `permission denied after active gps turns gps off and clears marker`() {
        val component = createComponent()

        component.onLocationResult(LocationRequestResult.LocationResolved(latitude = 55.7, longitude = 37.6))
        component.onViewportCommandConsumed()
        component.onCurrentLocationFocusClick()
        component.onLocationResult(LocationRequestResult.PermissionDenied)

        assertEquals(MyLocationMode.OFF, component.model.value.myLocationMode)
        assertNull(component.model.value.currentLocationMarker)
        assertNull(component.model.value.pendingLocationRequest)
    }

    @Test
    fun `my location click while gps active does not replace gps marker with placeholder`() {
        val component = createComponent()

        component.onLocationResult(LocationRequestResult.LocationResolved(latitude = 55.7, longitude = 37.6))
        component.onMyLocationClick()

        assertEquals(MyLocationMode.GPS, component.model.value.myLocationMode)
        assertEquals(55.7, component.model.value.currentLocationMarker?.latitude)
        assertEquals(37.6, component.model.value.currentLocationMarker?.longitude)
        assertFalse(component.model.value.currentLocationMarker?.isPlaceholder ?: true)
        assertNull(component.model.value.pendingLocationRequest)
    }

    @Test
    fun `current location focus click while gps active moves camera to current marker`() {
        val component = createComponent()

        component.onLocationResult(LocationRequestResult.LocationResolved(latitude = 55.7, longitude = 37.6))
        component.onViewportCommandConsumed()
        component.onCurrentLocationFocusClick()

        assertEquals(
            MapViewportCommand.MoveTo(latitude = 55.7, longitude = 37.6),
            component.model.value.pendingViewportCommand,
        )
        assertNull(component.model.value.pendingLocationRequest)
    }

    @Test
    fun `current location focus click while gps inactive does nothing without marker`() {
        val component = createComponent()

        component.onCurrentLocationFocusClick()

        assertNull(component.model.value.pendingLocationRequest)
        assertNull(component.model.value.pendingViewportCommand)
    }

    @Test
    fun `current location focus click moves camera to manual placeholder marker`() {
        val component = createComponent()

        component.onCameraIdle(defaultSnapshot(latitude = 59.0, longitude = 30.0))
        component.onMyLocationClick()
        component.onCurrentLocationFocusClick()

        assertEquals(
            MapViewportCommand.MoveTo(latitude = 59.0, longitude = 30.0),
            component.model.value.pendingViewportCommand,
        )
        assertNull(component.model.value.pendingLocationRequest)
    }

    @Test
    fun `my location click while gps inactive creates manual placeholder from camera center`() {
        val component = createComponent()

        component.onCameraIdle(defaultSnapshot(latitude = 59.0, longitude = 30.0))
        component.onMyLocationClick()

        assertEquals(MyLocationMode.MANUAL_PLACEHOLDER, component.model.value.myLocationMode)
        assertEquals(59.0, component.model.value.currentLocationMarker?.latitude)
        assertEquals(30.0, component.model.value.currentLocationMarker?.longitude)
        assertTrue(component.model.value.currentLocationMarker?.isPlaceholder == true)
        assertNull(component.model.value.pendingLocationRequest)
    }

    @Test
    fun `my location click while gps inactive does nothing without camera snapshot`() {
        val component = createComponent()

        component.onMyLocationClick()

        assertEquals(MyLocationMode.OFF, component.model.value.myLocationMode)
        assertNull(component.model.value.currentLocationMarker)
        assertNull(component.model.value.pendingLocationRequest)
    }

    @Test
    fun `successful gps result clears manual placeholder mode`() {
        val component = createComponent()

        component.onCameraIdle(defaultSnapshot(latitude = 59.0, longitude = 30.0))
        component.onMyLocationClick()
        component.onLocationResult(LocationRequestResult.LocationResolved(latitude = 55.7, longitude = 37.6))

        assertEquals(MyLocationMode.GPS, component.model.value.myLocationMode)
        assertFalse(component.model.value.currentLocationMarker?.isPlaceholder ?: true)
    }

    @Test
    fun `current location marker is not stored as user created point`() {
        val component = createComponent()

        component.onLocationResult(LocationRequestResult.LocationResolved(latitude = 55.7, longitude = 37.6))

        assertTrue(component.model.value.mapState.points.isEmpty())
    }

    @Test
    fun `map tools placeholder actions close menu`() {
        val component = createComponent()

        component.onMapToolsClick()
        component.onAvailableMapsClick()
        assertFalse(component.model.value.isMapToolsMenuVisible)

        component.onMapToolsClick()
        component.onMapsOnScreenClick()
        assertFalse(component.model.value.isMapToolsMenuVisible)
    }

    @Test
    fun `zoom commands are enqueued and consumed`() {
        val component = createComponent()

        component.onZoomInClick()
        assertEquals(MapViewportCommand.ZoomIn, component.model.value.pendingViewportCommand)

        component.onViewportCommandConsumed()
        assertNull(component.model.value.pendingViewportCommand)

        component.onZoomOutClick()
        assertEquals(MapViewportCommand.ZoomOut, component.model.value.pendingViewportCommand)
    }

    @Test
    fun `point click opens info window from component state`() {
        val component = createComponent()

        component.onCameraIdle(defaultSnapshot())
        component.onCreatePointClick()
        component.onCreatePointTitleChange("Test point")
        component.onCreatePointConfirm()

        val point = component.model.value.mapState.points.single()
        component.onFeatureClick(
            featureKey = point.id,
            featureType = MapScreenComponent.FeatureType.POINT,
            anchor = MapScreenComponent.FeatureInfoWindowAnchor(screenX = 120, screenY = 240),
        )

        assertEquals(
            MapScreenComponent.FeatureInfoWindow(
                title = "Test point",
                createdAtText = "26.03.2026 10:00",
                anchor = MapScreenComponent.FeatureInfoWindowAnchor(screenX = 120, screenY = 240),
            ),
            component.model.value.selectedFeatureInfoWindow,
        )
    }

    @Test
    fun `camera idle clears visible feature info window`() {
        val component = createComponent()

        component.onCameraIdle(defaultSnapshot())
        component.onCreatePointClick()
        component.onCreatePointTitleChange("Test point")
        component.onCreatePointConfirm()

        val point = component.model.value.mapState.points.single()
        component.onFeatureClick(
            featureKey = point.id,
            featureType = MapScreenComponent.FeatureType.POINT,
            anchor = MapScreenComponent.FeatureInfoWindowAnchor(screenX = 120, screenY = 240),
        )

        component.onCameraIdle(
            MapCameraSnapshot(
                latitude = 55.76,
                longitude = 37.62,
                zoom = 12.0,
                bearing = 0.0,
            ),
        )

        assertNull(component.model.value.selectedFeatureInfoWindow)
    }

    @Test
    fun `line drawing requires two fixed vertices before details can open`() {
        val component = createComponent()

        component.onCameraIdle(defaultSnapshot())
        component.onCreateLineClick()

        component.onDrawingDetailsClick()
        assertFalse(component.model.value.isCreateShapeSheetVisible)

        component.onDrawingAddPositionClick()
        component.onDrawingDetailsClick()
        assertFalse(component.model.value.isCreateShapeSheetVisible)

        component.onCameraIdle(defaultSnapshot(latitude = 55.76, longitude = 37.62))
        component.onDrawingAddPositionClick()
        component.onDrawingDetailsClick()

        assertTrue(component.model.value.isCreateShapeSheetVisible)
    }

    @Test
    fun `line creation stores line and closes drawing flow`() {
        val component = createComponent()

        component.onCameraIdle(defaultSnapshot())
        component.onCreateLineClick()
        component.onDrawingAddPositionClick()
        component.onCameraIdle(defaultSnapshot(latitude = 55.76, longitude = 37.62))
        component.onDrawingAddPositionClick()
        component.onDrawingDetailsClick()
        component.onCreateShapeTitleChange("Route A")
        component.onCreateShapeConfirm()

        val line = component.model.value.mapState.lines.single()
        assertEquals("Route A", line.title)
        assertEquals(2, line.vertices.size)
        assertNull(component.model.value.shapeDrawingDraft)
        assertNull(component.model.value.drawingMode)
        assertFalse(component.model.value.isCreateShapeSheetVisible)
    }

    @Test
    fun `polygon creation stores polygon and can open shared info window`() {
        val component = createComponent()

        component.onCameraIdle(defaultSnapshot())
        component.onCreatePolygonClick()
        component.onDrawingAddPositionClick()
        component.onCameraIdle(defaultSnapshot(latitude = 55.76, longitude = 37.62))
        component.onDrawingAddPositionClick()
        component.onCameraIdle(defaultSnapshot(latitude = 55.77, longitude = 37.63))
        component.onDrawingAddPositionClick()
        component.onDrawingDetailsClick()
        component.onCreateShapeTitleChange("Area A")
        component.onCreateShapeConfirm()

        val polygon = component.model.value.mapState.polygons.single()
        assertEquals("Area A", polygon.title)
        assertEquals(3, polygon.vertices.size)

        component.onFeatureClick(
            featureKey = polygon.id,
            featureType = MapScreenComponent.FeatureType.POLYGON,
            anchor = MapScreenComponent.FeatureInfoWindowAnchor(screenX = 140, screenY = 260),
        )

        assertEquals(
            MapScreenComponent.FeatureInfoWindow(
                title = "Area A",
                createdAtText = "26.03.2026 10:00",
                anchor = MapScreenComponent.FeatureInfoWindowAnchor(screenX = 140, screenY = 260),
            ),
            component.model.value.selectedFeatureInfoWindow,
        )
    }

    private fun createComponent(): DefaultMapScreenComponent {
        var nextId = 0
        return DefaultMapScreenComponent(
            componentContext = DefaultComponentContext(LifecycleRegistry()),
            timeProvider = TimeProvider { 1_774_986_400_000L },
            featureIdProvider = FeatureIdProvider {
                nextId += 1
                "feature-$nextId"
            },
            featureInfoWindowStateMapper = DefaultMapFeatureInfoWindowStateMapper(
                createdAtFormatter = MapPointCreatedAtFormatter { "26.03.2026 10:00" },
            ),
        )
    }

    private fun defaultSnapshot(
        latitude: Double = 55.75,
        longitude: Double = 37.61,
    ): MapCameraSnapshot =
        MapCameraSnapshot(
            latitude = latitude,
            longitude = longitude,
            zoom = 12.0,
            bearing = 0.0,
        )
}
