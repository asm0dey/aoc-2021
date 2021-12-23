import java.io.File
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun main() {
    fun part1(input: List<String>): Int {
        val set = hashSetOf<Triple<Int, Int, Int>>()
        input.map { it.split("..", "=", ",", " ") }
            .forEach { command ->
                val state = command[0]

                val xs = command[2].toInt()..command[3].toInt()
                if (xs.last < -50 || xs.first > 50) return@forEach
                val ys = command[5].toInt()..command[6].toInt()
                if (ys.last < -50 || ys.first > 50) return@forEach
                val zs = command[8].toInt()..command[9].toInt()
                if (zs.last < -50 || zs.first > 50) return@forEach
                for (x in xs) {
                    for (y in ys) {
                        for (z in zs) {
                            if (x in -50..50 && y in -50..50 && z in -50..50) {
                                val point = Triple(x, y, z)
                                if (state == "on") set.add(point)
                                else set.remove(point)
                            }
                        }
                    }
                }
            }
        return set.size
    }

    data class Cuboid(val state: Boolean, val x: IntRange, val y: IntRange, val z: IntRange) {
        val size
            get() =
                (if (state) 1L else -1L) *
                        abs(x.last - x.first + 1).toLong() *
                        abs(y.last - y.first + 1) *
                        abs(z.last - z.first + 1)
    }

    fun Cuboid.intersect(
        next: Cuboid,
    ): Cuboid? {
        val minX = max(next.x.first, x.first)
        val maxX = min(next.x.last, x.last)
        val maxY = min(next.y.last, y.last)
        val minY = max(next.y.first, y.first)
        val maxZ = min(next.z.last, z.last)
        val minZ = max(next.z.first, z.first)
        val xIntersection = max(maxX - minX + 1, 0)
        val yIntersection = max(maxY - minY + 1, 0)
        val zIntersection = max(maxZ - minZ + 1, 0)
        return if (xIntersection == 0 || yIntersection == 0 || zIntersection == 0) null
        else Cuboid(!state, minX..maxX, minY..maxY, minZ..maxZ)
    }

    fun part2(input: List<String>): Long {
        var maxDepth = 0
        val x = input.map { it.split("..", "=", ",", " ") }
            .asSequence()
            .map { command ->
                val xs = command[2].toInt()..command[3].toInt()
                val ys = command[5].toInt()..command[6].toInt()
                val zs = command[8].toInt()..command[9].toInt()
                Cuboid(command[0] == "on", xs, ys, zs)
            }
            .fold(listOf<Cuboid>()) { currentCuboids, incomingCuboid ->
                buildList {
                    for (currentCuboid in currentCuboids) {
                        add(currentCuboid)
                        val intersection = currentCuboid.intersect(incomingCuboid) ?: continue
                        add(intersection)
                    }
                    if (incomingCuboid.state) add(incomingCuboid)
                }
            }
        println(x)
        return x
            .sumOf {
                it.size
            }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = File("src", "${"Day22_test"}.txt").readLines()
    check(part1(testInput) == 590784)
    val input = File("src", "${"Day22"}.txt").readLines()
    println(part1(input))
    println(part2(File("src", "${"Day22_test3"}.txt").readLines()))
//    println(part2(File("src", "${"Day22_test2"}.txt").readLines()))
//    println(part2(input))
}

