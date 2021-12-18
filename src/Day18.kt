import BracketExpression.ExprPair
import BracketExpression.Leaf
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.github.h0tk3y.betterParse.combinators.map
import com.github.h0tk3y.betterParse.combinators.or
import com.github.h0tk3y.betterParse.combinators.times
import com.github.h0tk3y.betterParse.combinators.unaryMinus
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.h0tk3y.betterParse.grammar.parser
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken
import com.github.h0tk3y.betterParse.parser.Parser
import kotlin.math.ceil
import kotlin.math.floor
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

val mapper = jacksonObjectMapper()

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
private sealed interface BracketExpression {
    fun addToLeftMost(second: Int?): Boolean
    fun addToRightMost(second: Int?): Boolean

    data class ExprPair(var left: BracketExpression, var right: BracketExpression) : BracketExpression {

        fun reduceMax(): ExprPair {
            while (true)
                if (reduce() != null) continue
                else if (split()) continue
                else break
            return this
        }

        fun magnitude(): Long {
            return when (left) {
                is Leaf -> when (right) {
                    is Leaf -> (left as Leaf).num.toLong() * 3 + (right as Leaf).num.toLong() * 2
                    is ExprPair -> (left as Leaf).num.toLong() * 3 + (right as ExprPair).magnitude() * 2
                }
                is ExprPair -> when (right) {
                    is Leaf -> (left as ExprPair).magnitude() * 3 + (right as Leaf).num.toLong() * 2
                    is ExprPair -> (left as ExprPair).magnitude() * 3 + (right as ExprPair).magnitude() * 2
                }
            }
        }

        override fun addToLeftMost(second: Int?): Boolean {
            if (second == null) return true
            return left.addToLeftMost(second)
        }

        override fun addToRightMost(second: Int?): Boolean {
            if (second == null) return true
            return right.addToRightMost(second)
        }

        fun split(): Boolean {
            if (left is Leaf && (left as Leaf).num > 9) {
                val current = (left as Leaf).num
                left = ExprPair(Leaf(floor(current.toDouble() / 2).toInt()), Leaf(ceil(current.toDouble() / 2).toInt()))
                return true
            }
            if (left is ExprPair) {
                if ((left as ExprPair).split()) return true

            }
            if (right is Leaf && (right as Leaf).num > 9) {
                val current = (right as Leaf).num
                right =
                    ExprPair(Leaf(floor(current.toDouble() / 2).toInt()), Leaf(ceil(current.toDouble() / 2).toInt()))
                return true
            }
            if (right is ExprPair) {
                if ((right as ExprPair).split()) return true
            }
            return false
        }

        operator fun plus(other: BracketExpression) = ExprPair(this, other)

        fun reduce(level: Int = 0): Pair<Int, Int>? {
            if (level > 2) {
                if (left is ExprPair && (left as ExprPair).left is Leaf && (left as ExprPair).right is Leaf) {
                    val result = Pair(((left as ExprPair).left as Leaf).num, ((left as ExprPair).right as Leaf).num)
                    left = Leaf(0)
                    right.addToLeftMost(result.second)
                    return result.first to 0
                } else if (right is ExprPair && (right as ExprPair).left is Leaf && (right as ExprPair).right is Leaf) {
                    val result = ((right as ExprPair).left as Leaf).num to ((right as ExprPair).right as Leaf).num
                    right = Leaf(0)
                    left.addToRightMost(result.first)
                    return 0 to result.second
                } else if (left is ExprPair) {
                    val x = tryReduceLeft(level)
                    if (x != null) return x
                } else if (right is ExprPair) {
                    val x = tryReduceRight(level)
                    if (x != null) return x
                }
            }
            if (left is ExprPair) {
                val x = tryReduceLeft(level)
                if (x != null) return x
            }
            if (right is ExprPair) {
                val x = tryReduceRight(level)
                if (x != null) return x
            }
            return null
        }

        private fun tryReduceLeft(level: Int): Pair<Int, Int>? {
            val result = (left as ExprPair).reduce(level + 1)
            return if (result != null && result.second != 0 && right is Leaf) {
                (right as Leaf).num += result.second
                result.copy(second = 0)
            } else if (result != null && result.second != 0 && right.addToLeftMost(result.second)) {
                0 to 0
            } else result
        }

        private fun tryReduceRight(level: Int): Pair<Int, Int>? {
            val result = (right as ExprPair).reduce(level + 1)
            return if (result != null && result.first != 0 && left is Leaf) {
                (left as Leaf).num += result.first
                result.copy(first = 0)
            } else if (result != null && result.first != 0 && left.addToRightMost(result.first)) {
                0 to 0
            } else result
        }

        override fun toString() = "[$left,$right]"
    }


    data class Leaf(var num: Int) : BracketExpression {
        override fun addToLeftMost(second: Int?): Boolean {
            if (second == null) return false
            num += second
            return true
        }

        override fun addToRightMost(second: Int?): Boolean {
            if (second == null) return false
            num += second
            return true
        }

        override fun toString() = num.toString()
    }
}

