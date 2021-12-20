fun main() {

    fun printMap(map: Set<Pair<Int, Int>>) {
        for (i in map.minOf { it.first }..map.maxOf { it.first }) {
            for (j in map.minOf { it.second }..map.maxOf { it.second }) {
                print(if (map.contains(i to j)) '#' else '.')
            }
            println()
        }
    }

    fun solve(input: List<String>, iterations: Int): Int {
        val algo = input.first()

        val pic = input.drop(2)
        var lit = LinkedHashSet<Pair<Int, Int>>(pic.size * pic.size)
        for (i in pic.indices) {
            for (j in pic[0].indices) {
                if (pic[i][j] == '#') lit.add(i to j)
            }
        }
        var minY = lit.minOf { it.first }
        var maxY = lit.maxOf { it.first }
        var minX = lit.minOf { it.second }
        var maxX = lit.maxOf { it.second }

        val blink = algo[0] == '#' && algo[511] == '.'
        val binaryBuilder = StringBuilder(9)
        repeat(iterations) {
            val infinityColor = (if (blink) it % 2 else 0).digitToChar()
            val nextLit = LinkedHashSet<Pair<Int, Int>>(lit.size)
            for (y in (minY - 1)..(maxY + 1)) {
                for (x in (minX - 1)..(maxX + 1)) {
                    for (a in y - 1..y + 1) {
                        for (b in x - 1..x + 1) {
                            binaryBuilder.append(
                                if (a < minY || a > maxY || b < minX || b > maxX) infinityColor
                                else if (lit.contains(a to b)) '1'
                                else '0'
                            )
                        }
                    }
                    val binString = binaryBuilder.toString()
                    binaryBuilder.clear()
                    if (algo[binString.toInt(2)] == '#') {
                        nextLit.add(y to x)
                    }
                }
            }
            lit = nextLit
            minY--
            minX--
            maxY++
            maxX++
        }
        printMap(lit)
        return lit.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day20_test")
    val input = readInput("Day20")
    check(solve(testInput, 2) == 35)
    println(solve(input, 2))
    check(solve(testInput, 50) == 3351)
    println(solve(input, 50))
}

