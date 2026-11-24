package com.offroute.app.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object OffRouteRepository {

    var activeRole: UserRole = UserRole.RANGER
    var isFlareActive: Boolean = false
    var isNavigationActive: Boolean = false
    var currentCallsign: String = "ECHO-04"
    var offlineKeySaved: Boolean = true

    // Default Anchor Coordinates: Jakarta Pusat / Demo Sector (-6.2088, 106.8456)
    val defaultCenterLat: Double = -6.2088
    val defaultCenterLon: Double = 106.8456

    // Nearest Evacuation Shelter
    val nearestShelter = ShelterPoint(
        id = "SHELTER-01",
        name = "Grand Ave Station",
        distanceText = "1.2 KM",
        etaText = "ETA. 14 MIN",
        latitude = -6.2045,
        longitude = 106.8525,
        capacityInfo = "Kapasitas: 450/800 Jiwa • Logistik Cukup"
    )

    // Dynamic Hazard Zones
    private val _hazards = mutableListOf(
        HazardZone(
            id = "HAZ-09",
            type = HazardType.LANDSLIDE,
            title = "Longsor Tebing Cidadap",
            description = "Jalan Tertutup (450m via COR-02)",
            latitude = -6.2065,
            longitude = 106.8480,
            radiusMeters = 250.0,
            severity = "BAHAYA",
            reportedTime = "14:15"
        ),
        HazardZone(
            id = "HAZ-01",
            type = HazardType.FIRE,
            title = "Kebakaran Lahan Sektor Barat",
            description = "Asap tebal mengarah ke timur laut, visibilitas < 50m",
            latitude = -6.2110,
            longitude = 106.8420,
            radiusMeters = 320.0,
            severity = "KRITIS",
            reportedTime = "13:50"
        ),
        HazardZone(
            id = "HAZ-02",
            type = HazardType.FLOOD,
            title = "Genangan Banjir Arus Cepat",
            description = "Ketinggian air 1.2m, jembatan lintasan terputus",
            latitude = -6.2135,
            longitude = 106.8495,
            radiusMeters = 280.0,
            severity = "WASPADA",
            reportedTime = "14:02"
        )
    )

    val hazards: List<HazardZone>
        get() = _hazards.toList()

    fun addHazard(hazard: HazardZone) {
        _hazards.add(0, hazard)
    }

    // Operational Units
    private val _units = mutableListOf(
        RescueUnit(
            id = "U-02",
            callsign = "BRAVO-02",
            role = "SAR Lapangan",
            status = UnitStatus.SOS,
            statusLabel = "Sinyal SOS Aktif • 340m Timur",
            batteryLevel = 42,
            distanceText = "340m Timur",
            infoText = "Unit lapor insiden lereng sektor B. Respon medis dan pemandu lapangan dibutuhkan segera.",
            latitude = -6.2075,
            longitude = 106.8490,
            channel = "VHF 142.100"
        ),
        RescueUnit(
            id = "U-01",
            callsign = "ECHO-01 (REG-4)",
            role = "Patroli",
            status = UnitStatus.STABLE,
            statusLabel = "[ • STABIL ]",
            batteryLevel = 84,
            distanceText = "850m Barat",
            infoText = "Patroli pemantauan jalur evakuasi aman.",
            latitude = -6.2095,
            longitude = 106.8390,
            channel = "CH-01 P2P"
        ),
        RescueUnit(
            id = "U-03",
            callsign = "MED-ALPHA (MEDIS)",
            role = "Ambulans",
            status = UnitStatus.TRANSIT,
            statusLabel = "[ • TRANSIT ] Menuju Shelter | ETA 6m",
            batteryLevel = 91,
            distanceText = "1.2 km Selatan",
            infoText = "Membawa 2 korban luka ringan ke posko medis Grand Ave.",
            latitude = -6.2160,
            longitude = 106.8460,
            channel = "VHF 142.100"
        ),
        RescueUnit(
            id = "U-04",
            callsign = "BASE-01 (HQ)",
            role = "Posko BPBD",
            status = UnitStatus.CONNECTED,
            statusLabel = "[ • TERHUBUNG ] Kanal Utama VHF 142.100 | 100% LAT",
            batteryLevel = 100,
            distanceText = "2.1 km Utara",
            infoText = "Pusat komando taktis dan koordinasi bantuan relawan.",
            latitude = -6.1950,
            longitude = 106.8450,
            channel = "MAIN-GATEWAY"
        )
    )

    val units: List<RescueUnit>
        get() = _units

    fun assistUnit(id: String) {
        _units.find { it.id == id }?.let { unit ->
            unit.isAssisted = true
            unit.status = UnitStatus.TRANSIT
            unit.statusLabel = "[ TIM BANTUAN DISPATCH ] Menuju Lokasi Unit"
        }
    }

    // Tactical Missions
    private val _missions = mutableListOf(
        TacticalMission(
            id = "MISI-01",
            priority = 1,
            title = "MISI PRIORITAS 1: Evakuasi Korban Longsor",
            location = "Lereng Cidadap (< 15m)",
            etaText = "ETA < 15m",
            urgencyBadge = "• DARURAT",
            equipmentList = listOf("Tandu", "P3K", "Harnes Tali"),
            progressPercent = 0,
            isAccepted = false
        ),
        TacticalMission(
            id = "MISI-402",
            priority = 2,
            title = "BERJALAN | Giat #402: Pembersihan Pohon Tumbang",
            location = "Jl. Ir. H. Djuanda • Echo-01",
            etaText = "ETA 35m",
            urgencyBadge = "• BERJALAN",
            equipmentList = listOf("Gergaji Mesin", "Tali Derek", "Pelindung"),
            progressPercent = 65,
            isAccepted = true
        )
    )

    val missions: List<TacticalMission>
        get() = _missions

    fun acceptMission(missionId: String) {
        _missions.find { it.id == missionId }?.let {
            it.isAccepted = true
            it.progressPercent = 25
        }
    }

    // Mesh Topology
    val meshNodes = listOf(
        MeshNode("N1", "Anda (LOKAL)", "LOKAL NODE", 0, 0, 100, "ONLINE"),
        MeshNode("N2", "Bravo (0.4 km)", "RELAY HOP 1", 1, 8, 92, "RELAY"),
        MeshNode("N3", "Med (1.2 km)", "RELAY HOP 2", 2, 14, 88, "RELAY"),
        MeshNode("N4", "Base (GATEWAY)", "MAIN GATEWAY", 3, 12, 94, "GATEWAY")
    )

    // Mesh Activity Logs
    private val _meshLogs = mutableListOf(
        MeshLog("L1", "14:28", "Bravo-02 suar SOS diterima", LogLevel.DANGER),
        MeshLog("L2", "14:26", "Rute terpendek diperbarui via Relay Hop 1", LogLevel.WARNING),
        MeshLog("L3", "14:22", "5 node tersinkronisasi | Enkripsi P2P aktif", LogLevel.INFO)
    )

    val meshLogs: List<MeshLog>
        get() = _meshLogs

    fun broadcastSosBeacon() {
        val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        _meshLogs.add(0, MeshLog(
            id = "L-${System.currentTimeMillis()}",
            timestamp = time,
            message = "BROADCAST DARURAT: Suar SOS terkirim ke 4 Simpul Mesh",
            level = LogLevel.DANGER
        ))
    }

    // Safe Navigation Route Coordinates (Curving around fire & flood to Grand Ave Station)
    val safeRouteWaypoints = listOf(
        GeoPointData(-6.2088, 106.8456), // Start: Current location
        GeoPointData(-6.2080, 106.8465),
        GeoPointData(-6.2070, 106.8475),
        GeoPointData(-6.2058, 106.8490),
        GeoPointData(-6.2050, 106.8510),
        GeoPointData(-6.2045, 106.8525)  // End: Grand Ave Station
    )
}
