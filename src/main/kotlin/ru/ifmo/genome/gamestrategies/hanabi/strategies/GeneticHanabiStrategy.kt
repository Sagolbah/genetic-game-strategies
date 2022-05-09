package ru.ifmo.genome.gamestrategies.hanabi.strategies

import ru.ifmo.genome.gamestrategies.core.Individual
import kotlin.random.Random

class GeneticHanabiStrategy(private val rules: List<HanabiAction>) : RuleBasedHanabiStrategy,
    Individual<GeneticHanabiStrategy> {
    private val strategySize = rules.size
    private val mutationRate = 1.0 / rules.size
    private var fitness = -1.0

    override fun mutate(): GeneticHanabiStrategy {
        val newRules = rules.toMutableList()
        var indices = rules.indices.filter { Random.nextDouble() < mutationRate }
        if (indices.isEmpty()) {
            indices = listOf(Random.nextInt(strategySize))
        }
        for (idx in indices) {
            newRules[idx] = randomAction()
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

    override fun equals(other: Any?): Boolean {
        if (other !is GeneticHanabiStrategy) return false
        return rules == other.rules
    }

    override fun hashCode(): Int {
        var result = rules.hashCode()
        result = 31 * result + strategySize
        result = 31 * result + mutationRate.hashCode()
        result = 31 * result + fitness.hashCode()
        return result
    }

}