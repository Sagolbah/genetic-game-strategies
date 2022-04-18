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
     * Discards card with highest rank
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

}
