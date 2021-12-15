import java.util.*

fun main() {


    data class Point(val x: Int, val y: Int)
    data class State(val point: Point, val risk: Int)

    fun dijkstra(start: Point, destination: Point, getAdjacent: (Point) -> Sequence<State>): Int {
        val risks = hashMapOf(start to 0)
        val pq = PriorityQueue<State>(compareBy { it.risk })
        pq.add(State(start, 0))

        while (pq.isNotEmpty()) {
            val closest = pq.remove()
            for (adj in getAdjacent(closest.point)) {
                val newRisk = adj.risk + closest.risk
                if (risks.getOrDefault(adj.point, Int.MAX_VALUE) > newRisk) {
                    risks[adj.point] = newRisk
                    pq.add(State(adj.point, newRisk))
                }
            }
        }

        return risks[destination]!!
    }

    fun parseField(input: List<String>) = input
        .indices
        .flatMap { y ->
            input[0].indices.map { x ->
                Point(x, y) to input[y][x].digitToInt()
            }
        }
        .toMap()

    fun MutableMap<Point, Int>.adj(it: Point) =
        sequenceOf(it.copy(x = it.x - 1), it.copy(x = it.x + 1), it.copy(y = it.y + 1), it.copy(y = it.y - 1))
            .filter { contains(it) }
            .map { State(it, this[it]!!) }

    fun part1(input: List<String>): Int {
        val field = parseField(input).toMutableMap()
        return dijkstra(Point(0, 0), Point(field.keys.maxOf { it.x }, field.keys.maxOf { it.y })) {
            field.adj(it)
        }
    }

    fun MutableMap<Point, Int>.modifyForPart2(input: List<String>): MutableMap<Point, Int> {
        for (y in 0 until input.size * 5) {
            for (x in 0 until input[0].length * 5) {
                if (contains(Point(x, y))) continue
                val incrX = x / input[0].length
                val tarX = x % input[0].length
                val incrY = y / input.size
                val tarY = y % input.size
                var cur = this[Point(tarX, tarY)]!! + incrX + incrY
                if (cur % 9 != 0) cur %= 9
                this[Point(x, y)] = cur
            }
        }
        return this
    }


    fun part2(input: List<String>): Int {
        val field = parseField(input).toMutableMap().modifyForPart2(input)
        return dijkstra(Point(0, 0), Point(field.keys.maxOf { it.x }, field.keys.maxOf { it.y })) {
            field.adj(it)
        }

    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day15_test")
    check(part1(testInput) == 40)
    val input = readInput("Day15")
    println(part1(input))

    println(part2(input))

}

