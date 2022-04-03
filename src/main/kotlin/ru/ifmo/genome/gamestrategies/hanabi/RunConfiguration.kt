package ru.ifmo.genome.gamestrategies.hanabi

import kotlinx.serialization.Serializable
import ru.ifmo.genome.gamestrategies.hanabi.strategies.HanabiAction

/**
 * Configuration for Hanabi interactor
 * @param rounds how many rounds to play
 * @param seed seed for random generator, which is used in fitness evaluation
 * @param players participating strategies, one agent can be used multiple times for mirror matches
 */
@Serializable
data class RunConfiguration(val rounds: Int, val seed: Int, val players: List<List<HanabiAction>>)
