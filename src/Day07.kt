import kotlin.math.abs

fun main() {
    fun part1(input: List<String>): Int {
        val positions = input.first().split(',').map { it.toInt() }
        return (positions.minOf { it }..positions.maxOf { it })
            .asSequence()
            .map { position ->
                positions.sumOf { abs(it - position) }
            }
            .minOf { it }
    }

    fun part2(input: List<String>): Int {
        val positions = input.first().split(',').map { it.toInt() }
        return (positions.minOf { it }..positions.maxOf { it })
            .asSequence()
            .map { position ->
                positions.map { abs(position - it) }.sumOf { (it + 1) * it / 2 }
            }
            .minOf { it }
    }


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day07_test")
    check(part1(testInput) == 37)
    val input = readInput("Day07")
    println(part1(input))
    check(part2(testInput) == 168)
    println(part2(input))
}

