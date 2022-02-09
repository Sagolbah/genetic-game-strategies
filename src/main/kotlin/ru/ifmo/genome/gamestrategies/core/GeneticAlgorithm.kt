package ru.ifmo.genome.gamestrategies.core

/**
 * Abstract class for genetic algorithm
 * @param env Environment used to evaluate fitness
 */
abstract class GeneticAlgorithm<T : Individual>(protected open val env: Environment<T>) {
    protected var epoch = 0
    protected var currentPopulation = emptyList<T>()

    abstract fun terminateCondition(): Boolean

    abstract fun initPopulation(): List<T>

    abstract fun selectParents(): List<T>

    abstract fun crossover() : List<T>

    open fun evaluatePopulation() {
        currentPopulation.forEach { x -> x.setFitness(env.fit(x)) }
    }

    open fun mutate() {
        currentPopulation.forEach { x -> x.mutate() }
    }

    open fun evaluate() {
        currentPopulation = initPopulation()
        evaluatePopulation()
        while (!terminateCondition()) {
            epoch++
            currentPopulation = selectParents()
            currentPopulation = crossover()
            mutate()
            evaluatePopulation()
        }
    }

    fun getGenerationNumber(): Int {
        return epoch
    }

}