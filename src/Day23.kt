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
    var memoizedHelper by notNull<(hallway: Map<Int, Char>, rooms: List<List<Char?>>, roomSize: Int) -> Long>()
    infix fun <T> Int.times(n: T): List<T> = List(this) { n }

    fun helper(hallway: Map<Int, Char>, rooms: List<List<Char?>>, roomSize: Int): Long {
        if (rooms == listOf(roomSize times 'A', roomSize times 'B', roomSize times 'C', roomSize times 'D'))
            return 0
        var bestCost = Int.MAX_VALUE.toLong() + 1
        outer@ for ((hallPlace, square) in hallway) { // move from the hallway
            val dest = destination[square]!!
            for (roommate in rooms[dest])
                if (roommate != null && roommate != square)
                    continue@outer
            val startWalk = hallPlace + if (roomMap[dest] > hallPlace) 1 else -1
            val endWalk = roomMap[dest]
            val placesToCheck = if (startWalk < endWalk) startWalk until endWalk else startWalk downTo endWalk
            for (j in placesToCheck)
                if (hallway[j] != null)
                    continue@outer
            val emptyPlaces = rooms[dest].count { it == null }
            val newRoom = List<Char?>(emptyPlaces - 1) { null } + List(roomSize - emptyPlaces + 1) { square }
            val newHall = hallway.filterNot { it.key == hallPlace }
            val newRooms = rooms.subList(0, dest) + listOf(newRoom) + rooms.subList(dest + 1, rooms.size)
            val steps = emptyPlaces + abs(hallPlace - roomMap[dest])
            val newCost = steps.toLong() * costs[square]!! + memoizedHelper(newHall, newRooms, roomSize)
            if (newCost < bestCost)
                bestCost = newCost
        }
        for ((i, room) in rooms.withIndex()) { // rooms to hallway
            var wantsToMove = false
            for (elem in room)
                if (elem != null && destination[elem] != i)
                    wantsToMove = true
            if (!wantsToMove) continue
            val emptyPlaces = room.count { it == null }
            val steps = emptyPlaces + 1
            val square = room[emptyPlaces]
            hallSearch@ for (hallDestination in hallSpots) {
                val destinationSteps = steps + abs(hallDestination - roomMap[i])
                val destinationCost = destinationSteps.toLong() * costs[square]!!
                for (j in min(hallDestination, roomMap[i])..max(hallDestination, roomMap[i]))
                    if (hallway[j] != null)
                        continue@hallSearch
                val newRoom = List<Char?>(emptyPlaces + 1) { null } + room.subList(emptyPlaces + 1, room.size)
                val newHall = hallway + (hallDestination to square!!)
                val newRooms = rooms.subList(0, i) + listOf(newRoom) + rooms.subList(i + 1, rooms.size)
                val helperResult = memoizedHelper(newHall, newRooms, roomSize)
                val newCost = destinationCost + helperResult
                if (newCost < bestCost)
                    bestCost = newCost
            }
        }
        return bestCost
    }

    fun solve(input: List<List<Char>>): Long {
        memoizedHelper = ::helper.memoize()
        val roomSz = input[0].size
        val result = memoizedHelper(mapOf(), input, roomSz)
        return result
    }

    println(solve("BACDBCDA".chunked(2) { it.toList() }))
    println(solve("DDACCBAB".chunked(2) { it.toList() }))
    println(solve("BDDACCBDBBACDACA".chunked(4) { it.toList() }))
    println(solve("DDDDACBCCBABAACB".chunked(4) { it.toList() }))
}


