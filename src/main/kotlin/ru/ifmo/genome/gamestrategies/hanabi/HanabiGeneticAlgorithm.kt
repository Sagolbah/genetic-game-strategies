package ru.ifmo.genome.gamestrategies.hanabi

import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import ru.ifmo.genome.gamestrategies.core.GeneticAlgorithm
import ru.ifmo.genome.gamestrategies.hanabi.strategies.GeneticHanabiStrategy
import ru.ifmo.genome.gamestrategies.hanabi.strategies.randomAction
import kotlin.random.Random
import kotlinx.serialization.encodeToString as jsonEncode


class HanabiGeneticAlgorithm(
    override val env: HanabiEnvironment,
    private val epochs: Int,
    private val strategySize: Int = 7,
    private val populationSize: Int = 40,
    private val tournamentSize: Int = 3,
    private val elitismCount: Int = 4,
    private val forceNewChildren: Boolean = true  // If true, all parents except top $elitismCount are eliminated.
) : GeneticAlgorithm<GeneticHanabiStrategy>(env) {

    override fun terminateCondition(): Boolean {
        return epoch == epochs
    }

    override fun initPopulation(): List<GeneticHanabiStrategy> {
        return List(populationSize) {
            GeneticHanabiStrategy(List(strategySize) { randomAction() })
        }
    }

    override suspend fun evaluatePopulation(population: List<GeneticHanabiStrategy>) {
        coroutineScope {
            population.map { x ->
                async {
                    if (x.getFitness() == -1.0) {  // Use cached values
                        x.setFitness(env.fit(x))
                    }
                }
            }.awaitAll()
        }
    }

    override fun selectParents(): List<GeneticHanabiStrategy> {
        return List(populationSize) { getParent() }
    }

    override fun crossover(parents: List<GeneticHanabiStrategy>): List<GeneticHanabiStrategy> {
        val children = mutableListOf<GeneticHanabiStrategy>()
        for (i in parents.indices step 2) {
            val crossResult = orderedCrossover(parents[i], parents[i + 1])
            children.add(crossResult.first)
            children.add(crossResult.second)
        }
        return children
    }

    override fun selectSurvivors(
        oldPopulation: List<GeneticHanabiStrategy>,
        children: List<GeneticHanabiStrategy>
    ): List<GeneticHanabiStrategy> {
        val newPopulation = mutableSetOf<GeneticHanabiStrategy>()
        val elites = oldPopulation.take(elitismCount)
        newPopulation.addAll(elites)
        if (forceNewChildren) {
            val bestChildren = children.sortedByDescending { x -> x.getFitness() }.take(populationSize - elitismCount)
            newPopulation.addAll(bestChildren)
            return newPopulation.toList()
        }
        val remainingOld = oldPopulation.takeLast(oldPopulation.size - elitismCount)
        newPopulation.addAll(remainingOld)
        newPopulation.addAll(children)
        return newPopulation.sortedByDescending { x -> x.getFitness() }.take(populationSize)
    }

    override fun onEpochBeginning() {
        currentPopulation = currentPopulation.sortedByDescending { x -> x.getFitness() }
        println(
            "Epoch %d, best fitness: %.3f, avg across elites: %.5f, population size: %d".format(
                epoch,
                currentPopulation[0].getFitness(),
                currentPopulation.take(elitismCount).map { x -> x.getFitness() }.average(),
                currentPopulation.size
            )
        )
        println("Current best individual: " + Json.jsonEncode(currentPopulation[0].getStrategy()))
        println(currentPopulation.map { x -> x.getFitness() }.toString())
    }

    /**
     * Tournament selection of parents
     *
     * @return selected parent to participate in crossover
     */
    private fun getParent(): GeneticHanabiStrategy {
        return currentPopulation.shuffled().take(tournamentSize).maxByOrNull { x -> x.getFitness() }!!
    }


    private fun orderedCrossover(
        parent1: GeneticHanabiStrategy,
        parent2: GeneticHanabiStrategy
    ): Pair<GeneticHanabiStrategy, GeneticHanabiStrategy> {
        var idx1 = Random.nextInt(strategySize)
        var idx2 = Random.nextInt(strategySize)
        if (idx1 > idx2) {
            val tmp = idx1
            idx1 = idx2
            idx2 = tmp
        }
        val child1 = parent1.getStrategy().toMutableList()
        val child2 = parent2.getStrategy().toMutableList()
        for (i in idx1..idx2) {
            val tmp = child1[i]
            child1[i] = child2[i]
            child2[i] = tmp
        }
        return Pair(GeneticHanabiStrategy(child1), GeneticHanabiStrategy(child2))
    }

}