package io.github.kotlinmania.serdeyaml.libyaml

// port-lint: source serde_yaml/src/libyaml/cstr.rs

/**
 * C string and lossy display/debug formatting.
 */
public class CStr(
    public val bytes: ByteArray,
) {
    public constructor(string: String) : this(string.encodeToByteArray())

    public fun len(): Int = bytes.size

    public fun isEmpty(): Boolean = bytes.isEmpty()

    public fun toBytes(): ByteArray = bytes

    public fun to_bytes(): ByteArray = bytes

    public fun fmt(sb: StringBuilder) {
        display_lossy(bytes, sb)
    }

    override fun toString(): String {
        val sb = StringBuilder()
        display_lossy(bytes, sb)
        return sb.toString()
    }

    public companion object {
        public fun from_bytes_with_nul(bytes: ByteArray): CStr = fromBytesWithNul(bytes)

        public fun fromBytesWithNul(bytes: ByteArray): CStr {
            val len = if (bytes.isNotEmpty() && bytes.last() == 0.toByte()) bytes.size - 1 else bytes.size
            return CStr(bytes.copyOf(len))
        }

        public fun from_ptr(bytes: ByteArray): CStr = CStr(bytes)

        public fun fromString(string: String): CStr = CStr(string)

        public fun display_lossy(bytes: ByteArray, sb: StringBuilder) {
            sb.append(bytes.decodeToString())
        }

        public fun displayLossy(bytes: ByteArray): String {
            val sb = StringBuilder()
            display_lossy(bytes, sb)
            return sb.toString()
        }

        public fun debug_lossy(bytes: ByteArray, sb: StringBuilder) {
            sb.append('"')
            val str = bytes.decodeToString()
            for (ch in str) {
                when (ch) {
                    '\\' -> sb.append("\\\\")
                    '"' -> sb.append("\\\"")
                    '\n' -> sb.append("\\n")
                    '\r' -> sb.append("\\r")
                    '\t' -> sb.append("\\t")
                    else -> {
                        if (ch.code in 32..126) {
                            sb.append(ch)
                        } else {
                            val code = ch.code
                            if (code < 256) {
                                sb.append("\\x").append(code.toString(16).padStart(2, '0'))
                            } else {
                                sb.append("\\u").append(code.toString(16).padStart(4, '0'))
                            }
                        }
                    }
                }
            }
            sb.append('"')
        }

        public fun debugLossy(bytes: ByteArray): String {
            val sb = StringBuilder()
            debug_lossy(bytes, sb)
            return sb.toString()
        }
    }
}

public typealias Cstr = CStr
