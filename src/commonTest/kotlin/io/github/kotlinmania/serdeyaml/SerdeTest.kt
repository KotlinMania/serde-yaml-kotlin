package io.github.kotlinmania.serdeyaml

// port-lint: tests tests/test_serde.rs

import io.github.kotlinmania.serdeyaml.value.Tag
import io.github.kotlinmania.serdeyaml.value.TaggedValue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SerdeTest {
    @Test
    fun testDefault() {
        assertEquals(Value.Null, Value.Null)
    }

    @Test
    fun testInt() {
        val num = Value.Number(Number.from(256))
        val yaml = toString(num)
        assertEquals("256\n", yaml)
        assertEquals(num, fromStr(yaml))
    }

    @Test
    fun testIntMaxU64() {
        val num = Value.Number(Number.from(ULong.MAX_VALUE))
        val yaml = toString(num)
        assertEquals("18446744073709551615\n", yaml)
        assertEquals(num, fromStr(yaml))
    }

    @Test
    fun testIntMinI64() {
        val num = Value.Number(Number.from(Long.MIN_VALUE))
        val yaml = toString(num)
        assertEquals("-9223372036854775808\n", yaml)
        assertEquals(num, fromStr(yaml))
    }

    @Test
    fun testIntMaxI64() {
        val num = Value.Number(Number.from(Long.MAX_VALUE))
        val yaml = toString(num)
        assertEquals("9223372036854775807\n", yaml)
        assertEquals(num, fromStr(yaml))
    }

    @Test
    fun testFloat() {
        val num = Value.Number(Number.from(25.6))
        val yaml = toString(num)
        assertEquals("25.6\n", yaml)
        assertEquals(num, fromStr(yaml))

        val inf = Value.Number(Number.from(Double.POSITIVE_INFINITY))
        val yamlInf = toString(inf)
        assertEquals(".inf\n", yamlInf)
        assertEquals(inf, fromStr(yamlInf))

        val negInf = Value.Number(Number.from(Double.NEGATIVE_INFINITY))
        val yamlNegInf = toString(negInf)
        assertEquals("-.inf\n", yamlNegInf)
        assertEquals(negInf, fromStr(yamlNegInf))

        val nan = Value.Number(Number.from(Double.NaN))
        val yamlNan = toString(nan)
        assertEquals(".nan\n", yamlNan)
        val deNan = fromStr(yamlNan)
        assertTrue(deNan.isF64())
        assertTrue((deNan as Value.Number).number.isNan())
    }

    @Test
    fun testVec() {
        val thing =
            Value.Sequence(
                mutableListOf(
                    Value.Number(Number.from(1)),
                    Value.Number(Number.from(2)),
                    Value.Number(Number.from(3)),
                ),
            )
        val yaml = toString(thing)
        assertEquals("- 1\n- 2\n- 3\n", yaml)
        assertEquals(thing, fromStr(yaml))
    }

    @Test
    fun testMap() {
        val map = Mapping()
        map[Value.Str("x")] = Value.Number(Number.from(1))
        map[Value.Str("y")] = Value.Number(Number.from(2))
        val thing = Value.Mapping(map)
        val yaml = toString(thing)
        assertEquals("x: 1\ny: 2\n", yaml)
        assertEquals(thing, fromStr(yaml))
    }

    @Test
    fun testMapping() {
        val map = Mapping()
        map[Value.Str("k")] = Value.Number(Number.from(107))
        val valMap = Value.Mapping(map)
        val yaml = toString(valMap)
        assertEquals("k: 107\n", yaml)
        assertEquals(valMap, fromStr(yaml))
    }

    @Test
    fun testSequence() {
        val seq = Value.Sequence(mutableListOf(Value.Str("a"), Value.Str("b")))
        val yaml = toString(seq)
        assertEquals("- a\n- b\n", yaml)
        assertEquals(seq, fromStr(yaml))
    }

    @Test
    fun testTaggedMapValue() {
        val map = Mapping()
        map[Value.Str("class_name")] = Value.Str("ApplicationConfig")
        val tagged = Value.Tagged(TaggedValue(Tag("ClassValidator"), Value.Mapping(map)))
        val outerMap = Mapping()
        outerMap[Value.Str("profile")] = tagged
        val valMap = Value.Mapping(outerMap)
        val yaml = toString(valMap)
        assertEquals(valMap, fromStr(yaml))
    }

    @Test
    fun testValue() {
        val seq =
            mutableListOf(
                Value.Null,
                Value.Bool(true),
                Value.Number(Number.from(65535)),
                Value.Number(Number.from(0.54321)),
                Value.Str("s"),
                Value.Mapping(Mapping()),
            )
        val map = Mapping()
        map[Value.Str("type")] = Value.Str("primary")
        map[Value.Str("config")] = Value.Sequence(seq)
        val thing = Value.Mapping(map)
        val yaml = toString(thing)
        assertEquals(thing, fromStr(yaml))
    }
}
