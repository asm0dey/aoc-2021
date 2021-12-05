fun main() {
    data class Point(val x: Int, val y: Int)

    @Suppress("unused")
    fun HashMap<Point, Int>.print() {
        val maxX = keys.maxByOrNull { it.x }!!
        val maxY = keys.maxByOrNull { it.y }!!
        for (y in 0..maxY.y) {
            for (x in 0..maxX.x) {
                print(this[Point(x, y)] ?: ".")
            }
            print("\n")
        }
    }

    fun HashMap<Point, Int>.updatePoint(x: Int, y: Int) {
        val point = Point(x, y)
        if (this[point] == null) this[point] = 0
        this[point] = this[point]!! + 1
    }

    fun readPoints(input: List<String>) = input
        .asSequence()
        .map { it.split("->") }
        .map { (a, b) -> a.trim() to b.trim() }
        .map { (a, b) ->
            a.split(',').map { it.toInt() } to
                    b.split(',').map { it.toInt() }
        }
        .map { (a, b) -> Point(a[0], a[1]) to Point(b[0], b[1]) }

    fun part1(input: List<String>): Int {

        val field = hashMapOf<Point, Int>()

        val lines = readPoints(input)
            .filter { (a, b) -> a.x == b.x || a.y == b.y }
            .toList()

        for ((start, finish) in lines) {
            val minX = minOf(start.x, finish.x)
            val minY = minOf(start.y, finish.y)
            val maxX = maxOf(start.x, finish.x)
            val maxY = maxOf(start.y, finish.y)

            for (x in minX..maxX)
                for (y in minY..maxY)
                    field.updatePoint(x, y)
        }
        return field.values.count { it > 1 }
    }


    fun part2(input: List<String>): Int {

        val field = hashMapOf<Point, Int>()

        val lines = readPoints(input)
            .toList()

        for ((start, finish) in lines) {
            var minX = minOf(start.x, finish.x)
            var minY = minOf(start.y, finish.y)
            val maxX = maxOf(start.x, finish.x)
            var maxY = maxOf(start.y, finish.y)

            if (start.x == finish.x || start.y == finish.y) {
                for (x in minX..maxX)
                    for (y in minY..maxY)
                        field.updatePoint(x, y)
            } else if (start.x - finish.x == start.y - finish.y) {
                while (minX <= maxX) {
                    field.updatePoint(minX, minY)
                    minX++
                    minY++
                }
            } else {
                while (minX <= maxX) {
                    field.updatePoint(minX, maxY)
                    minX++
                    maxY--
                }
            }
        }
        return field.values.count { it > 1 }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05_test")
    check(part1(testInput) == 5)
    val input = readInput("Day05")
    println(part1(input))
    check(part2(testInput) == 12)
    println(part2(input))
}

