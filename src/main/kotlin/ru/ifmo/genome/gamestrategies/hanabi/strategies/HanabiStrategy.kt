package ru.ifmo.genome.gamestrategies.hanabi.strategies

import kotlinx.serialization.Serializable
import ru.ifmo.genome.gamestrategies.core.Individual

class HanabiStrategy : Individual<HanabiStrategy> {
    private var root = HanabiNode(HanabiHintCondition.HasHintTokens(1), HanabiAction.RandomHint, null)

    override fun mutate(): HanabiStrategy {
        TODO("Not yet implemented")
    }

    override fun getFitness(): Int {
        TODO("Not yet implemented")
    }

    override fun setFitness(fitness: Int) {
        TODO("Not yet implemented")
    }

    fun getRootNode() : HanabiNode {
        return root
    }

    @Serializable
    data class HanabiNode(val condition: HanabiHintCondition, val action: HanabiAction, val nextNode: HanabiNode?)

}