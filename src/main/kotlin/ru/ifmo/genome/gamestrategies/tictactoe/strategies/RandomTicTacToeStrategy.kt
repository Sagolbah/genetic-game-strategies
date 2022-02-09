package ru.ifmo.genome.gamestrategies.tictactoe.strategies

import ru.ifmo.genome.gamestrategies.tictactoe.FieldStatus
import ru.ifmo.genome.gamestrategies.tictactoe.TicTacToeEnvironment
import kotlin.random.Random

class RandomTicTacToeStrategy(override val env: TicTacToeEnvironment, private val seed: Int) : TicTacToeStrategy(env) {
    private var rng = Random(seed)

    override fun makeTurn(): Pair<Int, Int> {
        var coords = Pair(rng.nextInt(3), rng.nextInt(3))
        while (env.getField()[coords.first][coords.second] != FieldStatus.FREE) {
            coords = Pair(rng.nextInt(3), rng.nextInt(3))
        }
        return coords
    }

    fun resetRandom() {
        rng = Random(seed)
    }
}
