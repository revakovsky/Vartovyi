package com.revakovskyi.vartovyi.model

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test

class KeywordsBackupTest {

    @Test
    fun `decoding a backup without a version field falls back to CURRENT_VERSION`() {
        val jsonWithoutVersion = """
            {
              "keywords": [
                "Київ",
                "ракета+Харків",
                "\"ціль на Одесу\"",
                "shahed",
                "КАБ+Суми"
              ],
              "stopWords": [
                "Пригород",
                "розвід",
                "ППО",
                "off topic",
                "навчання"
              ],
              "telegramChannels": [
                "🚨 Повітряні Сили ЗС України",
                "Полтава радар | Radar Poltava",
                "TLK News",
                "@air_alert_ua"
              ],
              "isTelegramChannelFilterEnabled": true
            }
        """.trimIndent()

        val backup = Json.decodeFromString(KeywordsBackup.serializer(), jsonWithoutVersion)

        assertThat(backup.version).isEqualTo(KeywordsBackup.CURRENT_VERSION)
        assertThat(backup.keywords).containsExactly(
            "Київ",
            "ракета+Харків",
            "\"ціль на Одесу\"",
            "shahed",
            "КАБ+Суми",
        )
        assertThat(backup.stopWords).containsExactly(
            "Пригород",
            "розвід",
            "ППО",
            "off topic",
            "навчання",
        )
        assertThat(backup.telegramChannels).containsExactly(
            "🚨 Повітряні Сили ЗС України",
            "Полтава радар | Radar Poltava",
            "TLK News",
            "@air_alert_ua",
        )
        assertThat(backup.isTelegramChannelFilterEnabled).isTrue()
    }

}
