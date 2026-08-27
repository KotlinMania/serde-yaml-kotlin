package io.github.kotlinmania.serdeyaml

// port-lint: source serde_yaml/src/number.rs

/**
 * Represents a YAML number, whether integer or floating point.
 */
public class Number internal constructor(internal val n: N) : Comparable<Number> {

    /**
     * Returns true if the `Number` is an integer between `Long.MIN_VALUE` and `Long.MAX_VALUE`.
     */
    public fun isI64(): Boolean = when (n) {
        is N.PosInt -> n.value <= Long.MAX_VALUE.toULong()
        is N.NegInt -> true
        is N.Float -> false
    }

    public fun is_i64(): Boolean = isI64()

    /**
     * Returns true if the `Number` is an integer between zero and `ULong.MAX_VALUE`.
     */
    public fun isU64(): Boolean = when (n) {
        is N.PosInt -> true
        is N.NegInt, is N.Float -> false
    }

    public fun is_u64(): Boolean = isU64()

    /**
     * Returns true if the `Number` can be represented by Double.
     */
    public fun isF64(): Boolean = when (n) {
        is N.Float -> true
        is N.PosInt, is N.NegInt -> false
    }

    public fun is_f64(): Boolean = isF64()

    /**
     * If the `Number` is an integer, represent it as Long if possible.
     */
    public fun asI64(): Long? = when (n) {
        is N.PosInt -> if (n.value <= Long.MAX_VALUE.toULong()) n.value.toLong() else null
        is N.NegInt -> n.value
        is N.Float -> null
    }

    public fun as_i64(): Long? = asI64()

    /**
     * If the `Number` is an integer, represent it as ULong if possible.
     */
    public fun asU64(): ULong? = when (n) {
        is N.PosInt -> n.value
        is N.NegInt, is N.Float -> null
    }

    public fun as_u64(): ULong? = asU64()

    /**
     * Represents the number as Double if possible.
     */
    public fun asF64(): Double? = when (n) {
        is N.PosInt -> n.value.toDouble()
        is N.NegInt -> n.value.toDouble()
        is N.Float -> n.value
    }

    public fun as_f64(): Double? = asF64()

    /**
     * Returns true if this value is NaN and false otherwise.
     */
    public fun isNan(): Boolean = when (n) {
        is N.PosInt, is N.NegInt -> false
        is N.Float -> n.value.isNaN()
    }

    public fun is_nan(): Boolean = isNan()

    /**
     * Returns true if this value is positive infinity or negative infinity and false otherwise.
     */
    public fun isInfinite(): Boolean = when (n) {
        is N.PosInt, is N.NegInt -> false
        is N.Float -> n.value.isInfinite()
    }

    public fun is_infinite(): Boolean = isInfinite()

    /**
     * Returns true if this number is neither infinite nor NaN.
     */
    public fun isFinite(): Boolean = when (n) {
        is N.PosInt, is N.NegInt -> true
        is N.Float -> !n.value.isNaN() && !n.value.isInfinite()
    }

    public fun is_finite(): Boolean = isFinite()

    public fun fmt(sb: StringBuilder) {
        sb.append(toString())
    }

    public fun eq(other: Number): Boolean = equals(other)

    public fun partial_cmp(other: Number): Int = totalCmp(other)

    public fun total_cmp(other: Number): Int = totalCmp(other)

    public fun hash(): Int = hashCode()

    override fun toString(): String = when (n) {
        is N.PosInt -> n.value.toString()
        is N.NegInt -> n.value.toString()
        is N.Float -> {
            val f = n.value
            if (f.isNaN()) {
                ".nan"
            } else if (f == Double.POSITIVE_INFINITY) {
                ".inf"
            } else if (f == Double.NEGATIVE_INFINITY) {
                "-.inf"
            } else {
                f.toString()
            }
        }
    }

    internal fun totalCmp(other: Number): Int = n.totalCmp(other.n)

