package io.github.kotlinmania.serdeyaml

// port-lint: source lib.rs

public typealias Sequence = List<Value>

/**
 * Top-level SerdeYaml module and version constants.
 */
public object SerdeYaml {
    public const val VERSION: String = "0.9.34"

    public fun fromStr(yaml: String): Value = io.github.kotlinmania.serdeyaml.fromStr(yaml)
    public fun toString(value: Value): String = io.github.kotlinmania.serdeyaml.toString(value)
    public fun toValue(value: Any?): Value = io.github.kotlinmania.serdeyaml.toValue(value)
}
