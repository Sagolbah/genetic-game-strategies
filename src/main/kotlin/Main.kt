import kotlinx.serialization.json.Json
import ru.ifmo.genome.gamestrategies.hanabi.HanabiEnvironment
import ru.ifmo.genome.gamestrategies.hanabi.strategies.HanabiStrategy
import kotlinx.serialization.encodeToString as jsonEncode

fun main(args: Array<String>) {
    val env = HanabiEnvironment()
    println(Json.jsonEncode(HanabiStrategy().getStrategy()))
    val size = 100
    val result = IntArray(size)
    val strategy = HanabiStrategy()
    repeat(size) {i -> result[i] = env.fit(strategy)}
    println(result.joinToString())
    println(result.average())
}

// listOf(HanabiAction.SafePlay, HanabiAction.RandomHint, HanabiAction.RandomDiscard) -> 3.74 pts avg.