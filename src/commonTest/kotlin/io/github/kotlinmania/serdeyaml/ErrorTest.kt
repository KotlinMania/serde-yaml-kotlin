package io.github.kotlinmania.serdeyaml

// port-lint: tests serde_yaml/src/test_error.rs

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ErrorTest {
    @Test
    fun testTwoDocumentsError() {
        val yaml =
            """
            ---
            0
            ---
            1
            """.trimIndent()
        assertFailsWith<Error> {
            fromStr(yaml)
        }
    }

    @Test
    fun testDuplicateKeys() {
        val yaml =
            """
            ---
            thing: true
            thing: false
            """.trimIndent()
        val value = fromStr(yaml)
        assertEquals(Value.Bool(false), value[Value.Str("thing")])
    }

    @Test
    fun testScanError() {
        val yaml = ">\n@"
        assertFailsWith<Error> {
            fromStr(yaml)
        }
    }

    @Test
    fun testEmpty() {
        val value = fromStr("")
        assertEquals(Value.Null, value)
    }

    @Test
    fun testUnknownAnchor() {
        val yaml =
            """
            ---
            *some
            """.trimIndent()
        assertFailsWith<Error> {
            fromStr(yaml)
        }
    }

    @Test
    fun testSecondDocumentSyntaxError() {
        val yaml =
            """
            ---
            0
            ---
            ]
            """.trimIndent()
        val de = Deserializer.from_str(yaml).iterator()
        val first = de.next()
        assertEquals(Value.Number(Number.from(0)), first)
        assertFailsWith<Error> {
            de.next()
        }
    }
}
