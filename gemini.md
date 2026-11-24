# OFFROUTE — Android Mobile Prototype Specification (`gemini.md`)

## 1. Project Overview & Core Objective
- **App Name:** OFFROUTE (Research Code Name: *not G map*)
- **Domain:** Off-grid disaster navigation, Delay-Tolerant Bayesian Search (DTBS), and BLE Mesh emergency rescue prototype.
- **Target Deliverable:** High-polish, interactive Android front-end prototype for live presentation. All data is hardcoded/simulated locally with rich UI states, animations, and interactive maps.
- **Tech Stack:**
    - **Language:** Kotlin
    - **UI Toolkit:** Android XML Views (`ConstraintLayout`, `CoordinatorLayout`, `MaterialCardView`, `ViewPager2`, `BottomNavigationView`, Custom `View` Canvas)
    - **Map Engine:** **OSMdroid** (`org.osmdroid:osmdroid-android:6.1.18`) styled with a custom dark tactical `ColorMatrix` filter so real map tiles render in dark slate/charcoal tones without requiring paid API keys or internet during presentation.
    - **Min SDK:** 24 | **Target SDK:** 34 | **ViewBinding:** Enabled (`buildFeatures { viewBinding = true }`)

---

## 2. Design System & Visual Tokens (Tactical Dark-Grid UI)

### 2.1 Color Palette (`res/values/colors.xml`)
- `bg_obsidian`: `#0D0E12` (Primary screen background)
- `surface_card`: `#16181F` (Elevated cards, bottom sheets, containers)
- `surface_elevated`: `#1E212B` (Input fields, secondary buttons, icon containers)
- `surface_nav`: `#12141A` (Bottom navigation bar background)
- `border_subtle`: `#262A36` (1dp card outlines)
- `accent_green`: `#4ADE80` (Primary CTA buttons, safe status, optimal mesh, turn-by-turn banner)
- `accent_green_dark`: `#143322` (Badge/chip tint for safe/connected status)
- `accent_red`: `#FF1E42` (Critical SOS, Fire/Landslide hazard, active radar sweep, danger badges)
- `accent_red_surface`: `#2A1219` (Dark crimson tint for SOS priority cards & hazard zones)
- `accent_amber`: `#FBBF24` (Warnings, transit status, battery telemetry, NFC/Paper Key badges)
- `accent_amber_dark`: `#2E2410` (Dark amber badge background)
- `accent_blue`: `#3B82F6` (Flood zone overlay, user GPS location marker, navigation route polyline)
- `text_primary`: `#F8FAFC` (Primary headings & bold values)
- `text_secondary`: `#94A3B8` (Body copy, descriptions)
- `text_muted`: `#64748B` (Monospace telemetry labels, timestamps, inactive tabs)

### 2.2 Typography & Hierarchy (`res/values/styles.xml`)
- **Headlines:** Sans-serif Bold (`sans-serif-medium` / `sans-serif-black`), tight tracking, high contrast (`#F8FAFC`).
- **Telemetry / Tactical Labels / Coordinates:** Monospace (`monospace`), uppercase, letter-spacing `0.08`, used for system status (`GRID AKTIF // SIAGA`, `SYS//CALL-ID`, `SIG: 0.00dB`, `ETA. 12 MIN`).
- **Buttons:**
    - Primary CTA: Full-width or prominent rounded rectangle (`12dp` radius), `accent_green` (`#4ADE80`) fill, `#0D0E12` bold text, subtle green elevation glow.
    - Danger CTA: `accent_red` (`#FF1E42`) fill, `#F8FAFC` bold text.
    - Tactical Secondary: `surface_elevated` (`#1E212B`) fill with `1dp` `border_subtle` stroke.

---

## 3. Complete Screen Inventory & Interactive Flows (16 Screens)

