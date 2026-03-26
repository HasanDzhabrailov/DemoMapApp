package ru.tech.demomapapp

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.arkivanov.decompose.defaultComponentContext
import org.maplibre.android.MapLibre
import ru.tech.demomapapp.app.App
import ru.tech.demomapapp.root.api.RootComponent
import ru.tech.demomapapp.root.impl.createRootComponent

class MainActivity : ComponentActivity() {
    private lateinit var rootComponent: RootComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.BLACK),
            navigationBarStyle = SystemBarStyle.dark(Color.BLACK),
        )
        super.onCreate(savedInstanceState)

        MapLibre.getInstance(applicationContext)

        rootComponent = createRootComponent(
            componentContext = defaultComponentContext(),
        )

        setContent {
            App(component = rootComponent)
        }
    }
}
