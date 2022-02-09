package ru.ifmo.genome.gamestrategies.core

interface Individual {
    /**
     * Performs mutation in-place
     */
    fun mutate()

    /**
     * Get fitness of individual.
     * If individual was not fit, result may be
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