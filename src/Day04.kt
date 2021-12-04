fun main() {
    fun parseInput(input: List<String>): Pair<List<Int>, MutableList<List<MutableList<Int>>>> {
        val calledNumbers = input[0].split(',').map { it.toInt() }
        val boards = input.subList(2, input.size).chunked(6)
            .map { it.dropLast(1) }
            .map { boardStrings ->
                boardStrings.map { row ->
                    row.chunked(3) { num ->
                        num.trim().toString().toInt()
                    }.toMutableList()
                }
            }.toMutableList()
        return Pair(calledNumbers, boards)
    }

    fun List<MutableList<Int>>.boardScore(calledNumber: Int) =
        flatten().filter { it != -1 }.sum() * calledNumber


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
        var lastCall = 0
        var latestBoard = listOf<MutableList<Int>>()
        for (calledNumber in calledNumbers) {
            lastCall = calledNumber
            for (board in boards) {
                for (row in board) {
                    val index = row.indexOf(calledNumber)
                    if (index >= 0) row[index] = -1
                }
            }
            val toRemove = hashSetOf<List<MutableList<Int>>>()
            for (board in boards) {
                for (row in board) {
                    if (row.all { it == -1 }) {
                        toRemove.add(board)
                        latestBoard = board
                    }
                }
                for (i in board.indices) {
                    if (board.map { it[i] }.all { it == -1 }) {
                        toRemove.add(board)
                        latestBoard = board
                    }
                }
            }
            boards.removeAll(toRemove)
            if (boards.isEmpty()) break
        }

        return latestBoard.boardScore(lastCall)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    check(part1(testInput) == 4512)

    val input = readInput("Day04")
    println(part1(input))

    check(part2(testInput)==1924)
    println(part2(input))
}

