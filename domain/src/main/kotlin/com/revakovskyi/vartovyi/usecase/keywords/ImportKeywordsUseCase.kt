package com.revakovskyi.vartovyi.usecase.keywords

import com.revakovskyi.vartovyi.model.KeywordsBackup
import com.revakovskyi.vartovyi.repository.KeywordsRepository
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.IOException

sealed interface ImportResult {
    data object Success : ImportResult
    data class InvalidFormat(val exception: Exception) : ImportResult
    data class WriteError(val exception: Exception) : ImportResult
    data class UnsupportedVersion(val fileVersion: Int) : ImportResult
}

interface ImportKeywordsUseCase {
    suspend operator fun invoke(jsonContent: String): ImportResult
}

class ImportKeywordsUseCaseImpl(
    private val keywordsRepository: KeywordsRepository,
) : ImportKeywordsUseCase {

    override suspend operator fun invoke(jsonContent: String): ImportResult {
        val backup = try {
            Json.decodeFromString(KeywordsBackup.serializer(), jsonContent)
        } catch (e: SerializationException) {
            return ImportResult.InvalidFormat(e)
        } catch (e: IllegalArgumentException) {
            return ImportResult.InvalidFormat(e)
        }

        if (backup.version > KeywordsBackup.CURRENT_VERSION) {
            return ImportResult.UnsupportedVersion(backup.version)
        }

        return try {
            keywordsRepository.clearAllKeywordsPreferences()

            backup.keywords.forEach { keywordsRepository.addKeyword(it) }
            backup.stopWords.forEach { keywordsRepository.addStopWord(it) }
            backup.telegramChannels.forEach { keywordsRepository.addTelegramChannel(it) }
            keywordsRepository.setTelegramChannelFilterEnabled(backup.isTelegramChannelFilterEnabled)

            ImportResult.Success
        } catch (e: IOException) {
            ImportResult.WriteError(e)
        }
    }

}
