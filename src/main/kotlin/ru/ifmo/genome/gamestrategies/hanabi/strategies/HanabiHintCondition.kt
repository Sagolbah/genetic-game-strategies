package ru.ifmo.genome.gamestrategies.hanabi.strategies

import kotlinx.serialization.Serializable

@Serializable
sealed class HanabiHintCondition {
    @Serializable
    // On multiple colors with same rank, the priority is TODO
    data class HasCardOfCurrentColorRank(val colorRank: Int) : HanabiHintCondition()

    @Serializable
    // Check if >= requiredTokens hint tokens available
    data class HasHintTokens(val requiredTokens: Int) : HanabiHintCondition()

}
