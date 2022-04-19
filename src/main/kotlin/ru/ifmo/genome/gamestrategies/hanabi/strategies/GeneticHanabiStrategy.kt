package ru.ifmo.genome.gamestrategies.hanabi.strategies

import ru.ifmo.genome.gamestrategies.core.Individual
import ru.ifmo.genome.gamestrategies.hanabi.strategies.preparedAgents.*
import kotlin.random.Random

class GeneticHanabiStrategy(private var rules: List<HanabiAction>) : RuleBasedHanabiStrategy, Individual<GeneticHanabiStrategy> {
    private val strategySize = rules.size
    private val probabilities = listOf(0.0, 0.2, 0.4, 0.5, 0.6, 0.8)
    private val probabilitiesEmptyDeck = listOf(0.0, 0.1, 0.25, 0.4, 0.5)
    private var fitness = -1.0

    override fun mutate(): GeneticHanabiStrategy {
        val newRules = rules.toMutableList()
        val swapWithPool = Random.nextBoolean()
        if (swapWithPool) {
            val oldRuleIdx = Random.nextInt(strategySize)
            var newRule = hanabiActionPool[Random.nextInt(hanabiActionPool.size)]
            if (newRule is HanabiAction.ProbabilityPlay) {
                newRule = HanabiAction.ProbabilityPlay(probabilities.random())
            } else if (newRule is HanabiAction.EmptyDeckProbabilityPlay) {
                newRule = HanabiAction.EmptyDeckProbabilityPlay(probabilitiesEmptyDeck.random())
            }
            newRules[oldRuleIdx] = newRule
        } else {
            val idx1 = Random.nextInt(strategySize)
            val idx2 = Random.nextInt(strategySize)
            val tmp = newRules[idx1]
            newRules[idx1] = newRules[idx2]
            newRules[idx2] = tmp
        }
        return GeneticHanabiStrategy(newRules)
    }

    override fun getFitness(): Double {
        return fitness
    }

    override fun setFitness(fitness: Double) {
        this.fitness = fitness
    }

    override fun getStrategy(): List<HanabiAction> {
        return rules
    }

}