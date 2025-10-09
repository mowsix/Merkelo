package com.merqueloapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.merqueloapp.navigation.Routes
import com.merqueloapp.ui.components.AppBottomBar
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Text


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreenOSM(
    currentRoute: String = Routes.MAP,
    onBack: () -> Unit,
    onSelectTab: (String) -> Unit
) {
    val context = LocalContext.current

    // Cadenas
    val chains = listOf("D1", "Ara", "Éxito", "Carulla", "Euro", "supermu")
    var selectedChain by remember { mutableStateOf(chains.first()) }

    // Puntos "quemados" por cadena (Medellín)
    val pins = remember(selectedChain) { HARD_PINS_MEDELLIN[selectedChain].orEmpty() }

    // Crea el MapView una sola vez
    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(12.0)
            controller.setCenter(GeoPoint(6.2442, -75.5812)) // Centro Medellín
        }
    }

    // Función para dibujar marcadores y encuadrar
    fun renderMarkers() {
        // quita los markers previos
        mapView.overlays.removeAll { it is Marker }

        val geoPoints = pins.map { GeoPoint(it.lat, it.lon) }
        pins.forEach { pin ->
            val m = Marker(mapView).apply {
                position = GeoPoint(pin.lat, pin.lon)
                title = "${selectedChain} - ${pin.label}"
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            }
            mapView.overlays.add(m)
        }
        if (geoPoints.isNotEmpty()) {
            val bbox = BoundingBox.fromGeoPoints(geoPoints)
            // padding en píxeles para ver todos los pines
            mapView.zoomToBoundingBox(bbox, true, 100)
        } else {
            mapView.controller.setZoom(12.0)
            mapView.controller.setCenter(GeoPoint(6.2442, -75.5812))
        }
        mapView.invalidate()
    }

    // Redibuja cuando cambia el filtro
    LaunchedEffect(selectedChain) { renderMarkers() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mapa de tiendas (${selectedChain})") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        },
        bottomBar = { AppBottomBar(currentRoute = currentRoute, onSelect = onSelectTab) }
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
        ) {
            // Filtro por cadena
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                chains.forEach { c ->
                    FilterChip(
                        selected = selectedChain == c,
                        onClick = { selectedChain = c },
                        label = { Text(c) }
                    )
                }
            }

            // El mapa real (OSMDroid)
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { mapView },
                update = { /* ya actualizamos con renderMarkers() en LaunchedEffect */ }
            )
        }
    }
}

/* --------- Datos quemados Medellín (lat/lon aprox por barrio) --------- */
private data class StorePin(val label: String, val lat: Double, val lon: Double)

private val HARD_PINS_MEDELLIN: Map<String, List<StorePin>> = mapOf(
    "D1" to listOf(
        StorePin("Laureles/Estadio", 6.2447, -75.5891),
        StorePin("El Poblado",       6.2089, -75.5670),
        StorePin("Belén",            6.2317, -75.6066),
        StorePin("Robledo",          6.2770, -75.5930),
        StorePin("Buenos Aires",     6.2405, -75.5600)
    ),
    "Ara" to listOf(
        StorePin("Buenos Aires",     6.2380, -75.5560),
        StorePin("Manrique",         6.2788, -75.5565),
        StorePin("Castilla",         6.2860, -75.5720),
        StorePin("Guayabal",         6.2098, -75.5892),
        StorePin("San Javier",       6.2486, -75.6227)
    ),
    "Éxito" to listOf(
        StorePin("Poblado",            6.2079, -75.5679),
        StorePin("Laureles",           6.2444, -75.5890),
        StorePin("San Antonio/Centro", 6.2476, -75.5690),
        StorePin("San Ignacio",        6.2516, -75.5680),
        StorePin("Belén",              6.2317, -75.6066)
    ),
    "Carulla" to listOf(
        StorePin("El Poblado",       6.2095, -75.5665),
        StorePin("Laureles",         6.2438, -75.5900),
        StorePin("Ciudad del Río",   6.2219, -75.5746),
        StorePin("Las Palmas (baja)",6.2100, -75.5510),
        StorePin("Boston",           6.2459, -75.5609)
    ),
    "Euro" to listOf(
        StorePin("Laureles/Nutibara", 6.2449, -75.5882),
        StorePin("Loma de los Bernal",6.2210, -75.6060),
        StorePin("La Floresta",       6.2550, -75.6040),
        StorePin("La América",        6.2520, -75.6070)
    ),
    "supermu" to listOf(
        StorePin("Belén (demo)",     6.2290, -75.6040),
        StorePin("Aranjuez (demo)",  6.2770, -75.5585)
    )
)
