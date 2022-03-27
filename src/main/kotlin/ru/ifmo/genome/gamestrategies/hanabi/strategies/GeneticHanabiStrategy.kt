package ru.ifmo.genome.gamestrategies.hanabi.strategies

import ru.ifmo.genome.gamestrategies.core.Individual

class GeneticHanabiStrategy : RuleBasedHanabiStrategy, Individual<GeneticHanabiStrategy> {
    private var strategy = listOf(
        HanabiAction.SafePlay,
        HanabiAction.CompletePlayableHint,
        HanabiAction.PlayableHint,
        HanabiAction.UselessDiscard,
        HanabiAction.RandomDiscard
    )

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