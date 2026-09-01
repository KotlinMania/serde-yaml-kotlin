package io.github.kotlinmania.serdeyaml.libyaml

// port-lint: source libyaml/parser.rs

public enum class ScalarStyle {
    AnyStyle,
    Plain,
    SingleQuoted,
    DoubleQuoted,
    Literal,
    Folded,
}

public class LossySlice(
    public val bytes: ByteArray,
) {
    public fun fmt(sb: StringBuilder) {
        CStr.debug_lossy(bytes, sb)
    }

    override fun toString(): String {
        val sb = StringBuilder()
        fmt(sb)
        return sb.toString()
    }
}

public sealed class Event {
    public object StreamStart : Event()

    public object StreamEnd : Event()

    public object DocumentStart : Event()

    public object DocumentEnd : Event()

    public data class Alias(
        public val anchor: Anchor,
    ) : Event()

    public data class Scalar(
        public val scalar: io.github.kotlinmania.serdeyaml.libyaml.Scalar,
    ) : Event()

    public data class SequenceStart(
        public val sequenceStart: io.github.kotlinmania.serdeyaml.libyaml.SequenceStart,
    ) : Event()

    public object SequenceEnd : Event()

    public data class MappingStart(
        public val mappingStart: io.github.kotlinmania.serdeyaml.libyaml.MappingStart,
    ) : Event()

    public object MappingEnd : Event()
}

public class Scalar(
    public var anchor: Anchor? = null,
    public var tag: Tag? = null,
    public val value: ByteArray = ByteArray(0),
    public val style: ScalarStyle = ScalarStyle.Plain,
    public val repr: ByteArray? = null,
) {
    public constructor(
        stringValue: String,
        style: ScalarStyle = ScalarStyle.Plain,
        tag: Tag? = null,
        anchor: Anchor? = null,
    ) : this(
        anchor = anchor,
        tag = tag,
        value = stringValue.encodeToByteArray(),
        style = style,
        repr = null,
    )

    public fun asString(): String = value.decodeToString()

    public fun valueString(): String = value.decodeToString()

    public fun fmt(sb: StringBuilder) {
        sb
            .append("Scalar(anchor=")
            .append(anchor)
            .append(", tag=")
            .append(tag)
            .append(", value=")
            .append(LossySlice(value))
            .append(", style=")
            .append(style)
            .append(")")
    }

    override fun toString(): String {
        val sb = StringBuilder()
        fmt(sb)
        return sb.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Scalar) return false
        return anchor == other.anchor &&
            tag == other.tag &&
            value.contentEquals(other.value) &&
            style == other.style
    }

    override fun hashCode(): Int {
        var result = anchor?.hashCode() ?: 0
        result = 31 * result + (tag?.hashCode() ?: 0)
        result = 31 * result + value.fold(1) { acc, b -> 31 * acc + b.toInt() }
        result = 31 * result + style.hashCode()
        return result
    }
}

public data class SequenceStart(
    public var anchor: Anchor? = null,
    public var tag: Tag? = null,
)

public data class MappingStart(
    public var anchor: Anchor? = null,
    public var tag: Tag? = null,
)

public class Anchor(
    public val bytes: ByteArray,
) : Comparable<Anchor> {
    public constructor(string: String) : this(string.encodeToByteArray())

    public fun asString(): String = bytes.decodeToString()

    public fun fmt(sb: StringBuilder) {
        CStr.debug_lossy(bytes, sb)
    }

    override fun compareTo(other: Anchor): Int {
        val minLen = minOf(bytes.size, other.bytes.size)
        for (i in 0 until minLen) {
            val a = bytes[i].toInt() and 0xFF
            val b = other.bytes[i].toInt() and 0xFF
            if (a != b) return a.compareTo(b)
        }
        return bytes.size.compareTo(other.bytes.size)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other is String) return asString() == other
        if (other !is Anchor) return false
        return bytes.contentEquals(other.bytes)
    }

    override fun hashCode(): Int = bytes.fold(1) { acc, b -> 31 * acc + b.toInt() }

    override fun toString(): String = asString()
}

