import kotlin.math.abs

fun main() {
    fun parseInput(input: List<String>): HashSet<Pair<Int, Int>> = input
        .asSequence()
        .filter { it.matches("\\d+,\\d+".toRegex()) }
        .map { it.split(',').map { num -> num.toInt() } }
        .mapTo(hashSetOf()) { (a, b) -> a to b }

    fun processFold(str: String, transparency: HashSet<Pair<Int, Int>>) {
        val (direction, amountString) = str.split(' ').last().split('=')
        val amount = amountString.toInt()
        if (direction == "x") {
            transparency
                .filter { it.first > amount }
                .forEach {
                    transparency.remove(it)
                    transparency.add(it.copy(first = amount - abs(it.first - amount)))
                }
        } else if (direction == "y") {
            transparency
                .filter { it.second > amount }
                .forEach {
                    transparency.remove(it)
                    transparency.add(it.copy(second = amount - abs(it.second - amount)))
                }
        }
    }

    fun part1(input: List<String>): Int {
        val transparency = parseInput(input)
        processFold(input.first { it.matches(Regex("fold.*")) }, transparency)
        return transparency.size
    }

    fun part2(input: List<String>) {
        val transparency = parseInput(input)

        input
            .filter { it.matches(Regex("fold.*")) }
            .forEach { processFold(it, transparency) }
        val maxX = transparency.maxByOrNull { it.first }!!
        val maxy = transparency.maxByOrNull { it.second }!!
        for (y in 0..maxy.second) {
            for (x in 0..maxX.first) {
                print(if (transparency.contains(x to y)) '#' else ' ')
            }
            print('\n')
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day13_test")
    check(part1(testInput) == 17)
    val input = readInput("Day13")
    println(part1(input))
    println()
    part2(testInput)
    println()
    part2(input)
}

