import kotlin.math.ceil
import kotlin.math.floor

fun main() {
    val numberPair = "\\[(\\d+),(\\d+)]".toRegex()
    fun magnitude(source: String): Int {
        var current = source
        var findAll = numberPair.findAll(current)
        while (findAll.any()) {
            for (match in findAll) {
                val (fst, snd) = match.destructured
                val mag = (fst.toInt() * 3) + (snd.toInt() * 2)
                current = current.replace(match.value, mag.toString())
            }
            findAll = numberPair.findAll(current)
        }
        return current.toInt()
    }

    fun String.countOpen(res: MatchResult) =
        subSequence(0 until res.range.first)
            .map { if (it == '[') 1 else if (it==']') -1 else 0 }
            .sum()

    fun reduce(source: String): String {
        val match = numberPair.findAll(source)
            .firstOrNull { source.countOpen(it) >= 4 } ?: return source
        val (d1, d2) = match.destructured
        val before = "\\d+".toRegex().findAll(source.substring(0, match.range.first)).lastOrNull()
        val after = "\\d+".toRegex().find(source, match.range.last)
        val result = StringBuilder(source)
        if (after != null) {
            result.replace(
                after.range.first,
                after.range.last + 1,
                (d2.toInt() + after.value.toInt()).toString()
            )
        }
        result.replace(match.range.first, match.range.last + 1, "0")
        if (before != null) {
            result.replace(
                before.range.first,
                before.range.last + 1,
                (before.value.toInt() + d1.toInt()).toString()
            )
        }
        return result.toString()
    }

    fun split(s: String): String {
        val match = "\\d+".toRegex().findAll(s).filter { it.value.toInt() > 9 }.firstOrNull() ?: return s
        val intMatch = match.value.toInt()
        val left = floor(intMatch.toDouble() / 2).toInt()
        val right = ceil(intMatch.toDouble() / 2).toInt()
        return s.replaceFirst(match.value, "[$left,$right]")
    }

    infix fun String.snailPlus(other: String) = "[$this,$other]"

    fun reduceMax(s: String): String {
        var current = s
        while (true) {
            current = reduce(current).takeIf { it != current } ?: split(current).takeIf { it != current } ?: break
        }
        return current
    }

    fun part1(input: List<String>): String {
        return input.reduce { a, b ->
            reduceMax(a snailPlus b)
        }
    }


    fun part2(input: List<String>): Int {
        val pairs = sequence {
            for (i in input) {
                for (j in input) {
                    if (i != j) yield(magnitude(reduceMax(i snailPlus j)))
                }
            }
        }
        return pairs.maxOf { it }
    }


    // test if implementation meets criteria from the description, like:
    check(reduce("[[[[[9,8],1],2],3],4]") == "[[[[0,9],2],3],4]")
    check(reduce("[7,[6,[5,[4,[3,2]]]]]") == "[7,[6,[5,[7,0]]]]")
    check(reduce("[[6,[5,[4,[3,2]]]],1]") == "[[6,[5,[7,0]]],3]")
    check(reduce("[[3,[2,[1,[7,3]]]],[6,[5,[4,[3,2]]]]]") == "[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]")
    check(reduce("[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]") == "[[3,[2,[8,0]]],[9,[5,[7,0]]]]")

    check(split("[[[[0,7],4],[15,[0,13]]],[1,1]]") == "[[[[0,7],4],[[7,8],[0,13]]],[1,1]]")
    check(split("[[[[0,7],4],[[7,8],[0,13]]],[1,1]]") == "[[[[0,7],4],[[7,8],[0,[6,7]]]],[1,1]]")

    check(part1("[1,1]\n[2,2]\n[3,3]\n[4,4]".lines()) == "[[[[1,1],[2,2]],[3,3]],[4,4]]")
    check(part1("[1,1]\n[2,2]\n[3,3]\n[4,4]\n[5,5]".lines()) == "[[[[3,0],[5,3]],[4,4]],[5,5]]")
    check(part1("[1,1]\n[2,2]\n[3,3]\n[4,4]\n[5,5]\n[6,6]".lines()) == "[[[[5,0],[7,4]],[5,5]],[6,6]]")

    check(reduceMax("[[[[[4,3],4],4],[7,[[8,4],9]]],[1,1]]") == "[[[[0,7],4],[[7,8],[6,0]]],[8,1]]")

    check(magnitude("[[1,2],[[3,4],5]]") == 143)
    check(magnitude("[[[[0,7],4],[[7,8],[6,0]]],[8,1]]") == 1384)
    check(magnitude("[[[[1,1],[2,2]],[3,3]],[4,4]]") == 445)
    check(magnitude("[[[[3,0],[5,3]],[4,4]],[5,5]]") == 791)
    check(magnitude("[[[[5,0],[7,4]],[5,5]],[6,6]]") == 1137)
    check(magnitude("[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]") == 3488)

    val testInput = readInput("Day18_test")
    val input = readInput("Day18")
    check(part1(testInput) == "[[[[6,6],[7,6]],[[7,7],[7,0]]],[[[7,7],[7,7]],[[7,8],[9,9]]]]")
    println(magnitude(part1(testInput)))
    println(magnitude(part1(input)))
    println(part2(testInput))
    println(part2(input))
}


