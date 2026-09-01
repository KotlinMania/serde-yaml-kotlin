package io.github.kotlinmania.serdeyaml

// port-lint: source ser.rs

public class Indent(
    public var level: Int = 0,
)

public enum class State {
    Start,
    Sequence,
    Mapping,
    End,
}

public class SerializerMap(
    private val map: Mapping = Mapping(),
) {
    public fun serialize_entry(key: Value, value: Value) {
        map.insert(key, value)
    }

    public fun end(): Value = Value.Mapping(map)
}

public class SerializerStruct(
    private val map: Mapping = Mapping(),
) {
    public fun serialize_field(key: String, value: Value) {
        map.insert(Value.Str(key), value)
    }

    public fun end(): Value = Value.Mapping(map)
}

public class Serializer(
    private val out: StringBuilder = StringBuilder(),
) {
    public fun serialize(value: Value): String {
        formatValue(value, 0, false)
        if (!out.endsWith("\n")) {
            out.append("\n")
        }
        return out.toString()
    }

    private fun formatValue(value: Value, indent: Int, inSeq: Boolean) {
        when (value) {
            is Value.Null -> out.append("null")
            is Value.Bool -> out.append(value.value)
            is Value.Number -> out.append(value.number.toString())
            is Value.Str -> formatString(value.string)
            is Value.Sequence -> formatSequence(value.sequence, indent, inSeq)
            is Value.Mapping -> formatMapping(value.mapping, indent, inSeq)
            is Value.Tagged -> {
                out.append(value.tagged.tag.toString()).append(" ")
                formatValue(value.tagged.value, indent, inSeq)
            }
        }
    }

    private fun formatString(s: String) {
        if (s.isEmpty()) {
            out.append("''")
            return
        }
        if (s.contains("\n")) {
            out.append("|\n")
            val lines = s.lines()
            for (line in lines) {
                out.append("  ".repeat(1)).append(line).append("\n")
            }
            return
        }
        val needsQuotes =
            s == "true" ||
                s == "false" ||
                s == "null" ||
                s == "~" ||
                s.startsWith("@") ||
                s.startsWith("`") ||
                s.startsWith("%") ||
                s.contains(": ") ||
                s.endsWith(":") ||
                s.contains("#") ||
                s.startsWith("[") ||
                s.startsWith("{") ||
                s.startsWith("&") ||
                s.startsWith("*") ||
                s.startsWith("!") ||
                s.startsWith("|") ||
                s.startsWith(">") ||
                s.startsWith("'") ||
                s.startsWith("\"") ||
                s.startsWith("- ") ||
                s == "-" ||
                s.startsWith("?") ||
                s.toLongOrNull() != null ||
                s.toDoubleOrNull() != null

        if (needsQuotes) {
            val escaped =
                s
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t")
            out.append('"').append(escaped).append('"')
        } else {
            out.append(s)
        }
    }

    private fun formatSequence(seq: List<Value>, indent: Int, inSeq: Boolean) {
        if (seq.isEmpty()) {
            out.append("[]")
            return
        }
        for (i in seq.indices) {
            if (i > 0 || !inSeq) {
                if (i > 0 || out.isNotEmpty() && !out.endsWith("\n")) {
                    out.append("\n")
                }
                out.append("  ".repeat(indent))
            }
            out.append("- ")
            val elem = seq[i]
            formatValue(elem, indent + 1, true)
        }
    }

    private fun formatMapping(map: Mapping, indent: Int, inSeq: Boolean) {
        if (map.isEmpty()) {
            out.append("{}")
            return
        }
        var first = true
        for ((k, v) in map) {
            if (!first || !inSeq) {
                if (!first || (out.isNotEmpty() && !out.endsWith("\n"))) {
                    out.append("\n")
                }
                out.append("  ".repeat(indent))
            }
            formatValue(k, indent, false)
            out.append(": ")
            if (v is Value.Mapping || v is Value.Sequence) {
                out.append("\n").append("  ".repeat(indent + 1))
                formatValue(v, indent + 1, false)
            } else if (v is Value.Tagged && (v.tagged.value is Value.Mapping || v.tagged.value is Value.Sequence)) {
                out.append(v.tagged.tag.toString()).append("\n").append("  ".repeat(indent + 1))
                formatValue(v.tagged.value, indent + 1, false)
            } else {
                formatValue(v, indent + 1, false)
            }
            first = false
        }
    }

    public companion object {
        public fun toString(value: Value): String = Serializer().serialize(value)

        public fun toVec(value: Value): ByteArray = toString(value).encodeToByteArray()

        public fun toWriter(writer: Appendable, value: Value) {
            writer.append(toString(value))
        }

        public fun to_string(value: Value): String = toString(value)

        public fun to_vec(value: Value): ByteArray = toVec(value)

        public fun to_writer(writer: Appendable, value: Value): Unit = toWriter(writer, value)
    }
}

public fun toString(value: Value): String = Serializer.toString(value)

public fun toVec(value: Value): ByteArray = Serializer.toVec(value)

public fun toWriter(writer: Appendable, value: Value): Unit = Serializer.toWriter(writer, value)

public fun to_string(value: Value): String = toString(value)

public fun to_vec(value: Value): ByteArray = toVec(value)

public fun to_writer(writer: Appendable, value: Value): Unit = toWriter(writer, value)

public fun to_value(value: Any?): Value = toValue(value)

public fun toValue(value: Any?): Value =
    when (value) {
        null -> Value.Null
        is Value -> value
        is Boolean -> Value.Bool(value)
        is Byte -> Value.Number(Number.from(value))
        is Short -> Value.Number(Number.from(value))
        is Int -> Value.Number(Number.from(value))
        is Long -> Value.Number(Number.from(value))
        is UByte -> Value.Number(Number.from(value))
        is UShort -> Value.Number(Number.from(value))
        is UInt -> Value.Number(Number.from(value))
        is ULong -> Value.Number(Number.from(value))
        is Float -> Value.Number(Number.from(value))
        is Double -> Value.Number(Number.from(value))
        is String -> Value.Str(value)
        is List<*> -> Value.Sequence(value.map { toValue(it) })
        is Map<*, *> -> {
            val m = Mapping()
            for ((k, v) in value) {
                m[toValue(k)] = toValue(v)
            }
            Value.Mapping(m)
        }
        else -> Value.Str(value.toString())
    }
