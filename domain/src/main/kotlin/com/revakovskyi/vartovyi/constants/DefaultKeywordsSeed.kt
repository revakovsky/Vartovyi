package com.revakovskyi.vartovyi.constants

const val CITY_PLACEHOLDER: String = "<НАЗВА_МІСТА>"

/** One demo entry per rule type, seeded on first launch as a learning example. */
val DEFAULT_KEYWORDS_SEED: List<String> = listOf(
    "шахед",
    "ракета+$CITY_PLACEHOLDER",
    "\"ціль на $CITY_PLACEHOLDER\"",
)

/** Common false-positive terms, seeded on first launch so the user starts with sane defaults. */
val DEFAULT_STOP_WORDS_SEED: List<String> = listOf(
    "Пригород",
    "розвід",
    "ППО",
)