fun main() {
    val pairParser = object : Grammar<ExprPair>() {
        val number by regexToken("\\d+".toRegex())
        val comma by literalToken(",")
        val open by literalToken("[")
        val close by literalToken("]")

        val leaf by number map { Leaf(it.text.toInt()) }

        val el by leaf or parser(this::rootParser)
        val pair by (-open * el * -comma * el * -close) map { (a, b) -> ExprPair(a, b) }


        override val rootParser: Parser<ExprPair> by pair
    }

    fun part1(input: List<String>): ExprPair {
        return input
            .map { pairParser.parseToEnd(it.trim()) }
            .reduce { a, b ->
                val x = a + b
                x.reduceMax()
            }
    }

    fun ExprPair.deepCopy(): ExprPair {
        return mapper.readValue(mapper.writeValueAsString(this))
    }

    fun cartesianProduct(a: List<ExprPair>, b: List<ExprPair>): Sequence<Pair<ExprPair, ExprPair>> = sequence {
        for (x in a) {
            for (y in b) {
                if (x == y) continue
                yield(x.deepCopy() to y.deepCopy())
            }
        }
    }

    fun part2(input: List<String>): Long {
        val allTrees = input.map { pairParser.parseToEnd(it.trim()) }
        return cartesianProduct(allTrees, allTrees)
            .map { (a, b) ->
                print("Sum of $a + $b = ")
                (a + b).reduceMax().magnitude().also { println(it) }
            }
            .maxOf { it }

    }

    // test if implementation meets criteria from the description, like:
    fun String.reducedOnce(): ExprPair {
        val res = pairParser.parseToEnd(this)
        res.reduce()
        return res
    }

    fun String.reducedMax(): ExprPair {
        val res = pairParser.parseToEnd(this)
        res.reduceMax()
        return res
    }

    fun String.splittedOnce(): ExprPair {
        val res = pairParser.parseToEnd(this)
        res.split()
        return res
    }

    check("[[[[[9,8],1],2],3],4]".reducedOnce().toString() == "[[[[0,9],2],3],4]")
    check("[7,[6,[5,[4,[3,2]]]]]".reducedOnce().toString() == "[7,[6,[5,[7,0]]]]")
    check("[[6,[5,[4,[3,2]]]],1]".reducedOnce().toString() == "[[6,[5,[7,0]]],3]")
    check("[[3,[2,[1,[7,3]]]],[6,[5,[4,[3,2]]]]]".reducedOnce().toString() == "[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]")
    check("[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]".reducedOnce().toString() == "[[3,[2,[8,0]]],[9,[5,[7,0]]]]")

    check("[[[[0,7],4],[15,[0,13]]],[1,1]]".splittedOnce().toString() == "[[[[0,7],4],[[7,8],[0,13]]],[1,1]]")
    check("[[[[0,7],4],[[7,8],[0,13]]],[1,1]]".splittedOnce().toString() == "[[[[0,7],4],[[7,8],[0,[6,7]]]],[1,1]]")

    check(part1("[1,1]\n[2,2]\n[3,3]\n[4,4]".lines()).toString() == "[[[[1,1],[2,2]],[3,3]],[4,4]]")
    check(part1("[1,1]\n[2,2]\n[3,3]\n[4,4]\n[5,5]".lines()).toString() == "[[[[3,0],[5,3]],[4,4]],[5,5]]")
    check(part1("[1,1]\n[2,2]\n[3,3]\n[4,4]\n[5,5]\n[6,6]".lines()).toString() == "[[[[5,0],[7,4]],[5,5]],[6,6]]")

    check((pairParser.parseToEnd("[[[[4,3],4],4],[7,[[8,4],9]]]") + pairParser.parseToEnd("[1,1]")).toString() == "[[[[[4,3],4],4],[7,[[8,4],9]]],[1,1]]")

    check("[[[[[4,3],4],4],[7,[[8,4],9]]],[1,1]]".reducedMax().toString() == "[[[[0,7],4],[[7,8],[6,0]]],[8,1]]")

    check(pairParser.parseToEnd("[[1,2],[[3,4],5]]").magnitude() == 143L)
    check(pairParser.parseToEnd("[[[[0,7],4],[[7,8],[6,0]]],[8,1]]").magnitude() == 1384L)
    check(pairParser.parseToEnd("[[[[1,1],[2,2]],[3,3]],[4,4]]").magnitude() == 445L)
    check(pairParser.parseToEnd("[[[[3,0],[5,3]],[4,4]],[5,5]]").magnitude() == 791L)
    check(pairParser.parseToEnd("[[[[5,0],[7,4]],[5,5]],[6,6]]").magnitude() == 1137L)
    check(pairParser.parseToEnd("[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]").magnitude() == 3488L)

    val testInput = readInput("Day18_test")
    val input = readInput("Day18")
    check(part1(testInput).toString() == "[[[[6,6],[7,6]],[[7,7],[7,0]]],[[[7,7],[7,7]],[[7,8],[9,9]]]]")
    println(part1(testInput).magnitude())
    println(part1(input).magnitude())
    println(part2(testInput))
    println(part2(input))
}

