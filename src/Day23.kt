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
    fun List<Char?>.everybodyOnTheirPlaces(roomIndex: Int) = none { it != null && destination(it) != roomIndex }

    fun moveFromRoomToHallway(hallway: Map<Int, Char>, rooms: List<List<Char?>>, bestCost: Int): Int {
        var nextBest = bestCost
        for ((roomIndex, room) in rooms.withIndex()) {
            if (room.everybodyOnTheirPlaces(roomIndex)) continue
            val emptyPlaces = room.count { it == null }
            val steps = emptyPlaces + 1
            val amphipod = room[emptyPlaces]!!
            for (hallDestination in hallSpots) {
                val destinationSteps = steps + abs(hallDestination - roomMap[roomIndex])
                val destinationCost = destinationSteps * costs(amphipod)
                val toCheck = min(hallDestination, roomMap[roomIndex])..max(hallDestination, roomMap[roomIndex])
                if (!hallway.wayIsFree(toCheck)) continue
                val newRoom = room.mapIndexed { ind, cur -> if (emptyPlaces + 1 == ind) null else cur }
                val newHall = hallway + (hallDestination to amphipod)
                val newRooms = rooms.mapIndexed { ind, el -> if (roomIndex == ind) newRoom else el }
                val helperResult = memoizedHelper(newHall, newRooms)
                val newCost = destinationCost + helperResult
                if (newCost < nextBest)
                    nextBest = newCost
            }
        }
        return nextBest
    }

    fun moveFromHallwaysToRooms(hallway: Map<Int, Char>, rooms: List<List<Char?>>, roomSize: Int, bestCost: Int): Int {
        var nextBest = bestCost
        for ((hallPlace, amphipod) in hallway) {
            val dest = destination(amphipod)
            val room = rooms[dest]
            if (!room.hasPlaceFor(amphipod)) continue
            val startWalk = hallPlace + if (roomMap[dest] > hallPlace) 1 else -1
            val endWalk = roomMap[dest]
            val toCheck = if (startWalk < endWalk) startWalk until endWalk else startWalk downTo endWalk
            if (!hallway.wayIsFree(toCheck)) continue
            val emptyPlaces = room.count { it == null }
            val newRoom = room.mapIndexed { i, cur -> if (i == emptyPlaces - 1) amphipod else cur }
            val newHall = hallway.filterNot { it.key == hallPlace }
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
        bestCost = moveFromHallwaysToRooms(hallway, rooms, roomSize, bestCost)
        bestCost = moveFromRoomToHallway(hallway, rooms, bestCost)
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


