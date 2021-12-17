import kotlin.math.sign

fun main() {
    fun tryVelocity(
        initialX: Int,
        initialY: Int,
        targetX: IntRange,
        targetY: IntRange,
        onHit: (maxY: Int) -> Unit,
    ) {
        var x = 0
        var y = 0
        var vx = initialX
        var vy = initialY
        var maxY = Int.MIN_VALUE
        while (x < targetX.last && y > targetY.first) {
            x += vx
            y += vy
            if (y > maxY) maxY = y
            vx -= vx.sign
            vy -= 1
            if (x in targetX && y in targetY) {
                onHit(maxY)
                break
            }
        }
    }

    fun parseInput(input: String): Pair<IntRange, IntRange> {
        val split = "-?\\d+".toRegex().findAll(input).map { it.value.toInt() }.toList()
        val x = split[0]..split[1]
        val y = split[2]..split[3]
        return x to y
    }

    fun part1(input: String): Int {
        val (targetX, targetY) = parseInput(input)
        var maxY = Int.MIN_VALUE
        for (i in 1..targetX.last) {
            for (j in (targetY.first * 2)..-targetY.first) {
                tryVelocity(i, j, targetX, targetY) { if (it > maxY) maxY = it }
            }
        }
        return maxY
    }


    fun part2(input: String): Int {
        val (x, y) = parseInput(input)
        var counter = 0
        for (i in 1..x.last) {
            for (j in (y.first * 2)..-y.first) {
                tryVelocity(i, j, x, y) { counter++ }
            }
        }
        return counter
    }

    // test if implementation meets criteria from the description, like:
    val testInput = "target area: x=20..30, y=-10..-5"
    val input = "target area: x=57..116, y=-198..-148"
    check(part1(testInput) == 45)
    println(part1(input))
    check(part2(testInput) == 112)
    println(part2(input))
}

