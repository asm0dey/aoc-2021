import BracketExpression.ExprPair
import BracketExpression.Leaf
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

private sealed interface BracketExpression {
    fun addToLeftMost(second: Int?): BracketExpression
    fun addToRightMost(second: Int?): BracketExpression

    data class ExprPair(val left: BracketExpression, val right: BracketExpression) : BracketExpression {

        fun reduceMax(): ExprPair {
            var current = this
            while (true) {
                val (afterReduce, _) = current.reduce()
                if (afterReduce != current) {
                    current = afterReduce
//                    println(current)
                    continue
                }
                val ar2 = current.split()
                if (ar2 != current) {
                    current = ar2
//                    println(current)
                    continue
                }
                break

            }

            return current
        }

        fun magnitude(): Long {
            return when (left) {
                is Leaf -> when (right) {
                    is Leaf -> left.num.toLong() * 3 + right.num.toLong() * 2
                    is ExprPair -> left.num.toLong() * 3 + right.magnitude() * 2
                }
                is ExprPair -> when (right) {
                    is Leaf -> left.magnitude() * 3 + right.num.toLong() * 2
                    is ExprPair -> left.magnitude() * 3 + right.magnitude() * 2
                }
            }
        }

        override fun addToLeftMost(second: Int?): BracketExpression {
            if (second == null) return this
            val nl = left.addToLeftMost(second)
            return copy(left = nl)
        }

        override fun addToRightMost(second: Int?): BracketExpression {
            if (second == null) return this
            val nr = right.addToRightMost(second)
            return copy(right = nr)
        }

        fun split(): ExprPair {
            if (left is Leaf && left.num > 9) {
                return copy(left = ExprPair(Leaf(floor(left.num.toDouble() / 2).toInt()),
                    Leaf(ceil(left.num.toDouble() / 2).toInt())))
            }
            if (left is ExprPair) {
                val nl = left.split()
                if (nl != left) return copy(left = nl)
            }
            if (right is Leaf && right.num > 9) {
                val current = right.num
                return copy(right = ExprPair(Leaf(floor(current.toDouble() / 2).toInt()),
                    Leaf(ceil(current.toDouble() / 2).toInt())))
            }
            if (right is ExprPair) {
                val nr = right.split()
                if (nr != right) return copy(right = nr)
            }
            return this
        }

        operator fun plus(other: BracketExpression) = ExprPair(this, other)

        fun reduce(level: Int = 0): Pair<ExprPair, Pair<Int, Int>> {
            if (level > 2) {
                if (left is ExprPair && left.left is Leaf && left.right is Leaf) {
                    val result = Pair(left.left.num, left.right.num)
                    val nl = Leaf(0)
                    val nr = right.addToLeftMost(result.second)
                    return copy(left = nl, right = nr) to (result.first to 0)
                } else if (right is ExprPair && right.left is Leaf && right.right is Leaf) {
                    val result = right.left.num to right.right.num
                    val nr = Leaf(0)
                    val nl = left.addToRightMost(result.first)
                    return copy(left = nl, right = nr) to (0 to result.second)
                } else if (left is ExprPair) {
                    val (nl, result) = tryReduceLeft(level)
                    if (nl.left != left) return copy(left = nl) to result
                } else if (right is ExprPair) {
                    val (nr, result) = tryReduceRight(level)
                    if (nr.right != right) return copy(right = nr) to result
                }
            }
            if (left is ExprPair) {
                val (nl, result) = tryReduceLeft(level)
                if (nl != this) return nl to result
            }
            if (right is ExprPair) {
                val (nr, result) = tryReduceRight(level)
                if (nr != this) return nr to result
                return this to result
            }
            return this to (0 to 0)
        }

        private fun tryReduceLeft(level: Int): Pair<ExprPair, Pair<Int, Int>> {
            val (nl, result) = (left as ExprPair).reduce(level + 1)
            if (nl != left && result.second != 0) {
                return if (right is Leaf) {
                    val nr = right.copy(num = right.num + result.second)
                    copy(left = nl, right = nr) to result.copy(second = 0)
                } else {
                    val nr = right.addToLeftMost(result.second)
                    copy(left = nl, right = nr) to (0 to 0)
                }
            }
            return copy(left = nl) to result
        }

        private fun tryReduceRight(level: Int): Pair<ExprPair, Pair<Int, Int>> {
            val (nr, result) = (right as ExprPair).reduce(level + 1)
            if (nr != right && result.first != 0)
                return when (left) {
                    is Leaf -> {
                        val nl = left.copy(num = left.num + result.first)
                        copy(left = nl, right = nr) to result.copy(first = 0)
                    }
                    is ExprPair -> {
                        val nl = left.addToRightMost(result.first)
                        copy(left = nl, right = nr) to (0 to 0)
                    }
                }
            return copy(right = nr) to result
        }

        override fun toString() = "[$left,$right]"
    }


    data class Leaf(val num: Int) : BracketExpression {
        override fun addToLeftMost(second: Int?): Leaf {
            if (second == null) return this
            return Leaf(num + second)
        }

        override fun addToRightMost(second: Int?): Leaf {
            if (second == null) return this
            return Leaf(num + second)
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
//                println("Reducing $x")
                x.reduceMax()
            }
    }

    fun cartesianProduct(a: List<ExprPair>, b: List<ExprPair>): Sequence<Pair<ExprPair, ExprPair>> = sequence {
        for (x in a) {
            for (y in b) {
                if (x == y) continue
                yield(x to y)
            }
        }
    }

    fun part2(input: List<String>): Long {
        val allTrees = input.map { pairParser.parseToEnd(it.trim()) }
        return cartesianProduct(allTrees, allTrees)
            .map { (a, b) ->
//                print("Sum of $a + $b = ")
                (a + b).reduceMax().magnitude()/*.also { println(it) }*/
            }
            .maxOf { it }

    }

    fun String.reducedOnce(): ExprPair {
        return pairParser.parseToEnd(this).reduce().first
    }

    fun String.reducedMax(): ExprPair {
        return pairParser.parseToEnd(this).reduceMax()
    }

    fun String.splittedOnce(): ExprPair {
        return pairParser.parseToEnd(this).split()
    }

    // test if implementation meets criteria from the description, like:
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

