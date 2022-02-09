package ru.ifmo.genome.gamestrategies.tictactoe.strategies

import ru.ifmo.genome.gamestrategies.tictactoe.TicTacToeEnvironment


abstract class TicTacToeStrategy(protected open val env: TicTacToeEnvironment) {

    abstract fun makeTurn(): Pair<Int, Int>

}