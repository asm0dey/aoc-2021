import arrow.syntax.function.memoize
import kotlin.math.max
import kotlin.math.min
import kotlin.properties.Delegates.notNull

fun main() {
    fun part1(player1Start: Int, player2Start: Int): Int {
        val die = iterator {
            while (true) {
                for (it in 1..100) {
                    yield(it)
                }
            }
        }
        var player1Score = 0
        var player2Score = 0
        var dieRolled = 0
        var p1Pos = player1Start
        var p2Pos = player2Start
        var nextTurn = true
        while (player1Score < 1000 && player2Score < 1000) {
            val dice = die.next() + die.next() + die.next()
            dieRolled += 3
            if (nextTurn) {
                p1Pos = (p1Pos + dice) % 10
                if (p1Pos == 0) p1Pos = 10
                player1Score += p1Pos
            } else {
                p2Pos = (p2Pos + dice) % 10
                if (p2Pos == 0) p2Pos = 10
                player2Score += p2Pos
            }
            nextTurn = !nextTurn
        }
        return dieRolled * min(player1Score, player2Score)
    }

    var count = 0
    var outcomesMemoized by notNull<(p1Pos: Int, p2Pos: Int, p1Score: Long, p2Score: Long, next: Boolean) -> Pair<Long, Long>>()
    fun countOutcomes(
        p1Pos: Int,
        p2Pos: Int,
        p1Score: Long,
        p2Score: Long,
        next: Boolean,
    ): Pair<Long, Long> {
        if (p1Score >= 21) return 1L to 0L
        else if (p2Score >= 21) return 0L to 1L
        var xwins = 0L
        var ywins = 0L
        for (a in (1..3)) {
            for (b in (1..3)) {
                for (c in 1..3) {
                    val dice = a + b + c
                    val res: Pair<Long, Long>
                    if (next) {
                        var nextP = (p1Pos + dice) % 10
                        if (nextP == 0) nextP = 10
                        res = outcomesMemoized(nextP, p2Pos, p1Score + nextP, p2Score, false)
                    } else {
                        var nextP = (p2Pos + dice) % 10
                        if (nextP == 0) nextP = 10
                        res = outcomesMemoized(p1Pos, nextP, p1Score, p2Score + nextP, true)
                    }
                    xwins += res.first
                    ywins += res.second
                }
            }
        }
        count++
        return xwins to ywins
    }
    outcomesMemoized = ::countOutcomes.memoize()

    fun part2(player1Start: Int, player2Start: Int): Long {
        count = 0
        val (wins1, wins2) = outcomesMemoized(player1Start, player2Start, 0, 0, true)
        return max(wins1, wins2)
    }

    // test if implementation meets criteria from the description, like:
    check(part1(4, 8) == 739785)
    println(part1(10, 2))
    for (i in 1..10) {
        for (j in 1..10) {
            println("Result for $i $j is ${part2(i, j)}, it took $count additional entries")
        }
    }
}

