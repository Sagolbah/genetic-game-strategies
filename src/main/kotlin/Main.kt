import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString as jsonEncode
import ru.ifmo.genome.gamestrategies.core.basic.OnePlusOneGeneticAlgorithm
import ru.ifmo.genome.gamestrategies.hanabi.HanabiEnvironment
import ru.ifmo.genome.gamestrategies.hanabi.strategies.GeneticHanabiStrategy
import ru.ifmo.genome.gamestrategies.hanabi.strategies.hanabiActionPool

fun main(args: Array<String>) {
    val env = HanabiEnvironment()
    val startStrategy = GeneticHanabiStrategy(List(7) { hanabiActionPool.random() })
    val algo = OnePlusOneGeneticAlgorithm(env, 500, startStrategy)
    val result = algo.evaluate()
    println(Json.jsonEncode(result[0].getStrategy()))
}
// internal 9.576, outer 13.068
// new internal 9.8, outer

