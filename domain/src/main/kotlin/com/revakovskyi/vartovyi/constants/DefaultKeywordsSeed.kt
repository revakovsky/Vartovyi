package com.revakovskyi.vartovyi.constants

/** One demo entry per rule type, seeded on first launch as a learning example. */
val DEFAULT_KEYWORDS_SEED: List<String> = listOf(
    "<НАЗВА_МІСТА>",
    "ракета+<НАЗВА_МІСТА>",
    "\"ціль на <НАЗВА_МІСТА>\"",
)

/** Common false-positive terms, seeded on first launch so the user starts with sane defaults. */
val DEFAULT_STOP_WORDS_SEED: List<String> = listOf(
    "Пригород",
    "розвід",
    "ППО",
)
