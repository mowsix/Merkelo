package com.merqueloapp.ui.screens

import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.merqueloapp.ui.components.AppBottomBar
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.FolderOverlay
import org.osmdroid.views.overlay.Marker

/* ---------- Datos quemados (Medellín) ---------- */

private data class Poi(val name: String, val lat: Double, val lon: Double)

private val CENTER_MEDELLIN = GeoPoint(6.2442, -75.5812)

private val MED_STORES: Map<String, List<Poi>> = mapOf(
    "D1" to listOf(
        Poi("D1 - Laureles", 6.2453, -75.5897),
        Poi("D1 - Poblado 10", 6.2089, -75.5674),
        Poi("D1 - Robledo", 6.2790, -75.5880),
        Poi("D1 - Belén", 6.2256, -75.5964)
    ),
    "Ara" to listOf(
        Poi("Ara - San Juan", 6.2468, -75.5830),
        Poi("Ara - Manrique", 6.2752, -75.5595),
        Poi("Ara - Castilla", 6.2835, -75.5799)
    ),
    "Éxito" to listOf(
        Poi("Éxito - San Antonio", 6.2487, -75.5707),
        Poi("Éxito - Poblado", 6.2037, -75.5670),
        Poi("Éxito - Colombia", 6.2423, -75.5739)
    ),
    "Carulla" to listOf(
        Poi("Carulla - Laureles", 6.2450, -75.5976),
        Poi("Carulla - Poblado", 6.2054, -75.5718)
    ),
    "Euro" to listOf(
        Poi("Euro - La 33", 6.2344, -75.5871),
        Poi("Euro - Estadio", 6.2588, -75.5921)
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreenOSM(
    currentRoute: String,
    onSelectTab: (String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    // 1) Mapa "recordado" (no se recrea en cada recomposición)
    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            controller.setZoom(12.0)
            controller.setCenter(CENTER_MEDELLIN)
        }
    }

    // 2) Overlay reusado (mejor perf)
    val folderOverlay = remember { FolderOverlay() }

    // 3) Filtro
    var selectedChain by remember { mutableStateOf(MED_STORES.keys.first()) }

    // 4) Marcadores cuando cambia el filtro
    LaunchedEffect(selectedChain) {
        val points = MED_STORES[selectedChain].orEmpty()
        // Reutilizamos el folder overlay
        if (!mapView.overlays.contains(folderOverlay)) {
            mapView.overlays.add(folderOverlay)
        }
        folderOverlay.items.clear()
        points.forEach { p -> folderOverlay.add(marker(mapView, p)) }

        // Encadre después del layout
        mapView.post {
            if (points.isNotEmpty()) {
                val box = BoundingBox.fromGeoPoints(points.map { GeoPoint(it.lat, it.lon) })
                mapView.zoomToBoundingBox(box, true, 100)
            } else {
                mapView.controller.setCenter(CENTER_MEDELLIN)
                mapView.controller.setZoom(12.0)
            }
            mapView.invalidate()
        }
    }

    // 5) Limpieza al salir
    DisposableEffect(Unit) {
        onDispose { mapView.onDetach() }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Mapa de tiendas (${selectedChain})") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                        }
                    }
                )
                // Chips SIEMPRE visibles arriba
                ChainsRow(
                    chains = MED_STORES.keys.toList(),
                    selected = selectedChain,
                    onSelect = { selectedChain = it }
                )
            }
        },
        bottomBar = { AppBottomBar(currentRoute = currentRoute, onSelect = onSelectTab) }
    ) { inner ->
        // El mapa ocupa el resto
        AndroidView(
            factory = { mapView },
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
        )
    }
}

@Composable
private fun ChainsRow(
    chains: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        chains.forEach { c ->
            FilterChip(
                selected = c == selected,
                onClick = { onSelect(c) },
                label = { Text(c) }
            )
        }
    }
}

private fun marker(mapView: MapView, poi: Poi): Marker =
    Marker(mapView).apply {
        position = GeoPoint(poi.lat, poi.lon)
        title = poi.name
        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
    }
