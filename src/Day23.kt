import arrow.syntax.function.memoize
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.properties.Delegates.notNull

fun main() {
    val roomMap = listOf(2, 4, 6, 8)
    val hallSpots = listOf(0, 1, 3, 5, 7, 9, 10)
    val destination = mapOf('A' to 0, 'B' to 1, 'C' to 2, 'D' to 3)
    val costs = mapOf('A' to 1, 'B' to 10, 'C' to 100, 'D' to 1000)
    var memoizedHelper by notNull<(hallway: Map<Int, Char>, rooms: List<List<Char?>>) -> Int>()
    infix fun <T> Int.times(n: T): List<T> = List(this) { n }

    fun List<Char?>.hasPlaceFor(amphipod: Char) = none { it != null && it != amphipod }

    fun Map<Int, Char>.wayIsEmpty(range: IntProgression) = range.none { this[it] != null }

    fun helper(hallway: Map<Int, Char>, rooms: List<List<Char?>>): Int {
        val roomSize = rooms[0].size
        if (rooms == listOf(roomSize times 'A', roomSize times 'B', roomSize times 'C', roomSize times 'D'))
            return 0
        var bestCost = 100000
        for ((hallPlace, amphipod) in hallway) { // move from the hallway
            val dest = destination[amphipod]!!
            if (!rooms[dest].hasPlaceFor(amphipod)) continue
            val startWalk = hallPlace + if (roomMap[dest] > hallPlace) 1 else -1
            val endWalk = roomMap[dest]
            val toCheck = if (startWalk < endWalk) startWalk until endWalk else startWalk downTo endWalk
            if (!hallway.wayIsEmpty(toCheck)) continue
            val emptyPlaces = rooms[dest].count { it == null }
            val newRoom = List<Char?>(emptyPlaces - 1) { null } + List(roomSize - emptyPlaces + 1) { amphipod }
            val newHall = hallway.filterNot { it.key == hallPlace }
            val newRooms = rooms.subList(0, dest) + listOf(newRoom) + rooms.subList(dest + 1, rooms.size)
            val steps = emptyPlaces + abs(hallPlace - roomMap[dest])
            val newCost = steps * costs[amphipod]!! + memoizedHelper(newHall, newRooms)
            if (newCost < bestCost)
                bestCost = newCost
        }
        for ((i, room) in rooms.withIndex()) { // rooms to hallway
            if (!room.any { it != null && destination[it] != i }) continue
            val emptyPlaces = room.count { it == null }
            val steps = emptyPlaces + 1
            val amphipod = room[emptyPlaces]
            for (hallDestination in hallSpots) {
                val destinationSteps = steps + abs(hallDestination - roomMap[i])
                val destinationCost = destinationSteps * costs[amphipod]!!
                val toCheck = min(hallDestination, roomMap[i])..max(hallDestination, roomMap[i])
                if (!hallway.wayIsEmpty(toCheck)) continue
                val newRoom = List<Char?>(emptyPlaces + 1) { null } + room.subList(emptyPlaces + 1, room.size)
                val newHall = hallway + (hallDestination to amphipod!!)
                val newRooms = rooms.subList(0, i) + listOf(newRoom) + rooms.subList(i + 1, rooms.size)
                val helperResult = memoizedHelper(newHall, newRooms)
                val newCost = destinationCost + helperResult
                if (newCost < bestCost)
                    bestCost = newCost
            }
        }
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


