package ru.tech.demomapapp.feature.map.drawing

import ru.tech.demomapapp.feature.map.api.DrawingMode
import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapVertex
import ru.tech.demomapapp.feature.map.api.ShapeDrawingDraft
import ru.tech.demomapapp.feature.map.render.RenderDrawingPreview
import ru.tech.demomapapp.feature.map.render.RenderMapVertex

internal fun interface ShapeDrawingPreviewMapper {
    fun map(draft: ShapeDrawingDraft?, currentSnapshot: MapCameraSnapshot?): RenderDrawingPreview?
}

internal object DefaultShapeDrawingPreviewMapper : ShapeDrawingPreviewMapper {
    override fun map(draft: ShapeDrawingDraft?, currentSnapshot: MapCameraSnapshot?): RenderDrawingPreview? {
        val currentVertex = currentSnapshot?.toPreviewVertex() ?: return null
        val activeDraft = draft ?: return null
        val fixedVertices = activeDraft.fixedVertices.map { it.toRenderVertex() }
        if (fixedVertices.isEmpty()) {
            return null
        }

        val previewVertex = currentVertex.toRenderVertex()
        return when (activeDraft.mode) {
            DrawingMode.LINE -> RenderDrawingPreview(
                fixedLineVertices = fixedVertices,
                previewLineVertices = listOfNotNull(fixedVertices.lastOrNull(), previewVertex),
            )

            DrawingMode.POLYGON -> {
                val previewLineVertices = buildList {
                    if (fixedVertices.isNotEmpty()) {
                        add(fixedVertices.last())
                        add(previewVertex)
                    }
                    if (fixedVertices.size >= 2) {
                        add(fixedVertices.first())
                    }
                }
                RenderDrawingPreview(
                    previewLineVertices = previewLineVertices,
                    fixedPolygonVertices = if (fixedVertices.size >= 3) fixedVertices else emptyList(),
                )
            }
        }
    }
}

private fun ru.tech.demomapapp.feature.map.api.MapVertex.toRenderVertex(): RenderMapVertex = RenderMapVertex(
    latitude = latitude,
    longitude = longitude,
)

private fun MapCameraSnapshot.toPreviewVertex(): MapVertex = MapVertex(
    latitude = latitude,
    longitude = longitude,
)
