import ru.ifmo.genome.gamestrategies.core.basic.OnePlusOneGeneticAlgorithm
import ru.ifmo.genome.gamestrategies.tictactoe.TicTacToeEnvironment
import ru.ifmo.genome.gamestrategies.tictactoe.strategies.GeneticTicTacToeStrategy

fun main(args: Array<String>) {
    val env = TicTacToeEnvironment()
    val alg = OnePlusOneGeneticAlgorithm(env, 2000, GeneticTicTacToeStrategy(env))
    //val alg = TicTacToeGeneticAlgorithm(env)
    val result = alg.evaluate()[0]
    for (action in result.getActions()) {
        println(action.actionParameters.joinToString() + " " + action.fieldStatus)
    }
    println(result)
}