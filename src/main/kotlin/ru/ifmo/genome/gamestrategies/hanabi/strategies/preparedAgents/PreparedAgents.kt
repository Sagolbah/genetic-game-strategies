package ru.ifmo.genome.gamestrategies.hanabi.strategies.preparedAgents

import ru.ifmo.genome.gamestrategies.hanabi.strategies.HanabiAction
import ru.ifmo.genome.gamestrategies.hanabi.strategies.RuleBasedHanabiStrategy

val IGGI = RuleBasedHanabiStrategy {
    listOf(
        HanabiAction.SafePlay,
        HanabiAction.PlayableHint(0),
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
        HanabiAction.PlayableHint(0),
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
        HanabiAction.PlayableHint(0),
        HanabiAction.UselessCardHint,
        HanabiAction.GreedyHint,
        HanabiAction.VDBProbabilityDiscard
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
        HanabiAction.EmptyDeckProbabilityPlay(0.0),
        HanabiAction.SafePlay,
        HanabiAction.ProbabilityPlay(0.6),
        HanabiAction.PlayableHint(0),
        HanabiAction.PiersUselessCardHint,
        HanabiAction.UselessDiscard,
        HanabiAction.OldestDiscard,
        HanabiAction.RandomHint,
        HanabiAction.RandomDiscard
    )
}

val OnePlusOneGenerated = RuleBasedHanabiStrategy {
    listOf(
        HanabiAction.PlayableHint(0),
        HanabiAction.ProbabilityPlay(0.5),
        HanabiAction.UselessDiscard,
        HanabiAction.NonHintedDiscard,
        HanabiAction.RankHint(4),
        HanabiAction.CompletePlayableHint,
        HanabiAction.SafePlay
    )
}

