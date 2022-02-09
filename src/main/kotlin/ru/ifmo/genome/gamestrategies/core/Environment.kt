package ru.ifmo.genome.gamestrategies.core

interface Environment<T : Individual> {
    /**
     * Calculates fitness of single individual
     */
    fun fit(individual : T) : Int
}