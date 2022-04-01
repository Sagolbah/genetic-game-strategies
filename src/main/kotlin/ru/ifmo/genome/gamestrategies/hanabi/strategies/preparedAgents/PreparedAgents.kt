package ru.ifmo.genome.gamestrategies.hanabi.strategies.preparedAgents

import ru.ifmo.genome.gamestrategies.hanabi.strategies.HanabiAction
import ru.ifmo.genome.gamestrategies.hanabi.strategies.RuleBasedHanabiStrategy

val IGGI = RuleBasedHanabiStrategy {
    listOf(
        HanabiAction.SafePlay,
        HanabiAction.PlayableHint,
        HanabiAction.UselessDiscard,
        HanabiAction.OldestDiscard
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

val VanDenBergh = RuleBasedHanabiStrategy {
    listOf(
        HanabiAction.ProbabilityPlay(0.6),
        HanabiAction.SafePlay,
        HanabiAction.UselessDiscard,
        HanabiAction.PlayableHint,
        HanabiAction.UselessCardHint,
        HanabiAction.GreedyHint,
        HanabiAction.VDBProbabilityPlay
    )
}

val Flawed = RuleBasedHanabiStrategy {
    listOf(
        HanabiAction.SafePlay,
        HanabiAction.ProbabilityPlay(0.25),
        HanabiAction.RandomHint,
        HanabiAction.UselessDiscard,
        HanabiAction.OldestDiscard,
        HanabiAction.RandomDiscard
    )
}

val Piers = RuleBasedHanabiStrategy {
    listOf(
        HanabiAction.PiersProbabilityPlay,
        HanabiAction.SafePlay,
        HanabiAction.ProbabilityPlay(0.6),
        HanabiAction.PlayableHint,
        HanabiAction.PiersUselessCardHint,
        HanabiAction.UselessDiscard,
        HanabiAction.OldestDiscard,
        HanabiAction.RandomHint,
        HanabiAction.RandomDiscard
    )
}
