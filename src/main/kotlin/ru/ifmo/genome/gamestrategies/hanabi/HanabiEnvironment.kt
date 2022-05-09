package ru.ifmo.genome.gamestrategies.hanabi

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString as jsonEncode
import ru.ifmo.genome.gamestrategies.core.Environment
import ru.ifmo.genome.gamestrategies.hanabi.strategies.GeneticHanabiStrategy

class HanabiEnvironment(val rounds: Int = 50) : Environment<GeneticHanabiStrategy> {
    private val seed = System.currentTimeMillis().toInt()
    private val gate = Semaphore(10)  // Limit sent queries to prevent unexpected timeouts

    private val client = HttpClient(CIO) {
        install(WebSockets) {
            // Configure WebSockets
        }
    }


    /**
     * Sends dummy requests sequentially. This is a workaround for hanabi_env.py to behave correctly.
     * Python Websockets library mixed with process pool behaves strange, on launch the server needs a warmup.
     * Otherwise, on large amount (approx. 30) of concurrent requests it can become irresponsible.
     *
     * If multiple environments are created, only one warmup across them is required.
     */
    suspend fun warmup() {
        runBlocking {
            repeat(5) {
                client.webSocket(method = HttpMethod.Get, host = "127.0.0.1", port = 8765, path = "/") {
                    send(Json.jsonEncode(RunConfiguration(1, 0, listOf(emptyList(), emptyList()))))
                    incoming.receive()
                }
            }
        }
    }

    override suspend fun fit(individual: GeneticHanabiStrategy): Double = coroutineScope {
        gate.withPermit {
            val twoPlayers = async(Dispatchers.IO) { runEnvironment(individual, 0, 2) }
            val threePlayers = async(Dispatchers.IO) { runEnvironment(individual, 1, 3) }
            val fourPlayers = async(Dispatchers.IO) { runEnvironment(individual, 2, 4) }
            (twoPlayers.await() + threePlayers.await() + fourPlayers.await()) / 3.0
        }
    }

    private suspend fun runEnvironment(individual: GeneticHanabiStrategy, seedShift: Int, playersCnt: Int): Double {
        var result: Double? = null
        client.webSocket(method = HttpMethod.Get, host = "127.0.0.1", port = 8765, path = "/") {
            send(
                Json.jsonEncode(
                    RunConfiguration(
                        rounds,
                        seed + seedShift,
                        List(playersCnt) { individual.getStrategy() })
                )
            )
            result = (incoming.receive() as? Frame.Text)?.readText()!!.toDouble()
        }
        return result ?: throw IllegalStateException("Result is not initialized")
    }

}