    override fun compareTo(other: Number): Int = n.totalCmp(other.n)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Number) return false
        return n == other.n
    }

    override fun hashCode(): Int = when (n) {
        is N.Float -> 3
        is N.PosInt -> n.value.hashCode()
        is N.NegInt -> n.value.hashCode()
    }

    public companion object {
        public fun from(i: Byte): Number = from(i.toLong())
        public fun from(i: Short): Number = from(i.toLong())
        public fun from(i: Int): Number = from(i.toLong())
        public fun from(i: Long): Number = if (i < 0) Number(N.NegInt(i)) else Number(N.PosInt(i.toULong()))

        public fun from(u: UByte): Number = from(u.toULong())
        public fun from(u: UShort): Number = from(u.toULong())
        public fun from(u: UInt): Number = from(u.toULong())
        public fun from(u: ULong): Number = Number(N.PosInt(u))

        public fun from(f: Float): Number = from(f.toDouble())
        public fun from(f: Double): Number {
            val canonical = if (f.isNaN()) Double.NaN else f
            return Number(N.Float(canonical))
        }

        public fun from_str(repr: String): Number = fromStr(repr)
        public fun fromString(repr: String): Number = fromStr(repr)
        public fun fromStrOrNull(repr: String): Number? = try { fromStr(repr) } catch (_: Exception) { null }


        public fun fromStr(repr: String): Number {
            // Must not have leading/trailing whitespace in raw representation
            if (repr.isEmpty() || repr.startsWith(" ") || repr.endsWith(" ") || repr.startsWith("\t") || repr.endsWith("\t")) {
                throw newError(ErrorImpl.FailedToParseNumber)
            }
            val trimmed = repr
            if (trimmed == "null" || trimmed == "true" || trimmed == "false") {
                throw newError(ErrorImpl.FailedToParseNumber)
            }

            // Multiple signs are invalid numbers in YAML
            if (trimmed.startsWith("++") || trimmed.startsWith("+-") || trimmed.startsWith("-+") || trimmed.startsWith("--")) {
                throw newError(ErrorImpl.FailedToParseNumber)
            }

            // NaN
            if (trimmed.equals(".nan", ignoreCase = true)) {
                return Number(N.Float(Double.NaN))
            }
            // Inf
            if (trimmed.equals(".inf", ignoreCase = true) || trimmed.equals("+.inf", ignoreCase = true)) {
                return Number(N.Float(Double.POSITIVE_INFINITY))
            }
            if (trimmed.equals("-.inf", ignoreCase = true)) {
                return Number(N.Float(Double.NEGATIVE_INFINITY))
            }

            // Hex, Octal, Binary with optional +/-
            var sign = 1
            var numStr = trimmed
            if (numStr.startsWith("+")) {
                numStr = numStr.substring(1)
            } else if (numStr.startsWith("-")) {
                sign = -1
                numStr = numStr.substring(1)
            }

            if (numStr.startsWith("+") || numStr.startsWith("-")) {
                throw newError(ErrorImpl.FailedToParseNumber)
            }

            if (numStr.startsWith("0x") || numStr.startsWith("0X")) {
                val hex = numStr.substring(2)
                if (hex.isEmpty() || !hex.all { it in '0'..'9' || it in 'a'..'f' || it in 'A'..'F' }) {
                    throw newError(ErrorImpl.FailedToParseNumber)
                }
                val ul = hex.toULongOrNull(16) ?: throw newError(ErrorImpl.FailedToParseNumber)
                return if (sign == 1) Number(N.PosInt(ul)) else Number(N.NegInt(-(ul.toLong())))
            }
            if (numStr.startsWith("0o") || numStr.startsWith("0O")) {
                val oct = numStr.substring(2)
                if (oct.isEmpty() || !oct.all { it in '0'..'7' }) {
                    throw newError(ErrorImpl.FailedToParseNumber)
                }
                val ul = oct.toULongOrNull(8) ?: throw newError(ErrorImpl.FailedToParseNumber)
                return if (sign == 1) Number(N.PosInt(ul)) else Number(N.NegInt(-(ul.toLong())))
            }
            if (numStr.startsWith("0b") || numStr.startsWith("0B")) {
                val bin = numStr.substring(2)
                if (bin.isEmpty() || !bin.all { it in '0'..'1' }) {
                    throw newError(ErrorImpl.FailedToParseNumber)
                }
                val ul = bin.toULongOrNull(2) ?: throw newError(ErrorImpl.FailedToParseNumber)
                return if (sign == 1) Number(N.PosInt(ul)) else Number(N.NegInt(-(ul.toLong())))
            }

            // In YAML 1.2, a number with a leading zero followed by digits (e.g. 0127) is NOT a valid number.
            if (numStr.length > 1 && numStr.startsWith("0") && numStr[1].isDigit()) {
                throw newError(ErrorImpl.FailedToParseNumber)
            }

            // Integer decimal
            if (numStr.all { it in '0'..'9' }) {
                val ul = numStr.toULongOrNull()
                if (ul != null) {
                    return if (sign == 1) Number(N.PosInt(ul)) else Number(N.NegInt(-(ul.toLong())))
                }
            }

            // Floating point decimal
            if (numStr.contains('.') || numStr.contains('e') || numStr.contains('E')) {
                val d = trimmed.toDoubleOrNull()
                if (d != null) {
                    return Number(N.Float(d))
                }
            }

            throw newError(ErrorImpl.FailedToParseNumber)
        }

    }
}

public class NumberVisitor {
    public fun expecting(sb: StringBuilder) {
        sb.append("a YAML number")
    }

    public fun visit_i64(v: Long): Number = Number.from(v)
    public fun visit_u64(v: ULong): Number = Number.from(v)
    public fun visit_f64(v: Double): Number = Number.from(v)
}

internal sealed class N {
    data class PosInt(val value: ULong) : N()
    data class NegInt(val value: Long) : N()
    data class Float(val value: Double) : N()

    fun totalCmp(other: N): Int = when (this) {
        is PosInt -> when (other) {
            is PosInt -> value.compareTo(other.value)
            is NegInt -> 1
            is Float -> -1
        }
        is NegInt -> when (other) {
            is PosInt -> -1
            is NegInt -> value.compareTo(other.value)
            is Float -> -1
        }
        is Float -> when (other) {
            is PosInt -> 1
            is NegInt -> 1
            is Float -> {
                if (value.isNaN() && other.value.isNaN()) {
                    0
                } else if (value.isNaN()) {
                    1
                } else if (other.value.isNaN()) {
                    -1
                } else {
                    value.compareTo(other.value)
                }
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is N) return false
        return when (this) {
            is PosInt -> other is PosInt && value == other.value
            is NegInt -> other is NegInt && value == other.value
            is Float -> {
                if (other !is Float) false
                else if (value.isNaN() && other.value.isNaN()) true
                else value == other.value
            }
        }
    }

    override fun hashCode(): Int = when (this) {
        is PosInt -> value.hashCode()
        is NegInt -> value.hashCode()
        is Float -> if (value.isNaN()) 0 else value.hashCode()
    }
}

