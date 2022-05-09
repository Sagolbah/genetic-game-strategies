import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString as jsonEncode
import ru.ifmo.genome.gamestrategies.core.basic.OnePlusOneGeneticAlgorithm
import ru.ifmo.genome.gamestrategies.hanabi.HanabiEnvironment
import ru.ifmo.genome.gamestrategies.hanabi.HanabiGeneticAlgorithm
import ru.ifmo.genome.gamestrategies.hanabi.benchmark.MirrorBenchmark
import ru.ifmo.genome.gamestrategies.hanabi.strategies.GeneticHanabiStrategy
import ru.ifmo.genome.gamestrategies.hanabi.strategies.HanabiAction
import ru.ifmo.genome.gamestrategies.hanabi.strategies.hanabiActionPool
import ru.ifmo.genome.gamestrategies.hanabi.strategies.randomAction

fun main(args: Array<String>) {
    runBlocking {
        val env = HanabiEnvironment(rounds = 50)
        env.warmup()
        println("Running genetic algorithm")
        val strategies = runGeneticAlgorithm(env)
        println("Evaluating best individuals on large data")
        for (strategy in strategies) {
            println("Evaluating strategy: " + Json.jsonEncode(strategy.getStrategy()))
            MirrorBenchmark.runBenchmark(strategy)
        }
    }
}


suspend fun runGeneticAlgorithm(env: HanabiEnvironment): List<GeneticHanabiStrategy> {
    val algorithm = HanabiGeneticAlgorithm(
        env,
        epochs = 100,
        populationSize = 100,
        elitismCount = 10,
        strategySize = 8,
        forceNewChildren = true
    )
    val result = algorithm.evaluate().sortedByDescending { x -> x.getFitness() }
    return result.take(5)
}


suspend fun evaluateOnePlusOne(env: HanabiEnvironment, size: Int) {
    println("=== $size ===")
    val startStrategy = GeneticHanabiStrategy(List(size) { randomAction() })
    val algo = OnePlusOneGeneticAlgorithm(env, 2000, startStrategy)
    val result = algo.evaluate()
    println(Json.jsonEncode(result[0].getStrategy()))
}