package ru.tech.demomapapp.map

internal class StyleLoadCoordinator {
    private var pendingStyleUrl: String? = null
    private var currentStyleUrl: String? = null

    fun onLoadRequested(styleUrl: String, hasCurrentStyle: Boolean): LoadRequestDecision {
        if (pendingStyleUrl == styleUrl) {
            return LoadRequestDecision.AwaitPending
        }

        val cancelPending = pendingStyleUrl != null
        pendingStyleUrl = null

        if (currentStyleUrl == styleUrl && hasCurrentStyle) {
            return LoadRequestDecision.UseCurrent(cancelPending = cancelPending)
        }

        pendingStyleUrl = styleUrl
        return LoadRequestDecision.StartNew(cancelPending = cancelPending)
    }

    fun shouldAcceptLoadedStyle(styleUrl: String): Boolean = pendingStyleUrl == styleUrl

    fun onLoadCompleted(styleUrl: String) {
        if (pendingStyleUrl == styleUrl) {
            pendingStyleUrl = null
            currentStyleUrl = styleUrl
        }
    }

    fun onLoadCancelled(styleUrl: String) {
        if (pendingStyleUrl == styleUrl) {
            pendingStyleUrl = null
        }
    }

    fun reset() {
        pendingStyleUrl = null
        currentStyleUrl = null
    }
}

internal sealed interface LoadRequestDecision {
    data object AwaitPending : LoadRequestDecision

    data class UseCurrent(
        val cancelPending: Boolean,
    ) : LoadRequestDecision

    data class StartNew(
        val cancelPending: Boolean,
    ) : LoadRequestDecision
}
