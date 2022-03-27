package ru.ifmo.genome.gamestrategies.hanabi.strategies.preparedAgents

import ru.ifmo.genome.gamestrategies.hanabi.strategies.HanabiAction
import ru.ifmo.genome.gamestrategies.hanabi.strategies.RuleBasedHanabiStrategy

val IGGI = RuleBasedHanabiStrategy {
    listOf(
        HanabiAction.SafePlay,
        HanabiAction.PlayableHint,
        HanabiAction.UselessDiscard,
        TODO("discard oldest first")
    )
}

val Internal = RuleBasedHanabiStrategy {
    listOf(
        HanabiAction.SafePlay,
        HanabiAction.UselessDiscard,
        HanabiAction.WeakPlayableHint,
        HanabiAction.RandomHint,
        HanabiAction.RandomDiscard
    )
}

val Outer = RuleBasedHanabiStrategy {
    listOf(
        HanabiAction.SafePlay,
        HanabiAction.UselessDiscard,
        HanabiAction.PlayableHint,
        HanabiAction.RandomHint,
        HanabiAction.RandomDiscard
    )
}

val LegalRandom = RuleBasedHanabiStrategy { listOf(HanabiAction.LegalRandom) }

val VanDenBergh = RuleBasedHanabiStrategy { emptyList() }

val Flawed = RuleBasedHanabiStrategy { emptyList() }

val Piers = RuleBasedHanabiStrategy { emptyList() }
