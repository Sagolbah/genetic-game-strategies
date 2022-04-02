import kotlinx.serialization.json.Json
import ru.ifmo.genome.gamestrategies.hanabi.HanabiEnvironment
import ru.ifmo.genome.gamestrategies.hanabi.strategies.GeneticHanabiStrategy
import kotlinx.serialization.encodeToString as jsonEncode

fun main(args: Array<String>) {
    val env = HanabiEnvironment()
    val strategy = GeneticHanabiStrategy()
    println(env.fit(strategy))
}
// internal 9.576, outer 13.068
// new internal 9.8, outer