public sealed class YamlToken {
    public abstract val mark: Mark

    public data class DocStart(
        override val mark: Mark,
    ) : YamlToken()

    public data class DocEnd(
        override val mark: Mark,
    ) : YamlToken()

    public data class AnchorTok(
        val name: String,
        override val mark: Mark,
    ) : YamlToken()

    public data class AliasTok(
        val name: String,
        override val mark: Mark,
    ) : YamlToken()

    public data class TagTok(
        val name: String,
        override val mark: Mark,
    ) : YamlToken()

    public data class ScalarTok(
        val value: String,
        val style: ScalarStyle,
        val raw: String? = null,
        override val mark: Mark,
    ) : YamlToken()

    public data class SeqStartTok(
        override val mark: Mark,
    ) : YamlToken()

    public data class SeqEndTok(
        override val mark: Mark,
    ) : YamlToken()

    public data class MapStartTok(
        override val mark: Mark,
    ) : YamlToken()

    public data class MapEndTok(
        override val mark: Mark,
    ) : YamlToken()
}

public class YamlTokenizer(
    private val text: String,
) {
    public fun tokenize(): List<YamlToken> {
        val tokens = mutableListOf<YamlToken>()
        val lines = text.lines()
        var lineNum = 0uL

        var inDoc = false
        for (rawLine in lines) {
            lineNum++
            val trimmed = rawLine.trim()
            val mark = Mark(0u, lineNum, 0u)
            if (trimmed.isEmpty() || trimmed.startsWith("#")) continue

            if (trimmed == "---") {
                tokens.add(YamlToken.DocStart(mark))
                inDoc = true
                continue
            }
            if (trimmed == "...") {
                tokens.add(YamlToken.DocEnd(mark))
                inDoc = false
                continue
            }

            tokenizeLine(rawLine, mark, tokens)
        }
        return tokens
    }

    private fun tokenizeLine(rawLine: String, mark: Mark, tokens: MutableList<YamlToken>) {
        val line = rawLine.trim()
        if (line.isEmpty() || line.startsWith("#")) return

        if (line.startsWith("[") && line.endsWith("]")) {
            tokens.add(YamlToken.SeqStartTok(mark))
            val inner = line.substring(1, line.length - 1).trim()
            if (inner.isNotEmpty()) {
                val parts = splitFlow(inner)
                for (p in parts) {
                    tokenizeValue(p.trim(), mark, tokens)
                }
            }
            tokens.add(YamlToken.SeqEndTok(mark))
            return
        }

        if (line.startsWith("{") && line.endsWith("}")) {
            tokens.add(YamlToken.MapStartTok(mark))
            val inner = line.substring(1, line.length - 1).trim()
            if (inner.isNotEmpty()) {
                val parts = splitFlow(inner)
                for (p in parts) {
                    val kv = p.split(":", limit = 2)
                    if (kv.size == 2) {
                        tokenizeValue(kv[0].trim(), mark, tokens)
                        tokenizeValue(kv[1].trim(), mark, tokens)
                    }
                }
            }
            tokens.add(YamlToken.MapEndTok(mark))
            return
        }

        if (line.startsWith("- ")) {
            tokens.add(YamlToken.ScalarTok(line.substring(2).trim(), ScalarStyle.Plain, null, mark))
            return
        }

        if (line.contains(":")) {
            val idx = line.indexOf(':')
            val k = line.substring(0, idx).trim()
            val v = line.substring(idx + 1).trim()
            tokenizeValue(k, mark, tokens)
            if (v.isNotEmpty()) {
                tokenizeValue(v, mark, tokens)
            }
            return
        }

        tokenizeValue(line, mark, tokens)
    }

    private fun tokenizeValue(valStr: String, mark: Mark, tokens: MutableList<YamlToken>) {
        var str = valStr
        if (str.startsWith("&")) {
            val spaceIdx = str.indexOf(' ')
            if (spaceIdx > 0) {
                tokens.add(YamlToken.AnchorTok(str.substring(1, spaceIdx), mark))
                str = str.substring(spaceIdx + 1).trim()
            } else {
                tokens.add(YamlToken.AnchorTok(str.substring(1), mark))
                return
            }
        }
        if (str.startsWith("*")) {
            tokens.add(YamlToken.AliasTok(str.substring(1), mark))
            return
        }
        if (str.startsWith("!")) {
            val spaceIdx = str.indexOf(' ')
            if (spaceIdx > 0) {
                tokens.add(YamlToken.TagTok(str.substring(0, spaceIdx), mark))
                str = str.substring(spaceIdx + 1).trim()
            }
        }

        if (str.startsWith("\"") && str.endsWith("\"") && str.length >= 2) {
            tokens.add(YamlToken.ScalarTok(str.substring(1, str.length - 1), ScalarStyle.DoubleQuoted, str, mark))
        } else if (str.startsWith("'") && str.endsWith("'") && str.length >= 2) {
            tokens.add(YamlToken.ScalarTok(str.substring(1, str.length - 1), ScalarStyle.SingleQuoted, str, mark))
        } else {
            tokens.add(YamlToken.ScalarTok(str, ScalarStyle.Plain, str, mark))
        }
    }

    private fun splitFlow(s: String): List<String> {
        val result = mutableListOf<String>()
        var depth = 0
        var start = 0
        var inQuotes = false
        var quoteChar = ' '

        for (i in s.indices) {
            val c = s[i]
            if (inQuotes) {
                if (c == quoteChar && (i == 0 || s[i - 1] != '\\')) {
                    inQuotes = false
                }
            } else {
                when (c) {
                    '"', '\'' -> {
                        inQuotes = true
                        quoteChar = c
                    }
                    '[', '{' -> depth++
                    ']', '}' -> depth--
                    ',' -> {
                        if (depth == 0) {
                            result.add(s.substring(start, i))
                            start = i + 1
                        }
                    }
                }
            }
        }
        if (start < s.length) {
            result.add(s.substring(start))
        }
        return result
    }
}

