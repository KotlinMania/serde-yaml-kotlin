package io.github.kotlinmania.serdeyaml.value

// port-lint: source value/tagged.rs

import io.github.kotlinmania.serdeyaml.Value

/**
 * A representation of YAML's `!Tag` syntax, used for enums.
 */
public class Tag(
    public val string: String,
) : Comparable<Tag> {
    init {
        require(string.isNotEmpty()) { "empty YAML tag is not allowed" }
    }

    public fun fmt(sb: StringBuilder) {
        sb.append(toString())
    }

    public fun eq(other: Tag): Boolean = equals(other)

    public fun partial_cmp(other: Tag): Int = compareTo(other)

    public fun cmp(other: Tag): Int = compareTo(other)

    public fun hash(): Int = hashCode()

    override fun compareTo(other: Tag): Int = nobang(string).compareTo(nobang(other.string))

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other is String) return nobang(string) == nobang(other)
        if (other !is Tag) return false
        return nobang(string) == nobang(other.string)
    }

    override fun hashCode(): Int = nobang(string).hashCode()

    override fun toString(): String = "!${nobang(string)}"

    public companion object {
        public fun new(string: String): Tag = Tag(string)
    }
}

/**
 * A `Tag` + `Value` representing a tagged YAML scalar, sequence, or mapping.
 */
public data class TaggedValue(
    public var tag: Tag,
    public var value: Value,
) : Comparable<TaggedValue> {
    public fun fmt(sb: StringBuilder) {
        sb.append(toString())
    }

    public fun eq(other: TaggedValue): Boolean = equals(other)

    public fun partial_cmp(other: TaggedValue): Int = compareTo(other)

    public fun cmp(other: TaggedValue): Int = compareTo(other)

    public fun hash(): Int = hashCode()

    override fun compareTo(other: TaggedValue): Int {
        val cmp = tag.compareTo(other.tag)
        if (cmp != 0) return cmp
        return value.compareTo(other.value)
    }

    override fun toString(): String = "$tag $value"
}

public fun nobang(maybeBanged: String): String =
    if (maybeBanged.startsWith("!")) {
        maybeBanged.substring(1)
    } else {
        maybeBanged
    }

public sealed class MaybeTag<out T> {
    public data class Tag(
        val tag: String,
    ) : MaybeTag<Nothing>()

    public data class NotTag<T>(
        val value: T,
    ) : MaybeTag<T>()
}

public fun checkForTag(value: String): MaybeTag<String> = check_for_tag(value)

public fun check_for_tag(value: String): MaybeTag<String> =
    if (value.startsWith("!") && value.length > 1) {
        MaybeTag.Tag(value.substring(1))
    } else {
        MaybeTag.NotTag(value)
    }

public class TagVisitor {
    public fun expecting(sb: StringBuilder) {
        sb.append("a YAML tag")
    }

    public fun visit_str(v: String): Tag = Tag.new(v)
}

public class TaggedValueVisitor {
    public fun expecting(sb: StringBuilder) {
        sb.append("a YAML tagged value")
    }
}

public class TaggedMapVisitor {
    public fun expecting(sb: StringBuilder) {
        sb.append("a YAML tagged map")
    }
}
