import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.websocket.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString as jsonEncode
import ru.ifmo.genome.gamestrategies.core.basic.OnePlusOneGeneticAlgorithm
import ru.ifmo.genome.gamestrategies.hanabi.strategies.HanabiAction
import ru.ifmo.genome.gamestrategies.hanabi.strategies.HanabiStrategy
import ru.ifmo.genome.gamestrategies.tictactoe.TicTacToeEnvironment
import ru.ifmo.genome.gamestrategies.tictactoe.strategies.GeneticTicTacToeStrategy
import java.util.*

fun main(args: Array<String>) {
    /*
    val env = TicTacToeEnvironment()
    val alg = OnePlusOneGeneticAlgorithm(env, 2000, GeneticTicTacToeStrategy(env))
    val result = alg.evaluate()[0]
    for (action in result.getActions()) {
        println(action.actionParameters.joinToString() + " " + action.fieldStatus)
    }
    println(result)
     */

    val client = HttpClient(CIO) {
        install(WebSockets) {
            // Configure WebSockets
        }
    }
    runBlocking {
        client.webSocket(method = HttpMethod.Get, host = "127.0.0.1", port = 8765, path = "/") {
            val strat = HanabiStrategy()
            send(Json.jsonEncode(strat.getRootNode()))
            val othersMessage = incoming.receive() as? Frame.Text
            println(othersMessage?.readText())
        }
    }

}