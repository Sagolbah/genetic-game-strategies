import kotlinx.serialization.json.Json
import ru.ifmo.genome.gamestrategies.hanabi.HanabiEnvironment
import ru.ifmo.genome.gamestrategies.hanabi.strategies.GeneticHanabiStrategy
import kotlinx.serialization.encodeToString as jsonEncode

fun main(args: Array<String>) {
    val env = HanabiEnvironment()
    println(Json.jsonEncode(GeneticHanabiStrategy().getStrategy()))
    val size = 100
    val result = IntArray(size)
    val strategy = GeneticHanabiStrategy()
    repeat(size) {i -> result[i] = env.fit(strategy)}
    println(result.joinToString())
    println(result.average())
}

