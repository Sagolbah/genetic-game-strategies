package ru.ifmo.genome.gamestrategies.hanabi

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString as jsonEncode
import ru.ifmo.genome.gamestrategies.core.Environment
import ru.ifmo.genome.gamestrategies.hanabi.strategies.GeneticHanabiStrategy

class HanabiEnvironment(val rounds: Int = 50) : Environment<GeneticHanabiStrategy> {
    private val seed = System.currentTimeMillis().toInt()

    private val client = HttpClient(CIO) {
        install(WebSockets) {
            // Configure WebSockets
        }
    }

    override fun fit(individual: GeneticHanabiStrategy): Double {
        return runBlocking {
            val twoPlayers = async(Dispatchers.IO) { runEnvironment(individual, 0, 2) }
            val threePlayers = async(Dispatchers.IO) { runEnvironment(individual, 1, 3) }
            val fourPlayers = async(Dispatchers.IO) { runEnvironment(individual, 2, 4) }
            (twoPlayers.await() + threePlayers.await() + fourPlayers.await()) / 3.0
        }
    }

    private suspend fun runEnvironment(individual: GeneticHanabiStrategy, seedShift: Int, playersCnt: Int): Double {
        var result: Double? = null
        client.webSocket(method = HttpMethod.Get, host = "127.0.0.1", port = 8765, path = "/") {
            send(Json.jsonEncode(RunConfiguration(rounds, seed + seedShift, List(playersCnt) { individual.getStrategy() })))
            result = (incoming.receive() as? Frame.Text)?.readText()!!.toDouble()
        }
        return result ?: throw IllegalStateException("Result is not initialized")
    }

}