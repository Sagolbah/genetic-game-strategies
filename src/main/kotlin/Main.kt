import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString as jsonEncode
import ru.ifmo.genome.gamestrategies.core.basic.OnePlusOneGeneticAlgorithm
import ru.ifmo.genome.gamestrategies.hanabi.HanabiEnvironment
import ru.ifmo.genome.gamestrategies.hanabi.HanabiGeneticAlgorithm
import ru.ifmo.genome.gamestrategies.hanabi.benchmark.MirrorBenchmark
import ru.ifmo.genome.gamestrategies.hanabi.strategies.GeneticHanabiStrategy
import ru.ifmo.genome.gamestrategies.hanabi.strategies.HanabiAction
import ru.ifmo.genome.gamestrategies.hanabi.strategies.hanabiActionPool

fun main(args: Array<String>) {
    println("Running genetic algorithm")
    val strategies = runGeneticAlgorithm()
    val validationEnv = HanabiEnvironment(rounds = 1000)
    for (strategy in strategies) {
        println("Evaluating strategy: " + Json.jsonEncode(strategy.getStrategy()))
        println(validationEnv.fit(strategy))
    }
}


fun runGeneticAlgorithm(): List<GeneticHanabiStrategy> {
    val env = HanabiEnvironment(rounds = 50)
    val result =
        HanabiGeneticAlgorithm(env, 100, strategySize = 8).evaluate().sortedByDescending { x -> x.getFitness() }
    return result.take(5)
}


fun evaluateOnePlusOne(env: HanabiEnvironment, size: Int) {
    println("=== $size ===")
    val startStrategy = GeneticHanabiStrategy(List(size) { hanabiActionPool.random() })
    val algo = OnePlusOneGeneticAlgorithm(env, 500, startStrategy)
    val result = algo.evaluate()
    println(Json.jsonEncode(result[0].getStrategy()))
}