### Flow A: Onboarding (`OnboardingActivity` + `ViewPager2` — 3 Screens)
Top bar across all 3 steps: Radar logo icon + `OFFROUTE / ONBOARDING STEP X`, `[ LEWATI ]` button (jumps to `AuthGatewayActivity`), and profile/status circle icon. Segmented top progress bar (`accent_red` active segment).
1. **Step 1 — Navigasi Off-Grid (`fragment_onboarding_1.xml`):**
    - Visual: Custom tactical radar grid graphic (`GRID // 07°14'S 112°45'E`, `• OFF-GRID SYS`, `SIG: 0.00dB [ZERO-NET]`).
    - Pill badge: `OFF-GRID READY`. Title: `NAVIGASI TANPA INTERNET`.
    - 2 feature cards: `PETA VEKTOR (100% Cache Lokal)` & `AUTO RE-ROUTE (Hindari Bahaya)`.
    - CTA: `LANJUT ->` (advances to Step 2) + `LEWATI PENGENALAN`.
2. **Step 2 — Jaringan Mesh (`fragment_onboarding_2.xml`):**
    - Visual: 3-node diagram `[UNIT A] CH//01` <---> `[UNIT B] RELAY HOP +1.2KM` <---> `[POSKO] COMMAND` (`SIGNAL: OFF-GRID LOCAL`, `LATENCY: 14MS`).
    - Tag: `[ PEER-TO-PEER MESH ] RF//BLE-DIRECT`. Title: `Komunikasi Tanpa Menara BTS`.
    - 2 feature cards: `Multi-Hop Relay` & `Enkripsi P2P`. Status bar: `TOPOLOGI SIAP: 3 SIMPUL TERDETEKSI | 100% AIR-GAP`.
    - Navigation: `< Kembali` & `Lanjut >`.
3. **Step 3 — Suar Darurat (`fragment_onboarding_3.xml`):**
    - Visual: Pulsing crimson emergency beacon radar (`EMERGENCY BEACON : TX ACTIVE`, `LAT: -06.2088° // LON: +106.8456°`).
    - Tag: `SIAGA LAPANGAN SYS.BEACON.V3`. Title: `Suar Darurat & Respon Cepat`.
    - 2 status rows: `Izin Lokasi & Bluetooth Siap [✓ SIAP]` & `Mode Siaga Aktif (1.2% / jam) [• STANDBY]`.
    - CTA: `MULAI OPERASI ->` (navigates to `AuthGatewayActivity`).

### Flow B: Authentication & Role Routing (4 Screens)
4. **Auth Gateway (`AuthGatewayActivity` / `activity_auth_gateway.xml`):**
    - Top pill: `• GRID AKTIF // SIAGA`. Center glowing red radar emblem + `OFFROUTE` + `Sistem Navigasi & Komando Darurat`.
    - **Interactive Role Switcher (Segmented Toggle):**
        - Option 1: `Operator Ranger` (selected by default with elevated card highlight)
        - Option 2: `Warga / Tamu`
    - **Routing Logic:**
        - Clicking `MASUK DENGAN ID TAKTIS` (Green CTA): If `Operator Ranger` is selected -> opens `OperatorSignInActivity`; if `Warga / Tamu` is selected -> opens `CivilMainActivity` directly (or quick guest session).
        - Clicking `Evakuasi Cepat [INSTAN]` -> immediately launches `CivilMainActivity` in emergency mode.
5. **Sign In Operator (`OperatorSignInActivity` / `activity_operator_sign_in.xml`):**
    - Header: `OFFROUTE OPERATOR SIGN IN`. Tag: `• AUTORISASI MISI LAPANGAN`. Title: `Masuk Operator`.
    - `ID CALLSIGN` input (`ECHO-04`) with clickable quick-fill chips: `[ ECHO-04 ]`, `[ BRAVO-02 ]`, `[ TANGO-09 ]`.
    - `KUNCI TAKTIS / SANDI` password input with visibility toggle + QR icon.
    - Switch card: `Simpan Kunci Offline` (Red switch ON).
    - Green CTA: `MASUK KE SISTEM ->` -> opens `PinVerificationActivity`.
    - Secondary button: `Pindai Kartu Lapangan (NFC)` (shows simulated NFC scan dialog -> proceeds to PIN).
    - Footer link: `Lupa Kunci Taktis? Gunakan Paper Key` -> opens `EmergencyRecoveryActivity`.
