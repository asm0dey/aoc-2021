fun main() {
    fun parseInput(input: List<String>): HashSet<Pair<Int, Int>> = input
        .asSequence()
        .filter { it.matches("\\d+,\\d+".toRegex()) }
        .map { it.split(',').map { num -> num.toInt() } }
        .mapTo(hashSetOf()) { (a, b) -> a to b }

    fun HashSet<Pair<Int, Int>>.fold(str: String) {
        val (direction, amountString) = str.split(' ').last().split('=')
        val amount = amountString.toInt()
        filter {
            if (direction == "x") it.first > amount
            else it.second > amount
        }.forEach {
            remove(it)
            if (direction == "x")
                add((amount * 2 - it.first) to it.second)
            else if (direction == "y")
                add(it.first to (amount * 2 - it.second))
        }
    }

    fun part1(input: List<String>): Int {
        val transparency = parseInput(input)
        transparency.fold(input.first { it.matches(Regex("fold.*")) })
        return transparency.size
    }

    fun part2(input: List<String>) {
        val transparency = parseInput(input)

        input
            .filter { it.matches(Regex("fold.*")) }
            .forEach { transparency.fold(it) }
        val maxX = transparency.maxByOrNull { it.first }!!
        val maxY = transparency.maxByOrNull { it.second }!!
        for (y in 0..maxY.second) {
            for (x in 0..maxX.first)
                print(if (transparency.contains(x to y)) '█' else ' ')
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

