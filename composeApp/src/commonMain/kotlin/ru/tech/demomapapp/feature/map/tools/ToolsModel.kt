package ru.tech.demomapapp.feature.map.tools

/**
 * ToolsModel is now defined in the API package.
 * This typealias preserves backward compatibility.
 */
typealias ToolsModel = ru.tech.demomapapp.feature.map.api.ToolsModel

/**
 * Factory function to create ToolsModel with default values.
 * Note: MapScreenComponent.Model no longer contains tools-specific state
 * as part of MAP-API-002. Child components own their private state.
 */
fun createDefaultToolsModel(): ToolsModel = ToolsModel()