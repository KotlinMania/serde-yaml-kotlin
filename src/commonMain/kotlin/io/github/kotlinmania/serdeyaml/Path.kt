package io.github.kotlinmania.serdeyaml

// port-lint: source path.rs

public typealias Parent = Path

/**
 * Path to the current value in the input, like `dependencies.serde.typo1`.
 */
public sealed class Path {
    public object Root : Path() {
        override fun toString(): String = "."
    }

    public data class Seq(val parent: Path, val index: Long) : Path() {
        override fun toString(): String = "${parent}[$index]"
    }

    public data class Map(val parent: Path, val key: String) : Path() {
        override fun toString(): String {
            val prefix = if (parent is Root) "" else "$parent."
            return "$prefix$key"
        }
    }

    public data class Alias(val parent: Path) : Path() {
        override fun toString(): String = parent.toString()
    }

    public data class Unknown(val parent: Path) : Path() {
        override fun toString(): String {
            val prefix = if (parent is Root) "" else "$parent."
            return "$prefix?"
        }
    }

    public fun fmt(sb: StringBuilder) {
        sb.append(toString())
    }
}

