package com.revakovskyi.vartovyi.usecase.keywords

import com.revakovskyi.vartovyi.model.KeywordsBackup
import com.revakovskyi.vartovyi.repository.KeywordsRepository
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json

sealed interface ExportResult {
    data class Success(val jsonContent: String) : ExportResult
    data class Error(val exception: Exception) : ExportResult
}

interface ExportKeywordsUseCase {
    suspend operator fun invoke(): ExportResult
}

class ExportKeywordsUseCaseImpl(
    private val keywordsRepository: KeywordsRepository,
) : ExportKeywordsUseCase {

    companion object {
        private val JSON = Json { encodeDefaults = true }
    }

    override suspend operator fun invoke(): ExportResult {
        return try {
            val backup = KeywordsBackup(
                version = KeywordsBackup.CURRENT_VERSION,
                keywords = keywordsRepository.keywords.first(),
                stopWords = keywordsRepository.stopWords.first(),
                telegramChannels = keywordsRepository.telegramChannels.first(),
                isTelegramChannelFilterEnabled = keywordsRepository.isTelegramChannelFilterEnabled.first(),
            )

            ExportResult.Success(JSON.encodeToString(KeywordsBackup.serializer(), backup))
        } catch (e: Exception) {
            ExportResult.Error(e)
        }
    }

}
