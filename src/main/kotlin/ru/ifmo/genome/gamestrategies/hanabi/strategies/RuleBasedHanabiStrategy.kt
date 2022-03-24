package ru.ifmo.genome.gamestrategies.hanabi.strategies

fun interface RuleBasedHanabiStrategy {
    fun getStrategy() : List<HanabiAction>
}