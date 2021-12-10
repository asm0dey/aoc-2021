fun main() {
    fun part1(input: List<String>): Int {
        val map = input.map { it.toList().map { it.digitToInt() } }
        var counter = 0
        for (i in map.indices) {
            for (j in map[0].indices) {
                val point = map[i][j]
                val count = (i - 1..i + 1).flatMap { a ->
                    (j - 1..j + 1).map { b ->
                        a to b
                    }
                }
                    .filterNot { (a, b) -> a < 0 || b < 0 || a == map.size || b == map[0].size || (a == i && b == j) }
                    .count { (a, b) -> map[a][b] < point }
                if (count == 0) {
                    println("Adding $point at coords $i:$j")
                    counter += (point + 1)
                }
            }
        }
        println("Final counter is $counter")
        return counter
    }

    fun part2(input: List<String>): Int {
        val map = input.map { it.toList().map { it.digitToInt() } }

        val lower = map.indices.flatMap { i ->
            map[0].indices.mapNotNull { j ->
                val point = map[i][j]
                val count = (i - 1..i + 1).flatMap { a ->
                    (j - 1..j + 1).map { b ->
                        a to b
                    }
                }
                    .filterNot { (a, b) -> a < 0 || b < 0 || a == map.size || b == map[0].size || (a == i && b == j) }
                    .count { (a, b) -> map[a][b] < point }
                if (count == 0) i to j
                else null
            }
        }

        fun findBasin(coord: Pair<Int, Int>, visited: MutableSet<Pair<Int, Int>> = hashSetOf()): Set<Pair<Int, Int>> {
            val toVisit = sequenceOf(coord.first - 1 to coord.second,
                coord.first + 1 to coord.second,
                coord.first to coord.second - 1,
                coord.first to coord.second + 1)
                .filterNot { (i, j) -> i < 0 || j < 0 || i == map.size || j == map[0].size || visited.contains(i to j) }
                .filterNot { (i, j) -> map[i][j] == 9 }
                .toList()
            visited.addAll(toVisit)
            for (point in toVisit) {
                findBasin(point, visited)
            }
            return visited
        }

        return lower.map {
            findBasin(it).size
        }
            .sorted()
            .takeLast(3)
            .reduce { a, b -> a * b }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day09_test")
    check(part1(testInput) == 15)

    val input = readInput("Day09")
    println(part1(input))

    check(part2(testInput) == 1134)
    println(part2(input))
}


