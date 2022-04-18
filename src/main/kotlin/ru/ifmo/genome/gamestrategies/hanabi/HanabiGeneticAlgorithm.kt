package ru.ifmo.genome.gamestrategies.hanabi

import ru.ifmo.genome.gamestrategies.core.Environment
import ru.ifmo.genome.gamestrategies.core.GeneticAlgorithm
import ru.ifmo.genome.gamestrategies.hanabi.strategies.GeneticHanabiStrategy
import ru.ifmo.genome.gamestrategies.hanabi.strategies.hanabiActionPool

class HanabiGeneticAlgorithm(
    override val env: Environment<GeneticHanabiStrategy>, private val epochs: Int,
    private val strategySize: Int = 7,
    private val populationSize: Int = 32,
    private val elitismCount: Int = 8,
    private val mutationRate: Double = 0.2,
    private val crossoverRate: Double = 0.8
) :
    GeneticAlgorithm<GeneticHanabiStrategy>(env) {


    override fun terminateCondition(): Boolean {
        return epoch == epochs
    }

    override fun initPopulation(): List<GeneticHanabiStrategy> {
        return List(populationSize) {
            GeneticHanabiStrategy(List(strategySize) { hanabiActionPool.random() })
        }
    }

    override fun selectParents(): List<GeneticHanabiStrategy> {
        return currentPopulation.sortedByDescending { i -> i.getFitness() }.take(elitismCount)
    }

    override fun crossover(parents: List<GeneticHanabiStrategy>): List<GeneticHanabiStrategy> {
        TODO("Not yet implemented")
    }

    override fun selectSurvivors(
        parents: List<GeneticHanabiStrategy>,
        children: List<GeneticHanabiStrategy>
    ): List<GeneticHanabiStrategy> {
        TODO("Not yet implemented")
    }
}