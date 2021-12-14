fun main() {
    fun parseInput(input: List<String>): Pair<String, Map<String, String>> {
        val source = input.first()
        val replacements = input
            .filter { it.contains("->") }
            .map { it.split(" -> ") }
            .associate { (a, b) -> a to b }
        return Pair(source, replacements)
    }

    fun part1(input: List<String>): Int {
        val (source, replacements) = parseInput(input)
        val result = (0..9).fold(source) { it, _ ->
            it
                //  ABC
                //      AXB BYC
                //          AX BY C
                .windowed(2, partialWindows = true) {
                    if (it.length == 2) {
                        if (replacements.contains(it.toString())) "${it[0]}${replacements[it.toString()]}"
                        else it.toString()
                    } else it
                }
                .joinToString("")
        }
        val freqs = result.groupingBy { it }.eachCount().values
        val max = freqs.maxOf { it }
        val min = freqs.minOf { it }
        return max - min
    }

    fun countPairs(source: String): Map<String, Long> {
        return source
            .windowed(2) { it.toString() }
            .groupingBy { it }
            .eachCount()
            .mapValues { it.value.toLong() }
    }

    fun part2(input: List<String>, iterations: Int): Long {
        val (source, replacements) = parseInput(input)
        val final = (0 until iterations).fold(countPairs(source)) { map, _ ->
            val next = mutableMapOf<String, Long>()
            for ((pair, count) in map) {
                if (replacements.contains(pair)) {
                    val newChar = replacements[pair]!!
                    val fst = pair[0] + newChar
                    val snd = newChar + pair[1]
                    for (newPair in listOf(fst, snd)) {
                        next.compute(newPair) { _, cur ->
                            if (cur == null) count
                            else cur + count
                        }
                    }
                }
            }
            next
        }
        val rawFreqs = hashMapOf<Char, Long>()
        for ((pair, count) in final) {
            for (char in pair) {
                rawFreqs.compute(char) { _, cur ->
                    if (cur == null) count
                    else cur + count
                }
            }
        }
        val freqs = rawFreqs.mapValues { (char, count) ->
            //  AN
            //    AX XN    (AXN)
            //        AA AX XB BX XN NN (AAXBXNN)
            if (char == source.first() || char == source.last()) (count + 1) / 2
            else count / 2
        }
        val max = freqs.maxOf { it.value }
        val min = freqs.minOf { it.value }
        return max - min
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day14_test")
    check(part1(testInput) == 1588)
    println(part1(testInput))
    println(part2(testInput, 10))

    val input = readInput("Day14")
    println(part1(input))
    println(part2(input, 10))

    println(part2(input, 40))
}

