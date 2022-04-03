package ru.ifmo.genome.gamestrategies.hanabi.strategies

import ru.ifmo.genome.gamestrategies.core.Individual
import ru.ifmo.genome.gamestrategies.hanabi.strategies.preparedAgents.*

class GeneticHanabiStrategy : RuleBasedHanabiStrategy, Individual<GeneticHanabiStrategy> {
    private var strategy = VanDenBergh.getStrategy()
    private var fitness = 0.0

    override fun mutate(): GeneticHanabiStrategy {
        TODO("Not yet implemented")
    }

    override fun getFitness(): Double {
        return fitness
    }

    override fun setFitness(fitness: Double) {
        this.fitness = fitness
    }

    override fun getStrategy(): List<HanabiAction> {
        return strategy
    }

}