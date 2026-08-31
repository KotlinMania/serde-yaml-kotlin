package io.github.kotlinmania.serdeyaml.value

// port-lint: source serde_yaml/src/value/from.rs

import io.github.kotlinmania.serdeyaml.Mapping
import io.github.kotlinmania.serdeyaml.Number
import io.github.kotlinmania.serdeyaml.Value

public fun Value.Companion.from(b: Boolean): Value = Value.Bool(b)

public fun Value.Companion.from(s: String): Value = Value.Str(s)

public fun Value.Companion.from(n: Number): Value = Value.Number(n)

public fun Value.Companion.from(i: Byte): Value = Value.Number(Number.from(i))

public fun Value.Companion.from(i: Short): Value = Value.Number(Number.from(i))

public fun Value.Companion.from(i: Int): Value = Value.Number(Number.from(i))

public fun Value.Companion.from(i: Long): Value = Value.Number(Number.from(i))

public fun Value.Companion.from(u: UByte): Value = Value.Number(Number.from(u))

public fun Value.Companion.from(u: UShort): Value = Value.Number(Number.from(u))

public fun Value.Companion.from(u: UInt): Value = Value.Number(Number.from(u))

public fun Value.Companion.from(u: ULong): Value = Value.Number(Number.from(u))

public fun Value.Companion.from(f: Float): Value = Value.Number(Number.from(f))

public fun Value.Companion.from(f: Double): Value = Value.Number(Number.from(f))

public fun Value.Companion.from(m: Mapping): Value = Value.Mapping(m)

public fun Value.Companion.from(seq: List<Value>): Value = Value.Sequence(seq)

public fun Value.Companion.from(t: TaggedValue): Value = Value.Tagged(t)

public fun Value.Companion.from_iter(iter: Iterable<Value>): Value = Value.Sequence(iter.toList())
