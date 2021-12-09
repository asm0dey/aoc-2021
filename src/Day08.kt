fun main() {
    fun part1(input: List<String>): Int {
        return input.map { it.split('|') }.map { it[1] }.flatMap { it.split(' ') }
            .count { it.length == 2 || it.length == 4 || it.length == 3 || it.length == 7 }
    }

    fun part2(input: List<String>): Long {
        return input.map { it.split(" | ", " ") }.map { lst ->
            val set = lst.take(10).toSet()
            val one = set.find { it.length == 2 }!!.toSet()
            val seven = set.find { it.length == 3 }!!.toSet()
            val four = set.find { it.length == 4 }!!.toSet()
            val eight = set.find { it.length == 7 }!!.toSet()
            val three = set
                .filter { it.length == 5 }
                .single { it.toSet().intersect(one).size == 2 }
                .toSet()
            val five = set
                .filter { it.length == 5 }
                .filter { it.toSet() != three }
                .single { it.toSet().intersect(four.toSet()).size == 3 }
                .toSet()
            val two = set
                .filter { it.length == 5 }
                .single { it.toSet() != five && it.toSet() != three }
                .toSet()
            val six = set
                .filter { it.length == 6 }
                .single { it.toSet().intersect(one).size == 1 }
                .toSet()
            val zero = set.filter { it.length == 6 }
                .single {
                    it.toSet().intersect(one).size == 2 &&
                            it.toSet().intersect(six).size == 5 &&
                            it.toSet().intersect(three).size == 4
                }
                .toSet()
            val nine = set.filter { it.length == 6 }.single { it.toSet() != zero && it.toSet() != six }.toSet()
            val allDigits = mapOf(zero to 0,
                one to 1,
                two to 2,
                three to 3,
                four to 4,
                five to 5,
                six to 6,
                seven to 7,
                eight to 8,
                nine to 9)
            lst.takeLast(4).map { it.toSet() }.joinToString("") { allDigits[it]!!.toString() }.toLong()
                .also { println(it) }
        }
            .sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day08_test")
    check(part1(testInput) == 26)

    val input = readInput("Day08")
    println(part1(input))

    check(part2(testInput) == 61229L) { "Was ${part2(testInput)}" }
    println(part2(input))
}

