import kotlin.math.max
import kotlin.math.min

private typealias Point = Pair<Int, Int>

private val Point.x
    get() = first
private val Point.y
    get() = second

fun main() {
    operator fun Point.rangeTo(p: Point): Sequence<Point> {
        val ys = (min(y, p.y)..max(y, p.y)).asSequence()
        val xs = (min(x, p.x)..max(x, p.x)).asSequence()
        val ysDown = (max(y, p.y) downTo min(y, p.y)).asSequence()
        return if (x == p.x) ys.map { x to it }
        else if (y == p.y) xs.map { it to y }
        else if (x - p.x == y - p.y) xs.zip(ys).map { (a, b) -> a to b }
        else xs.zip(ysDown).map { (a, b) -> a to b }
    }

    @Suppress("unused")
    fun HashMap<Point, Int>.print() {
        val maxX = keys.maxByOrNull { it.x }!!
        val maxY = keys.maxByOrNull { it.y }!!
        for (y in 0..maxY.y) {
            for (x in 0..maxX.x) {
                print(this[x to y] ?: ".")
            }
            print("\n")
        }
    }

    fun part1(input: List<String>): Int {
        return input
            .asSequence()
            .map { it.split("->").map { it.trim() } }
            .map { (a, b) ->
                a.split(',').map { it.toInt() } to
                        b.split(',').map { it.toInt() }
            }
            .map { (a, b) -> Point(a[0], a[1]) to Point(b[0], b[1]) }
            .filter { (a, b) -> a.x == b.x || a.y == b.y }
            .map { (a, b) -> a..b }
            .flatten()
            .groupingBy { it }
            .eachCount()
            .count { it.value > 1 }
    }


    fun part2(input: List<String>) = input
        .asSequence()
        .map { it.split("->").map { it.trim() } }
        .map { (a, b) ->
            a.split(',').map { it.toInt() } to b.split(',').map { it.toInt() }
        }
        .map { (a, b) -> Point(a[0], a[1])..Point(b[0], b[1]) }
        .flatten()
        .groupingBy { it }
        .eachCount()
        .count { it.value > 1 }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05_test")
    check(part1(testInput) == 5)
    val input = readInput("Day05")
    println(part1(input))
    check(part2(testInput) == 12)
    println(part2(input))
}

