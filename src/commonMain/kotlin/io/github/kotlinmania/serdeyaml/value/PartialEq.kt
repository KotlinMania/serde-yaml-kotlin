package io.github.kotlinmania.serdeyaml.value

// port-lint: source serde_yaml/src/value/partial_eq.rs

import io.github.kotlinmania.serdeyaml.Value

public fun Value.eq(other: String): Boolean = asStr() == other
public fun Value.eq(other: Boolean): Boolean = asBool() == other
public fun Value.eq(other: Long): Boolean = asI64() == other
public fun Value.eq(other: Int): Boolean = asI64() == other.toLong()
public fun Value.eq(other: ULong): Boolean = asU64() == other
public fun Value.eq(other: Double): Boolean = asF64() == other

public fun Value.equalsString(other: String): Boolean = eq(other)
public fun Value.equalsBool(other: Boolean): Boolean = eq(other)
public fun Value.equalsLong(other: Long): Boolean = eq(other)
public fun Value.equalsULong(other: ULong): Boolean = eq(other)
public fun Value.equalsDouble(other: Double): Boolean = eq(other)

