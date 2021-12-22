import java.lang.StringBuilder

sealed interface Packet {
    val version: Int
    val type: Int
    val length: Int
    val value: Long
    val typeName:String

    companion object {
        fun Operator(version: Int, type: Int, packets: List<Packet>, lengthTypeId: Char): Operator = when (type) {
            0 -> Sum(version, type, packets, lengthTypeId)
            1 -> Product(version, type, packets, lengthTypeId)
            2 -> Min(version, type, packets, lengthTypeId)
            3 -> Max(version, type, packets, lengthTypeId)
            5 -> GT(version, type, packets, lengthTypeId)
            6 -> LT(version, type, packets, lengthTypeId)
            7 -> EQ(version, type, packets, lengthTypeId)
            else -> error("Unsupported op type $type")
        }
    }

    data class Literal(
        override val version: Int,
        override val type: Int,
        val data: String,
    ) : Packet {
        override val length: Int = 3 + 3 + (data.length / 4) + data.length
        override val value get() = data.toLong(2)
        override val typeName = "Literal"
    }


    sealed interface Operator : Packet {
        override val length: Int
            get() = 3 + 3 + 1 + packets.sumOf { it.length } + if (lengthTypeId == '1') 11 else 15
        val packets: List<Packet>
        val lengthTypeId: Char
    }

    data class Sum(
        override val version: Int,
        override val type: Int,
        override val packets: List<Packet>,
        override val lengthTypeId: Char,
    ) : Operator {
        override val value = packets.sumOf { it.value }
        override val typeName = "Sum"
    }

    data class Product(
        override val version: Int,
        override val type: Int,
        override val packets: List<Packet>,
        override val lengthTypeId: Char,
    ) : Operator {
        override val value = packets.map { it.value }.reduce { a, b -> a * b }
        override val typeName = "Product"
    }

    data class Min(
        override val version: Int,
        override val type: Int,
        override val packets: List<Packet>,
        override val lengthTypeId: Char,
    ) : Operator {
        override val value = packets.map { it.value }.minOf { it }
        override val typeName = "Min"
    }

    data class Max(
        override val version: Int,
        override val type: Int,
        override val packets: List<Packet>,
        override val lengthTypeId: Char,
    ) : Operator {
        override val value = packets.map { it.value }.maxOf { it }
        override val typeName = "Max"
    }

    data class GT(
        override val version: Int,
        override val type: Int,
        override val packets: List<Packet>,
        override val lengthTypeId: Char,
    ) : Operator {
        override val value: Long = run {
            check(packets.size == 2)
            if (packets.first().value > packets[1].value) 1 else 0
        }
        override val typeName = "GreaterThen"
    }

    data class LT(
        override val version: Int,
        override val type: Int,
        override val packets: List<Packet>,
        override val lengthTypeId: Char,
    ) : Operator {
        override val value: Long = run {
            check(packets.size == 2)
            if (packets.first().value < packets[1].value) 1 else 0
        }
        override val typeName = "Less Then"
    }

    data class EQ(
        override val version: Int,
        override val type: Int,
        override val packets: List<Packet>,
        override val lengthTypeId: Char,
    ) : Operator {
        override val value: Long = run {
            check(packets.size == 2)
            if (packets.first().value == packets[1].value) 1 else 0
        }
        override val typeName = "Equals"
    }

}


fun main() {
    fun String.hexaDecimalToBinary(): List<Char> = flatMap {
        it
            .digitToInt(16)
            .toString(2)
            .padStart(4, '0')
            .toList()
    }

    fun ArrayDeque<Char>.getN(n: Int) = (0 until n).map { removeFirst() }.joinToString("")
    fun ArrayDeque<Char>.getNAsInt(n: Int) = getN(n).toInt(2)

    fun ArrayDeque<Char>.parsePacket(): Packet {
        val version = getNAsInt(3)
        val type = getNAsInt(3)
        if (type == 4) {
            val result = StringBuilder()
            var counter = 0
            while (true) {
                val chunk = getN(5)
                result.append(chunk.takeLast(4))
                counter += 5
                if (chunk.startsWith('0')) break
            }
            return Packet.Literal(version, type, result.toString())
        } else {
            val lengthTypeId = removeFirst()
            return if (lengthTypeId == '1') {
                val subPacketsNumber = getNAsInt(11)
                val subPackets = (0 until subPacketsNumber).map { parsePacket() }
                Packet.Operator(version, type, subPackets, lengthTypeId)
            } else {
                val totalSubPacketsLength = getNAsInt(15)
                val children = arrayListOf<Packet>()
                while (children.sumOf { it.length } < totalSubPacketsLength) {
                    children.add(parsePacket())
                }
                Packet.Operator(version, type, children, lengthTypeId)
            }
        }
    }

    fun part1(input: String): Int {
        val packet = ArrayDeque(input.hexaDecimalToBinary()).parsePacket()
        val toCheck = ArrayDeque(listOf(packet))
        var result = 0
        while (toCheck.isNotEmpty()) {
            val next = toCheck.removeFirst()
            result += next.version
            if (next is Packet.Operator) {
                toCheck.addAll(next.packets)
            }
        }
        return result
    }

    fun part2(input: String): Long {
        return ArrayDeque(input.hexaDecimalToBinary()).parsePacket().value
    }

    check(part1("8A004A801A8002F478") == 16)
    check(part1("620080001611562C8802118E34") == 12)
    check(part1("C0015000016115A2E0802F182340") == 23)
    check(part1("A0016C880162017C3686B18A3D4780") == 31)
    val input =
        "6053231004C12DC26D00526BEE728D2C013AC7795ACA756F93B524D8000AAC8FF80B3A7A4016F6802D35C7C94C8AC97AD81D30024C00D1003C80AD050029C00E20240580853401E98C00D50038400D401518C00C7003880376300290023000060D800D09B9D03E7F546930052C016000422234208CC000854778CF0EA7C9C802ACE005FE4EBE1B99EA4C8A2A804D26730E25AA8B23CBDE7C855808057C9C87718DFEED9A008880391520BC280004260C44C8E460086802600087C548430A4401B8C91AE3749CF9CEFF0A8C0041498F180532A9728813A012261367931FF43E9040191F002A539D7A9CEBFCF7B3DE36CA56BC506005EE6393A0ACAA990030B3E29348734BC200D980390960BC723007614C618DC600D4268AD168C0268ED2CB72E09341040181D802B285937A739ACCEFFE9F4B6D30802DC94803D80292B5389DFEB2A440081CE0FCE951005AD800D04BF26B32FC9AFCF8D280592D65B9CE67DCEF20C530E13B7F67F8FB140D200E6673BA45C0086262FBB084F5BF381918017221E402474EF86280333100622FC37844200DC6A8950650005C8273133A300465A7AEC08B00103925392575007E63310592EA747830052801C99C9CB215397F3ACF97CFE41C802DBD004244C67B189E3BC4584E2013C1F91B0BCD60AA1690060360094F6A70B7FC7D34A52CBAE011CB6A17509F8DF61F3B4ED46A683E6BD258100667EA4B1A6211006AD367D600ACBD61FD10CBD61FD129003D9600B4608C931D54700AA6E2932D3CBB45399A49E66E641274AE4040039B8BD2C933137F95A4A76CFBAE122704026E700662200D4358530D4401F8AD0722DCEC3124E92B639CC5AF413300700010D8F30FE1B80021506A33C3F1007A314348DC0002EC4D9CF36280213938F648925BDE134803CB9BD6BF3BFD83C0149E859EA6614A8C"
    println(part1(input))
    println(part2(input))
}


