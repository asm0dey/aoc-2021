fun main() {
    fun part1(input: List<String>): Int {
        val size = input[0].length
        val freq = StringBuilder()
        val rare = StringBuilder()
        for (i in 0 until size) {
            val zeros = input.count { it[i] == '0' }
            val ones = input.size - zeros
            freq.append(if (zeros > ones) '0' else '1')
            rare.append(if (zeros <= ones) '0' else '1')
        }
        return freq.toString().toInt(2) * rare.toString().toInt(2)
    }

    fun part2(input: List<String>): Int {
        val size = input[0].length

        fun findOnly(mostFrequent: Boolean): Int {
            val copy = input.toMutableList()
            for (i in 0 until size) {
                val zeros = copy.count { it[i] == '0' }
                val ones = copy.size - zeros
                val toKeep =
                    if (zeros <= ones) if (mostFrequent) '1' else '0'
                    else if (mostFrequent) '0' else '1'
                copy.removeIf { it[i] != toKeep }
                if (copy.size == 1) return copy.first().toInt(2)
            }
            error("Didn't find the only element")
        }

        return findOnly(true) * findOnly(false)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 198)

    val input = readInput("Day03")
    println(part1(input))

    check(part2(testInput) == 230)
    println(part2(input))
}

