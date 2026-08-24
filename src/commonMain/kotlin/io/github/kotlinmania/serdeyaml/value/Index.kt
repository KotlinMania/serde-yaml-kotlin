package io.github.kotlinmania.serdeyaml.value

// port-lint: source value/index.rs

import io.github.kotlinmania.serdeyaml.Mapping
import io.github.kotlinmania.serdeyaml.Number
import io.github.kotlinmania.serdeyaml.Value

/**
 * A type that can be used to index into a `serde_yaml::Value`.
 */
public interface Index {
    public fun indexInto(v: Value): Value?
    public fun isKeyInto(v: Value): Boolean

    public fun index_into(v: Value): Value? = indexInto(v)
    public fun index_into_mut(v: Value): Value? = indexInto(v)
    public fun index_or_insert(v: Value): Value = indexInto(v) ?: Value.Null
}

public class IntIndex(public val index: Int) : Index {
    override fun indexInto(v: Value): Value? = when (val u = v.untag()) {
        is Value.Sequence -> if (index in 0 until u.sequence.size) u.sequence[index] else null
        is Value.Mapping -> u.mapping.get(Value.Number(Number.from(index)))
        else -> null
    }

    override fun isKeyInto(v: Value): Boolean = when (val u = v.untag()) {
        is Value.Sequence -> index in 0 until u.sequence.size
        is Value.Mapping -> u.mapping.containsKey(Value.Number(Number.from(index)))
        else -> false
    }
}

public class StringIndex(public val index: String) : Index {
    override fun indexInto(v: Value): Value? = when (val u = v.untag()) {
        is Value.Mapping -> u.mapping.get(index)
        else -> null
    }

    override fun isKeyInto(v: Value): Boolean = when (val u = v.untag()) {
        is Value.Mapping -> u.mapping.containsKey(index)
        else -> false
    }
}

public class ValueIndex(public val index: Value) : Index {
    override fun indexInto(v: Value): Value? = when (val u = v.untag()) {
        is Value.Mapping -> u.mapping[index]
        else -> null
    }

    override fun isKeyInto(v: Value): Boolean = when (val u = v.untag()) {
        is Value.Mapping -> u.mapping.containsKey(index)
        else -> false
    }
}

public fun index_into_mapping(index: Value, v: Value): Value? = when (val u = v.untagRef()) {
    is Value.Mapping -> u.mapping.get(index)
    else -> null
}

public fun index_into_mut_mapping(index: Value, v: Value): Value? = index_into_mapping(index, v)

public fun index_or_insert_mapping(index: Value, v: Value): Value = when (val u = v.untagMut()) {
    is Value.Mapping -> u.mapping.getOrPut(index) { Value.Null }
    else -> Value.Null
}

public fun index(v: Value, index: Index): Value = index.index_into(v) ?: Value.Null
public fun index_mut(v: Value, index: Index): Value = index.index_or_insert(v)

internal class Type(val value: Value) {
    fun fmt(sb: StringBuilder) {
        when (value) {
            is Value.Null -> sb.append("null")
            is Value.Bool -> sb.append("boolean")
            is Value.Number -> sb.append("number")
            is Value.Str -> sb.append("string")
            is Value.Sequence -> sb.append("sequence")
            is Value.Mapping -> sb.append("mapping")
            is Value.Tagged -> sb.append("tagged")
        }
    }

    override fun toString(): String {
        val sb = StringBuilder()
        fmt(sb)
        return sb.toString()
    }
}

