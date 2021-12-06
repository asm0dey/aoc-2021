fun main() {
    fun solve(input: List<String>, iterNum: Int): Long {
        val timers = ArrayDeque<Long>(List(9) { 0 })

        for (fish in input.first().split(","))
            timers[fish.toInt()]++

        repeat(iterNum) {
            timers.addLast(timers.removeFirst())
            timers[6] += timers[8]
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

