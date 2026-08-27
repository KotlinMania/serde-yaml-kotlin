package io.github.kotlinmania.serdeyaml.libyaml

// port-lint: source libyaml/error.rs

public typealias Result<T> = kotlin.Result<T>

/**
 * Libyaml error representation.
 */
public class Error(
    public val kind: Int = 0,
    public val problem: CStr = CStr.fromString("libyaml error"),
    public val problemOffset: ULong = 0u,
    public val problemMark: Mark = Mark(0u, 0u, 0u),
    public val context: CStr? = null,
    public val contextMark: Mark = Mark(0u, 0u, 0u),
) {
    public fun mark(): Mark = problemMark

    public fun fmt(sb: StringBuilder) {
        sb.append(problem.toString())
        if (problemMark.line != 0uL || problemMark.column != 0uL) {
            sb.append(" at ").append(problemMark.toString())
        } else if (problemOffset != 0uL) {
            sb.append(" at position ").append(problemOffset)
        }
        if (context != null) {
            sb.append(", ").append(context.toString())
            if ((contextMark.line != 0uL || contextMark.column != 0uL) &&
                (contextMark.line != problemMark.line || contextMark.column != problemMark.column)
            ) {
                sb.append(" at ").append(contextMark.toString())
            }
        }
    }

    override fun toString(): String {
        val sb = StringBuilder()
        fmt(sb)
        return sb.toString()
    }

    public companion object {
        public fun parse_error(
            problem: String,
            mark: Mark = Mark(0u, 0u, 0u),
            context: String? = null,
            contextMark: Mark = Mark(0u, 0u, 0u),
        ): Error = parseError(problem, mark, context, contextMark)

        public fun parseError(
            problem: String,
            mark: Mark = Mark(0u, 0u, 0u),
            context: String? = null,
            contextMark: Mark = Mark(0u, 0u, 0u),
        ): Error =
            Error(
                kind = 1,
                problem = CStr.fromString(problem),
                problemOffset = mark.index,
                problemMark = mark,
                context = context?.let { CStr.fromString(it) },
                contextMark = contextMark,
            )

        public fun emit_error(problem: String): Error = emitError(problem)

        public fun emitError(problem: String): Error =
            Error(
                kind = 2,
                problem = CStr.fromString(problem),
                problemOffset = 0u,
                problemMark = Mark(0u, 0u, 0u),
                context = null,
                contextMark = Mark(0u, 0u, 0u),
            )
    }
}

public data class Mark(
    public val index: ULong = 0u,
    public val line: ULong = 0u,
    public val column: ULong = 0u,
) {
    public fun fmt(sb: StringBuilder) {
        if (line != 0uL || column != 0uL) {
            sb
                .append("line ")
                .append(line + 1u)
                .append(" column ")
                .append(column + 1u)
        } else {
            sb.append("position ").append(index)
        }
    }

    override fun toString(): String {
        val sb = StringBuilder()
        fmt(sb)
        return sb.toString()
    }
}
