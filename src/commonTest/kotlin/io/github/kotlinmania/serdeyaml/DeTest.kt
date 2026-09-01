package io.github.kotlinmania.serdeyaml

// port-lint: tests tests/test_de.rs

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DeTest {
    @Test
    fun testBorrowed() {
        val yaml =
            """
            - plain nonàscii
            - 'single quoted'
            - "double quoted"
            """.trimIndent()
        val value = fromStr(yaml)
        assertTrue(value.isSequence())
        val seq = value.asSequence()!!
        assertEquals(3, seq.size)
        assertEquals(Value.Str("plain nonàscii"), seq[0])
        assertEquals(Value.Str("single quoted"), seq[1])
        assertEquals(Value.Str("double quoted"), seq[2])
    }

    @Test
    fun testAlias() {
        val yaml =
            """
            first:
              &alias
              1
            second:
              *alias
            third: 3
            """.trimIndent()
        val value = fromStr(yaml)
        assertTrue(value.isMapping())
        val map = value.asMapping()!!
        assertEquals(Value.Number(Number.from(1)), map[Value.Str("first")])
        assertEquals(Value.Number(Number.from(1)), map[Value.Str("second")])
        assertEquals(Value.Number(Number.from(3)), map[Value.Str("third")])
    }

    @Test
    fun testOption() {
        val yaml =
            """
            b:
            c: true
            """.trimIndent()
        val value = fromStr(yaml)
        assertTrue(value.isMapping())
        val map = value.asMapping()!!
        assertEquals(Value.Null, map[Value.Str("b")])
        assertEquals(Value.Bool(true), map[Value.Str("c")])
    }

    @Test
    fun testOptionAlias() {
        val yaml =
            """
            none_f:
              &none_f
              ~
            none_s:
              &none_s
              ~
            none_b:
              &none_b
              ~

            some_f:
              &some_f
              1.0
            some_s:
              &some_s
              x
            some_b:
              &some_b
              true

            a: *none_f
            b: *none_s
            c: *none_b
            d: *some_f
            e: *some_s
            f: *some_b
            """.trimIndent()
        val value = fromStr(yaml)
        assertTrue(value.isMapping())
        val map = value.asMapping()!!
        assertEquals(Value.Null, map[Value.Str("a")])
        assertEquals(Value.Null, map[Value.Str("b")])
        assertEquals(Value.Null, map[Value.Str("c")])
        assertEquals(Value.Number(Number.from(1.0)), map[Value.Str("d")])
        assertEquals(Value.Str("x"), map[Value.Str("e")])
        assertEquals(Value.Bool(true), map[Value.Str("f")])
    }

    @Test
    fun testEnumAlias() {
        val yaml =
            """
            aref:
              &aref
              A
            bref:
              &bref
              !B
                - 1
                - 2

            a: *aref
            b: *bref
            """.trimIndent()
        val value = fromStr(yaml)
        assertTrue(value.isMapping())
        val map = value.asMapping()!!
        assertEquals(Value.Str("A"), map[Value.Str("a")])
        val bVal = map[Value.Str("b")]!!
        assertTrue(bVal.isTagged())
        val tagged = (bVal as Value.Tagged).tagged
        assertEquals("B", tagged.tag.string)
        val seq = tagged.value.asSequence()!!
        assertEquals(Value.Number(Number.from(1)), seq[0])
        assertEquals(Value.Number(Number.from(2)), seq[1])
    }

    @Test
    fun testEnumRepresentations() {
        val yaml =
            """
            - Unit
            - 'Unit'
            - !Unit
            - !Unit ~
            - !Unit null
            - !Tuple [0, 0]
            - !Tuple
              - 0
              - 0
            - !Struct {x: 0, y: 0}
            - !Struct
              x: 0
              y: 0
            - !String '...'
            - !String ...
            - !Number 0
            """.trimIndent()
        val value = fromStr(yaml)
        assertTrue(value.isSequence())
        val seq = value.asSequence()!!
        assertEquals(12, seq.size)
    }

    @Test
    fun testNumberAsString() {
        val yaml =
            """
            value: 340282366920938463463374607431768211457
            """.trimIndent()
        val value = fromStr(yaml)
        assertTrue(value.isMapping())
    }

    @Test
    fun testEmptyString() {
        val yaml =
            """
            empty:
            tilde: ~
            """.trimIndent()
        val value = fromStr(yaml)
        assertTrue(value.isMapping())
        val map = value.asMapping()!!
        assertEquals(Value.Null, map[Value.Str("empty")])
        assertEquals(Value.Null, map[Value.Str("tilde")])
    }

    @Test
    fun testNumberAliasAsString() {
        val yaml =
            """
            version: &a 1.10
            value: *a
            """.trimIndent()
        val value = fromStr(yaml)
        assertTrue(value.isMapping())
        val map = value.asMapping()!!
        assertEquals(map[Value.Str("version")], map[Value.Str("value")])
    }

    @Test
    fun testDeMapping() {
        val yaml =
            """
            substructure:
              a: 'foo'
              b: 'bar'
            """.trimIndent()
        val value = fromStr(yaml)
        assertTrue(value.isMapping())
        val map = value.asMapping()!!
        val sub = map[Value.Str("substructure")]!!.asMapping()!!
        assertEquals(Value.Str("foo"), sub[Value.Str("a")])
        assertEquals(Value.Str("bar"), sub[Value.Str("b")])
    }

    @Test
    fun testByteOrderMark() {
        val yaml = "\uFEFF- 0\n"
        val value = fromStr(yaml)
        assertTrue(value.isSequence())
        val seq = value.asSequence()!!
        assertEquals(Value.Number(Number.from(0)), seq[0])
    }

    @Test
    fun testNumbers() {
        val cases =
            listOf(
                "0xF0" to "240",
                "+0xF0" to "240",
                "-0xF0" to "-240",
                "0o70" to "56",
                "+0o70" to "56",
                "-0o70" to "-56",
                "0b10" to "2",
                "+0b10" to "2",
                "-0b10" to "-2",
                "127" to "127",
                "+127" to "127",
                "-127" to "-127",
                ".inf" to ".inf",
                ".Inf" to ".inf",
                ".INF" to ".inf",
                "-.inf" to "-.inf",
                "-.Inf" to "-.inf",
                "-.INF" to "-.inf",
                ".nan" to ".nan",
                ".NaN" to ".nan",
                ".NAN" to ".nan",
                "0.1" to "0.1",
            )
        for ((input, expected) in cases) {
            val value = fromStr(input)
            assertTrue(value.isNumber(), "expected number for $input")
            val num = (value as Value.Number).number
            assertEquals(expected, num.toString())
        }

        val nonNumbers =
            listOf(
                "0127",
                "+0127",
                "-0127",
                "++.inf",
                "+-.inf",
                "++1",
                "+-1",
                "-+1",
                "--1",
                "0x+1",
                "0x-1",
                "-0x+1",
                "-0x-1",
                "++0x1",
                "+-0x1",
                "-+0x1",
                "--0x1",
            )
        for (input in nonNumbers) {
            val value = fromStr(input)
            assertTrue(value.isString(), "expected string for $input")
            assertEquals(input, (value as Value.Str).string)
        }
    }

    @Test
    fun testNan() {
        val f32 = fromStr(".nan").asF64()!!
        assertFalse(f32 < 0)
        val f64 = fromStr(".nan").asF64()!!
        assertFalse(f64 < 0)
    }

    @Test
    fun testNoRequiredFields() {
        for (doc in listOf("", "# comment\n")) {
            val value = fromStr(doc)
            assertEquals(Value.Null, value)
        }
    }

    @Test
    fun testEmptyScalar() {
        val yaml = "thing:\n"
        val value = fromStr(yaml)
        assertTrue(value.isMapping())
        val map = value.asMapping()!!
        assertEquals(Value.Null, map[Value.Str("thing")])
    }

    @Test
    fun testPythonSafeDump() {
        val yaml = "\"foo\": !!int |-\n    7200\n"
        val value = fromStr(yaml)
        assertTrue(value.isMapping())
        val map = value.asMapping()!!
        assertEquals(Value.Number(Number.from(7200)), map[Value.Str("foo")])
    }

    @Test
    fun testTagResolution() {
        val yaml =
            """
            - null
            - Null
            - NULL
            - ~
            -
            - true
            - True
            - TRUE
            - false
            - False
            - FALSE
            - y
            - Y
            - yes
            - Yes
            - YES
            - n
            - N
            - no
            - No
            - NO
            - on
            - On
            - ON
            - off
            - Off
            - OFF
            """.trimIndent()
        val value = fromStr(yaml)
        assertTrue(value.isSequence())
        val seq = value.asSequence()!!
        assertEquals(27, seq.size)
        assertEquals(Value.Null, seq[0])
        assertEquals(Value.Null, seq[1])
        assertEquals(Value.Null, seq[2])
        assertEquals(Value.Null, seq[3])
        assertEquals(Value.Null, seq[4])
        assertEquals(Value.Bool(true), seq[5])
        assertEquals(Value.Bool(true), seq[6])
        assertEquals(Value.Bool(true), seq[7])
        assertEquals(Value.Bool(false), seq[8])
        assertEquals(Value.Bool(false), seq[9])
        assertEquals(Value.Bool(false), seq[10])
        assertEquals(Value.Str("y"), seq[11])
        assertEquals(Value.Str("Y"), seq[12])
        assertEquals(Value.Str("yes"), seq[13])
        assertEquals(Value.Str("Yes"), seq[14])
        assertEquals(Value.Str("YES"), seq[15])
        assertEquals(Value.Str("n"), seq[16])
        assertEquals(Value.Str("N"), seq[17])
        assertEquals(Value.Str("no"), seq[18])
        assertEquals(Value.Str("No"), seq[19])
        assertEquals(Value.Str("NO"), seq[20])
        assertEquals(Value.Str("on"), seq[21])
        assertEquals(Value.Str("On"), seq[22])
        assertEquals(Value.Str("ON"), seq[23])
        assertEquals(Value.Str("off"), seq[24])
        assertEquals(Value.Str("Off"), seq[25])
        assertEquals(Value.Str("OFF"), seq[26])
    }

    @Test
    fun testParseNumber() {
        val n1 = Number.fromStr("111")
        assertEquals(Number.from(111), n1)

        val n2 = Number.fromStr("-111")
        assertEquals(Number.from(-111), n2)

        val n3 = Number.fromStr("-1.1")
        assertEquals(Number.from(-1.1), n3)

        val n4 = Number.fromStr(".nan")
        assertTrue(n4.isNan())

        val n5 = Number.fromStr(".inf")
        assertEquals(Number.from(Double.POSITIVE_INFINITY), n5)

        val n6 = Number.fromStr("-.inf")
        assertEquals(Number.from(Double.NEGATIVE_INFINITY), n6)

        assertFails { Number.fromStr("null") }
        assertFails { Number.fromStr(" 1 ") }
    }
}
