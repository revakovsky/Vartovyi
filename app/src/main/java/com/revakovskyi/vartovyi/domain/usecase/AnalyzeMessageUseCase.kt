package com.revakovskyi.vartovyi.domain.usecase

import com.revakovskyi.vartovyi.utils.KeywordMatcher

interface AnalyzeMessageUseCase {
    operator fun invoke(
        text: String,
        keywords: List<String>,
        stopWords: List<String>,
    ): String?
}

class AnalyzeMessageUseCaseImpl(
    private val matcher: KeywordMatcher,
) : AnalyzeMessageUseCase {

    override operator fun invoke(
        text: String,
        keywords: List<String>,
        stopWords: List<String>,
    ): String? {
        if (
            !matcher.matches(
                text = text,
                keywords = keywords,
                stopWords = stopWords
            )
        ) return null

        return matcher.findMatchedKeyword(text, keywords)
    }

}
