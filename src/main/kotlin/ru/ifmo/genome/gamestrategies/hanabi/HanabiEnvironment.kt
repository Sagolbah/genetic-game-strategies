package ru.ifmo.genome.gamestrategies.hanabi

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.websocket.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString as jsonEncode
import ru.ifmo.genome.gamestrategies.core.Environment
import ru.ifmo.genome.gamestrategies.hanabi.strategies.GeneticHanabiStrategy

class HanabiEnvironment : Environment<GeneticHanabiStrategy> {
    private val seed = System.currentTimeMillis().toInt()

    private val client = HttpClient(CIO) {
        install(WebSockets) {
            // Configure WebSockets
        }
    }

    override fun fit(individual: GeneticHanabiStrategy): Double {
        var result = 0.0
        runBlocking {
            client.webSocket(method = HttpMethod.Get, host = "127.0.0.1", port = 8765, path = "/") {
                send(Json.jsonEncode(RunConfiguration(15, seed, List(2) {individual.getStrategy()})))
                result += (incoming.receive() as? Frame.Text)?.readText()!!.toDouble()
            }
            client.webSocket(method = HttpMethod.Get, host = "127.0.0.1", port = 8765, path = "/") {
                send(Json.jsonEncode(RunConfiguration(15, seed, List(3) { individual.getStrategy() })))
                result += (incoming.receive() as? Frame.Text)?.readText()!!.toDouble()
            }
        }
        return result / 2
    }
}