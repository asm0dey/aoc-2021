import java.io.File

fun main() {
    fun part1(input: List<String>): Long {
        val rules = mapOf('[' to ']', '(' to ')', '<' to '>', '{' to '}')
        val scores = mapOf(')' to 3L, ']' to 57L, '}' to 1197L, '>' to 25137L)
        var result = 0L
        for (s in input) {
            val data = ArrayDeque<Char>()
            for (char in s) {
                if (rules.keys.contains(char)) {
                    data.addLast(char)
                } else if (rules[data.last()]!! == char)
                    data.removeLast()
                else {
                    result += scores[char]!!
                    break
                }
            }
        }
        return result
    }

    fun part2(input: List<String>): Long {
        val rules = mapOf('[' to ']', '(' to ')', '<' to '>', '{' to '}')
        val scores = mapOf(')' to 1L, ']' to 2L, '}' to 3L, '>' to 4L)
        val result = input
            .mapNotNull { s ->
                val data = ArrayDeque<Char>()
                for (c in s) {
                    if (rules.keys.contains(c)) {
                        data.addLast(c)
                    } else if (rules[data.last()]!! == c)
                        data.removeLast()
                    else return@mapNotNull null
                }
                data.foldRight(0L) { char, acc ->
                    acc * 5 + scores[rules[char]]!!
                }
            }
            .sorted()
        return result[result.size / 2]
    }

    // test if implementation meets criteria from the description, like:
    val testInput = File("src", "${"Day10_test"}.txt").readLines()
    check(part1(testInput) == 26397L)

    val input = File("src", "${"Day10"}.txt").readLines()
    println(part1(input))

    check(part2(testInput) == 288957L)
    println(part2(input))
}

