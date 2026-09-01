package io.github.kotlinmania.serdeyaml.libyaml

// port-lint: source libyaml/tag.rs

/**
 * YAML tag representation.
 */
public class Tag(
    public val utf8: ByteArray,
) : Comparable<Tag> {
    public val bytes: ByteArray
        get() = utf8

    public constructor(string: String) : this(string.encodeToByteArray())

    public fun startsWith(prefix: ByteArray): Boolean {
        if (utf8.size < prefix.size) return false
        for (i in prefix.indices) {
            if (utf8[i] != prefix[i]) return false
        }
        return true
    }

    public fun startsWith(prefix: String): Boolean = startsWith(prefix.encodeToByteArray())

    public fun eq(other: Tag): Boolean = utf8.contentEquals(other.utf8)

    public fun eq(other: ByteArray): Boolean = utf8.contentEquals(other)

    public fun eq(other: String): Boolean = utf8.contentEquals(other.encodeToByteArray())

    public fun deref(): ByteArray = utf8

    public fun fmt(sb: StringBuilder) {
        sb.append(utf8.decodeToString())
    }

    public fun asString(): String = utf8.decodeToString()

    override fun compareTo(other: Tag): Int {
        val minLen = minOf(utf8.size, other.utf8.size)
        for (i in 0 until minLen) {
            val a = utf8[i].toInt() and 0xFF
            val b = other.utf8[i].toInt() and 0xFF
            if (a != b) return a.compareTo(b)
        }
        return utf8.size.compareTo(other.utf8.size)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other is String) return asString() == other
        if (other !is Tag) return false
        return utf8.contentEquals(other.utf8)
    }

    override fun hashCode(): Int = utf8.contentHashCode()

    override fun toString(): String = asString()

    public companion object {
        public val NULL: Tag = Tag("tag:yaml.org,2002:null")
        public val BOOL: Tag = Tag("tag:yaml.org,2002:bool")
        public val INT: Tag = Tag("tag:yaml.org,2002:int")
        public val FLOAT: Tag = Tag("tag:yaml.org,2002:float")
    }
}
