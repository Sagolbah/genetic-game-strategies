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

    override fun evaluatePopulation() {
        super.evaluatePopulation()
        println("Epoch %d, fitness %d".format(epoch, currentPopulation[0].getFitness()))
    }

    override fun mutate(): List<T> {
        return currentPopulation  // skip the mutation phase
    }

    override fun selectParents(): List<T> {
        val newCandidate = currentPopulation[0].mutate()
        newCandidate.setFitness(env.fit(newCandidate))
        return if (newCandidate.getFitness() > currentPopulation[0].getFitness()) {
            listOf(newCandidate)
        } else {
            currentPopulation
        }
    }

    override fun crossover(): List<T> {
        return currentPopulation
    }
}