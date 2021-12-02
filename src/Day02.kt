fun main() {
    fun part1(input: List<String>): Int {
        var horizontalPosition = 0
        var depth = 0
        val instructions = input.map { it.split(' ') }
        for ((direction, amountString) in instructions) {
            val amount = amountString.toInt()
            when (direction) {
                "forward" -> horizontalPosition += amount
                "up" -> depth -= amount
                "down" -> depth += amount
            }
        }
        return horizontalPosition * depth
    }

    fun part2(input: List<String>): Int {
        var horizontalPosition = 0
        var depth = 0
        var aim = 0
        val instructions = input.map { it.split(' ') }
        for ((direction, amountString) in instructions) {
            val amount = amountString.toInt()
            when (direction) {
                "forward" -> {
                    horizontalPosition += amount
                    depth += (aim * amount)
                }
                "up" -> aim -= amount
                "down" -> aim += amount
            }
        }
        return horizontalPosition * depth

    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 150)

    val input = readInput("Day02")
    println(part1(input))

    check(part2(testInput) == 900)
    println(part2(input))
}

