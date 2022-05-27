package ru.ifmo.genome.gamestrategies.hanabi.strategies

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class HanabiAction {

    // HINTS ACTIONS
    // ALL actions will happen only when hint token is available.

    /**
     * Tells random hint
     */
    @Serializable
    @SerialName("RandomHint")
    object RandomHint : HanabiAction()

    /**
     * Tells a hint on any playable, not fully known card.
     * If card is known partially, completes it.
     */
    @Serializable
    @SerialName("PlayableHint")
    object PlayableHint : HanabiAction()

    /**
     * If a playable card was hinted color or rank, performs second hint with missing information.
     */
    @Serializable
    @SerialName("CompletePlayableHint")
    object CompletePlayableHint : HanabiAction()

    /**
     * Tells a random hint about useless card.
     */
    @Serializable
    @SerialName("UselessCardHint")
    object UselessCardHint : HanabiAction()

    /**
     * Tells a random hint about unknown card.
     */
    @Serializable
    @SerialName("UnknownCardHint")
    object UnknownCardHint : HanabiAction()

    /**
     * Same as [FutureStackDefenseHint], but views only playable cards.
     */
    @Serializable
    @SerialName("StackDefenseHint")
    data class StackDefenseHint(@SerialName("value") val maxRank: Int) : HanabiAction()

    /**
     * Tells a hint about card, which is the last possible one to play on the current stack
     * Designed to prevent discarding of these cards
     * If multiple ranks in stack are in risk of being fully discarded, uses the lower one.
     *
     * @param maxRank max rank to trigger this rule
     */
    @Serializable
    @SerialName("FutureStackDefenseHint")
    data class FutureStackDefenseHint(@SerialName("value") val maxRank: Int) : HanabiAction()

    /**
     * Gives rank hint.
     * Mostly interested in 0 and 4 ranks, since they are useful in early/late game.
     * Ranks are provided in 0-indexation
     *
     * @param value rank to hint
     */
    @Serializable
    @SerialName("RankHint")
    data class RankHint(val value: Int) : HanabiAction()

    // DISCARD ACTIONS

    /**
     * Discards random card
     */
    @Serializable
    @SerialName("RandomDiscard")
    object RandomDiscard : HanabiAction()

    /**
     * Discards a card which cannot be played in current game
     * For example, all red cards are useless if "5-Red stack" is on the board.
     */
    @Serializable
    @SerialName("UselessDiscard")
    object UselessDiscard : HanabiAction()

    /**
     * Discards a card which was not hinted
     */
    @Serializable
    @SerialName("NonHintedDiscard")
    object NonHintedDiscard : HanabiAction()

    /**
     * Discards card with the highest rank
     */
    @Serializable
    @SerialName("HighestRankDiscard")
    object HighestRankDiscard : HanabiAction()


    // PLAY ACTIONS

    /**
     * Play a safe card. Card is safe if playing it will not result in life token loss.
     * For example, card is safe if color&rank is known, and it is available.
     * Another example - card with rank 1 on empty table.
     */
    @Serializable
    @SerialName("SafePlay")
    object SafePlay : HanabiAction()

    /**
     * May be used only if > 1 life tokens available.
     * Only hinted cards are participating.
     *
     * Loops through all cards.
     * Plays a card with probability of correct guess higher or equal than provided parameter.
     * On multiple cards will play card with the highest probability.
     * The probability is P / A, where A is number of possible cards that can occupy a slot in hand,
     * and P is number of playable cards across them.
     *
     * @param value probability threshold
     */
    @Serializable
    @SerialName("ProbabilityPlay")
    data class ProbabilityPlay(val value: Double) : HanabiAction()

    /**
     * Plays card with the highest success probability if deck is empty and more than 1 life remaining.
     * Used in Piers agent.
     * Required probabilities are generally lower
     *
     * @param value probability threshold
     */
    @Serializable
    @SerialName("EmptyDeckProbabilityPlay")
    data class EmptyDeckProbabilityPlay(val value: Double) : HanabiAction()


    // UTILITY SECTION - MOSTLY ALL USED ONLY IN VALIDATION AGENTS


    /**
     * Legal random action, including play.
     * Used only in "Legal Random" agent in validation.
     */
    @Serializable
    @SerialName("LegalRandom")
    object LegalRandom : HanabiAction()

    /**
     * Same as [PlayableHint], but can repeat hints.
     * Used in "Internal" agent.
     */
    @Serializable
    @SerialName("WeakPlayableHint")
    object WeakPlayableHint : HanabiAction()

    /**
     * Discards a card, which was held in hand for longest period
     * Used in "IGGI" and "Flawed" agents.
     */
    @Serializable
    @SerialName("OldestDiscard")
    object OldestDiscard : HanabiAction()

    /**
     * Same as [UselessCardHint], but works only if < 4 information tokens available.
     */
    @Serializable
    @SerialName("PiersUselessCardHint")
    object PiersUselessCardHint : HanabiAction()

    /**
     * Discards card with the highest probability of being useless
     * Probability works the same as [ProbabilityPlay], but instead of playable cards we count useless.
     */
    @Serializable
    @SerialName("VDBProbabilityDiscard")
    object VDBProbabilityDiscard : HanabiAction()

    /**
     * Tells hint that affects most cards in hand
     * Used in Van den Bergh agent.
     */
    @Serializable
    @SerialName("GreedyHint")
    object GreedyHint : HanabiAction()

    /**
     * Tells any missing information, used in Outer agent. Prioritizes color hints
     * (as stated in https://github.com/fossgalaxy/hanabi)
     * Implemented after research, therefore it's not presented in pool, but can be added later
     */
    @Serializable
    @SerialName("UnknownOuterHint")
    object UnknownOuterHint : HanabiAction()

    /**
     * Interpretation of [PlayableHint] featured in Walton-Rivers agents and HOAD.
     *
     * Loops in order of player turns and card slots, giving the hint on first applicable card.
     * Prioritizes rank hints.
     *
     * Implemented after research, therefore it's not presented in pool, but can be added in next version
     * (possibly as PlayableHint's parameter)
     */
    @Serializable
    @SerialName("WaltonPlayableHint")
    object WaltonPlayableHint : HanabiAction()

    /**
     * Same as [ProbabilityPlay], but uses probabilities for unknown cards (more complex version)
     * Used in Van den Bergh and Piers agents
     *
     * Applicable for more difficult action pools due to additional probability calculation. May outperform default
     * ProbabilityPlay because of it.
     *
     * Implemented after research.
     */
    @Serializable
    @SerialName("FullProbabilityPlay")
    data class FullProbabilityPlay(val value: Double) : HanabiAction()

    /**
     * Same as [FullProbabilityPlay], but triggers on empty deck
     */
    @Serializable
    @SerialName("FullEmptyDeckProbabilityPlay")
    data class FullEmptyDeckProbabilityPlay(val value: Double) : HanabiAction()
}
