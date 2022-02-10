package ru.ifmo.genome.gamestrategies.tictactoe.strategies

import ru.ifmo.genome.gamestrategies.core.Individual
import ru.ifmo.genome.gamestrategies.tictactoe.FieldStatus
import ru.ifmo.genome.gamestrategies.tictactoe.TicTacToeEnvironment
import kotlin.random.Random

class GeneticTicTacToeStrategy(override val env: TicTacToeEnvironment) : TicTacToeStrategy(env), Individual<GeneticTicTacToeStrategy> {
    private val actions: Array<Action> = Array(9) { Action() }
    private val mutationRate = 0.02
    private var fitness = 0

    companion object {
        val fieldStatuses = FieldStatus.values()
    }

    override fun makeTurn(): Pair<Int, Int> {
        for (action in actions) {
            if (action.matches()) {
                return Pair(action.getActionColumn(), action.getActionRow())
            }
        }
        return randomMove()
    }

    override fun mutate(): GeneticTicTacToeStrategy {
        val newStrategy = GeneticTicTacToeStrategy(env)
        for (i in actions.indices) {
            newStrategy.setAction(i, actions[i].doMutation())
        }
        return newStrategy
    }

    override fun getFitness(): Int {
        return fitness
    }

    override fun setFitness(fitness: Int) {
        this.fitness = fitness
    }

    fun getActions(): Array<Action> {
        return actions
    }

    fun setAction(idx: Int, action: Action) {
        actions[idx] = action
    }

    private fun randomMove(): Pair<Int, Int> {
        var coords = Pair(Random.nextInt(3), Random.nextInt(3))
        while (env.getField()[coords.first][coords.second] != FieldStatus.FREE) {
            coords = Pair(Random.nextInt(3), Random.nextInt(3))
        }
        return coords
    }

    inner class Action {
        // Condition check: whether target cell has given status
        val actionParameters: IntArray
        val fieldStatus: FieldStatus

        fun matches(): Boolean {
            return env.getField()[getColumn()][getRow()] == fieldStatus
                    && env.getField()[getActionColumn()][getActionRow()] == FieldStatus.FREE
        }

        constructor() {
            actionParameters = IntArray(4) { Random.nextInt(3) }
            fieldStatus = fieldStatuses[Random.nextInt(fieldStatuses.size)]
        }

        constructor(actionParameters: IntArray, fieldStatus: FieldStatus) {
            this.actionParameters = actionParameters
            this.fieldStatus = fieldStatus
        }

        fun getColumn(): Int {
            return actionParameters[0]
        }

        fun getRow(): Int {
            return actionParameters[1]
        }

        fun getActionColumn(): Int {
            return actionParameters[2]
        }

        fun getActionRow(): Int {
            return actionParameters[3]
        }

        fun doMutation(): Action {
            val newParams = actionParameters.copyOf()
            for (i in 0..3) {
                if (Random.nextDouble() < mutationRate) {
                    newParams[i] = Random.nextInt(3)
                }
            }
            val newFieldStatus: FieldStatus = if (Random.nextDouble() < mutationRate) {
                fieldStatuses[Random.nextInt(fieldStatuses.size)]
            } else {
                fieldStatus
            }
            return Action(newParams, newFieldStatus)
        }
    }


}