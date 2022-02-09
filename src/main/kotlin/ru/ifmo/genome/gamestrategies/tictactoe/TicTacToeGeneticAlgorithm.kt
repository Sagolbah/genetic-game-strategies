package ru.ifmo.genome.gamestrategies.tictactoe

import ru.ifmo.genome.gamestrategies.core.GeneticAlgorithm
import ru.ifmo.genome.gamestrategies.tictactoe.strategies.GeneticTicTacToeStrategy
import kotlin.random.Random

class TicTacToeGeneticAlgorithm(override val env: TicTacToeEnvironment) :
    GeneticAlgorithm<GeneticTicTacToeStrategy>(env) {
    private val populationSize = 16

    override fun evaluatePopulation() {
        super.evaluatePopulation()
        val sum = currentPopulation.sumOf { x -> x.getFitness() }
        println(
            "Epoch: %d. Total fitness: %d. Best: %d. Avg per individual: %f".format(
                epoch,
                sum,
                currentPopulation.maxOf { i -> i.getFitness() },
                sum / 16.0
            )
        )
    }

    override fun terminateCondition(): Boolean {
        return epoch == 1000
    }

    override fun initPopulation(): List<GeneticTicTacToeStrategy> {
        return List(populationSize) { GeneticTicTacToeStrategy(env) }
    }

    override fun selectParents(): List<GeneticTicTacToeStrategy> {
        var population = currentPopulation.shuffled()
        while (population.size != 2) {
            val winners = mutableListOf<GeneticTicTacToeStrategy>()
            for (i in 0 until population.size / 2) {
                val player1 = population[i]
                val player2 = population[population.size - i - 1]
                val winner = if (player1.getFitness() > player2.getFitness()) player1 else player2
                val loser = if (winner == player1) player2 else player1
                val toAdd = if (Random.nextDouble() < 0.9) winner else loser
                winners.add(toAdd)
            }
            population = winners
        }
        return population
    }

    override fun crossover(): List<GeneticTicTacToeStrategy> {
        val population = currentPopulation.toMutableList()
        while (population.size != 16) {
            population.add(doCrossover(population[0], population[1]))
            population.add(doCrossover(population[1], population[0]))
        }
        return population
    }

    private fun doCrossover(
        primary: GeneticTicTacToeStrategy,
        secondary: GeneticTicTacToeStrategy
    ): GeneticTicTacToeStrategy {
        val newIndividual = GeneticTicTacToeStrategy(env)
        val secondaryGene = Random.nextInt(newIndividual.getActions().size)
        for (i in newIndividual.getActions().indices) {
            newIndividual.setAction(i, primary.Action(primary.getActions()[i]))
        }
        newIndividual.setAction(secondaryGene, secondary.Action(secondary.getActions()[secondaryGene]))
        newIndividual.mutate()
        return newIndividual
    }
}