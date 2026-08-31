package io.github.kotlinmania.serdeyaml.value

// port-lint: source serde_yaml/src/value/debug.rs

import io.github.kotlinmania.serdeyaml.Mapping
import io.github.kotlinmania.serdeyaml.Number
import io.github.kotlinmania.serdeyaml.Value

public fun fmt(v: Value, sb: StringBuilder) {
    sb.append(debugString(v))
}

public fun fmt(m: Mapping, sb: StringBuilder) {
    sb.append(debugString(m))
}

public fun fmt(n: Number, sb: StringBuilder) {
    sb.append(debugString(n))
}

public fun debugString(v: Value): String =
    when (v) {
        is Value.Null -> "Null"
        is Value.Bool -> "Bool(${v.value})"
        is Value.Number -> "Number(${v.number})"
        is Value.Str -> "String(\"${v.string}\")"
        is Value.Sequence -> "Sequence ${v.sequence.map { debugString(it) }}"
        is Value.Mapping -> debugString(v.mapping)
        is Value.Tagged -> "Tagged(${v.tagged})"
    }

public fun debugString(m: Mapping): String {
    val sb = StringBuilder("Mapping {")
    var first = true
    for ((k, v) in m) {
        if (!first) sb.append(", ")
        sb.append(debugString(k)).append(": ").append(debugString(v))
        first = false
    }
    sb.append("}")
    return sb.toString()
}

public fun debugString(n: Number): String = "Number($n)"
