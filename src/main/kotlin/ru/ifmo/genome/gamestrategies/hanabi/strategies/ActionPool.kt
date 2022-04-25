package ru.ifmo.genome.gamestrategies.hanabi.strategies

/**
 * Pool of all available Hanabi actions.
 */
val hanabiActionPool: List<HanabiAction> = listOf(
    HanabiAction.RandomHint,
    HanabiAction.PlayableHint,
    HanabiAction.CompletePlayableHint,
    HanabiAction.UselessCardHint,
    HanabiAction.UnknownCardHint,
    HanabiAction.RankHint(0),
    HanabiAction.RankHint(4),
    HanabiAction.StackDefenseHint(0),
    HanabiAction.RandomDiscard,
    HanabiAction.UselessDiscard,
    HanabiAction.NonHintedDiscard,
    HanabiAction.HighestRankDiscard,
    HanabiAction.SafePlay,
    HanabiAction.ProbabilityPlay(0.5),
    HanabiAction.EmptyDeckProbabilityPlay(0.0),
    HanabiAction.OldestDiscard
)

/**
 * Parameter values for [HanabiAction.ProbabilityPlay]
 */
val probabilityPlayParams = listOf(0.0, 0.2, 0.4, 0.5, 0.6, 0.8)

/**
 * Parameter values for [HanabiAction.EmptyDeckProbabilityPlay]
 */
val emptyDeckProbabilityPlayParams = listOf(0.0, 0.1, 0.2, 0.3, 0.5)

/**
 * Parameter values for [HanabiAction.StackDefenseHint]
 * Rank 5 is excluded, as it's covered in [HanabiAction.PlayableHint]
 */
val stackDefenseParams = IntArray(4) { it }
