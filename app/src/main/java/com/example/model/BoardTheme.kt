package com.example.model

import androidx.compose.ui.graphics.Color

data class BoardTheme(
    val id: String,
    val name: String,
    val price: Int,
    val surfaceColor: Color,
    val innerPatternColor: Color,
    val borderColor: Color,
    val pocketRimColor: Color,
    val pocketNetColor: Color,
    val centerCircleColor: Color,
    val arrowColor: Color,
    val frictionMultiplier: Float = 1.0f,
    val description: String
) {
    companion object {
        val ClassicMahogany = BoardTheme(
            id = "classic_mahogany",
            name = "Classic Teakwood",
            price = 0,
            surfaceColor = Color(0xFFF3E5AB), // Natural light birch wood
            innerPatternColor = Color(0xFF8B2500), // Deep red/brown ornamental lines
            borderColor = Color(0xFF3E1F11), // Rich dark mahogany border
            pocketRimColor = Color(0xFF1E100A),
            pocketNetColor = Color(0xFF150A05),
            centerCircleColor = Color(0xFFC0392B),
            arrowColor = Color(0xFF8B0000),
            frictionMultiplier = 1.0f,
            description = "Traditional Indian tournament teakwood board with smooth natural lacquer finish."
        )

        val RoyalEmerald = BoardTheme(
            id = "royal_emerald",
            name = "Royal Emerald",
            price = 1200,
            surfaceColor = Color(0xFF0F382A), // Deep luxury emerald felt
            innerPatternColor = Color(0xFFD4AF37), // Metallic gold lines
            borderColor = Color(0xFF071F17),
            pocketRimColor = Color(0xFFD4AF37),
            pocketNetColor = Color(0xFF04120D),
            centerCircleColor = Color(0xFFFFD700),
            arrowColor = Color(0xFFECC94B),
            frictionMultiplier = 0.92f,
            description = "Velvet emerald table top with brushed golden inlay for ultra-fast sliding."
        )

        val CyberMidnight = BoardTheme(
            id = "cyber_midnight",
            name = "Midnight Cyber",
            price = 2500,
            surfaceColor = Color(0xFF121424), // Dark obsidian blue
            innerPatternColor = Color(0xFF00E5FF), // Cyan neon glow lines
            borderColor = Color(0xFF0A0C18),
            pocketRimColor = Color(0xFFFF007F),
            pocketNetColor = Color(0xFF05070E),
            centerCircleColor = Color(0xFFFF007F),
            arrowColor = Color(0xFF00E5FF),
            frictionMultiplier = 0.88f,
            description = "High-tech synthwave aesthetic with glowing reactive neon board markings."
        )

        val ImperialMarble = BoardTheme(
            id = "imperial_marble",
            name = "Imperial Marble",
            price = 4500,
            surfaceColor = Color(0xFFECEFF1), // Pure polished marble
            innerPatternColor = Color(0xFF37474F), // Slate ornamental accents
            borderColor = Color(0xFF263238),
            pocketRimColor = Color(0xFFFFB300),
            pocketNetColor = Color(0xFF1B2428),
            centerCircleColor = Color(0xFFD32F2F),
            arrowColor = Color(0xFFC2185B),
            frictionMultiplier = 0.82f,
            description = "Heavy polished royal marble surface with ultra-low glide resistance."
        )

        val ALL_BOARDS = listOf(ClassicMahogany, RoyalEmerald, CyberMidnight, ImperialMarble)
    }
}

data class StrikerTheme(
    val id: String,
    val name: String,
    val price: Int,
    val primaryColor: Color,
    val secondaryColor: Color,
    val ringColor: Color,
    val trailColor: Color,
    val mass: Float = 3.0f,
    val powerMultiplier: Float = 1.0f,
    val aimLengthMultiplier: Float = 1.0f,
    val description: String
) {
    companion object {
        val ClassicIvory = StrikerTheme(
            id = "classic_ivory",
            name = "Ivory Monarch",
            price = 0,
            primaryColor = Color(0xFFFAF0E6),
            secondaryColor = Color(0xFFD4AF37),
            ringColor = Color(0xFF8B4513),
            trailColor = Color(0x66D4AF37),
            mass = 3.0f,
            powerMultiplier = 1.0f,
            aimLengthMultiplier = 1.0f,
            description = "Standard tournament balanced striker with ivory surface."
        )

        val CrimsonDragon = StrikerTheme(
            id = "crimson_dragon",
            name = "Crimson Dragon",
            price = 1500,
            primaryColor = Color(0xFFB71C1C),
            secondaryColor = Color(0xFFFF5252),
            ringColor = Color(0xFFFFD700),
            trailColor = Color(0x88FF1744),
            mass = 3.3f,
            powerMultiplier = 1.15f,
            aimLengthMultiplier = 1.05f,
            description = "Heavy dragon-forged striker packing superior kinetic impact."
        )

        val CyanPhantom = StrikerTheme(
            id = "cyan_phantom",
            name = "Cyan Phantom",
            price = 2800,
            primaryColor = Color(0xFF00E5FF),
            secondaryColor = Color(0xFF006064),
            ringColor = Color(0xFFFFFFFF),
            trailColor = Color(0x8800E5FF),
            mass = 2.8f,
            powerMultiplier = 1.05f,
            aimLengthMultiplier = 1.30f,
            description = "Super-aerodynamic striker equipped with extended laser aiming trajectory."
        )

        val OnyxTitan = StrikerTheme(
            id = "onyx_titan",
            name = "Onyx Titan",
            price = 5000,
            primaryColor = Color(0xFF212121),
            secondaryColor = Color(0xFFFFB300),
            ringColor = Color(0xFFFFD700),
            trailColor = Color(0x88FFB300),
            mass = 3.6f,
            powerMultiplier = 1.25f,
            aimLengthMultiplier = 1.20f,
            description = "Legendary heavy striker with maximum smash power and golden stardust trail."
        )

        val ALL_STRIKERS = listOf(ClassicIvory, CrimsonDragon, CyanPhantom, OnyxTitan)
    }
}
