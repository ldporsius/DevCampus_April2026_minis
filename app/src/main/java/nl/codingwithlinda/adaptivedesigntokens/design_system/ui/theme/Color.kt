package nl.codingwithlinda.adaptivedesigntokens.design_system.ui.theme

import androidx.compose.ui.graphics.Color

// Base palette (from design)
val Bg = Color(0xFFF3F5F6)
val Surface = Color(0xFFFFFFFF)
val SurfaceLower = Color(0xFFE1E7EA)
val Primary = Color(0xFF8F36FD)
val OnPrimary = Color(0xFFFFFFFF)
val TextPrimary = Color(0xFF071121)
val TextSecondary = Color(0xFF546175)
val Overlay = Color(0x0D071121) // #071121 at 5% opacity

// Primary derivations
val PrimaryContainer = Color(0xFFEFDFFF)   // light purple tint
val OnPrimaryContainer = Color(0xFF2D0071) // deep purple
val InversePrimary = Color(0xFFCB9EFF)     // softened purple for dark surfaces

// Secondary — blue-gray family (derived from TextSecondary #546175)
val Secondary = Color(0xFF546175)
val OnSecondary = Color(0xFFFFFFFF)
val SecondaryContainer = Color(0xFFD7E3EF) // light blue-gray
val OnSecondaryContainer = Color(0xFF071121)

// Tertiary — muted teal complement to the purple primary
val Tertiary = Color(0xFF2B7A8D)
val OnTertiary = Color(0xFFFFFFFF)
val TertiaryContainer = Color(0xFFBEEAF5)  // light teal
val OnTertiaryContainer = Color(0xFF002D38)

// Error — Material 3 standard
val Error = Color(0xFFBA1A1A)
val OnError = Color(0xFFFFFFFF)
val ErrorContainer = Color(0xFFFFDAD6)
val OnErrorContainer = Color(0xFF410002)

// Surfaces & inverse
val InverseSurface = Color(0xFF2C3540)     // dark navy-gray
val InverseOnSurface = Color(0xFFEEF1F3)  // near-white for dark bg

// Outlines — between SurfaceLower and TextSecondary
val Outline = Color(0xFF8596A8)
val OutlineVariant = Color(0xFFC4CDD6)