6. **Verifikasi PIN (`PinVerificationActivity` / `activity_pin_verification.xml`):**
    - Header: `OFFROUTE TERMINAL CLEARANCE`. Shield icon + `PROTOKOL OTORISASI` + `VERIFIKASI KUNCI`.
    - 6 PIN indicator boxes (filled digits show glowing green dots, empty show `—`) + live countdown pill `• Berlaku 02:45`.
    - **Interactive Custom Numeric Keypad (0–9, Backspace, Mesh icon):** Tapping digits fills the 6 boxes; upon entering 6 digits (any 6 digits accepted for demo, e.g., `123456`), auto-validates and navigates to `RangerMainActivity`.
    - Footer: `Kirim Ulang Kode` (resets timer) & `Opsi lain: Verifikasi via Mesh P2P ->` (bypasses directly to `RangerMainActivity`).
7. **Pemulihan Darurat (`EmergencyRecoveryActivity` / `activity_emergency_recovery.xml`):**
    - Header: `OFFROUTE RECOVERY PROTOCOL` + bar `SYS // RECOVERY_PROTOCOL [ OFFLINE_AUTH ]`.
    - `12-PHRASE SEED` — `KUNCI PEMULIHAN`.
    - Input box `TERMINAL://KEY_INPUT` with live word counter `0/12 KATA`.
    - Interactive `[ TEMPEL ]` button: auto-populates 12 tactical seed words (`alpha beacon radar echo sierra vector hazard orbit delta pulse ridge shelter`) and updates counter to `12/12 KATA`.
    - Green CTA: `PULIHKAN AKSES` -> validates 12 words and navigates to `RangerMainActivity`.

### Flow C: Civil / Warga Mode (`CivilMainActivity` — 5 Screens)
Hosted in `CivilMainActivity` with a 4-tab `BottomNavigationView`: `MAP`, `REPORT`, `SOS`, `SETTINGS`.
8. **Civil Map — Main (`fragment_civil_map_main.xml`):**
    - Full-screen **dark-styled OSMdroid MapView** centered on Jakarta (`-6.2088, 106.8456` / Charing Cross demo coordinates).
    - Top floating pill: `• Offline Mode - v1.4 LOCAL` + search bar `Search Safe Points, Shelter...`.
    - Map overlays:
        - **Fire Hazard Circle** (`accent_red` border + translucent fill + flame marker).
        - **Flood Zone Circle** (`accent_blue` border + translucent fill + warning marker).
        - **User GPS Marker** (pulsing blue dot).
        - **Destination Shelter Marker** (`Grand Ave Station`).
    - Floating right action buttons: Red `REPORT` button (switches to Report tab), `Recenter GPS` button, `Map Layers` toggle button.
    - Bottom sheet card: `NEAREST SHELTER: Grand Ave Station` | `1.2 KM (ETA. 14 MIN)` + large Green CTA `FIND EVACUATION ROUTE` (transitions smoothly to **Active Navigation State**).
9. **Civil Map — Active Navigation (`fragment_civil_map_active.xml` / active state of Map):**
    - Top green turn-by-turn banner: `NEXT TURN: Turn Right on Charing Cross St (200M)`.
    - Map renders a safe blue route polyline curving *around* the red Fire Hazard & Flood circles toward `Grand Ave Station`.
    - Bottom control card: `DESTINATION: Grand Ave Station` (`950 M | ETA. 12 MIN`) with two buttons: `REPORT HAZARD` (dark button with red alert icon) and `END NAVIGATION` (green button, returns to Main Map state).
10. **Report Hazard (`fragment_civil_report.xml`):**
    - `SELECT INCIDENT TYPE` (3 selectable cards with active border highlight): `Fire` (red), `Flood` (blue), `Blockage` (amber).
    - `CURRENT LOCATION` (`• LIVE GPS ACTIVE`): Interactive mini OSMdroid map with draggable/centered red pin + address text.
    - `ADDITIONAL INFORMATION (OPSIONAL)`: `Capture Photo` dashed card + sample incident photo preview card with remove `×` button + `Describe the situation...` EditText.
    - Red CTA: `REPORT HAZARD` -> adds the newly reported hazard circle onto the shared `MockRepository` so it immediately appears on the Civil & Ranger maps, shows a tactical confirmation snackbar, and returns to `MAP`.
