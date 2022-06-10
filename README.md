# Genetic Game Strategies

This repository contains source code of "Generation of simple game strategies with evolutionary algorithms"
project. This project was developed as bachelor's degree work in ITMO University, CTD.

### Project structure

The project is divided by two parts: the evolution part written in Kotlin and game polygon written in Python
with "Hanabi Learning Environment" by DeepMind. This library provides game logic and enforces that all original Hanabi rules
are not violated by player's turns.

Python part provides a WebSocket server with Hanabi game. Developed server is multiprocess-safe.

Kotlin part performs the evolution process, communicating and evaluating efficiently with coroutines

### Requirements

* Kotlin 1.5 or newer
* Ktor-Client 2.0.0 or newer (for WebSocket communication)
* kotlinx.serialization package 1.6.10 or newer
* Python 3.7 or newer. PyPy is **highly** recommended for better performance
* [Hanabi Learning Environment library](https://github.com/deepmind/hanabi-learning-environment)
