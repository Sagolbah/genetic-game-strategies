package ru.ifmo.genome.gamestrategies.core

interface Environment<T : Individual<T>> {
    /**
     * Calculates fitness of single individual
     */
    fun fit(individual : T) : Double
}