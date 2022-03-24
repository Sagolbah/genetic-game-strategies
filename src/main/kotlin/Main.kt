import kotlinx.serialization.json.Json
import ru.ifmo.genome.gamestrategies.hanabi.HanabiEnvironment
import ru.ifmo.genome.gamestrategies.hanabi.strategies.GeneticHanabiStrategy
import kotlinx.serialization.encodeToString as jsonEncode

fun main(args: Array<String>) {
    val env = HanabiEnvironment()
    println(Json.jsonEncode(GeneticHanabiStrategy().getStrategy()))
    val size = 1000
    val result = IntArray(size)
    val strategy = GeneticHanabiStrategy()
    repeat(size) {i -> result[i] = env.fit(strategy)}
    println(result.joinToString())
    println(result.average())
}

// 2 player mirror match, 1000 runs:
// listOf(HanabiAction.SafePlay, HanabiAction.RandomHint, HanabiAction.RandomDiscard) -> 3.961 pts avg.
// listOf(HanabiAction.SafePlay, HanabiAction.RandomHint, HanabiAction.NonHintedDiscard, HanabiAction.RandomDiscard) -> 4.192 pts avg.
