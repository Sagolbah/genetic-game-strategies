package ru.ifmo.genome.gamestrategies.hanabi.strategies

import kotlinx.serialization.Serializable

@Serializable
sealed class HanabiAction {

    // HINTS ACTIONS
    // ALL actions will happen only when hint token is available.

    /**
     * Tells random hint
     */
    @Serializable
    object RandomHint : HanabiAction()

    /**
     * Tells hint that affects most cards in hand
     */
    @Serializable
    object GreedyHint : HanabiAction()

    /**
     * Tells a hint on playable cards. Hint is chosen randomly across all.
     */
    @Serializable
    object PlayableHint : HanabiAction()

    /**
     * Same as [PlayableHint], but hint affects maximum amount of cards.
     */
    @Serializable
    object GreedyPlayableHint : HanabiAction()

    /**
     * Gives rank hint.
     * The reason that only rank hints are used is that "Rank 1-hint" guarantees good early-game.
     * @param rank rank of hint
     */
    @Serializable
    data class RankHint(val rank: Int) : HanabiAction()

    // DISCARD ACTIONS

    /**
     * Discards random card
     */
    @Serializable
    object RandomDiscard : HanabiAction()

    /**
     * Discards a card which cannot be played in current game
     * For example, all red cards are useless if "5-Red stack" is on the board.
     */
    @Serializable
    object UselessDiscard : HanabiAction()

    /**
     * Discards a card which was not hinted
     */
    @Serializable
    object NonHintedDiscard : HanabiAction()

    // PLAY ACTIONS

    /**
     * Play a safe card. Card is safe if playing it will not result in life token loss.
     */
    @Serializable
    object SafePlay : HanabiAction()

    /**
     * May be used only if > 1 life tokens available.
     * Only hinted cards are participating.
     *
     * Loops through all cards.
     * Plays a card with probability of correct guess higher or equal than provided parameter.
     * On multiple cards: will play card with the highest probability.
     * The probability is calculated with following data:
     * > visible cards of opponents
     * > cards in starting deck
     * > cards in discard pile & remaining deck
     *
     * Available cards: DECK - DISCARD - OTHER PLAYERS
     * Good card: which will play successfully
     * Probability: 1 - P(all good cards are in DECK)
     */
    @Serializable
    data class UnsafeProbabilityPlay(val probability: Float) : HanabiAction()


}
