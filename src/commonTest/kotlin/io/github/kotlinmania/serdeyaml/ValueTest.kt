package io.github.kotlinmania.serdeyaml

// port-lint: tests serde_yaml/tests/test_value.rs

import io.github.kotlinmania.serdeyaml.value.Tag
import io.github.kotlinmania.serdeyaml.value.TaggedValue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ValueTest {

    @Test
    fun testNan() {
        val posNan = fromStr(".nan")
        assertTrue(posNan.isF64())
        assertEquals(posNan, posNan)

        val negFakeNan = fromStr("-.nan")
        assertTrue(negFakeNan.isString())

        val diffPosNan = Value.Number(Number.from(Double.NaN))
        assertEquals(posNan, diffPosNan)
    }

    @Test
    fun testDigits() {
        val numString = fromStr("01")
        assertTrue(numString.isString())
    }

    @Test
    fun testMerge() {
        val yaml = """
            ---
            - &CENTER { x: 1, y: 2 }
            - &LEFT { x: 0, y: 2 }
            - &BIG { r: 10 }
            - &SMALL { r: 1 }

            # All the following maps are equal:

            - # Explicit keys
              x: 1
              y: 2
              r: 10
              label: center/big

            - # Merge one map
              << : *CENTER
              r: 10
              label: center/big

            - # Merge multiple maps
              << : [ *CENTER, *BIG ]
              label: center/big

            - # Override
              << : [ *BIG, *LEFT, *SMALL ]
              x: 1
              label: center/big
        """.trimIndent()

        val value: Value = fromStr(yaml)
        val seq = value.asSequence()!!
        val explicit = seq[4]
        for (i in 5..7) {
            assertEquals(explicit, seq[i])
        }
    }

    @Test
    fun testDebug() {
        val yaml = """
            'Null': ~
            Bool: true
            Number: 1
            String: ...
            Sequence:
              - true
            EmptySequence: []
            EmptyMapping: {}
        """.trimIndent()

        val value: Value = fromStr(yaml)
        assertTrue(value.isMapping())
        val map = value.asMapping()!!
        assertEquals(Value.Null, map[Value.Str("Null")])
        assertEquals(Value.Bool(true), map[Value.Str("Bool")])
        assertEquals(Value.Number(Number.from(1)), map[Value.Str("Number")])
        assertEquals(Value.Str("..."), map[Value.Str("String")])
        assertEquals(Value.Sequence(mutableListOf(Value.Bool(true))), map[Value.Str("Sequence")])
        assertEquals(Value.Sequence(mutableListOf()), map[Value.Str("EmptySequence")])
        assertEquals(Value.Mapping(Mapping()), map[Value.Str("EmptyMapping")])
    }

    @Test
    fun testIntoDeserializer() {
        val value = fromStr("xyz")
        val de = value.into_deserializer()
        assertEquals("xyz", de.deserialize_str())

        val valueSeq = fromStr("- first\n- second\n- third")
        val deSeq = valueSeq.into_deserializer().deserialize_seq()
        assertEquals(Value.Str("first"), deSeq.next_element_seed())
        assertEquals(Value.Str("second"), deSeq.next_element_seed())
        assertEquals(Value.Str("third"), deSeq.next_element_seed())

    }

    @Test
    fun testTagged() {
        val tagged = Value.Tagged(TaggedValue(Tag("Variant"), Value.Number(Number.from(0))))
        val str = toString(tagged)
        assertEquals("!Variant 0\n", str)

        val deserialized = fromStr(str)
        assertEquals(tagged, deserialized)
    }
}
