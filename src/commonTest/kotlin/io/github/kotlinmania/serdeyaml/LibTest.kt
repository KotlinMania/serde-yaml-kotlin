// port-lint: tests serde_yaml/tests/test_serde.rs
package io.github.kotlinmania.serdeyaml

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LibTest {
    @Test
    fun testSerdeYamlVersion() {
        assertEquals("0.9.34", SerdeYaml.VERSION)
    }

    @Test
    fun testSerdeYamlRoundtrip() {
        val original = Value.Str("test_string")
        val serialized = SerdeYaml.toString(original)
        assertTrue(serialized.contains("test_string"))

        val deserialized = SerdeYaml.fromStr(serialized)
        assertEquals(original, deserialized)
    }
}
