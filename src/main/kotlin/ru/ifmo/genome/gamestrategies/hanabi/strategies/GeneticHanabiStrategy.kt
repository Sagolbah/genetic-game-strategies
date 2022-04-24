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
            newRules[idx] = rollRuleParameter(hanabiActionPool.random())
        }
        return GeneticHanabiStrategy(newRules)
    }

    private fun rollRuleParameter(rule: HanabiAction): HanabiAction {
        return when (rule) {
            is HanabiAction.ProbabilityPlay -> HanabiAction.ProbabilityPlay(probabilityPlayParams.random())
            is HanabiAction.EmptyDeckProbabilityPlay -> HanabiAction.EmptyDeckProbabilityPlay(
                emptyDeckProbabilityPlayParams.random()
            )
            else -> rule
        }
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