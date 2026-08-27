package io.github.kotlinmania.serdeyaml.libyaml

// port-lint: source serde_yaml/src/libyaml/emitter.rs

public sealed class EmitterError {
    public data class Libyaml(val error: io.github.kotlinmania.serdeyaml.libyaml.Error) : EmitterError()
    public data class Io(val message: String) : EmitterError()
}

public class EmitterPinned(
    public val sys: Int = 0,
    public var writeError: String? = null,
) {
    public fun drop() {}
}

public enum class EmitterScalarStyle {
    AnyStyle,
    Plain,
    SingleQuoted,
    Literal,
}

public data class EmitterScalar(
    public var tag: String? = null,
    public val value: String,
    public val style: EmitterScalarStyle = EmitterScalarStyle.AnyStyle,
)

public data class EmitterSequence(
    public var tag: String? = null,
)

public data class EmitterMapping(
    public var tag: String? = null,
)

public sealed class EmitterEvent {
    public object StreamStart : EmitterEvent()
    public object StreamEnd : EmitterEvent()
    public object DocumentStart : EmitterEvent()
    public object DocumentEnd : EmitterEvent()
    public data class Scalar(public val scalar: EmitterScalar) : EmitterEvent()
    public data class SequenceStart(public val sequence: EmitterSequence) : EmitterEvent()
    public object SequenceEnd : EmitterEvent()
    public data class MappingStart(public val mapping: EmitterMapping) : EmitterEvent()
    public object MappingEnd : EmitterEvent()
}

/**
 * Libyaml emitter wrapper.
 */
public class Emitter(private val out: StringBuilder = StringBuilder()) {
    private var writeError: String? = null

    public fun emit(event: EmitterEvent) {
        when (event) {
            is EmitterEvent.StreamStart -> {}
            is EmitterEvent.StreamEnd -> {}
            is EmitterEvent.DocumentStart -> {}
            is EmitterEvent.DocumentEnd -> {}
            is EmitterEvent.Scalar -> {
                val tagStr = if (event.scalar.tag != null) "!${event.scalar.tag} " else ""
                out.append(tagStr).append(event.scalar.value)
            }
            is EmitterEvent.SequenceStart -> {
                val tagStr = if (event.sequence.tag != null) "!${event.sequence.tag} " else ""
                out.append(tagStr)
            }
            is EmitterEvent.SequenceEnd -> {}
            is EmitterEvent.MappingStart -> {
                val tagStr = if (event.mapping.tag != null) "!${event.mapping.tag} " else ""
                out.append(tagStr)
            }
            is EmitterEvent.MappingEnd -> {}
        }
    }

    public fun flush() {}

    public fun error(): EmitterError {
        return writeError?.let { EmitterError.Io(it) }
            ?: EmitterError.Libyaml(Error.emit_error("libyaml emitter error"))
    }

    public fun drop() {}

    public fun output(): String = out.toString()

    public companion object {
        public fun new(sb: StringBuilder): Emitter = Emitter(sb)

        public fun write_handler(data: Any?, buffer: ByteArray, size: ULong): Int {
            return 1
        }
    }
}
