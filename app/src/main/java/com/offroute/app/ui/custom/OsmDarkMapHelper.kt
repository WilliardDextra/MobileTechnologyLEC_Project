package com.offroute.app.ui.custom

import android.content.Context
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import androidx.core.content.ContextCompat
import com.offroute.app.R
import com.offroute.app.data.GeoPointData
import com.offroute.app.data.HazardType
import com.offroute.app.data.HazardZone
import com.offroute.app.data.ShelterPoint
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.Polyline

object OsmDarkMapHelper {

    fun initOsmConfig(context: Context) {
        Configuration.getInstance().userAgentValue = context.packageName
    }

    /**
     * Applies dark tactical styling on OSMdroid tiles using a custom ColorMatrix.
     * Inverts light tile colors into dark slate/charcoal tones without requiring API keys or online styling.
     */
    fun applyDarkTacticalTheme(mapView: MapView) {
        mapView.setMultiTouchControls(true)
        mapView.setBuiltInZoomControls(false)
        mapView.setBackgroundColor(Color.parseColor("#0D0E12"))

        // Tactical Dark Matrix: inverts bright white tiles to deep charcoal/slate tones
        val darkTacticalMatrix = ColorMatrix(floatArrayOf(
            -0.75f, 0f, 0f, 0f, 180f,  // R
            0f, -0.75f, 0f, 0f, 190f,  // G
            0f, 0f, -0.70f, 0f, 215f,  // B
            0f, 0f, 0f, 1.0f, 0f       // A
        ))

        mapView.overlayManager.tilesOverlay.setColorFilter(ColorMatrixColorFilter(darkTacticalMatrix))
    }

    /**
     * Renders a tactical Hazard Circle with translucent fill & alert marker.
     */
    fun addHazardCircle(mapView: MapView, hazard: HazardZone) {
        val center = GeoPoint(hazard.latitude, hazard.longitude)
        val circlePolygon = Polygon(mapView)
        circlePolygon.points = Polygon.pointsAsCircle(center, hazard.radiusMeters)

        val strokeColor: Int
        val fillColor: Int
        val markerIconRes: Int

        when (hazard.type) {
            HazardType.FIRE -> {
                strokeColor = Color.parseColor("#FF1E42")
                fillColor = Color.parseColor("#44FF1E42")
                markerIconRes = R.drawable.ic_marker_hazard
            }
            HazardType.FLOOD -> {
                strokeColor = Color.parseColor("#3B82F6")
                fillColor = Color.parseColor("#443B82F6")
                markerIconRes = R.drawable.ic_marker_hazard
            }
            HazardType.LANDSLIDE, HazardType.BLOCKAGE -> {
                strokeColor = Color.parseColor("#FBBF24")
                fillColor = Color.parseColor("#44FBBF24")
                markerIconRes = R.drawable.ic_marker_hazard_amber
            }
        }

        circlePolygon.fillPaint.color = fillColor
        circlePolygon.outlinePaint.color = strokeColor
        circlePolygon.outlinePaint.strokeWidth = 3.5f
        circlePolygon.title = hazard.title
        circlePolygon.snippet = "${hazard.severity} • ${hazard.description}"

        mapView.overlays.add(circlePolygon)

        // Add Center Hazard Marker
        val marker = Marker(mapView)
        marker.position = center
        marker.title = "[ ${hazard.severity} ] ${hazard.title}"
        marker.snippet = hazard.description
        marker.icon = ContextCompat.getDrawable(mapView.context, markerIconRes)
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        mapView.overlays.add(marker)
    }

    /**
     * Adds User GPS Location Marker (pulsing blue dot).
     */
    fun addUserLocationMarker(mapView: MapView, geoPoint: GeoPoint): Marker {
        val marker = Marker(mapView)
        marker.position = geoPoint
        marker.title = "POSISI ANDA (LIVE GPS)"
        marker.snippet = "Sinyal Siaga • 100% Cache Lokal"
        marker.icon = ContextCompat.getDrawable(mapView.context, R.drawable.ic_marker_user)
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        mapView.overlays.add(marker)
        return marker
    }

    /**
     * Adds Destination Shelter Marker (Grand Ave Station).
     */
    fun addShelterMarker(mapView: MapView, shelter: ShelterPoint): Marker {
        val marker = Marker(mapView)
        val pos = GeoPoint(shelter.latitude, shelter.longitude)
        marker.position = pos
        marker.title = shelter.name
        marker.snippet = "${shelter.distanceText} • ${shelter.capacityInfo}"
        marker.icon = ContextCompat.getDrawable(mapView.context, R.drawable.ic_marker_shelter)
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        mapView.overlays.add(marker)
        return marker
    }

    /**
     * Renders a safe blue route polyline curving around hazards toward the shelter.
     */
    fun addSafeRoutePolyline(mapView: MapView, waypoints: List<GeoPointData>): Polyline {
        val polyline = Polyline(mapView)
        val points = waypoints.map { GeoPoint(it.latitude, it.longitude) }
        polyline.setPoints(points)

        polyline.outlinePaint.color = Color.parseColor("#3B82F6")
        polyline.outlinePaint.strokeWidth = 10f
        polyline.outlinePaint.strokeCap = Paint.Cap.ROUND
        polyline.outlinePaint.strokeJoin = Paint.Join.ROUND
        polyline.title = "RUTE AMAN EVAKUASI"
        polyline.snippet = "Melewati zona bebas api & banjir"

        mapView.overlays.add(polyline)
        return polyline
    }
}
