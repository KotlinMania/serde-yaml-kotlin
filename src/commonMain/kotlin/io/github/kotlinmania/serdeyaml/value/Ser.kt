package io.github.kotlinmania.serdeyaml.value

// port-lint: source serde_yaml/src/value/ser.rs

import io.github.kotlinmania.serdeyaml.Error
import io.github.kotlinmania.serdeyaml.Mapping
import io.github.kotlinmania.serdeyaml.Number
import io.github.kotlinmania.serdeyaml.Value
import io.github.kotlinmania.serdeyaml.toValue

public class Serializer {
    public fun serialize_bool(v: Boolean): Value = Value.Bool(v)
    public fun serialize_i8(v: Byte): Value = Value.Number(Number.from(v))
    public fun serialize_i16(v: Short): Value = Value.Number(Number.from(v))
    public fun serialize_i32(v: Int): Value = Value.Number(Number.from(v))
    public fun serialize_i64(v: Long): Value = Value.Number(Number.from(v))
    public fun serialize_u8(v: UByte): Value = Value.Number(Number.from(v))
    public fun serialize_u16(v: UShort): Value = Value.Number(Number.from(v))
    public fun serialize_u32(v: UInt): Value = Value.Number(Number.from(v))
    public fun serialize_u64(v: ULong): Value = Value.Number(Number.from(v))
    public fun serialize_f32(v: Float): Value = Value.Number(Number.from(v))
    public fun serialize_f64(v: Double): Value = Value.Number(Number.from(v))
    public fun serialize_char(v: Char): Value = Value.Str(v.toString())
    public fun serialize_str(v: String): Value = Value.Str(v)
    public fun serialize_bytes(v: ByteArray): Value = throw Error("bytes unsupported")
    public fun serialize_none(): Value = Value.Null
    public fun serialize_some(v: Value): Value = v
    public fun serialize_unit(): Value = Value.Null
    public fun serialize_unit_struct(name: String): Value = Value.Null
    public fun serialize_unit_variant(name: String, variantIndex: UInt, variant: String): Value = Value.Str(variant)
    public fun serialize_newtype_struct(name: String, value: Value): Value = value
    public fun serialize_newtype_variant(name: String, variantIndex: UInt, variant: String, value: Value): Value {
        val map = Mapping()
        map.insert(Value.Str(variant), value)
        return Value.Mapping(map)
    }
    public fun serialize_seq(len: Int?): SerializeSeq = SerializeSeq()
    public fun serialize_tuple(len: Int): SerializeTuple = SerializeTuple()
    public fun serialize_tuple_struct(name: String, len: Int): SerializeTupleStruct = SerializeTupleStruct()
    public fun serialize_tuple_variant(name: String, variantIndex: UInt, variant: String, len: Int): SerializeTupleVariant = SerializeTupleVariant(variant)
    public fun serialize_map(len: Int?): SerializeMap = SerializeMap()
    public fun serialize_struct(name: String, len: Int): SerializeStruct = SerializeStruct()
    public fun serialize_struct_variant(name: String, variantIndex: UInt, variant: String, len: Int): SerializeStructVariant = SerializeStructVariant(variant)
}

public class SerializeSeq {
    private val vec = mutableListOf<Value>()
    public fun serialize_element(value: Value) { vec.add(value) }
    public fun end(): Value = Value.Sequence(vec)
}

public class SerializeTuple {
    private val vec = mutableListOf<Value>()
    public fun serialize_element(value: Value) { vec.add(value) }
    public fun end(): Value = Value.Sequence(vec)
}

public class SerializeTupleStruct {
    private val vec = mutableListOf<Value>()
    public fun serialize_field(value: Value) { vec.add(value) }
    public fun end(): Value = Value.Sequence(vec)
}

public class SerializeTupleVariant(private val variant: String) {
    private val vec = mutableListOf<Value>()
    public fun serialize_field(value: Value) { vec.add(value) }
    public fun end(): Value {
        val map = Mapping()
        map.insert(Value.Str(variant), Value.Sequence(vec))
        return Value.Mapping(map)
    }
}

public class SerializeMap {
    private val map = Mapping()
    private var nextKey: Value? = null
    public fun serialize_key(key: Value) { nextKey = key }
    public fun serialize_value(value: Value) {
        val k = nextKey ?: error("serialize_value called before serialize_key")
        map.insert(k, value)
        nextKey = null
    }
    public fun serialize_entry(key: Value, value: Value) {
        map.insert(key, value)
    }
    public fun end(): Value = Value.Mapping(map)
}

public class SerializeStruct {
    private val map = Mapping()
    public fun serialize_field(key: String, value: Value) {
        map.insert(Value.Str(key), value)
    }
    public fun end(): Value = Value.Mapping(map)
}

public class SerializeStructVariant(private val variant: String) {
    private val map = Mapping()
    public fun serialize_field(key: String, value: Value) {
        map.insert(Value.Str(key), value)
    }
    public fun end(): Value {
        val outer = Mapping()
        outer.insert(Value.Str(variant), Value.Mapping(map))
        return Value.Mapping(outer)
    }
}

public fun toValue(value: Any?): Value = io.github.kotlinmania.serdeyaml.toValue(value)
