fun main() {
    data class Node(val name: String) {
        val connections = hashSetOf<Node>()
        val uppercase = name.matches(Regex("[A-Z]+"))
        override fun toString() = name
    }

    fun buildGraph(input: List<String>): Map<String, Node> {
        val knownNodes = hashMapOf<String, Node>()
        input.map { it.split('-') }.forEach { (a, b) ->
            knownNodes.compute(a) { _, current -> current ?: Node(a) }
            knownNodes.compute(b) { _, current -> current ?: Node(b) }
            knownNodes[a]!!.connections.add(knownNodes[b]!!)
            knownNodes[b]!!.connections.add(knownNodes[a]!!)
        }
        return knownNodes
    }

    fun part2(input: List<String>): Int {
        val knownNodes = buildGraph(input)
        val start = knownNodes["start"]!!
        val end = knownNodes["end"]!!
        fun search(
            start: Node,
            forgotSmall: Boolean = false,
            visited: Set<Node> = setOf(start),
            path: List<Node> = listOf(start),
        ): List<List<Node>> = start
            .connections
            .filterNot { visited.contains(it) }
            .flatMap {
                if (it == end)
                    listOf(path + end)
                else if (!it.uppercase)
                    search(it, forgotSmall, visited + it, path + it) +
                            if (!forgotSmall && it != start && it != end) search(it, true, visited, path + it)
                            else listOf()
                else search(it, forgotSmall, visited, path + it)
            }

        return search(start).distinct().count()
    }

    fun part1(input: List<String>): Int {
        val knownNodes = buildGraph(input)
        val start = knownNodes["start"]!!
        val end = knownNodes["end"]!!
        fun search(
            start: Node,
            visited: Set<Node> = setOf(start),
            path: List<Node> = listOf(start),
        ): List<List<Node>> =
            start
                .connections
                .filterNot { visited.contains(it) }
                .flatMap {
                    if (it == end)
                        listOf(path + end)
                    else
                        search(it, visited + (if (it.uppercase) setOf() else setOf(it)), path + it)
                }

        return search(start).count()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day12_test")
    check(part1(testInput) == 10)

    val input = readInput("Day12")
    println(part1(input))

    check(part2(testInput) == 36)
    println(part2(input))
}

