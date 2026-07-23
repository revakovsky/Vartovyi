package com.revakovskyi.vartovyi.usecase.keywords

import com.revakovskyi.vartovyi.contract.CrashReporter
import com.revakovskyi.vartovyi.repository.KeywordsRepository
import kotlinx.coroutines.CancellationException

private const val WRITE_FAILED_MESSAGE =
    "Channel filter migration was not persisted: the storage write reported a failure"
private const val UNEXPECTED_FAILURE_MESSAGE =
    "Channel filter migration failed with an unexpected error"

/**
 * Reported to [CrashReporter] when [MigrateLegacyChannelFilterUseCase] fails to run, either by
 * throwing or by the storage write silently returning `false`.
 *
 * Until this migration succeeds, a channel list saved while the old filter toggle was OFF stays
 * in storage as-is. Under the new "non-empty list = active filter" rule that stale list starts
 * blocking channels the user never meant to filter — a real risk of missed alerts, so this
 * failure must stay visible instead of being swallowed
 */
internal class ChannelFilterMigrationException(
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause)

interface MigrateLegacyChannelFilterUseCase {
    suspend operator fun invoke()
}

class MigrateLegacyChannelFilterUseCaseImpl(
    private val keywordsRepository: KeywordsRepository,
    private val crashReporter: CrashReporter,
) : MigrateLegacyChannelFilterUseCase {

    override suspend operator fun invoke() {
        val isPersisted = runCatching {
            keywordsRepository.migrateChannelFilterFlagIfNeeded()
        }.getOrElse { throwable ->
            if (throwable is CancellationException) throw throwable

            crashReporter.report(
                ChannelFilterMigrationException(
                    message = UNEXPECTED_FAILURE_MESSAGE,
                    cause = throwable,
                )
            )
            return
        }

        if (!isPersisted) {
            crashReporter.report(
                ChannelFilterMigrationException(message = WRITE_FAILED_MESSAGE)
            )
        }
    }

}
