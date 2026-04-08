package ru.tech.demomapapp.feature.map

import kotlin.test.Test
import kotlin.test.assertEquals
import ru.tech.demomapapp.feature.map.api.MapLayerEntry
import ru.tech.demomapapp.feature.map.api.MapLayerSourceRef
import ru.tech.demomapapp.feature.map.api.MapLine
import ru.tech.demomapapp.feature.map.api.MapPoint
import ru.tech.demomapapp.feature.map.api.MapPolygon
import ru.tech.demomapapp.feature.map.api.MapState
import ru.tech.demomapapp.feature.map.api.MapStyle
import ru.tech.demomapapp.feature.map.api.MapVertex
import ru.tech.demomapapp.feature.map.mapscreen.toRenderModel
import ru.tech.demomapapp.feature.map.render.RenderMapStyle

class MapRenderModelMapperTest {

    @Test
    fun `render model keeps tile layers and user geometry together`() {
        val state = MapState(
            style = MapStyle.OPEN_STREET_MAP,
            overlayLayers = listOf(
                MapLayerEntry(
                    id = "dem-overlay",
                    title = "DEM Overlay",
                    source = MapLayerSourceRef.RasterTileTemplate(
                        templateId = "dem-overlay",
                    ),
                    opacity = 0.47f,
                ),
            ),
            points = listOf(
                MapPoint(
                    id = "point-1",
                    latitude = 55.75,
                    longitude = 37.61,
                    title = "Point A",
                    createdAtEpochMillis = 1L,
                ),
            ),
            lines = listOf(
                MapLine(
                    id = "line-1",
                    vertices = listOf(
                        MapVertex(latitude = 55.75, longitude = 37.61),
                        MapVertex(latitude = 55.76, longitude = 37.62),
                    ),
                    title = "Line A",
                    createdAtEpochMillis = 1L,
                ),
            ),
            polygons = listOf(
                MapPolygon(
                    id = "polygon-1",
                    vertices = listOf(
                        MapVertex(latitude = 55.75, longitude = 37.61),
                        MapVertex(latitude = 55.76, longitude = 37.62),
                        MapVertex(latitude = 55.77, longitude = 37.63),
                    ),
                    title = "Polygon A",
                    createdAtEpochMillis = 1L,
                ),
            ),
        )

        val renderModel = state.toRenderModel()

        assertEquals(RenderMapStyle.OPEN_STREET_MAP, renderModel.style)
        assertEquals(1, renderModel.tileLayers.size)
        assertEquals("DEM Overlay", renderModel.tileLayers.single().title)
        assertEquals(0.47f, renderModel.tileLayers.single().opacity)
        assertEquals(1, renderModel.points.size)
        assertEquals(1, renderModel.lines.size)
        assertEquals(1, renderModel.polygons.size)
        assertEquals("Point A", renderModel.points.single().label)
        assertEquals("Line A", renderModel.lines.single().label)
        assertEquals("Polygon A", renderModel.polygons.single().label)
    }
}
