import kotlin.properties.Delegates.notNull

private typealias Board = List<IntArray>

fun main() {
    fun parseInput(input: List<String>): Pair<List<Int>, MutableList<Board>> {
        val calledNumbers = input[0].split(',').map { it.toInt() }
        val boards = input
            .asSequence()
            .drop(2)
            .chunked(6)
            .map { it.subList(0, 5) }
            .map { boardStrings ->
                boardStrings
                    .map { row ->
                        row
                            .chunked(3) { num -> num.trim().toString().toInt() }
                            .toIntArray()
                    }
            }
            .toMutableList()
        return Pair(calledNumbers, boards)
    }

    fun Board.boardScore(calledNumber: Int) =
        flatMap { it.asSequence() }.filter { it != -1 }.sum() * calledNumber


    fun part1(input: List<String>): Int {
        val (calledNumbers, boards) = parseInput(input)

        for (calledNumber in calledNumbers) {
            for (board in boards) {
                for (row in board) {
                    val index = row.indexOf(calledNumber)
                    if (index >= 0) row[index] = -1
                }
            }
            for (board in boards) {
                for (row in board) {
                    if (row.all { it == -1 }) {
                        return board.boardScore(calledNumber)
                    }
                }
                for (i in board.indices) {
                    if (board.map { it[i] }.all { it == -1 }) {
                        return board.boardScore(calledNumber)
                    }
                }
            }
        }
        error("Not accessible")
    }

    fun part2(input: List<String>): Int {
        val (calledNumbers, boards) = parseInput(input)
        var lastCall by notNull<Int>()
        var latestBoard by notNull<Board>()
        for (calledNumber in calledNumbers) {
            lastCall = calledNumber
            for (board in boards) {
                for (row in board) {
                    val index = row.indexOf(calledNumber)
                    if (index >= 0) row[index] = -1
                }
            }
            boards
                .filter { board ->
                    board.any { row -> row.all { it == -1 } } ||
                            board.indices.any { col -> board.map { it[col] }.all { it == -1 } }
                }
                .forEach {
                    boards.remove(it)
                    latestBoard = it
                }
            if (boards.isEmpty()) break
        }

        return latestBoard.boardScore(lastCall)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    check(part1(testInput) == 4512)

    val input = readInput("Day04")
    println(part1(input))

    check(part2(testInput) == 1924)
    println(part2(input))
}

