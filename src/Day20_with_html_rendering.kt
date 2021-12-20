import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import kotlinx.html.stream.createHTML

fun main() {

    fun printMap(map: Set<Pair<Int, Int>>) {
        val minX = map.minOf { it.second }
        val maxX = map.maxOf { it.second }
        val minY = map.minOf { it.first }
        val maxY = map.maxOf { it.first }
        val colored=setOf("c")
        System.out.appendHTML(prettyPrint = false)
            .html {
                head {
                    style {
                        unsafe {
                            //language=CSS
                            raw("table, tr, td {border: none;}  table {border-collapse: collapse;}  table > tr > td {height: 1px;width: 1px;}  td.c {color: black;background-color: black;}")
                        }
                    }
                }
                body {
                    table {
                        for (i in minY..maxY) {
                            tr {
                                for (j in minX..maxX) {
                                    td {
                                        if (!map.contains(i to j)) classes = colored
                                    }
                                }
                            }
                        }

                    }
                }
            }
        println()
    }

    fun solve(input: List<String>, iterations: Int): Int {
        val algo = input.first()

        val pic = input.drop(2)
        var lit = LinkedHashSet<Pair<Int, Int>>()
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
            val nextLit = LinkedHashSet<Pair<Int, Int>>()
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

