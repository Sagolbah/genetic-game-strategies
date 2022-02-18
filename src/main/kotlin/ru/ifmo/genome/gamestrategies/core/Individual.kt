package ru.ifmo.genome.gamestrategies.core

/**
 * Individual participating in genetic algorithm
 * Implementations must be immutable.
 */
interface Individual<T: Individual<T>> {
    /**
     * Performs mutation.
     * @return new mutated individual
     */
    fun mutate(): T

    /**
     * Get fitness of individual.
     * If individual was not fit, result may be undefined
     *
     * @return fitness of individual, if defined
     */
    fun getFitness(): Int

    /**
     * Set fitness of individual
     * @param fitness
     */
    fun setFitness(fitness: Int)
}
