fun main() {
    fun solve(input: List<String>, iterNum: Int): Long {
        var timers = LongArray(9)
        input.first()
            .split(",")
            .map { it.toInt() }
            .groupingBy { it }
            .eachCount()
            .forEach { (t, u) ->
                timers[t] = u.toLong()
            }

        repeat(iterNum) {
            val next = LongArray(9)
            timers
                .flatMapIndexed { i, count ->
                    if (i - 1 == -1) listOf(6 to count, 8 to count)
                    else listOf(i - 1 to count)
                }
                .forEach { next[it.first] += it.second }
            timers = next
            println(timers.contentToString())
        }
        return timers.sum()
    }


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day06_test")
    check(solve(testInput, 80) == 5934L)
    val input = readInput("Day06")
    println(solve(input, 80))

    check(solve(testInput, 256) == 26984457539)
    println(solve(input, 256))
}

