import kotlin.math.abs
import kotlin.system.measureTimeMillis

fun main() {

    data class Point3D(val x: Int, val y: Int, val z: Int) {
        override fun toString(): String {
            return "($x,$y,$z)"
        }

        operator fun get(index: Int): Int {
            return when (index) {
                0 -> x
                1 -> y
                2 -> z
                else -> error("Unsupported arity")
            }
        }

        operator fun minus(other: Point3D) = Point3D(x - other.x, y - other.y, z - other.z)
        operator fun plus(other: Point3D) = Point3D(x + other.x, y + other.y, z + other.z)
    }

    val coordRemaps = listOf(
        listOf(0, 1, 2),
        listOf(0, 2, 1),
        listOf(1, 0, 2),
        listOf(1, 2, 0),
        listOf(2, 0, 1),
        listOf(2, 1, 0)
    )
    val coordNegations = listOf(
        listOf(1, 1, 1),
        listOf(1, 1, -1),
        listOf(1, -1, 1),
        listOf(1, -1, -1),
        listOf(-1, 1, 1),
        listOf(-1, 1, -1),
        listOf(-1, -1, 1),
        listOf(-1, -1, -1),
    )

    fun Point3D.rotate(negation: List<Int>, remap: List<Int>) = Point3D(
        negation[0] * this[remap[0]], negation[1] * this[remap[1]], negation[2] * this[remap[2]],
    )

    val defaultPoint = Point3D(0, 0, 0)

    fun tryAlign(
        firstScan: List<Point3D>,
        secondScan: List<Point3D>,
        distancesFromScan0: ArrayList<Point3D>,
    ): List<Point3D>? {
        val firstScanSet = HashSet(firstScan)
        val allRemapped = Array(secondScan.size) { defaultPoint }
        var pointer: Int
        for (negation in coordNegations) {
            for (remap in coordRemaps) {
                val rotation = secondScan.map { it.rotate(negation, remap) }
                for (a in firstScan) {
                    for (b1 in rotation) {
                        var matches = 0
                        pointer = 0
                        val relativeToA = b1 - a
                        for (b2 in rotation) {
                            val remappedToA = b2 - relativeToA
                            if (firstScanSet.contains(remappedToA)) matches++
                            allRemapped[pointer++] = remappedToA
                        }
                        if (matches >= 12) {
                            println("Match: $relativeToA")
                            distancesFromScan0.add(relativeToA)
                            return allRemapped.take(pointer)
                        }
                    }
                }
            }
        }
        return null
    }

    fun Point3D.distanceTo(b: Point3D) = abs(x - b.x) + abs(y - b.y) + abs(z - b.z)

    fun solve(input: String): Pair<Int, Int> {
        val scans = input.split("\n\n").map { it.lines().drop(1) }
            .map { it.map { it.split(',') }.map { (a, b, c) -> Point3D(a.toInt(), b.toInt(), c.toInt()) } }

        val alignedIndices = hashSetOf(0)
        val aligned = hashMapOf(0 to scans[0])
        val noalign = hashSetOf<Pair<Int, Int>>()
        val distancesFromScan0 = arrayListOf<Point3D>()

        while (alignedIndices.size < scans.size) {
            for (i in scans.indices) {
                if (alignedIndices.contains(i)) continue
                for (j in alignedIndices) {
//                    println("Checking $i against $j")
                    if (noalign.contains(i to j)) continue
                    val remap = tryAlign(aligned[j]!!, scans[i], distancesFromScan0)
                    if (remap != null) {
                        alignedIndices.add(i)
                        aligned[i] = remap
                        break
                    }
                    noalign.add(i to j)
                }
            }
        }
        val p1 = aligned.values.flatten().toSet().size
        val distances = sequence {
            for (a in distancesFromScan0) {
                for (b in distancesFromScan0) {
                    yield(a.distanceTo(b))
                }
            }
        }
        val p2 = distances.maxOf { it }
        return p1 to p2
    }

    // test if implementation meets criteria from the description, like:
    val testInput = asText("Day19_test")
    val input = asText("Day19")
    check(solve(testInput) == 79 to 3621)
    repeat(10) {
        println(measureTimeMillis { println(solve(input)) })
    }
}

