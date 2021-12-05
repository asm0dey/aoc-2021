import kotlin.math.max
import kotlin.math.min

private typealias Line = Sequence<Point>

@JvmInline
value class Point(private val p: Pair<Int, Int>) {
    constructor(x: Int, y: Int) : this(x to y)

    val x get() = p.first
    val y get() = p.second
}

fun main() {

    operator fun Point.rangeTo(p: Point): Line {
        val ys = (min(y, p.y)..max(y, p.y)).asSequence()
        val xs = (min(x, p.x)..max(x, p.x)).asSequence()
        val ysDown = (max(y, p.y) downTo min(y, p.y)).asSequence()
        return (if (x == p.x) ys.map { x to it }
        else if (y == p.y) xs.map { it to y }
        else if (x - p.x == y - p.y) xs.zip(ys)
        else xs.zip(ysDown)).map { Point(it) }
    }

    fun part1(input: List<String>) = input
        .asSequence()
        .map { it.split(" -> ", ",").map { it.toInt() } }
        .filter { (x0, y0, x1, y1) -> x0 == x1 || y0 == y1 }
        .flatMap { (x0, y0, x1, y1) -> Point(x0, y0)..Point(x1, y1) }
        .groupingBy { it }
        .eachCount()
        .count { it.value > 1 }


    fun part2(input: List<String>) = input
        .asSequence()
        .map { it.split(" -> ", ",").map { it.toInt() } }
        .flatMap { (x0, y0, x1, y1) -> Point(x0, y0)..Point(x1, y1) }
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

