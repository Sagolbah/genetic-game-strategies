import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString as jsonEncode
import ru.ifmo.genome.gamestrategies.core.basic.OnePlusOneGeneticAlgorithm
import ru.ifmo.genome.gamestrategies.hanabi.HanabiEnvironment
import ru.ifmo.genome.gamestrategies.hanabi.HanabiGeneticAlgorithm
import ru.ifmo.genome.gamestrategies.hanabi.strategies.GeneticHanabiStrategy
import ru.ifmo.genome.gamestrategies.hanabi.strategies.HanabiAction
import ru.ifmo.genome.gamestrategies.hanabi.strategies.hanabiActionPool
import ru.ifmo.genome.gamestrategies.hanabi.strategies.preparedAgents.*

fun main(args: Array<String>) {
    val env = HanabiEnvironment()
    evaluate(env, 6)
}




fun evaluate(env: HanabiEnvironment, size: Int) {
    println("=== $size ===")
    val startStrategy = GeneticHanabiStrategy(List(size) { hanabiActionPool.random() })
    val algo = OnePlusOneGeneticAlgorithm(env, 500, startStrategy)
    val result = algo.evaluate()
    println(Json.jsonEncode(result[0].getStrategy()))
}