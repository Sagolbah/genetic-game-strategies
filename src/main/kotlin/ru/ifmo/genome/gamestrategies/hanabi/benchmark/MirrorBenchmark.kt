package ru.ifmo.genome.gamestrategies.hanabi.benchmark

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.websocket.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import ru.ifmo.genome.gamestrategies.hanabi.RunConfiguration
import ru.ifmo.genome.gamestrategies.hanabi.strategies.GeneticHanabiStrategy
import kotlinx.serialization.encodeToString as jsonEncode

class MirrorBenchmark {
    private val seed = System.currentTimeMillis().toInt()

    private val client = HttpClient(CIO) {
        install(WebSockets) {
            // Configure WebSockets
        }
    }

    fun runBenchmark(individual: GeneticHanabiStrategy) {
        runBlocking {
            client.webSocket(method = HttpMethod.Get, host = "127.0.0.1", port = 8765, path = "/") {
                send(Json.jsonEncode(RunConfiguration(10000, seed, List(2) { individual.getStrategy() })))
                println("2 players: " + (incoming.receive() as? Frame.Text)?.readText()!!)
            }
            client.webSocket(method = HttpMethod.Get, host = "127.0.0.1", port = 8765, path = "/") {
                send(Json.jsonEncode(RunConfiguration(10000, seed + 1, List(3) { individual.getStrategy() })))
                println("3 players: " + (incoming.receive() as? Frame.Text)?.readText()!!)
            }
            client.webSocket(method = HttpMethod.Get, host = "127.0.0.1", port = 8765, path = "/") {
                send(Json.jsonEncode(RunConfiguration(10000, seed + 2, List(4) { individual.getStrategy() })))
                println("4 players: " + (incoming.receive() as? Frame.Text)?.readText()!!)
            }
        }
    }
}