11. **SOS Flare Mode (`fragment_civil_sos.xml`):**
    - Info banner: `FLARE MODE BROADCASTS YOUR LOCATION TO NEARBY RESPONDERS VIA BLUETOOTH LOW ENERGY EVEN WITHOUT CELLULAR SERVICE.`
    - **Interactive Hold-to-Activate Button (`ACTIVATE FLARE - HOLD FOR 1.5S`):** Large circular crimson button (`#EF4444`) with circular progress ring. Holding for 1.5s triggers haptic feedback, pulsing radar waves, and toggles status to `FLARE ACTIVE — BROADCASTING 1s BLE`.
    - Status cards: `COMMUNICATION: BLE Beacon: Active` (blue icon) & `POWER MANAGEMENT: Battery Saving Level: 85%` (green icon).
    - 3 quick action tiles: `SOS CALL`, `SMS ALERT`, `RESPONDERS` (opens bottom sheet of nearby units).
12. **Civil Settings (`fragment_civil_settings.xml`):**
    - `OFFLINE MAPS`: `Indonesia, Jakarta (Region ID: JKT-101)` — `85%` green progress bar, `Storage Used: 2.4 GB / 10 GB`, `[ MANAGE ]` button.
    - `MESH NETWORK`: Interactive `MaterialSwitch` toggles for `Local UDP Sync` (ON) and `Satelite Relay` (OFF).
    - `POWER & FLARE`: Interactive `MaterialSwitch` toggles for `Extreme Battery Saver` (OFF -> toggling ON updates estimated battery runtime) and `BLE Beacon (SOS) — STANDBY ACTIVE` (ON).
    - `ACCOUNT`: `Muh Daffa Dwi (Civil)`, `Encryption Keys`, `App Lock`.

### Flow D: Ranger / SAR Operator Mode (`RangerMainActivity` — 4 Screens)
Hosted in `RangerMainActivity` with the same 4-tab `BottomNavigationView` (`MAP`, `REPORT`, `SOS`, `SETTINGS`) mapped to the 4 Ranger tactical views:
13. **Ranger 2 — Tugas Taktis (`MAP` Tab / `fragment_ranger_tasks.xml`):**
    - Header: `• TUGAS TAKTIS (2 Misi Aktif)` + notification bell with red badge.
    - `PRIORITAS UTAMA` (`SIAGA REGENT 04`): Hero mission card with landslide visual header, `[ • DARURAT ]` & `[ ETA < 15m ]` badges, `MISI PRIORITAS 1: Evakuasi Korban Longsor`, location `Lereng Cidadap (< 15m)`, logistics chips (`Tandu`, `P3K`, `Harnes Tali`), and Green CTA `Terima Tugas` (toggles to `MISI DITERIMA — NAVIGASI AKTIF` and jumps to Sector Radar / Map).
    - `STATUS LAPANGAN` (`UNIT AKTIF 1/3`): Card `BERJALAN | Giat #402: Pembersihan Pohon Tumbang` (`Jl. Ir. H. Djuanda • Echo-01`) with `65%` amber progress bar.
14. **Ranger 4 — Radar Sektor (`REPORT` Tab / `fragment_ranger_radar.xml`):**
    - Header: `SEKTOR •` | `84%` battery | profile icon.
    - Hazard alert banner: `HAZ-09 • Longsor Tebing Cidadap — Jalan Tertutup (450m via COR-02) [ BAHAYA ]`.
    - **Custom Interactive Radar Canvas (`TacticalRadarView.kt`):**
        - Concentric range rings (`UTARA 000°`, `R: 1.5 KM`), rotating crimson radar sweep cone, center `KAMU` marker, pulsing `HAZ-09` hazard blip, and pulsing `BRAVO-02 [SOS]` target blip.
        - Tapping a blip selects it and displays RSSI/distance telemetry.
    - 2 metric cards: `RADIUS RADAR: 1.5 km` & `ELEVASI MEDAN: 1,142 m`.
    - Action buttons: Green `PINDAI SEKTOR (MESH PING)` (triggers fast radar ping sweep animation + toast/status update) & Dark `SET TITIK ARAH`.
