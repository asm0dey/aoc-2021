fun main() {
    fun tryVelocity(
        initialX: Int,
        initialY: Int,
        targetX: IntRange,
        targetY: IntRange,
        onHit: (maxY: Int) -> Unit,
    ) {
        var velocity = initialX to initialY
        var position = 0 to 0
        var maxY = Int.MIN_VALUE
        while (position.first < targetX.last && position.second > targetY.first) {
            position = (position.first + velocity.first) to (position.second + velocity.second)
            if (position.second > maxY) maxY = position.second
            velocity = Pair(
                if (velocity.first > 0) velocity.first - 1 else if (velocity.first == 0) 0 else velocity.first + 1,
                velocity.second - 1
            )
            if (position.first in targetX && position.second in targetY) {
                onHit(maxY)
                break
            }
        }
    }

    fun parseInput(input: String): Pair<IntRange, IntRange> {
        val split = input.split(",", " ", "=", "..")
        val x = split[3].toInt()..split[4].toInt()
        val y = split[7].toInt()..split[8].toInt()
        return Pair(x, y)
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

