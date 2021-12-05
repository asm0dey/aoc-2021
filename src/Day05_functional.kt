import kotlin.math.max
import kotlin.math.min

private typealias Point = Pair<Int, Int>
private typealias Line = Sequence<Point>

fun main() {
    fun Point.getX() = first
    fun Point.getY() = second

    operator fun Point.rangeTo(p: Point): Line {
        val ys = (min(getY(), p.getY())..max(getY(), p.getY())).asSequence()
        val xs = (min(getX(), p.getX())..max(getX(), p.getX())).asSequence()
        val ysDown = (max(getY(), p.getY()) downTo min(getY(), p.getY())).asSequence()
        return if (getX() == p.getX()) ys.map { getX() to it }
        else if (getY() == p.getY()) xs.map { it to getY() }
        else if (getX() - p.getX() == getY() - p.getY()) xs.zip(ys)
        else xs.zip(ysDown)
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
            .filter { (a, b) -> a.getX() == b.getX() || a.getY() == b.getY() }
            .flatMap { (a, b) -> a..b }
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
        .flatMap { (a, b) -> Point(a[0], a[1])..Point(b[0], b[1]) }
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

