fun main() {
    fun solve(input: List<String>, iterNum: Int): Long {
        var timers = LongArray(9)
        for (fish in input.first().split(",")) {
            timers[fish.toInt()]++
        }

        repeat(iterNum) {
            val next = LongArray(9)
            for ((state, count) in timers.withIndex()) {
                if (state == 0) {
                    next[6] += count
                    next[8] = count
                } else
                    next[state - 1] += count
            }
            timers = next
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

