package com.revakovskyi.vartovyi.usecase.keywords

import com.revakovskyi.vartovyi.model.KeywordsBackup
import com.revakovskyi.vartovyi.repository.KeywordsRepository
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

sealed interface ImportResult {
    data object Success : ImportResult
    data class InvalidFormat(val exception: Exception) : ImportResult
    data object WriteError : ImportResult
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

        val allWritesSucceeded = keywordsRepository.clearAllKeywordsPreferences() &&
                backup.keywords.all { keywordsRepository.addKeyword(it) } &&
                backup.stopWords.all { keywordsRepository.addStopWord(it) } &&
                backup.telegramChannels.all { keywordsRepository.addTelegramChannel(it) } &&
                keywordsRepository.setTelegramChannelFilterEnabled(backup.isTelegramChannelFilterEnabled)

        return if (allWritesSucceeded) ImportResult.Success else ImportResult.WriteError
    }

}
