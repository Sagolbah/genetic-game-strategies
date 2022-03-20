package ru.ifmo.genome.gamestrategies.hanabi.strategies

import ru.ifmo.genome.gamestrategies.core.Individual

class HanabiStrategy : Individual<HanabiStrategy> {
    private var strategy = listOf(HanabiAction.SafePlay, HanabiAction.RandomHint, HanabiAction.RandomDiscard)



    override fun mutate(): HanabiStrategy {
        TODO("Not yet implemented")
    }

    override fun getFitness(): Int {
        TODO("Not yet implemented")
    }

    override fun setFitness(fitness: Int) {
        TODO("Not yet implemented")
    }

    fun getStrategy() : List<HanabiAction> {
        return strategy
    }

}