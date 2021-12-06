fun main() {
    fun solve(input: List<String>, iterNum: Int): Long {
        var timers = input.first()
                .split(",")
                .map { it.toInt() }
                .groupingBy { it }
                .eachCount()
                .mapValues { it.value.toLong() }
        repeat(iterNum) {
            timers = timers.flatMap { (i, count) ->
                if (i - 1 == -1)
                    listOf(6 to count, 8 to count)
                else
                    listOf(i - 1 to count)
            }
                .groupBy { it.first }
                .mapValues { it.value.map { it.second }.sum() }
        }
        return timers.values.sum()
    }


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day06_test")
    check(solve(testInput, 80) == 5934L)
    val input = readInput("Day06")
    println(solve(input, 80))

    check(solve(testInput, 256) == 26984457539)
    println(solve(input, 256))
}

