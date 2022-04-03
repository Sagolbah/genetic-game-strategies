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
import ru.ifmo.genome.gamestrategies.hanabi.strategies.preparedAgents.*

class HanabiEnvironment : Environment<GeneticHanabiStrategy> {
    private val client = HttpClient(CIO) {
        install(WebSockets) {
            // Configure WebSockets
        }
    }

    override fun fit(individual: GeneticHanabiStrategy): Double {
        var result = 0.0
        runBlocking {
            client.webSocket(method = HttpMethod.Get, host = "127.0.0.1", port = 8765, path = "/") {
                send(Json.jsonEncode(RunConfiguration(20, 1, listOf(individual.getStrategy(), VanDenBergh.getStrategy()))))
                val interactorResult = incoming.receive() as? Frame.Text
                result = interactorResult?.readText()!!.toDouble()
            }
        }
        return result
    }
}