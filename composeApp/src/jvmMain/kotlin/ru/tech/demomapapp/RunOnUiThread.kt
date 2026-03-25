package ru.tech.demomapapp

import javax.swing.SwingUtilities

internal fun <T> runOnUiThread(block: () -> T): T {
    var result: Result<T>? = null

    if (SwingUtilities.isEventDispatchThread()) {
        return block()
    }

    SwingUtilities.invokeAndWait {
        result = runCatching(block)
    }

    return requireNotNull(result).getOrThrow()
}
