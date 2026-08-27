package io.github.kotlinmania.serdeyaml.value

// port-lint: source value/de.rs

import io.github.kotlinmania.serdeyaml.Error
import io.github.kotlinmania.serdeyaml.Mapping
import io.github.kotlinmania.serdeyaml.Value
import io.github.kotlinmania.serdeyaml.Deserializer as TopDeserializer

public class Deserializer(
    public val value: Value,
) {
    public fun deserialize_any(): Value = value

    public fun deserialize_bool(): Boolean = value.asBool() ?: throw Error("expected bool")

    public fun deserialize_i8(): Byte = value.asI64()?.toByte() ?: throw Error("expected i8")

    public fun deserialize_i16(): Short = value.asI64()?.toShort() ?: throw Error("expected i16")

    public fun deserialize_i32(): Int = value.asI64()?.toInt() ?: throw Error("expected i32")

    public fun deserialize_i64(): Long = value.asI64() ?: throw Error("expected i64")

    public fun deserialize_u8(): UByte = value.asU64()?.toUByte() ?: throw Error("expected u8")

    public fun deserialize_u16(): UShort = value.asU64()?.toUShort() ?: throw Error("expected u16")

    public fun deserialize_u32(): UInt = value.asU64()?.toUInt() ?: throw Error("expected u32")

    public fun deserialize_u64(): ULong = value.asU64() ?: throw Error("expected u64")

    public fun deserialize_f32(): Float = value.asF64()?.toFloat() ?: throw Error("expected f32")

    public fun deserialize_f64(): Double = value.asF64() ?: throw Error("expected f64")

    public fun deserialize_char(): Char = value.asStr()?.singleOrNull() ?: throw Error("expected char")

    public fun deserialize_str(): String = value.asStr() ?: throw Error("expected str")

    public fun deserialize_string(): String = value.asStr() ?: throw Error("expected string")

    public fun deserialize_bytes(): ByteArray = throw Error("bytes unsupported")

    public fun deserialize_byte_buf(): ByteArray = throw Error("bytes unsupported")

    public fun deserialize_option(): Value? = if (value.isNull()) null else value

    public fun deserialize_unit() {
        if (!value.isNull()) throw Error("expected unit")
    }

    public fun deserialize_unit_struct(name: String): Unit = deserialize_unit()

    public fun deserialize_newtype_struct(name: String): Value = value

    public fun deserialize_seq(): SeqAccess =
        when (val v = value.untag()) {
            is Value.Sequence -> SeqAccess(v.sequence.iterator())
            else -> throw Error("expected sequence")
        }

    public fun deserialize_tuple(len: Int): SeqAccess = deserialize_seq()

    public fun deserialize_tuple_struct(name: String, len: Int): SeqAccess = deserialize_seq()

    public fun deserialize_map(): MapAccess =
        when (val v = value.untag()) {
            is Value.Mapping -> MapAccess(v.mapping.entries.iterator())
            else -> throw Error("expected mapping")
        }

    public fun deserialize_struct(name: String, fields: Array<String>): MapAccess = deserialize_map()

    public fun deserialize_enum(name: String, variants: Array<String>): EnumAccess = EnumAccess(value)

    public fun deserialize_ignored_any() {}
}

public class SeqAccess internal constructor(
    private val iter: Iterator<Value>,
) {
    public fun next_element_seed(): Value? = if (iter.hasNext()) iter.next() else null

    public fun size_hint(): Pair<Int, Int?> = Pair(0, null)
}

public class MapAccess internal constructor(
    private val iter: Iterator<Map.Entry<Value, Value>>,
) {
    private var currentEntry: Map.Entry<Value, Value>? = null

    public fun next_key_seed(): Value? {
        if (iter.hasNext()) {
            val entry = iter.next()
            currentEntry = entry
            return entry.key
        }
        return null
    }

    public fun next_value_seed(): Value {
        val entry = currentEntry ?: throw Error("next_value_seed called before next_key_seed")
        currentEntry = null
        return entry.value
    }

    public fun next_entry_seed(): Pair<Value, Value>? {
        if (iter.hasNext()) {
            val entry = iter.next()
            return Pair(entry.key, entry.value)
        }
        return null
    }

    public fun size_hint(): Pair<Int, Int?> = Pair(0, null)
}

public class EnumAccess(
    private val value: Value,
) {
    public fun variant_seed(): Pair<Value, VariantAccess> =
        when (value) {
            is Value.Str -> Pair(value, VariantAccess(Value.Null))
            is Value.Mapping -> {
                val entry = value.mapping.entries.firstOrNull() ?: throw Error("empty mapping for enum")
                Pair(entry.key, VariantAccess(entry.value))
            }
            is Value.Tagged -> Pair(Value.Str(value.tagged.tag.string), VariantAccess(value.tagged.value))
            else -> throw Error("expected enum variant")
        }
}

public class VariantAccess(
    private val payload: Value,
) {
    public fun unit_variant() {
        if (!payload.isNull()) throw Error("expected unit variant")
    }

    public fun newtype_variant_seed(): Value = payload

    public fun tuple_variant(len: Int): SeqAccess =
        when (val v = payload.untag()) {
            is Value.Sequence -> SeqAccess(v.sequence.iterator())
            else -> throw Error("expected sequence")
        }

    public fun struct_variant(fields: Array<String>): MapAccess =
        when (val v = payload.untag()) {
            is Value.Mapping -> MapAccess(v.mapping.entries.iterator())
            else -> throw Error("expected mapping")
        }
}

public class UnitOnly(
    public val value: Value,
)

public class Tagged(
    public val tag: Tag,
    public val value: Value,
)

public fun <T> from_value(value: Value): T =
    io.github.kotlinmania.serdeyaml
        .fromValue(value)

public fun <T> fromValue(value: Value): T =
    io.github.kotlinmania.serdeyaml
        .fromValue(value)

public fun fromStr(s: String): Value = TopDeserializer.fromStr(s).deserialize()

public fun fromSlice(v: ByteArray): Value = TopDeserializer.fromSlice(v).deserialize()
