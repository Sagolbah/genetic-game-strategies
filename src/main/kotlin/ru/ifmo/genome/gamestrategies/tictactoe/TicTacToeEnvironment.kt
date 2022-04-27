package ru.ifmo.genome.gamestrategies.tictactoe

import ru.ifmo.genome.gamestrategies.core.Environment
import ru.ifmo.genome.gamestrategies.tictactoe.strategies.GeneticTicTacToeStrategy
import ru.ifmo.genome.gamestrategies.tictactoe.strategies.RandomTicTacToeStrategy


/**
 * Tic-tac-toe environment.
 * Was made as a proof of concept for rule-based strategies and genetic algorithms to generate them.
 */
class TicTacToeEnvironment : Environment<GeneticTicTacToeStrategy> {
    private val field: Array<Array<FieldStatus>> = Array(3) { Array(3) { FieldStatus.FREE } }
    private val opponents: Array<RandomTicTacToeStrategy> = Array(1000) { i -> RandomTicTacToeStrategy(this, i) }
    private val matchCount = 10
    private val victoryReward = 3
    private val drawReward = 1

    override fun fit(individual: GeneticTicTacToeStrategy): Double {
        opponents.forEach { x -> x.resetRandom() }  // The fitness function must be deterministic.
        var fitness = 0
        for (player2 in opponents) {
            repeat(matchCount) {
                reset()
                while (true) {
                    val playerResult = move(individual.makeTurn(), FieldStatus.CROSS)
                    if (playerResult == GameResult.CROSS_WIN) {
                        fitness += victoryReward
                        break
                    }
                    if (playerResult == GameResult.DRAW) {
                        fitness += drawReward
                        break
                    }
                    val opponentResult = move(player2.makeTurn(), FieldStatus.ZERO)
                    if (opponentResult == GameResult.DRAW) {
                        fitness += drawReward
                        break
                    } else if (opponentResult == GameResult.ZEROS_WIN) {
                        break
                    }
                }
                player2.resetRandom()
            }
        }
        return fitness.toDouble()
    }

    fun getField(): Array<Array<FieldStatus>> {
        return field
    }

    private fun move(coords: Pair<Int, Int>, color: FieldStatus): GameResult {
        if (field[coords.first][coords.second] != FieldStatus.FREE) {
            throw AssertionError("Move on occupied field")
        }
        field[coords.first][coords.second] = color
        return getGameResult()
    }

    private fun getGameResult(): GameResult {
        // horizontal & vertical
        for (i in 0..2) {
            val cur = field[i][0]
            if (field[i][1] == field[i][2] && field[i][1] == cur) {
                val score = colorScore(cur)
                if (score != GameResult.UNFINISHED) return score
            }
        }
        for (j in 0..2) {
            val cur = field[0][j]
            if (field[1][j] == field[2][j] && field[1][j] == cur) {
                val score = colorScore(cur)
                if (score != GameResult.UNFINISHED) return score
            }
        }
        // diagonal
        val cur = field[1][1]
        if ((field[0][0] == field[2][2] && field[0][0] == cur) || (field[0][2] == field[2][0] && field[0][2] == cur)) {
            val score = colorScore(cur)
            if (score != GameResult.UNFINISHED) return score
        }
        // check draw
        for (i in 0..2) {
            for (j in 0..2) {
                if (field[i][j] == FieldStatus.FREE) {
                    return GameResult.UNFINISHED
                }
            }
        }
        return GameResult.DRAW
    }


    private fun colorScore(status: FieldStatus): GameResult {
        return when (status) {
            FieldStatus.FREE -> GameResult.UNFINISHED
            FieldStatus.CROSS -> GameResult.CROSS_WIN
            FieldStatus.ZERO -> GameResult.ZEROS_WIN
        }
    }

    private fun reset() {
        for (i in 0..2) {
            for (j in 0..2) {
                field[i][j] = FieldStatus.FREE
            }
        }
    }
}