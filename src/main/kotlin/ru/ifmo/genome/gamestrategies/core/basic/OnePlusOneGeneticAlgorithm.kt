package ru.ifmo.genome.gamestrategies.core.basic

import ru.ifmo.genome.gamestrategies.core.Environment
import ru.ifmo.genome.gamestrategies.core.GeneticAlgorithm
import ru.ifmo.genome.gamestrategies.core.Individual

class OnePlusOneGeneticAlgorithm<T : Individual<T>>(env: Environment<T>, val epochs: Int, val start: T) :
    GeneticAlgorithm<T>(env) {
    override fun terminateCondition(): Boolean {
        return epoch == epochs
    }

    override fun initPopulation(): List<T> {
        return listOf(start)
    }

    override fun selectParents(): List<T> {
        return currentPopulation
    }

    override fun crossover(parents: List<T>): List<T> {
        return currentPopulation  // skip crossover
    }

    override fun selectSurvivors(parents: List<T>, children: List<T>): List<T> {
        val parent = parents[0]
        val child = children[0]
        return listOf(if (parent.getFitness() > child.getFitness()) parent else child)
    }

    override fun logEpoch() {
        println("Epoch %d, fitness %f".format(epoch, currentPopulation[0].getFitness()))
    }
}