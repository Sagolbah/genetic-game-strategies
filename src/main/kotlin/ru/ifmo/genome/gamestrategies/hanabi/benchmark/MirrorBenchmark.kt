package ru.ifmo.genome.gamestrategies.hanabi.benchmark

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import ru.ifmo.genome.gamestrategies.hanabi.RunConfiguration
import ru.ifmo.genome.gamestrategies.hanabi.strategies.GeneticHanabiStrategy
import kotlinx.serialization.encodeToString as jsonEncode

class MirrorBenchmark {

    companion object {
        private const val seed = 0

        private val client = HttpClient(CIO) {
            install(WebSockets) {
            }
        }

        fun runBenchmark(individual: GeneticHanabiStrategy) {
            runBlocking {
                coroutineScope {
                    launch {
                        client.webSocket(method = HttpMethod.Get, host = "127.0.0.1", port = 8765, path = "/") {
                            send(Json.jsonEncode(RunConfiguration(10000, seed, List(2) { individual.getStrategy() }, true)))
                            println("2 players: " + (incoming.receive() as? Frame.Text)?.readText()!!)
                        }
                    }
                    launch {
                        client.webSocket(method = HttpMethod.Get, host = "127.0.0.1", port = 8765, path = "/") {
                            send(Json.jsonEncode(RunConfiguration(10000, seed + 1, List(3) { individual.getStrategy() }, true)))
                            println("3 players: " + (incoming.receive() as? Frame.Text)?.readText()!!)
                        }
                    }
                    launch {
                        client.webSocket(method = HttpMethod.Get, host = "127.0.0.1", port = 8765, path = "/") {
                            send(Json.jsonEncode(RunConfiguration(10000, seed + 2, List(4) { individual.getStrategy() }, true)))
                            println("4 players: " + (incoming.receive() as? Frame.Text)?.readText()!!)
                        }
                    }
                }
            }
        }
    }


}