public class Parser(
    private val input: ByteArray,
) {
    public constructor(inputString: String) : this(inputString.encodeToByteArray())

    private var pos = 0
    private var line = 0uL
    private var column = 0uL
    private var state = State.STREAM_START
    private val eventQueue = ArrayDeque<Pair<Event, Mark>>()

    private enum class State {
        STREAM_START,
        INSIDE_STREAM,
        STREAM_END,
    }

    public fun next(): Pair<Event, Mark> {
        if (eventQueue.isNotEmpty()) {
            return eventQueue.removeFirst()
        }

        when (state) {
            State.STREAM_START -> {
                state = State.INSIDE_STREAM
                parseStream()
                if (eventQueue.isNotEmpty()) {
                    return eventQueue.removeFirst()
                }
                return Pair(Event.StreamEnd, currentMark())
            }
            State.INSIDE_STREAM -> {
                if (eventQueue.isNotEmpty()) {
                    return eventQueue.removeFirst()
                }
                state = State.STREAM_END
                return Pair(Event.StreamEnd, currentMark())
            }
            State.STREAM_END -> {
                return Pair(Event.StreamEnd, currentMark())
            }
        }
    }

    public fun drop() {
        eventQueue.clear()
    }

    private fun currentMark(): Mark = Mark(pos.toULong(), line, column)

    private fun parseStream() {
        eventQueue.add(Pair(Event.StreamStart, currentMark()))
        val text = input.decodeToString()
        val tokenizer = YamlTokenizer(text)
        val tokens = tokenizer.tokenize()

        var inDoc = false
        var idx = 0

        while (idx < tokens.size) {
            val token = tokens[idx]
            when (token) {
                is YamlToken.DocStart -> {
                    if (inDoc) {
                        eventQueue.add(Pair(Event.DocumentEnd, token.mark))
                    }
                    eventQueue.add(Pair(Event.DocumentStart, token.mark))
                    inDoc = true
                    idx++
                }
                is YamlToken.DocEnd -> {
                    if (inDoc) {
                        eventQueue.add(Pair(Event.DocumentEnd, token.mark))
                        inDoc = false
                    }
                    idx++
                }
                else -> {
                    if (!inDoc) {
                        eventQueue.add(Pair(Event.DocumentStart, token.mark))
                        inDoc = true
                    }
                    idx = parseNode(tokens, idx)
                }
            }
        }

        if (inDoc) {
            eventQueue.add(Pair(Event.DocumentEnd, currentMark()))
        }
        eventQueue.add(Pair(Event.StreamEnd, currentMark()))
    }

    private fun parseNode(tokens: List<YamlToken>, startIndex: Int): Int {
        var idx = startIndex
        var anchor: Anchor? = null
        var tag: Tag? = null

        while (idx < tokens.size) {
            when (val tok = tokens[idx]) {
                is YamlToken.AnchorTok -> {
                    anchor = Anchor(tok.name.encodeToByteArray())
                    idx++
                }
                is YamlToken.TagTok -> {
                    tag = Tag(tok.name)
                    idx++
                }
                is YamlToken.AliasTok -> {
                    eventQueue.add(Pair(Event.Alias(Anchor(tok.name.encodeToByteArray())), tok.mark))
                    return idx + 1
                }
                is YamlToken.ScalarTok -> {
                    val sc =
                        Scalar(
                            anchor = anchor,
                            tag = tag,
                            value = tok.value.encodeToByteArray(),
                            style = tok.style,
                            repr = tok.raw?.encodeToByteArray(),
                        )
                    eventQueue.add(Pair(Event.Scalar(sc), tok.mark))
                    return idx + 1
                }
                is YamlToken.SeqStartTok -> {
                    eventQueue.add(Pair(Event.SequenceStart(SequenceStart(anchor, tag)), tok.mark))
                    idx++
                    while (idx < tokens.size && tokens[idx] !is YamlToken.SeqEndTok) {
                        idx = parseNode(tokens, idx)
                    }
                    if (idx < tokens.size && tokens[idx] is YamlToken.SeqEndTok) {
                        eventQueue.add(Pair(Event.SequenceEnd, tokens[idx].mark))
                        idx++
                    }
                    return idx
                }
                is YamlToken.MapStartTok -> {
                    eventQueue.add(Pair(Event.MappingStart(MappingStart(anchor, tag)), tok.mark))
                    idx++
                    while (idx < tokens.size && tokens[idx] !is YamlToken.MapEndTok) {
                        idx = parseNode(tokens, idx) // key
                        if (idx < tokens.size && tokens[idx] !is YamlToken.MapEndTok) {
                            idx = parseNode(tokens, idx) // value
                        }
                    }
                    if (idx < tokens.size && tokens[idx] is YamlToken.MapEndTok) {
                        eventQueue.add(Pair(Event.MappingEnd, tokens[idx].mark))
                        idx++
                    }
                    return idx
                }
                else -> {
                    idx++
                }
            }
        }
        return idx
    }

    public companion object {
        public fun new(input: ByteArray): Parser = Parser(input)

        public fun optional_anchor(anchor: ByteArray?): Anchor? = anchor?.let { Anchor(it) }

        public fun optional_tag(tag: ByteArray?): Tag? = tag?.let { Tag(it) }

        public fun convert_event(event: Event): Event = event

        public fun parity(p: Parser): Int = 0
    }
}

public class ParserPinned(
    public val input: ByteArray,
) {
    public fun drop() {}
}
