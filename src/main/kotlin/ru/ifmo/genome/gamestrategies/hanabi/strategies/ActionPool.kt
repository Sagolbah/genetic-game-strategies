package ru.ifmo.genome.gamestrategies.hanabi.strategies

/**
 * Pool of all available Hanabi actions.
 */
val hanabiActionPool: List<HanabiAction> = listOf(
    HanabiAction.RandomHint,
    HanabiAction.PlayableHint,
    HanabiAction.CompletePlayableHint,
    HanabiAction.UselessCardHint,
    HanabiAction.RankHint(0),
    HanabiAction.RankHint(4),
    HanabiAction.RandomDiscard,
    HanabiAction.UselessDiscard,
    HanabiAction.NonHintedDiscard,
    HanabiAction.HighestRankDiscard,
    HanabiAction.SafePlay,
    HanabiAction.ProbabilityPlay(0.5),
    HanabiAction.EmptyDeckProbabilityPlay(0.0),
    HanabiAction.OldestDiscard
)