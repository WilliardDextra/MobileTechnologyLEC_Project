package com.offroute.app.data

enum class HazardType {
    FIRE,
    FLOOD,
    LANDSLIDE,
    BLOCKAGE
}

enum class UnitStatus {
    SOS,
    STABLE,
    TRANSIT,
    CONNECTED
}

enum class LogLevel {
    DANGER,
    WARNING,
    INFO
}

enum class UserRole {
    RANGER,
    CIVIL
}

data class HazardZone(
    val id: String,
    val type: HazardType,
    val title: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val radiusMeters: Double,
    val severity: String, // "BAHAYA", "KRITIS", "WASPADA"
    val reportedTime: String
)

data class RescueUnit(
    val id: String,
    val callsign: String,
    val role: String,
    var status: UnitStatus,
    var statusLabel: String,
    var batteryLevel: Int,
    val distanceText: String,
    val infoText: String,
    val latitude: Double,
    val longitude: Double,
    val channel: String,
    var isAssisted: Boolean = false
)

data class TacticalMission(
    val id: String,
    val priority: Int,
    val title: String,
    val location: String,
    val etaText: String,
    val urgencyBadge: String,
    val equipmentList: List<String>,
    var progressPercent: Int,
    var isAccepted: Boolean = false
)

data class MeshNode(
    val id: String,
    val name: String,
    val roleLabel: String,
    val hopCount: Int,
    val latencyMs: Int,
    val signalQuality: Int,
    val status: String
)

data class MeshLog(
    val id: String,
    val timestamp: String,
    val message: String,
    val level: LogLevel
)

data class ShelterPoint(
    val id: String,
    val name: String,
    val distanceText: String,
    val etaText: String,
    val latitude: Double,
    val longitude: Double,
    val capacityInfo: String
)

data class GeoPointData(
    val latitude: Double,
    val longitude: Double
)
