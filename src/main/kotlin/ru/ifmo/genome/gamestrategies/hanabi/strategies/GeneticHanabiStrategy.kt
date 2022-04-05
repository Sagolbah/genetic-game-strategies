package ru.ifmo.genome.gamestrategies.hanabi.strategies

import ru.ifmo.genome.gamestrategies.core.Individual
import ru.ifmo.genome.gamestrategies.hanabi.strategies.preparedAgents.*
import kotlin.random.Random

class GeneticHanabiStrategy(private var rules: List<HanabiAction>) : RuleBasedHanabiStrategy, Individual<GeneticHanabiStrategy> {
    private val strategySize = rules.size
    private val probabilities = listOf(0.0, 0.25, 0.5, 0.75)
    private var fitness = 0.0

    override fun mutate(): GeneticHanabiStrategy {
        val newRules = rules.toMutableList()
        val swapWithPool = Random.nextBoolean()
        if (swapWithPool) {
            val oldRuleIdx = Random.nextInt(strategySize)
            var newRule = hanabiActionPool[Random.nextInt(hanabiActionPool.size)]
            if (newRule is HanabiAction.ProbabilityPlay) {
                newRule = HanabiAction.ProbabilityPlay(probabilities.random())
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