package ru.tech.demomapapp.feature.map.impl

import ru.tech.demomapapp.feature.map.api.MapCameraSnapshot
import ru.tech.demomapapp.feature.map.api.MapScreenComponent
import ru.tech.demomapapp.feature.map.render.RenderDrawingPreview
import ru.tech.demomapapp.feature.map.render.RenderMapVertex

internal fun interface ShapeDrawingPreviewMapper {
    fun map(
        draft: MapScreenComponent.ShapeDrawingDraft?,
        currentSnapshot: MapCameraSnapshot?,
    ): RenderDrawingPreview?
}

internal object DefaultShapeDrawingPreviewMapper : ShapeDrawingPreviewMapper {
    override fun map(
        draft: MapScreenComponent.ShapeDrawingDraft?,
        currentSnapshot: MapCameraSnapshot?,
    ): RenderDrawingPreview? {
        val currentVertex = currentSnapshot?.toVertex() ?: return null
        val activeDraft = draft ?: return null
        val fixedVertices = activeDraft.fixedVertices.map { it.toRenderVertex() }
        if (fixedVertices.isEmpty()) {
            return null
        }

        val previewVertex = currentVertex.toRenderVertex()
        return when (activeDraft.mode) {
            MapScreenComponent.DrawingMode.LINE -> RenderDrawingPreview(
                fixedLineVertices = fixedVertices,
                previewLineVertices = listOfNotNull(fixedVertices.lastOrNull(), previewVertex),
            )

            MapScreenComponent.DrawingMode.POLYGON -> {
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

private fun ru.tech.demomapapp.feature.map.api.MapVertex.toRenderVertex(): RenderMapVertex =
    RenderMapVertex(
        latitude = latitude,
        longitude = longitude,
    )
