package io.github.kotlinmania.serdeyaml

// port-lint: source error.rs

import io.github.kotlinmania.serdeyaml.libyaml.Mark
import io.github.kotlinmania.serdeyaml.libyaml.Error as LibyamlError

public typealias Result<T> = kotlin.Result<T>
public typealias MessageNoMark = String

/**
 * An error that happened serializing or deserializing YAML data.
 */
public class Error internal constructor(
    internal val inner: ErrorImpl,
) : Exception(inner.displayString()) {
    public constructor(message: String) : this(ErrorImpl.Message(message, null))

    /**
     * Returns the Location from the error if one exists.
     */
    public fun location(): Location? = inner.location()

    public fun source(): Throwable? =
        when (inner) {
            is ErrorImpl.Io -> inner.cause
            is ErrorImpl.FromUtf8 -> inner.cause
            else -> null
        }

    public fun fmt(sb: StringBuilder) {
        sb.append(inner.displayString())
    }

    public fun display(sb: StringBuilder) {
        sb.append(inner.displayString())
    }

    public fun debug(sb: StringBuilder) {
        sb.append(inner.debugString())
    }

    internal fun shared(): ErrorImpl = if (inner is ErrorImpl.Shared) inner.err else inner

    override fun toString(): String = inner.displayString()

    public companion object {
        public fun custom(msg: String): Error = Error(ErrorImpl.Message(msg, null))

        public fun new(inner: ErrorImpl): Error = Error(inner)

        public fun from(cause: Throwable): Error = Error(ErrorImpl.Io(cause))

        public fun shared(inner: ErrorImpl): Error = Error(ErrorImpl.Shared(inner))
    }
}

/**
 * The input location that an error occurred.
 */
public data class Location(
    private val _index: Long,
    private val _line: Long,
    private val _column: Long,
) {
    /**
     * The byte index of the error
     */
    public fun index(): Long = _index

    /**
     * The line of the error
     */
    public fun line(): Long = _line

    /**
     * The column of the error
     */
    public fun column(): Long = _column

    internal companion object {
        internal fun fromMark(mark: Mark): Location =
            Location(
                _index = mark.index.toLong(),
                _line = mark.line.toLong() + 1L,
                _column = mark.column.toLong() + 1L,
            )
    }
}

public sealed class ErrorImpl {
    public data class Message(
        public val msg: String,
        public var pos: Pos? = null,
    ) : ErrorImpl()

    public data class Libyaml(
        public val err: LibyamlError,
    ) : ErrorImpl()

    public data class Io(
        public val cause: Throwable,
    ) : ErrorImpl()

    public data class FromUtf8(
        public val cause: Throwable,
    ) : ErrorImpl()

    public object EndOfStream : ErrorImpl()

    public object MoreThanOneDocument : ErrorImpl()

    public data class RecursionLimitExceeded(
        public val mark: Mark,
    ) : ErrorImpl()

    public object RepetitionLimitExceeded : ErrorImpl()

    public object BytesUnsupported : ErrorImpl()

    public data class UnknownAnchor(
        public val mark: Mark,
    ) : ErrorImpl()

    public object SerializeNestedEnum : ErrorImpl()

    public object ScalarInMerge : ErrorImpl()

    public object TaggedInMerge : ErrorImpl()

    public object ScalarInMergeElement : ErrorImpl()

    public object SequenceInMergeElement : ErrorImpl()

    public object EmptyTag : ErrorImpl()

    public object FailedToParseNumber : ErrorImpl()

    public data class Shared(
        public val err: ErrorImpl,
    ) : ErrorImpl()

    public fun location(): Location? = mark()?.let { Location.fromMark(it) }

    public fun mark(): Mark? =
        when (this) {
            is Message -> pos?.mark
            is RecursionLimitExceeded -> mark
            is UnknownAnchor -> mark
            is Libyaml -> err.mark()
            is Shared -> err.mark()
            else -> null
        }

    public fun messageNoMark(): String =
        when (this) {
            is Message -> {
                val p = pos
                if (p != null && p.path != ".") {
                    "${p.path}: $msg"
                } else {
                    msg
                }
            }
            is Libyaml -> err.toString()
            is Io -> cause.message ?: "I/O error"
            is FromUtf8 -> cause.message ?: "UTF-8 error"
            is EndOfStream -> "EOF while parsing a value"
            is MoreThanOneDocument -> "deserializing from YAML containing more than one document is not supported"
            is RecursionLimitExceeded -> "recursion limit exceeded"
            is RepetitionLimitExceeded -> "repetition limit exceeded"
            is BytesUnsupported -> "serialization and deserialization of bytes in YAML is not implemented"
            is UnknownAnchor -> "unknown anchor"
            is SerializeNestedEnum -> "serializing nested enums in YAML is not supported yet"
            is ScalarInMerge -> "expected a mapping or list of mappings for merging, but found scalar"
            is TaggedInMerge -> "unexpected tagged value in merge"
            is ScalarInMergeElement -> "expected a mapping for merging, but found scalar"
            is SequenceInMergeElement -> "expected a mapping for merging, but found sequence"
            is EmptyTag -> "empty YAML tag is not allowed"
            is FailedToParseNumber -> "failed to parse YAML number"
            is Shared -> err.messageNoMark()
        }

    public fun displayString(): String =
        when (this) {
            is Libyaml -> err.toString()
            is Shared -> err.displayString()
            else -> {
                val msg = messageNoMark()
                val m = mark()
                if (m != null && (m.line != 0uL || m.column != 0uL)) {
                    "$msg at $m"
                } else {
                    msg
                }
            }
        }

    public fun debugString(): String =
        when (this) {
            is Libyaml -> err.toString()
            is Shared -> err.debugString()
            else -> {
                val msg = messageNoMark()
                val m = mark()
                if (m != null) {
                    "Error(\"$msg\", line: ${m.line + 1u}, column: ${m.column + 1u})"
                } else {
                    "Error(\"$msg\")"
                }
            }
        }
}

public data class Pos(
    public val mark: Mark,
    public val path: String,
)

public fun newError(inner: ErrorImpl): Error = Error(inner)

public fun sharedError(shared: ErrorImpl): Error = Error(ErrorImpl.Shared(shared))

public fun fixMark(error: Error, mark: Mark, path: Path): Error {
    val inner = error.inner
    if (inner is ErrorImpl.Message && inner.pos == null) {
        inner.pos = Pos(mark, path.toString())
    }
    return error
}
