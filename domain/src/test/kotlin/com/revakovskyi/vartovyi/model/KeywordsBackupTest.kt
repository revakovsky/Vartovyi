package com.revakovskyi.vartovyi.model

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import com.revakovskyi.vartovyi.usecase.keywords.ImportKeywordsUseCaseImpl
import org.junit.jupiter.api.Test

class KeywordsBackupTest {

    private val tolerantJson = ImportKeywordsUseCaseImpl.JSON

    @Test
    fun `legacy backup without version and with the dropped filter flag still decodes`() {
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

        val backup = tolerantJson.decodeFromString(
            KeywordsBackup.serializer(),
            jsonWithoutVersion,
        )

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
    }

}
