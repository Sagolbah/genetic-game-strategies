import ru.ifmo.genome.gamestrategies.tictactoe.TicTacToeEnvironment
import ru.ifmo.genome.gamestrategies.tictactoe.TicTacToeGeneticAlgorithm

fun main(args: Array<String>) {
    val env = TicTacToeEnvironment()
    val alg = TicTacToeGeneticAlgorithm(env)
    alg.evaluate()
}