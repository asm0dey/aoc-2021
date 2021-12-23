import arrow.syntax.function.memoize
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.properties.Delegates.notNull

fun main() {
    val roomMap = listOf(2, 4, 6, 8)
    val hallSpots = listOf(0, 1, 3, 5, 7, 9, 10)
    fun destination(x: Char) = when (x) {
        'A' -> 0
        'B' -> 1
        'C' -> 2
        'D' -> 3
        else -> error("Unsupported amphipod $x")
    }

    fun costs(x: Char) = when (x) {
        'A' -> 1
        'B' -> 10
        'C' -> 100
        'D' -> 1000
        else -> error("Unsupported amphipod $x")
    }

    var memoizedHelper by notNull<(hallway: Map<Int, Char>, rooms: List<List<Char?>>) -> Int>()
    infix fun <T> Int.times(n: T): List<T> = List(this) { n }

    fun List<Char?>.hasPlaceFor(amphipod: Char) = none { it != null && it != amphipod }
    fun Map<Int, Char>.wayIsFree(range: IntProgression) = range.none { this[it] != null }
    fun List<Char?>.isSettled(roomIndex: Int) = none { it != null && destination(it) != roomIndex }

    fun List<List<Char?>>.moveToHallway(hallway: Map<Int, Char>, bestCost: Int): Int {
        var nextBest = bestCost
        for ((roomIndex, room) in withIndex()) {
            if (room.isSettled(roomIndex)) continue
            val emptyPlaces = room.count { it == null }
            val amphipod = room[emptyPlaces]!!
            for (hallDestination in hallSpots) {
                val destinationSteps = emptyPlaces + 1 + abs(hallDestination - roomMap[roomIndex])
                val destinationCost = destinationSteps * costs(amphipod)
                val toCheck = min(hallDestination, roomMap[roomIndex])..max(hallDestination, roomMap[roomIndex])
                if (!hallway.wayIsFree(toCheck)) continue
                val newRoom = List<Char?>(emptyPlaces + 1) { null } + room.subList(emptyPlaces + 1, room.size)
                val newHall = hallway + (hallDestination to amphipod)
                val newRooms = mapIndexed { i, cur -> if (roomIndex == i) newRoom else cur }
                val helperResult = memoizedHelper(newHall, newRooms)
                val newCost = destinationCost + helperResult
                if (newCost < nextBest)
                    nextBest = newCost
            }
        }
        return nextBest
    }

    fun Map<Int, Char>.moveToRooms(rooms: List<List<Char?>>, bestCost: Int): Int {
        var nextBest = bestCost
        for ((hallPlace, amphipod) in this) {
            val dest = destination(amphipod)
            if (!rooms[dest].hasPlaceFor(amphipod)) continue
            val startWalk = hallPlace + if (roomMap[dest] > hallPlace) 1 else -1
            val endWalk = roomMap[dest]
            val toCheck = if (startWalk < endWalk) startWalk until endWalk else startWalk downTo endWalk
            if (!wayIsFree(toCheck)) continue
            val emptyPlaces = rooms[dest].count { it == null }
            val newRoom = rooms[dest].mapIndexed { i, cur -> if (i == emptyPlaces - 1) amphipod else cur }
            val newHall = filterNot { it.key == hallPlace }
            val newRooms = rooms.mapIndexed { i, el -> if (i == dest) newRoom else el }
            val steps = emptyPlaces + abs(hallPlace - roomMap[dest])
            val newCost = steps * costs(amphipod) + memoizedHelper(newHall, newRooms)
            if (newCost < nextBest)
                nextBest = newCost
        }
        return nextBest
    }

    fun helper(hallway: Map<Int, Char>, rooms: List<List<Char?>>): Int {
        val roomSize = rooms[0].size
        if (rooms == listOf(roomSize times 'A', roomSize times 'B', roomSize times 'C', roomSize times 'D'))
            return 0
        var bestCost = 100000
        bestCost = hallway.moveToRooms(rooms, bestCost)
        bestCost = rooms.moveToHallway(hallway, bestCost)
        return bestCost
    }

    memoizedHelper = ::helper.memoize()
    fun solve(input: List<List<Char>>): Int {
        return memoizedHelper(mapOf(), input)
    }

    println(solve("BACDBCDA".chunked(2) { it.toList() }))
    println(solve("DDACCBAB".chunked(2) { it.toList() }))
    println(solve("BDDACCBDBBACDACA".chunked(4) { it.toList() }))
    println(solve("DDDDACBCCBABAACB".chunked(4) { it.toList() }))
}


