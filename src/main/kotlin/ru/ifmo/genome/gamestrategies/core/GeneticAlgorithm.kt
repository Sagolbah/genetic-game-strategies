package ru.ifmo.genome.gamestrategies.core

/**
 * Abstract class for genetic algorithm
 * @param env Environment used to evaluate fitness
 */
abstract class GeneticAlgorithm<T : Individual<T>>(protected open val env: Environment<T>) {
    protected var epoch = 0
    protected var currentPopulation = emptyList<T>()

    abstract fun terminateCondition(): Boolean

    abstract fun initPopulation(): List<T>

    abstract fun selectParents(): List<T>

    abstract fun crossover() : List<T>

    open fun evaluatePopulation() {
        currentPopulation.forEach { x -> x.setFitness(env.fit(x)) }
    }

    open fun mutate() : List<T> {
        return currentPopulation.map { x -> x.mutate() }.toList()
    }

    open fun evaluate() : List<T> {
        currentPopulation = initPopulation()
        evaluatePopulation()
        while (!terminateCondition()) {
            epoch++
            currentPopulation = selectParents()
            currentPopulation = crossover()
            currentPopulation = mutate()
            evaluatePopulation()
        }
        return currentPopulation
    }

    fun getGenerationNumber(): Int {
        return epoch
    }

}