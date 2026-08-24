package io.github.kotlinmania.serdeyaml

// port-lint: source value/mod.rs

import io.github.kotlinmania.serdeyaml.value.Index
import io.github.kotlinmania.serdeyaml.value.IntIndex
import io.github.kotlinmania.serdeyaml.value.StringIndex
import io.github.kotlinmania.serdeyaml.value.TaggedValue
import io.github.kotlinmania.serdeyaml.value.ValueIndex

/**
 * Represents any valid YAML value.
 */
public sealed class Value : Comparable<Value> {
    public object Null : Value() {
        override fun toString(): kotlin.String = "null"
    }

    public data class Bool(val value: Boolean) : Value() {
        override fun toString(): kotlin.String = value.toString()
    }

    public data class Number(val number: io.github.kotlinmania.serdeyaml.Number) : Value() {
        public constructor(value: Long) : this(io.github.kotlinmania.serdeyaml.Number.from(value))
        public constructor(value: Double) : this(io.github.kotlinmania.serdeyaml.Number.from(value))

        override fun toString(): kotlin.String = number.toString()
    }

    public data class Str(val string: kotlin.String) : Value() {
        override fun toString(): kotlin.String = string
    }

    public data class Sequence(val sequence: List<Value>) : Value() {
        public constructor() : this(emptyList())

        override fun toString(): kotlin.String = sequence.toString()
    }

    public data class Mapping(val mapping: io.github.kotlinmania.serdeyaml.Mapping) : Value() {
        public constructor() : this(io.github.kotlinmania.serdeyaml.Mapping())

        override fun toString(): kotlin.String = mapping.toString()
    }

    public data class Tagged(val tagged: TaggedValue) : Value() {
        override fun toString(): kotlin.String = tagged.toString()
    }

    public fun isNull(): Boolean = untag() is Null
    public fun isBool(): Boolean = untag() is Bool
    public fun isNumber(): Boolean = untag() is Number
    public fun isI64(): Boolean = when (val u = untag()) {
        is Number -> u.number.isI64()
        else -> false
    }
    public fun isU64(): Boolean = when (val u = untag()) {
        is Number -> u.number.isU64()
        else -> false
    }
    public fun isF64(): Boolean = when (val u = untag()) {
        is Number -> u.number.isF64()
        else -> false
    }
    public fun isStr(): Boolean = untag() is Str
    public fun isString(): Boolean = untag() is Str
    public fun isSequence(): Boolean = untag() is Sequence
    public fun isMapping(): Boolean = untag() is Mapping
    public fun isTagged(): Boolean = this is Tagged

    public fun is_null(): Boolean = isNull()
    public fun is_bool(): Boolean = isBool()
    public fun is_number(): Boolean = isNumber()
    public fun is_i64(): Boolean = isI64()
    public fun is_u64(): Boolean = isU64()
    public fun is_f64(): Boolean = isF64()
    public fun is_str(): Boolean = isStr()
    public fun is_string(): Boolean = isString()
    public fun is_sequence(): Boolean = isSequence()
    public fun is_mapping(): Boolean = isMapping()
    public fun is_tagged(): Boolean = isTagged()

    public fun asBool(): Boolean? = when (val u = untag()) {
        is Bool -> u.value
        else -> null
    }

    public fun asI64(): Long? = when (val u = untag()) {
        is Number -> u.number.asI64()
        else -> null
    }

    public fun asU64(): ULong? = when (val u = untag()) {
        is Number -> u.number.asU64()
        else -> null
    }

    public fun asF64(): Double? = when (val u = untag()) {
        is Number -> u.number.asF64()
        else -> null
    }

    public fun asStr(): kotlin.String? = when (val u = untag()) {
        is Str -> u.string
        else -> null
    }

    public fun asString(): kotlin.String? = asStr()

    public fun asSequence(): List<Value>? = when (val u = untag()) {
        is Sequence -> u.sequence
        else -> null
    }

    public fun asSequenceMut(): List<Value>? = asSequence()

    public fun asMapping(): io.github.kotlinmania.serdeyaml.Mapping? = when (val u = untag()) {
        is Mapping -> u.mapping
        else -> null
    }

    public fun asMappingMut(): io.github.kotlinmania.serdeyaml.Mapping? = asMapping()

    public fun asTagged(): TaggedValue? = when (this) {
        is Tagged -> this.tagged
        else -> null
    }

    public fun asTaggedMut(): TaggedValue? = asTagged()

    public fun as_null(): Unit? = if (isNull()) Unit else null

    public fun as_bool(): Boolean? = asBool()
    public fun as_i64(): Long? = asI64()
    public fun as_u64(): ULong? = asU64()
    public fun as_f64(): Double? = asF64()
    public fun as_str(): kotlin.String? = asStr()
    public fun as_string(): kotlin.String? = asString()
    public fun as_sequence(): List<Value>? = asSequence()
    public fun as_sequence_mut(): List<Value>? = asSequenceMut()
    public fun as_mapping(): io.github.kotlinmania.serdeyaml.Mapping? = asMapping()
    public fun as_mapping_mut(): io.github.kotlinmania.serdeyaml.Mapping? = asMappingMut()
    public fun as_tagged(): TaggedValue? = asTagged()
    public fun as_tagged_mut(): TaggedValue? = asTaggedMut()

