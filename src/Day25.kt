fun main() {
    fun part1(input: List<String>): Long {
        val horizontalSize = input[0].length
        val verticalSize = input.size
        var map = hashMapOf<Pair<Int, Int>, Boolean>()
        input.forEachIndexed { y, s ->
            s.forEachIndexed { x, c ->
                if (c == '>') map[y to x] = false
                if (c == 'v') map[y to x] = true
            }
        }
        var counter = 0L
        while (true) {
            val next = hashMapOf<Pair<Int, Int>, Boolean>()
            for ((y, x) in map.filterValues { !it }.keys) {
                val toCheck = if (x == horizontalSize - 1) y to 0 else y to x + 1
                if (map.containsKey(toCheck))
                    next[y to x] = false
                else next[toCheck] = false
            }
            for ((y, x) in map.filterValues { it }.keys) {
                val toCheck = if (y == verticalSize - 1) 0 to x else y + 1 to x
                if (map[toCheck] == true || next[toCheck] == false)
                    next[y to x] = true
                else next[toCheck] = true
            }
            counter++
            if (next == map) break
            map = next
        }
        return counter

    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day25_test")
    check(part1(testInput) == 58L)
    println(part1(readInput("Day25")))
}

