package ru.tech.demomapapp.feature.map.impl

import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import ru.tech.demomapapp.feature.map.api.LocationRequestResult
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapLayerEntry
import ru.tech.demomapapp.feature.map.api.MapLayerSourceRef
import ru.tech.demomapapp.feature.map.api.MapLocationRequest
import ru.tech.demomapapp.feature.map.api.MapPoint
import ru.tech.demomapapp.feature.map.api.MapScreenComponent
import ru.tech.demomapapp.feature.map.api.MapState
import ru.tech.demomapapp.feature.map.api.MapStyle
import ru.tech.demomapapp.feature.map.api.MapVertex
import ru.tech.demomapapp.feature.map.api.MapViewportCommand
import ru.tech.demomapapp.feature.map.api.MyLocationMode
import ru.tech.demomapapp.feature.map.host.DefaultMapScreenComponent
import ru.tech.demomapapp.feature.map.impl.drawing.DrawingStoreFactory
import ru.tech.demomapapp.feature.map.impl.router.MapRouterStoreFactory

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
    fun `my location click cancels pending gps request and keeps manual placeholder`() {
        val component = createComponent()

        component.onCameraIdle(defaultSnapshot(latitude = 59.0, longitude = 30.0))
        component.onGpsToggle()
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
    fun `ruler enable uses gps marker when available`() {
        val component = createComponent()

        component.onCameraIdle(defaultSnapshot(latitude = 59.0, longitude = 30.0))
        component.onLocationResult(LocationRequestResult.LocationResolved(latitude = 55.7, longitude = 37.6))
        component.onViewportCommandConsumed()
        component.onRulerToggle()

        val rulerMeasurement = assertNotNull(component.model.value.rulerMeasurement)
        assertTrue(component.model.value.isRulerEnabled)
        assertEquals(55.7, rulerMeasurement.startLatitude)
        assertEquals(37.6, rulerMeasurement.startLongitude)
        assertEquals(59.0, rulerMeasurement.endLatitude)
        assertEquals(30.0, rulerMeasurement.endLongitude)
        assertTrue(component.model.value.rulerInfoWindow?.trueAzimuthText?.startsWith("A = ") == true)
    }

    @Test
    fun `ruler enable uses existing manual marker when available`() {
        val component = createComponent()

        component.onCameraIdle(defaultSnapshot(latitude = 59.0, longitude = 30.0))
        component.onMyLocationClick()
        component.onCameraIdle(defaultSnapshot(latitude = 59.1, longitude = 30.1))
        component.onRulerToggle()

        val rulerMeasurement = assertNotNull(component.model.value.rulerMeasurement)
        assertEquals(59.0, rulerMeasurement.startLatitude)
        assertEquals(30.0, rulerMeasurement.startLongitude)
        assertEquals(59.1, rulerMeasurement.endLatitude)
        assertEquals(30.1, rulerMeasurement.endLongitude)
    }

    @Test
    fun `ruler enable uses internal fallback without mutating shared location state`() {
        val component = createComponent()

        component.onCameraIdle(defaultSnapshot(latitude = 59.0, longitude = 30.0))
        component.onRulerToggle()

        assertEquals(MyLocationMode.OFF, component.model.value.myLocationMode)
        assertNull(component.model.value.currentLocationMarker)
        assertEquals("0 м", component.model.value.rulerInfoWindow?.distanceText)
    }

    @Test
    fun `ruler waits for first snapshot before creating fallback measurement`() {
        val component = createComponent()

        component.onRulerToggle()

        assertTrue(component.model.value.isRulerEnabled)
        assertNull(component.model.value.currentLocationMarker)
        assertNull(component.model.value.rulerMeasurement)

        component.onCameraIdle(defaultSnapshot(latitude = 59.0, longitude = 30.0))

        assertEquals(MyLocationMode.OFF, component.model.value.myLocationMode)
        assertNull(component.model.value.currentLocationMarker)
        assertNotNull(component.model.value.rulerMeasurement)
    }

    @Test
    fun `ruler flow does not use shared feature info window state`() {
        val component = createComponent()

        component.onCameraIdle(defaultSnapshot(latitude = 59.0, longitude = 30.0))
        component.onRulerToggle()

        assertNotNull(component.model.value.rulerInfoWindow)
        assertNull(component.model.value.selectedFeatureInfoWindow)
    }

    @Test
    fun `ruler recalculates on camera updates while active`() {
        val component = createComponent()

        component.onCameraIdle(defaultSnapshot(latitude = 59.0, longitude = 30.0))
        component.onMyLocationClick()
        component.onRulerToggle()
        component.onCameraIdle(defaultSnapshot(latitude = 59.2, longitude = 30.2))

        val rulerMeasurement = assertNotNull(component.model.value.rulerMeasurement)
        assertEquals(59.0, rulerMeasurement.startLatitude)
        assertEquals(30.0, rulerMeasurement.startLongitude)
        assertEquals(59.2, rulerMeasurement.endLatitude)
        assertEquals(30.2, rulerMeasurement.endLongitude)
    }

    @Test
    fun `successful gps result updates active ruler origin`() {
        val component = createComponent()

        component.onCameraIdle(defaultSnapshot(latitude = 59.0, longitude = 30.0))
        component.onRulerToggle()
        component.onLocationResult(LocationRequestResult.LocationResolved(latitude = 55.7, longitude = 37.6))

        val rulerMeasurement = assertNotNull(component.model.value.rulerMeasurement)
        assertEquals(MyLocationMode.GPS, component.model.value.myLocationMode)
        assertFalse(component.model.value.currentLocationMarker?.isPlaceholder ?: true)
        assertEquals(55.7, rulerMeasurement.startLatitude)
        assertEquals(37.6, rulerMeasurement.startLongitude)
        assertEquals(59.0, rulerMeasurement.endLatitude)
        assertEquals(30.0, rulerMeasurement.endLongitude)
    }

    @Test
    fun `ruler disable clears measurement state`() {
        val component = createComponent()

        component.onCameraIdle(defaultSnapshot(latitude = 59.0, longitude = 30.0))
        component.onRulerToggle()
        component.onRulerToggle()

        assertFalse(component.model.value.isRulerEnabled)
        assertNull(component.model.value.rulerMeasurement)
        assertNull(component.model.value.rulerInfoWindow)
    }

    @Test
    fun `ruler measurement is not stored as user created line`() {
        val component = createComponent()

        component.onCameraIdle(defaultSnapshot(latitude = 59.0, longitude = 30.0))
        component.onRulerToggle()

        assertTrue(component.model.value.mapState.lines.isEmpty())
    }

    @Test
    fun `current location marker is not stored as user created point`() {
        val component = createComponent()

        component.onLocationResult(LocationRequestResult.LocationResolved(latitude = 55.7, longitude = 37.6))

        assertTrue(component.model.value.mapState.points.isEmpty())
    }

    @Test
    fun `map tools layer actions open new overlays`() {
        val component = createComponent()

        component.onMapToolsClick()
        component.onAvailableMapsClick()
        assertFalse(component.model.value.isMapToolsMenuVisible)
        assertTrue(component.model.value.isAvailableMapsSheetVisible)

        component.onAvailableMapSelect("google-overlay")
        assertEquals("Google Map", component.model.value.selectedAvailableMap?.title)

        component.onAvailableMapConfirm()
        assertTrue(component.model.value.isMapsOnScreenSheetVisible)
        assertEquals(1, component.model.value.mapState.overlayLayers.size)

        val layerId = component.model.value.mapState.overlayLayers.single().id
        component.onMapLayerActionsClick(layerId)
        assertEquals(layerId, component.model.value.selectedOverlayLayer?.id)
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
    fun `feature info window dismiss clears shared info window`() {
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

        component.onFeatureInfoWindowDismiss()

        assertNull(component.model.value.selectedFeatureInfoWindow)
    }

    @Test
    fun `invalid point input does not create point and keeps draft open`() {
        val component = createComponent()

        component.onCameraIdle(defaultSnapshot())
        component.onCreatePointClick()
        component.onCreatePointLatitudeChange("invalid")
        component.onCreatePointTitleChange("Test point")
        component.onCreatePointConfirm()

        assertTrue(component.model.value.mapState.points.isEmpty())
        assertTrue(component.model.value.isCreatePointSheetVisible)
        assertEquals("invalid", component.model.value.createPointDraft?.latitudeInput)
    }

    @Test
    fun `point sheet dismiss clears draft`() {
        val component = createComponent()

        component.onCameraIdle(defaultSnapshot())
        component.onCreatePointClick()
        component.onCreatePointTitleChange("Test point")
        component.onCreatePointSheetDismiss()

        assertFalse(component.model.value.isCreatePointSheetVisible)
        assertNull(component.model.value.createPointDraft)
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
    fun `drawing remove last position updates draft in store-backed flow`() {
        val component = createComponent()

        component.onCameraIdle(defaultSnapshot())
        component.onCreateLineClick()
        component.onDrawingAddPositionClick()
        component.onCameraIdle(defaultSnapshot(latitude = 55.76, longitude = 37.62))
        component.onDrawingAddPositionClick()
        component.onDrawingRemoveLastPositionClick()

        assertEquals(1, component.model.value.shapeDrawingDraft?.fixedVertices?.size)
    }

    @Test
    fun `drawing dismiss clears draft and closes shape sheet`() {
        val component = createComponent()

        component.onCameraIdle(defaultSnapshot())
        component.onCreatePolygonClick()
        component.onDrawingAddPositionClick()
        component.onCameraIdle(defaultSnapshot(latitude = 55.76, longitude = 37.62))
        component.onDrawingAddPositionClick()
        component.onCameraIdle(defaultSnapshot(latitude = 55.77, longitude = 37.63))
        component.onDrawingAddPositionClick()
        component.onDrawingDetailsClick()
        component.onDrawingDismiss()

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

    @Test
    fun `line creation stores line and can open shared info window`() {
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
        component.onFeatureClick(
            featureKey = line.id,
            featureType = MapScreenComponent.FeatureType.LINE,
            anchor = MapScreenComponent.FeatureInfoWindowAnchor(screenX = 130, screenY = 250),
        )

        assertEquals(
            MapScreenComponent.FeatureInfoWindow(
                title = "Route A",
                createdAtText = "26.03.2026 10:00",
                anchor = MapScreenComponent.FeatureInfoWindowAnchor(screenX = 130, screenY = 250),
            ),
            component.model.value.selectedFeatureInfoWindow,
        )
    }

    @Test
    fun `initial style and overlay layers are preserved`() {
        val overlay = MapLayerEntry(
            id = "overlay-1",
            title = "Overlay",
            source = MapLayerSourceRef.RasterTileTemplate(templateId = "overlay-template"),
            opacity = 0.6f,
        )

        val component = createComponent(
            initialModel = MapScreenComponent.Model(
                mapState = MapState(
                    style = MapStyle.OPEN_STREET_MAP,
                    overlayLayers = listOf(overlay),
                ),
            ),
        )

        assertEquals(MapStyle.OPEN_STREET_MAP, component.model.value.mapState.style)
        assertEquals(listOf(overlay), component.model.value.mapState.overlayLayers)
    }

    @Test
    fun `initial drawing state is preserved`() {
        val point = MapPoint(
            id = "point-1",
            title = "Restored point",
            latitude = 55.7,
            longitude = 37.6,
            createdAtEpochMillis = 1_774_986_400_000L,
        )
        val component = createComponent(
            initialModel = MapScreenComponent.Model(
                mapState = MapState(points = listOf(point)),
                isCreatePointSheetVisible = true,
                createPointDraft = MapScreenComponent.CreatePointDraft(
                    latitudeInput = "55.7",
                    longitudeInput = "37.6",
                    titleInput = "Draft point",
                ),
                drawingMode = MapScreenComponent.DrawingMode.POLYGON,
                shapeDrawingDraft = MapScreenComponent.ShapeDrawingDraft(
                    mode = MapScreenComponent.DrawingMode.POLYGON,
                    fixedVertices = listOf(MapVertex(latitude = 55.7, longitude = 37.6)),
                    titleInput = "Draft polygon",
                ),
                isCreateShapeSheetVisible = true,
                lastCameraSnapshot = defaultSnapshot(latitude = 55.7, longitude = 37.6),
            ),
        )

        assertEquals(listOf(point), component.model.value.mapState.points)
        assertTrue(component.model.value.isCreatePointSheetVisible)
        assertEquals("Draft point", component.model.value.createPointDraft?.titleInput)
        assertEquals(MapScreenComponent.DrawingMode.POLYGON, component.model.value.drawingMode)
        assertEquals("Draft polygon", component.model.value.shapeDrawingDraft?.titleInput)
        assertTrue(component.model.value.isCreateShapeSheetVisible)
    }

    @Test
    fun `initial location and viewport state are preserved`() {
        val snapshot = defaultSnapshot(latitude = 59.0, longitude = 30.0)
        val marker = ru.tech.demomapapp.feature.map.api.MapLocationMarker(
            latitude = 59.0,
            longitude = 30.0,
            isPlaceholder = true,
        )
        val component = createComponent(
            initialModel = MapScreenComponent.Model(
                lastCameraSnapshot = snapshot,
                myLocationMode = MyLocationMode.MANUAL_PLACEHOLDER,
                currentLocationMarker = marker,
                pendingLocationRequest = MapLocationRequest.EnableGpsLocationRequest,
                pendingViewportCommand = MapViewportCommand.MoveTo(latitude = 59.0, longitude = 30.0),
                isCenterMarkerMenuVisible = true,
            ),
        )

        assertEquals(snapshot, component.model.value.lastCameraSnapshot)
        assertEquals(MyLocationMode.MANUAL_PLACEHOLDER, component.model.value.myLocationMode)
        assertEquals(marker, component.model.value.currentLocationMarker)
        assertEquals(MapLocationRequest.EnableGpsLocationRequest, component.model.value.pendingLocationRequest)
        assertEquals(
            MapViewportCommand.MoveTo(latitude = 59.0, longitude = 30.0),
            component.model.value.pendingViewportCommand,
        )
        assertTrue(component.model.value.isCenterMarkerMenuVisible)
    }

    private fun createComponent(
        initialModel: MapScreenComponent.Model = MapScreenComponent.Model(),
    ): DefaultMapScreenComponent {
        var nextId = 0
        return DefaultMapScreenComponent(
            componentContext = DefaultComponentContext(LifecycleRegistry()),
            initialModel = initialModel,
            mapRouterStoreFactory = MapRouterStoreFactory(
                featureInfoWindowStateMapper = DefaultMapFeatureInfoWindowStateMapper(
                    createdAtFormatter = MapPointCreatedAtFormatter { "26.03.2026 10:00" },
                ),
            ),
            drawingStoreFactory = DrawingStoreFactory(
                createMapPointUseCase = DefaultCreateMapPointUseCase(),
                createMapLineUseCase = DefaultCreateMapLineUseCase(),
                createMapPolygonUseCase = DefaultCreateMapPolygonUseCase(),
                timeProvider = { 1_774_986_400_000L },
                featureIdProvider = {
                    nextId += 1
                    "feature-$nextId"
                },
            ),
        )
    }

    private fun defaultSnapshot(latitude: Double = 55.75, longitude: Double = 37.61): MapCameraSnapshot =
        MapCameraSnapshot(
            latitude = latitude,
            longitude = longitude,
            zoom = 12.0,
            bearing = 0.0,
        )
}
