package ru.ifmo.genome.gamestrategies.hanabi.strategies

import ru.ifmo.genome.gamestrategies.core.Individual
import ru.ifmo.genome.gamestrategies.hanabi.strategies.preparedAgents.*

class GeneticHanabiStrategy : RuleBasedHanabiStrategy, Individual<GeneticHanabiStrategy> {
    private var strategy = VanDenBergh.getStrategy()

    override fun mutate(): GeneticHanabiStrategy {
        TODO("Not yet implemented")
    }

    override fun getFitness(): Int {
        TODO("Not yet implemented")
    }

    override fun setFitness(fitness: Int) {
        TODO("Not yet implemented")
    }

    override fun getStrategy(): List<HanabiAction> {
        return strategy
    }

}