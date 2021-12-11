@file:OptIn(ExperimentalTime::class)

import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

fun main() {
    fun Array<IntArray>.increaseAll() {
        for (i in indices) {
            for (j in this[0].indices) {
                this[i][j]++
            }
        }
    }

    fun increaseNeighbors(i: Int, j: Int, data: Array<IntArray>) {
        for (a in i - 1..i + 1) {
            for (b in j - 1..j + 1) {
                if (a < 0 || b < 0 || a == data.size || b == data[0].size || (a == i && b == j))
                    continue
                data[a][b]++
            }
        }
    }

    fun Array<IntArray>.decreaseFlashed() {
        for (i in indices) {
            for (j in this[0].indices) {
                if (this[i][j] > 9) this[i][j] = 0
            }
        }
    }

    fun findFlashers(data: Array<IntArray>, alreadyFlashed: Set<Pair<Int, Int>>) = sequence {
        for (i in data.indices)
            for (j in data[0].indices)
                if (data[i][j] > 9 && !alreadyFlashed.contains(i to j))
                    yield(i to j)
    }

    fun part1(input: List<String>): Int {
        val field = input.map { it.map { it.digitToInt() }.toIntArray() }.toTypedArray()
        var globalFlashCount = 0
        repeat(100) {
            field.increaseAll()
            val flashed = hashSetOf<Pair<Int, Int>>()
            var nextToFlash = findFlashers(field, flashed)
            while (nextToFlash.any()) {
                for ((i, j) in nextToFlash) {
                    globalFlashCount++
                    flashed.add(i to j)
                    increaseNeighbors(i, j, field)
                }
                nextToFlash = findFlashers(field, flashed)
            }
            field.decreaseFlashed()
        }
        return globalFlashCount
    }

    fun part2(input: List<String>): Int {
        val field = input.map { it.map { it.digitToInt() }.toIntArray() }.toTypedArray()
        var counter = 0
        while (true) {
            counter++
            field.increaseAll()
            val flashed = hashSetOf<Pair<Int, Int>>()
            var nextToFlash = findFlashers(field, flashed) //sorry for bad pun
            while (nextToFlash.any()) {
                for ((i, j) in nextToFlash) {
                    flashed.add(i to j)
                    increaseNeighbors(i, j, field)
                }
                nextToFlash = findFlashers(field, flashed)
            }
            field.decreaseFlashed()
            if (field.all { it.all { it == 0 } }) break
        }
        return counter
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day11_test")
    check(part1(testInput) == 1656) { "Got ${part1(testInput)}" }

    val input = readInput("Day11")

    measureTimedValue { part1(input) }.run {
        println("Calculating result of part 1 took $duration and the result was $value")
    }

    measureTimedValue { part2(input) }.run {
        println("Calculating result of part 2 took $duration and the result was $value")
    }

}

