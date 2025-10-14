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
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults

/* ---------- Datos quemados (Medellín) ---------- */

private data class Poi(val name: String, val lat: Double, val lon: Double)

private val CENTER_MEDELLIN = GeoPoint(6.2442, -75.5812)

private val MED_STORES: Map<String, List<Poi>> = mapOf(
    "D1" to listOf(
        Poi("D1 - Laureles Nutibara", 6.2453, -75.5897),
        Poi("D1 - Laureles Segundo Parque", 6.2476, -75.5962),
        Poi("D1 - Estadio", 6.2553, -75.5920),
        Poi("D1 - La América", 6.2512, -75.6006),
        Poi("D1 - San Juan", 6.2469, -75.5832),
        Poi("D1 - Boston", 6.2449, -75.5634),
        Poi("D1 - San Diego", 6.2335, -75.5690),
        Poi("D1 - Prado", 6.2529, -75.5698),
        Poi("D1 - Robledo", 6.2819, -75.5944),
        Poi("D1 - Castilla", 6.2865, -75.5790),
        Poi("D1 - Doce de Octubre", 6.3028, -75.5664),
        Poi("D1 - Manrique Central", 6.2806, -75.5572),
        Poi("D1 - Aranjuez", 6.2763, -75.5658),
        Poi("D1 - Villa Hermosa", 6.2505, -75.5502),
        Poi("D1 - Guayabal", 6.2164, -75.5929),
        Poi("D1 - Belén Rosales", 6.2290, -75.5885),
        Poi("D1 - Belén La Mota", 6.2050, -75.6045),
        Poi("D1 - San Javier", 6.2570, -75.6190),
        Poi("D1 - Poblado 10", 6.2089, -75.5674),
        Poi("D1 - Milla de Oro", 6.1990, -75.5650)
    ),
    "Ara" to listOf(
        Poi("Ara - San Juan", 6.2468, -75.5830),
        Poi("Ara - La Floresta", 6.2598, -75.6015),
        Poi("Ara - Carlos E. Restrepo", 6.2560, -75.5845),
        Poi("Ara - Prado", 6.2522, -75.5689),
        Poi("Ara - Boston", 6.2449, -75.5635),
        Poi("Ara - Buenos Aires", 6.2407, -75.5557),
        Poi("Ara - Manrique", 6.2752, -75.5595),
        Poi("Ara - Campo Valdés", 6.2760, -75.5582),
        Poi("Ara - Castilla", 6.2835, -75.5799),
        Poi("Ara - Robledo Cucaracho", 6.2870, -75.6020),
        Poi("Ara - Guayabal", 6.2166, -75.5934),
        Poi("Ara - San Diego", 6.2336, -75.5688)
    ),
    "Éxito" to listOf(
        Poi("Éxito - San Antonio", 6.2487, -75.5707),
        Poi("Éxito - Colombia", 6.2423, -75.5739),
        Poi("Éxito - La 33", 6.2352, -75.5830),
        Poi("Éxito - Unicentro", 6.2459, -75.5891),
        Poi("Éxito - Floresta", 6.2640, -75.5950),
        Poi("Éxito - Laureles", 6.2480, -75.5940),
        Poi("Éxito - Robledo", 6.2790, -75.5940),
        Poi("Éxito - San Juan", 6.2460, -75.5825),
        Poi("Éxito - Poblado", 6.2037, -75.5670),
        Poi("Éxito - San Diego", 6.2318, -75.5655)
    ),
    "Carulla" to listOf(
        Poi("Carulla - Laureles", 6.2450, -75.5976),
        Poi("Carulla - Laureles Segundo Parque", 6.2479, -75.5960),
        Poi("Carulla - Poblado (Oviedo)", 6.2019, -75.5699),
        Poi("Carulla - Santa María de los Ángeles", 6.1990, -75.5720),
        Poi("Carulla - Las Palmas", 6.2135, -75.5570),
        Poi("Carulla - San Diego", 6.2330, -75.5660),
        Poi("Carulla - Prado", 6.2558, -75.5680),
        Poi("Carulla - La 10", 6.2105, -75.5685)
    ),
    "Euro" to listOf(
        Poi("Euro - La 33", 6.2344, -75.5871),
        Poi("Euro - Estadio", 6.2588, -75.5921),
        Poi("Euro - Laureles", 6.2462, -75.5945),
        Poi("Euro - La América", 6.2520, -75.6030),
        Poi("Euro - Belén La Palma", 6.2310, -75.6000),
        Poi("Euro - Boston", 6.2450, -75.5610),
        Poi("Euro - Robledo", 6.2820, -75.5960),
        Poi("Euro - Manrique", 6.2790, -75.5550),
        Poi("Euro - San Javier", 6.2570, -75.6185),
        Poi("Euro - San Juan", 6.2465, -75.5827)
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
    val chipRed = Color(0xFFCE2B2B)      // rojo marca
    val chipRedDark = Color(0xFF9E1F1F)  // rojo seleccionado
    val white = Color(0xFFFFFFFF)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        chains.forEach { c ->
            val isSelected = c == selected
            FilterChip(
                selected = isSelected,
                onClick = { onSelect(c) },
                label = { Text(c) },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = chipRed,
                    labelColor = white,
                    selectedContainerColor = chipRedDark,
                    selectedLabelColor = white
                )
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
