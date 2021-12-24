
// This solution only works for my input: I've converted the source to Kotlin with search and replace and then refactored it heavily
// to leave predicate in separate variables. Each predicate should be equal 0 for z to not grow
fun main() {
    fun run(i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int,
            i8: Int, i9: Int, i10: Int, i11: Int, i12: Int, i13: Int, i14: Int
    ): Int {
        var z: Int = i1 * 17576 + i2 * 676 + i3 * 26 + i4 + 97750
        var x = z
        val b1 = if (x % 26 - 16 == i5) 0 else 1
        z = z / 26 * 25 * b1 + z / 26 // z/26
        z = z + (i5 + 8) * b1 // z/26
        x = z // ((p1 * 17576 + p2 * 676 + p3 * 26 + p4 + 97750)/26)
        val b2 = if (x % 26 - 11 == i6) 0 else 1
        z = z / 26 * 25 * b2 + z / 26 // (((p1 * 17576 + p2 * 676 + p3 * 26 + p4 + 97750)/26)/26)
        z += (i6 + 9) * b2
        x = z
        val b3 = if (x % 26 - 6 == i7) 0 else 1
        z = z / 26 * 25 * b3 + z / 26 // ((((p1 * 17576 + p2 * 676 + p3 * 26 + p4 + 97750)/26)/26)/26)
        z += (i7 + 2) * b3
        z = (z * 26 + i8 + 13) * 26 + i9 + 16
        x = z // ((((((p1 * 17576 + p2 * 676 + p3 * 26 + p4 + 97750)/26)/26)/26) * 26 + p8 + 13) * 26 + p9 + 16)
        val b4 = if (x % 26 - 10 == i10) 0 else 1
        z = z / 26 * 25 * b4 + z / 26
        z += (i10 + 6) * b4
        x = z // (((((((i1 * 17576 + i2 * 676 + i3 * 26 + i4 + 97750)/26)/26)/26) * 26 + i8 + 13) * 26 + i9 + 16)/26)
        val b5 = if (x % 26 - 8 == i11) 0 else 1
        z = z / 26 * 25 * b5 + z / 26
        z += (i11 + 6) * b5
        x = z // ((((((((i1 * 17576 + i2 * 676 + i3 * 26 + i4 + 97750)/26)/26)/26) * 26 + i8 + 13) * 26 + i9 + 16)/26)/26)
        val b6 = if (x % 26 - 11 == i12) 0 else 1
        z = z / 26 * 25 * b6 + z / 26
        z += (i12 + 9) * b6
        z = z * 26 + i13 + 11
        x = z // ((((((((((i1 * 17576 + i2 * 676 + i3 * 26 + i4 + 97750)/26)/26)/26) * 26 + i8 + 13) * 26 + i9 + 16)/26)/26)) * 26 + i13 + 11)
        val b7 = if (x % 26 - 15 == i14) 0 else 1
        z = z / 26 * 25 * b7 + z / 26
        z += (i14 + 5) * b7
        return z
    }


    val results = arrayListOf<String>()
    for (i1 in 1..9) for (i2 in 1..9) for (i3 in 1..9) for (i4 in 1..9) for (i5 in 1..9)
        if (i5 == (i1 * 17576 + i2 * 676 + i3 * 26 + i4 + 97750) % 26 - 16)
            for (i6 in 1..9) if (i6 == ((i1 * 17576 + i2 * 676 + i3 * 26 + i4 + 97750) / 26) % 26 - 11)
                for (i7 in 1..9) if ((((i1 * 17576 + i2 * 676 + i3 * 26 + i4 + 97750) / 26) / 26) % 26 - 6 == i7)
                    for (i8 in 1..9) for (i9 in 1..9) for (i10 in 1..9)
                        if (((((((i1 * 17576 + i2 * 676 + i3 * 26 + i4 + 97750) / 26) / 26) / 26) * 26 + i8 + 13) * 26 + i9 + 16) % 26 - 10 == i10)
                            for (i11 in 1..9)
                                if ((((((((i1 * 17576 + i2 * 676 + i3 * 26 + i4 + 97750) / 26) / 26) / 26) * 26 + i8 + 13) * 26 + i9 + 16) / 26) % 26 - 8 == i11)
                                    for (i12 in 1..9)
                                        if (((((((((i1 * 17576 + i2 * 676 + i3 * 26 + i4 + 97750) / 26) / 26) / 26) * 26 + i8 + 13) * 26 + i9 + 16) / 26) / 26) % 26 - 11 == i12)
                                            for (i13 in 1..9)
                                                for (i14 in 1..9)
                                                    if (run(i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12, i13, i14) == 0)
                                                        results.add("$i1$i2$i3$i4$i5$i6$i7$i8$i9$i10$i11$i12$i13$i14")
    println(results.last())
    println(results.first())
}



