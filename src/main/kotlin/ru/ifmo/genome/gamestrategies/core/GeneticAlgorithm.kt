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

    abstract fun crossover(parents: List<T>) : List<T>

    abstract fun selectSurvivors(oldPopulation: List<T>, children: List<T>): List<T>

    open fun evaluatePopulation(population: List<T>) {
        population.forEach { x -> x.setFitness(env.fit(x)) }
    }

    open fun mutate(individuals: List<T>) : List<T> {
        return individuals.map { x -> x.mutate() }.toList()
    }

    open fun onEpochBeginning() {}

    open fun evaluate() : List<T> {
        currentPopulation = initPopulation()
        evaluatePopulation(currentPopulation)
        while (!terminateCondition()) {
            onEpochBeginning()
            epoch++
            val parentsForCrossover = selectParents()
            val mutatedChildren = mutate(crossover(parentsForCrossover))
            evaluatePopulation(mutatedChildren)
            currentPopulation = selectSurvivors(currentPopulation, mutatedChildren)
        }
        return currentPopulation
    }

    fun getGenerationNumber(): Int {
        return epoch
    }

}