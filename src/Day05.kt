private data class Point(val x: Int, val y: Int)

@Suppress("unused")
private fun printField(field: HashMap<Point, Int>) {
    val maxX = field.keys.maxByOrNull { it.x }!!
    val maxY = field.keys.maxByOrNull { it.y }!!
    for (y in 0..maxY.y) {
        for (x in 0..maxX.x) {
            print(field[Point(x, y)] ?: ".")
        }
        print("\n")
    }
}

fun main() {

    fun part1(input: List<String>): Int {

        val field = hashMapOf<Point, Int>()

        val lines = input
            .asSequence()
            .map { it.split("->") }
            .map { (a, b) -> a.trim() to b.trim() }
            .map { (a, b) ->
                a.split(',').map { it.toInt() } to
                        b.split(',').map { it.toInt() }
            }
            .map { (a, b) -> Point(a[0], a[1]) to Point(b[0], b[1]) }.filter { (a, b) -> a.x == b.x || a.y == b.y }
            .toList()

        for ((start, finish) in lines) {
            if (start.x == finish.x) {
                for (i in minOf(start.y, finish.y)..maxOf(start.y, finish.y)) {
                    field.compute(Point(start.x, i)) { _, current ->
                        if (current == null) 1 else current + 1
                    }
                }
            } else if (start.y == finish.y) {
                for (i in minOf(start.x, finish.x)..maxOf(start.x, finish.x)) {
                    field.compute(Point(i, start.y)) { _, current ->
                        if (current == null) 1 else current + 1
                    }
                }
            }
        }
        return field.values.count { it > 1 }
    }


    fun part2(input: List<String>): Int {

        val field = hashMapOf<Point, Int>()

        val lines = input
            .asSequence()
            .map { it.split("->") }
            .map { (a, b) -> a.trim() to b.trim() }
            .map { (a, b) ->
                a.split(',').map { it.toInt() } to
                        b.split(',').map { it.toInt() }
            }
            .map { (a, b) -> Point(a[0], a[1]) to Point(b[0], b[1]) }
            .toList()

        for ((start, finish) in lines) {
            if (start.x == finish.x) {
                for (i in minOf(start.y, finish.y)..maxOf(start.y, finish.y)) {
                    field.compute(Point(start.x, i)) { _, current ->
                        if (current == null) 1 else current + 1
                    }
                }
            } else if (start.y == finish.y) {
                for (i in minOf(start.x, finish.x)..maxOf(start.x, finish.x)) {
                    field.compute(Point(i, start.y)) { _, current ->
                        if (current == null) 1 else current + 1
                    }
                }
            } else {
                var minX = minOf(start.x, finish.x)
                var minY = minOf(start.y, finish.y)
                val maxX = maxOf(start.x, finish.x)
                var maxY = maxOf(start.y, finish.y)
                if ((start.x < finish.x && start.y < finish.y) || (start.x > finish.x && start.y > finish.y)) {
                    while (minX <= maxX) {
                        field.compute(Point(minX, minY)) { _, current ->
                            if (minX == 0 && minY == 0) println(start to finish)
                            if (current == null) 1 else current + 1
                        }
                        minX++
                        minY++
                    }
                } else {
                    while (minX <= maxX) {
                        field.compute(Point(minX, maxY)) { _, current ->
                            if (minX == 0 && maxY == 0) println(start to finish)
                            if (current == null) 1 else current + 1
                        }
                        minX++
                        maxY--
                    }
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

