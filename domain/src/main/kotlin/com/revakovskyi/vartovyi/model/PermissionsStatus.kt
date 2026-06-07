package com.revakovskyi.vartovyi.model

/**
 * Aggregated permission state that drives the top-bar security badge color.
 *
 * - [GRANTED] — every mandatory and recommended permission is granted (green color indicator).
 * - [RECOMMENDED_MISSING] — all mandatory permissions are granted, but some
 *   recommended ones are not (orange color indicator) — the app works, yet can be improved.
 * - [MANDATORY_MISSING] — at least one mandatory permission is missing (red color indicator).
 */
enum class PermissionsStatus {
    GRANTED,
    RECOMMENDED_MISSING,
    MANDATORY_MISSING,
}