15. **Ranger 3 — Unit Status (`SOS` Tab / `fragment_ranger_units.xml`):**
    - Header: `UNIT STATUS (• 4 UNIT AKTIF • 1 SOS)` + search button.
    - High-priority crimson card: `[ SOS DARURAT ] PRIORITAS #1` — `BRAVO-02` (`Sinyal SOS Aktif • 340m Timur`, *"Unit lapar insiden lereng sektor B. Respon medis dan pemandu lapangan dibutuhkan."*) + Green CTA `BANTU UNIT (ASSIST)` (toggles dispatch confirmation & updates unit status).
    - `UNIT OPERASIONAL` (`Diperbarui realtime`) list:
        - `ECHO-01 (REG-4) Patroli` — `[ • STABIL ]`, `Baterai 84%` green progress bar.
        - `MED-ALPHA (MEDIS) Ambulans` — `[ • TRANSIT ]`, `Menuju Shelter | ETA 6m`.
        - `BASE-01 (HQ) Posko BPBD` — `[ • TERHUBUNG ]`, `Kanal Utama VHF 142.100 | 100% LAT`.
16. **Ranger 1 — Jaringan Mesh (`SETTINGS` Tab / `fragment_ranger_mesh.xml`):**
    - Header: `JARINGAN MESH / NODE AD-HOC ACTIVE` + pill `• 5 Node Terhubung`.
    - **Custom Interactive Mesh Topology View (`MeshTopologyView.kt`):**
        - Header `TOPOLOGI VISUAL AKTIF` + outlined green badge `Rute Optimal (4 Hop)`.
        - Visual graph showing connected nodes with glowing rings & dashed latency links: `Anda (LOKAL)` --`8ms`--> `Bravo (0.4 km)` --`14ms`--> `Med (1.2 km)` --`12ms`--> `Base (GATEWAY)`.
        - Footer: `Anda -> Bravo -> Med -> Base | Sinyal 94%`.
    - 3 telemetry cards: `INTEGRITAS: 99.4% (+0.2% optimal)`, `KECEPATAN: 2.4 p/s (+0.4 p/s laju)`, `LATENSI: 36 ms (-4 ms stabil)`.
    - `AKTIVITAS TERKINI (LIVE PING)` feed:
        - `14:28` — `Bravo-02 suar SOS diterima` (Red dot)
        - `14:26` — `Rute terpendek diperbarui` (Amber dot)
        - `14:22` — `5 node tersinkronisasi` (Green dot)
    - Green CTA: `BROADCAST DARURAT (SOS BEACON)` (triggers live packet broadcast animation across the mesh nodes and prepends a new timestamped log entry).

---

## 4. Technical Architecture & File Structure

Organize the Android project cleanly under `app/src/main/java/com/offroute/app/`:
```text
com.offroute.app/
├── data/
│   ├── OffRouteRepository.kt       # Singleton in-memory state (hazards, routes, units, mesh logs, active role)
│   └── Models.kt                   # Data classes: HazardZone, RescueUnit, TacticalMission, MeshNode, MeshLog
├── ui/
│   ├── onboarding/
│   │   ├── OnboardingActivity.kt
│   │   └── OnboardingPagerAdapter.kt
│   ├── auth/
│   │   ├── AuthGatewayActivity.kt
│   │   ├── OperatorSignInActivity.kt
│   │   ├── PinVerificationActivity.kt
│   │   └── EmergencyRecoveryActivity.kt
│   ├── civil/
│   │   ├── CivilMainActivity.kt
│   │   ├── CivilMapFragment.kt     # Handles both Main & Active Navigation states seamlessly
│   │   ├── CivilReportFragment.kt
│   │   ├── CivilSosFragment.kt
│   │   └── CivilSettingsFragment.kt
│   ├── ranger/
│   │   ├── RangerMainActivity.kt
│   │   ├── RangerTasksFragment.kt  # Ranger 2 (MAP tab)
│   │   ├── RangerRadarFragment.kt  # Ranger 4 (REPORT tab)
│   │   ├── RangerUnitsFragment.kt  # Ranger 3 (SOS tab)
│   │   └── RangerMeshFragment.kt   # Ranger 1 (SETTINGS tab)
│   └── custom/
│       ├── TacticalRadarView.kt    # Custom Canvas radar sweep + interactive blips
│       ├── MeshTopologyView.kt     # Custom Canvas 4-hop ad-hoc node graph
│       └── OsmDarkMapHelper.kt     # Configures OSMdroid dark ColorMatrix filter, polygons & polylines