    public fun get_mut(index: Index): Value? = get(index)
    public fun get_mut(index: Int): Value? = get(index)
    public fun get_mut(key: kotlin.String): Value? = get(key)
    public fun get_mut(key: Value): Value? = get(key)

    public fun into_deserializer(): io.github.kotlinmania.serdeyaml.value.Deserializer = io.github.kotlinmania.serdeyaml.value.Deserializer(this)

    public fun hash(): Int = hashCode()

    public fun untag(): Value {
        var cur = this
        while (cur is Tagged) {
            cur = cur.tagged.value
        }
        return cur
    }

    public fun untagRef(): Value = untag()
    public fun untagMut(): Value = untag()
    public fun untag_ref(): Value = untag()
    public fun untag_mut(): Value = untag()

    public fun apply_merge(): Unit = applyMerge()

    public fun applyMerge() {
        val stack = ArrayDeque<Value>()
        stack.addFirst(this)
        while (stack.isNotEmpty()) {
            val node = stack.removeFirst()
            when (node) {
                is Mapping -> {
                    val mapping = node.mapping
                    val mergeVal = mapping.remove(Str("<<"))
                    when (mergeVal) {
                        is Mapping -> {
                            for ((k, v) in mergeVal.mapping) {
                                if (!mapping.containsKey(k)) {
                                    mapping.put(k, v)
                                }
                            }
                        }
                        is Sequence -> {
                            for (item in mergeVal.sequence) {
                                when (item) {
                                    is Mapping -> {
                                        for ((k, v) in item.mapping) {
                                            if (!mapping.containsKey(k)) {
                                                mapping.put(k, v)
                                            }
                                        }
                                    }
                                    is Sequence -> throw Error.new(ErrorImpl.SequenceInMergeElement)
                                    is Tagged -> throw Error.new(ErrorImpl.TaggedInMerge)
                                    else -> throw Error.new(ErrorImpl.ScalarInMergeElement)
                                }
                            }
                        }
                        null -> {}
                        is Tagged -> throw Error.new(ErrorImpl.TaggedInMerge)
                        else -> throw Error.new(ErrorImpl.ScalarInMerge)
                    }
                    for (v in mapping.values) {
                        stack.addFirst(v)
                    }
                }
                is Sequence -> {
                    for (item in node.sequence) {
                        stack.addFirst(item)
                    }
                }
                is Tagged -> {
                    stack.addFirst(node.tagged.value)
                }
                else -> {}
            }
        }
    }

    public operator fun get(index: Int): Value? = IntIndex(index).indexInto(this)
    public operator fun get(key: kotlin.String): Value? = StringIndex(key).indexInto(this)
    public operator fun get(key: Value): Value? = ValueIndex(key).indexInto(this)
    public operator fun get(index: Index): Value? = index.indexInto(this)

    override fun compareTo(other: Value): Int = totalCmp(this, other)

    public companion object {
        public fun default(): Value = Null

        public fun from(value: Boolean): Value = Bool(value)
        public fun from(value: Long): Value = Number(value)
        public fun from(value: Int): Value = Number(value.toLong())
        public fun from(value: Double): Value = Number(value)
        public fun from(value: kotlin.String): Value = Value.Str(value)
        public fun from(value: io.github.kotlinmania.serdeyaml.Number): Value = Number(value)
        public fun from(value: io.github.kotlinmania.serdeyaml.Mapping): Value = Mapping(value)
        public fun from(value: List<Value>): Value = Sequence(value)
        public fun from(value: TaggedValue): Value = Tagged(value)


        public fun to_value(value: Any?): Value = io.github.kotlinmania.serdeyaml.toValue(value)
        public fun <T> from_value(value: Value): T = io.github.kotlinmania.serdeyaml.fromValue(value)

        public fun total_cmp(a: Value, b: Value): Int = totalCmp(a, b)

        public fun totalCmp(a: Value, b: Value): Int = when (a) {
            is Null -> when (b) {
                is Null -> 0
                else -> -1
            }
            is Bool -> when (b) {
                is Null -> 1
                is Bool -> a.value.compareTo(b.value)
                else -> -1
            }
            is Number -> when (b) {
                is Null, is Bool -> 1
                is Number -> a.number.compareTo(b.number)
                else -> -1
            }
            is Str -> when (b) {
                is Null, is Bool, is Number -> 1
                is Str -> a.string.compareTo(b.string)
                else -> -1
            }
            is Sequence -> when (b) {
                is Null, is Bool, is Number, is Str -> 1
                is Sequence -> {
                    val itA = a.sequence.iterator()
                    val itB = b.sequence.iterator()
                    while (itA.hasNext() && itB.hasNext()) {
                        val cmp = totalCmp(itA.next(), itB.next())
                        if (cmp != 0) return cmp
                    }
                    if (itA.hasNext()) 1 else if (itB.hasNext()) -1 else 0
                }
                else -> -1
            }
            is Mapping -> when (b) {
                is Null, is Bool, is Number, is Str, is Sequence -> 1
                is Mapping -> a.mapping.compareTo(b.mapping)
                else -> -1
            }
            is Tagged -> when (b) {
                is Tagged -> a.tagged.compareTo(b.tagged)
                else -> 1
            }
        }
    }
}
