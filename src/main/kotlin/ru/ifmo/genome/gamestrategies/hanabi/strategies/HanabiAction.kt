package ru.ifmo.genome.gamestrategies.hanabi.strategies

import kotlinx.serialization.Serializable

@Serializable
sealed class HanabiAction {
    @Serializable
    object RandomHint : HanabiAction()

    @Serializable
    object GreedyHint : HanabiAction()

    @Serializable
    data class RankHint(val rank: Int) : HanabiAction()

    @Serializable
    data class ColorHint(val color: Char) : HanabiAction()

    @Serializable
    data class Discard(val index: Int) : HanabiAction()

}
