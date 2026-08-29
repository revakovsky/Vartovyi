package com.revakovskyi.vartovyi.ui.screen.home

import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.revakovskyi.vartovyi.constants.DEFAULT_KEYWORDS_SEED
import org.junit.jupiter.api.Test

class HomeUiContractStateTest {

    @Test
    fun `needsKeywordsAttention is true when the trigger list is empty`() {
        val state = HomeUiContract.State(keywords = emptyList())

        assertThat(state.needsKeywordsAttention).isTrue()
    }

    @Test
    fun `needsKeywordsAttention is true when the trigger list is still the placeholder seed`() {
        val state = HomeUiContract.State(keywords = DEFAULT_KEYWORDS_SEED)

        assertThat(state.needsKeywordsAttention).isTrue()
    }

    @Test
    fun `needsKeywordsAttention is false once a city has been substituted into the seed`() {
        val state = HomeUiContract.State(
            keywords = listOf("шахед", "ракета+Харків", "\"ціль на Харків\""),
        )

        assertThat(state.needsKeywordsAttention).isFalse()
    }

}
