package io.github.kotlinmania.serdeyaml

// port-lint: source serde_yaml/src/loader.rs

import io.github.kotlinmania.serdeyaml.value.Tag
import io.github.kotlinmania.serdeyaml.value.TaggedValue

public class Document(
    public val root: Value,
    public val anchors: Map<String, Value> = emptyMap(),
)

public class Loader(private val input: String) {
    private val cleanInput = input.removePrefix("\uFEFF")
    private val docs = splitDocuments(cleanInput)
    private var documentCount = 0
    private val anchors = mutableMapOf<String, Value>()

    public fun next(): Document? {
        val nextDoc = nextDocument() ?: return null
        return Document(nextDoc, anchors)
    }

    public fun nextDocument(): Value? {
        val trimmed = cleanInput.trim()
        if (trimmed.isEmpty()) {
            if (documentCount == 0) {
                documentCount++
                return Value.Null
            }
            return null
        }

        if (documentCount >= docs.size) {
            return null
        }
        val docText = docs[documentCount++]
        return parseSingleDocument(docText, anchors)
    }

    public fun find_anchor(anchor: String): Value? = anchors[anchor]

    private fun splitDocuments(src: String): List<String> {
        val list = mutableListOf<String>()
        val current = StringBuilder()
        val lines = src.lines()
        for (line in lines) {
            val t = line.trim()
            if (t == "---" && current.isNotEmpty()) {
                list.add(current.toString())
                current.clear()
            } else if (t == "...") {
                if (current.isNotEmpty()) {
                    list.add(current.toString())
                    current.clear()
                }
            } else {
                current.append(line).append("\n")
            }
        }
        if (current.isNotEmpty() && current.toString().isNotBlank()) {
            list.add(current.toString())
        }
        return if (list.isEmpty()) listOf(src) else list
    }

    public companion object {
        public fun new(input: String): Loader = Loader(input)

        public fun document_to_error(err: Error): Error = err

        internal fun parseSingleDocument(src: String, anchors: MutableMap<String, Value>): Value {
            val parser = YamlParser(src, anchors)
            val v = parser.parseValue()
            parser.ensureEndOfInput()
            v.applyMerge()
            return v
        }
    }
}


internal class YamlParser(
    private val source: String,
    private val anchors: MutableMap<String, Value>,
) {
    private val lines = source.lines()
    private var lineIdx = 0

    fun parseValue(): Value {
        skipBlanksAndComments()
        if (lineIdx >= lines.size) return Value.Null

        val line = lines[lineIdx]
        val trimmed = line.trim()

        if (trimmed.isEmpty()) return Value.Null

        // Check if inline JSON/Flow style
        if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
            lineIdx++
            return parseFlowMapping(trimmed)
        }
        if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
            lineIdx++
            return parseFlowSequence(trimmed)
        }

        val indent = getIndent(line)
        return parseBlockNode(indent)
    }

    fun ensureEndOfInput() {
        skipBlanksAndComments()
        if (lineIdx < lines.size) {
            val remaining = lines[lineIdx].trim()
            if (remaining.isNotEmpty() && remaining != "..." && remaining != "---") {
                parseValue()
            }
        }
    }

    private fun skipBlanksAndComments() {
        while (lineIdx < lines.size) {
            val l = lines[lineIdx].trim()
            if (l.isEmpty() || l.startsWith("#") || l == "---") {
                lineIdx++
            } else {
                break
            }
        }
    }

    private fun getIndent(line: String): Int {
        var count = 0
        for (ch in line) {
            if (ch == ' ') count++
            else if (ch == '\t') count += 2
            else break
        }
        return count
    }

    private fun parseBlockNode(minIndent: Int): Value {
        skipBlanksAndComments()
        if (lineIdx >= lines.size) return Value.Null

        val line = lines[lineIdx]
        val indent = getIndent(line)
        if (indent < minIndent) return Value.Null

        val trimmed = line.trim()

        // Check standalone anchor
        if (trimmed.startsWith("&") && !trimmed.contains(" ")) {
            val anchorName = trimmed.substring(1)
            lineIdx++
            val v = parseBlockNode(indent)
            anchors[anchorName] = v
            return v
        }

        // Check tagged block node e.g. !B followed by indented sequence or mapping
        if (trimmed.startsWith("!") && !trimmed.contains(" ")) {
            val tagName = trimmed.substring(1)
            lineIdx++
            if (lineIdx < lines.size && getIndent(lines[lineIdx]) > indent) {
                val childIndent = getIndent(lines[lineIdx])
                val inner = parseBlockNode(childIndent)
                return Value.Tagged(TaggedValue(Tag(tagName), inner))
            } else {
                return Value.Tagged(TaggedValue(Tag(tagName), Value.Null))
            }
        }

        // Check sequence
        if (trimmed.startsWith("- ") || trimmed == "-" || trimmed.startsWith("-#") || trimmed.startsWith("- #")) {
            return parseBlockSequence(indent)
        }

        // Check mapping (key: value)
        if (isMappingLine(trimmed)) {
            return parseBlockMapping(indent)
        }

        // Otherwise scalar
        lineIdx++
        return parseScalar(trimmed)
    }

    private fun isMappingLine(trimmed: String): Boolean {
        if (trimmed.startsWith("-")) return false
        val colonIdx = findUnquotedColon(trimmed)
        return colonIdx > 0
    }

    private fun findUnquotedColon(s: String): Int {
        var inSingle = false
        var inDouble = false
        var depthBrace = 0
        var depthBracket = 0
        for (i in s.indices) {
            val c = s[i]
            if (c == '\'' && !inDouble) inSingle = !inSingle
            else if (c == '"' && !inSingle) inDouble = !inDouble
            else if (!inSingle && !inDouble) {
                if (c == '{') depthBrace++
                else if (c == '}') depthBrace--
                else if (c == '[') depthBracket++
                else if (c == ']') depthBracket--
                else if (c == ':' && depthBrace == 0 && depthBracket == 0) {
                    if (i == s.length - 1 || s[i + 1].isWhitespace()) {
                        return i
                    }
                }
            }
        }
        return -1
    }

    private fun parseBlockSequence(indent: Int): Value {
        val list = mutableListOf<Value>()
        while (lineIdx < lines.size) {
            skipBlanksAndComments()
            if (lineIdx >= lines.size) break
            val line = lines[lineIdx]
            val curIndent = getIndent(line)
            if (curIndent < indent) break
            val trimmed = line.trim()
            if (curIndent == indent && (trimmed.startsWith("- ") || trimmed == "-" || trimmed.startsWith("-#") || trimmed.startsWith("- #"))) {
                var afterDash = if (trimmed.startsWith("- ")) trimmed.substring(2).trim()
                                else if (trimmed.startsWith("-")) trimmed.substring(1).trim()
                                else ""
                val commentIdx = afterDash.indexOf('#')
                val effectiveAfterDash = if (commentIdx >= 0) afterDash.substring(0, commentIdx).trim() else afterDash
                lineIdx++
                if (effectiveAfterDash.isEmpty()) {
                    if (lineIdx < lines.size && getIndent(lines[lineIdx]) > indent) {
                        list.add(parseBlockNode(indent + 1))
                    } else {
                        list.add(Value.Null)
                    }
                } else if (effectiveAfterDash.startsWith("&") || effectiveAfterDash.startsWith("{") || effectiveAfterDash.startsWith("[")) {
                    list.add(parseScalar(effectiveAfterDash))
                } else if (effectiveAfterDash.startsWith("!") && !effectiveAfterDash.contains(" ") && lineIdx < lines.size && getIndent(lines[lineIdx]) > indent) {
                    val tagName = effectiveAfterDash.substring(1)
                    val inner = parseBlockNode(indent + 1)
                    list.add(Value.Tagged(TaggedValue(Tag(tagName), inner)))
                } else if (isMappingLine(effectiveAfterDash)) {
                    val map = Mapping()
                    val colonIdx = findUnquotedColon(effectiveAfterDash)
                    val k = parseScalar(effectiveAfterDash.substring(0, colonIdx).trim())
                    val vStr = effectiveAfterDash.substring(colonIdx + 1).trim()
                    if (vStr.isEmpty()) {
                        if (lineIdx < lines.size && getIndent(lines[lineIdx]) > indent) {
                            map[k] = parseBlockNode(indent + 1)
                        } else {
                            map[k] = Value.Null
                        }
                    } else {
                        map[k] = parseScalar(vStr)
                    }
                    while (lineIdx < lines.size) {
                        skipBlanksAndComments()
                        if (lineIdx >= lines.size) break
                        val nextLine = lines[lineIdx]
                        val nextIndent = getIndent(nextLine)
                        if (nextIndent <= indent) break
                        val nextTrimmed = nextLine.trim()
                        if (isMappingLine(nextTrimmed)) {
                            val nextColon = findUnquotedColon(nextTrimmed)
                            val nextK = parseScalar(nextTrimmed.substring(0, nextColon).trim())
                            val nextVStr = nextTrimmed.substring(nextColon + 1).trim()
                            lineIdx++
                            if (nextVStr.isEmpty()) {
                                if (lineIdx < lines.size && getIndent(lines[lineIdx]) > nextIndent) {
                                    map[nextK] = parseBlockNode(nextIndent + 1)
                                } else {
                                    map[nextK] = Value.Null
                                }
                            } else {
                                map[nextK] = parseScalar(nextVStr)
                            }
                        } else {
                            break
                        }
                    }
                    list.add(Value.Mapping(map))
                } else {
                    list.add(parseScalar(effectiveAfterDash))
                }
            } else if (curIndent > indent) {
                list.add(parseBlockNode(indent + 1))
            } else {
                break
            }
        }
        return Value.Sequence(list)
    }

    private fun parseBlockMapping(indent: Int): Value {
        val mapping = Mapping()
        while (lineIdx < lines.size) {
            skipBlanksAndComments()
            if (lineIdx >= lines.size) break
            val line = lines[lineIdx]
            val curIndent = getIndent(line)
            if (curIndent < indent) break
            val trimmed = line.trim()
            if (!isMappingLine(trimmed)) {
                if (curIndent > indent) {
                    lineIdx++
                    continue
                } else {
                    break
                }
            }
            val colonIdx = findUnquotedColon(trimmed)
            val rawKey = trimmed.substring(0, colonIdx).trim()
            val rawVal = trimmed.substring(colonIdx + 1).trim()
            val key = parseScalar(rawKey)
            lineIdx++
            if (rawVal.isEmpty()) {
                if (lineIdx < lines.size && getIndent(lines[lineIdx]) > indent) {
                    val childIndent = getIndent(lines[lineIdx])
                    val v = parseBlockNode(childIndent)
                    mapping[key] = v
                } else {
                    mapping[key] = Value.Null
                }
            } else if (rawVal.startsWith("!") && !rawVal.contains(" ") && lineIdx < lines.size && getIndent(lines[lineIdx]) > indent) {
                val tagName = rawVal.substring(1)
                val childIndent = getIndent(lines[lineIdx])
                val inner = parseBlockNode(childIndent)
                mapping[key] = Value.Tagged(TaggedValue(Tag(tagName), inner))
            } else if (rawVal.startsWith("!int") || rawVal.startsWith("!!int")) {
                if (rawVal.contains("|") || rawVal.contains(">")) {
                    val blockStr = parseBlockString(indent + 1, rawVal.contains(">"))
                    val num = Number.fromStr((blockStr as Value.Str).string.trim())
                    mapping[key] = Value.Number(num)
                } else {
                    val rest = rawVal.substringAfter(' ').trim()
                    val num = Number.fromStr(rest)
                    mapping[key] = Value.Number(num)
                }
            } else if (rawVal.startsWith("!float") || rawVal.startsWith("!!float")) {
                if (rawVal.contains("|") || rawVal.contains(">")) {
                    val blockStr = parseBlockString(indent + 1, rawVal.contains(">"))
                    val num = Number.fromStr((blockStr as Value.Str).string.trim())
                    mapping[key] = Value.Number(num)
                } else {
                    val rest = rawVal.substringAfter(' ').trim()
                    val num = Number.fromStr(rest)
                    mapping[key] = Value.Number(num)
                }
            } else if (rawVal == "|" || rawVal == "|-" || rawVal == "|+" || rawVal == ">" || rawVal == ">-" || rawVal == ">+") {
                mapping[key] = parseBlockString(indent + 1, rawVal.startsWith(">"))
            } else if (rawVal.startsWith("{") && rawVal.endsWith("}")) {
                mapping[key] = parseFlowMapping(rawVal)
            } else if (rawVal.startsWith("[") && rawVal.endsWith("]")) {
                mapping[key] = parseFlowSequence(rawVal)
            } else {
                mapping[key] = parseScalar(rawVal)
            }
        }
        return Value.Mapping(mapping)
    }

    private fun parseBlockString(indent: Int, folded: Boolean): Value {
        val sb = StringBuilder()
        while (lineIdx < lines.size) {
            val line = lines[lineIdx]
            val curIndent = getIndent(line)
            val trimmed = line.trim()
            if (trimmed.startsWith("@") || trimmed.startsWith("`")) {
                val mark = io.github.kotlinmania.serdeyaml.libyaml.Mark(0uL, lineIdx.toULong(), 0uL)
                throw Error(ErrorImpl.Message("scan error: invalid character '$trimmed'", Pos(mark, ".")))
            }

            if (trimmed.isNotEmpty() && curIndent < indent) break
            val content = if (line.length >= indent) line.substring(indent) else line.trim()
            if (sb.isNotEmpty()) {
                sb.append(if (folded) " " else "\n")
            }
            sb.append(content)
            lineIdx++
        }
        sb.append("\n")
        return Value.Str(sb.toString())
    }


    private fun parseBlockScalar(indicator: String): Value.Str {
        val stripChomping = indicator.contains("-")
        val keepChomping = indicator.contains("+")
        val isFolded = indicator.startsWith(">")
        skipBlanksAndComments()
        if (lineIdx >= lines.size) return Value.Str("")
        val baseIndent = getIndent(lines[lineIdx])
        if (baseIndent == 0) {
            val trimmedLine = lines[lineIdx].trim()
            if (trimmedLine.startsWith("@") || trimmedLine.startsWith("`")) {
                val mark = io.github.kotlinmania.serdeyaml.libyaml.Mark(0uL, lineIdx.toULong(), 0uL)
                throw Error(ErrorImpl.Message("scan error: invalid character '$trimmedLine'", Pos(mark, ".")))
            }
        }
        val sb = StringBuilder()
        while (lineIdx < lines.size) {
            val line = lines[lineIdx]
            val indent = getIndent(line)
            if (line.trim().isNotEmpty() && indent < baseIndent) break
            lineIdx++
            val content = if (line.length >= baseIndent) line.substring(baseIndent) else ""
            if (sb.isNotEmpty()) {
                if (isFolded && content.isNotEmpty()) sb.append(" ")
                else sb.append("\n")
            }
            sb.append(content)
        }
        var res = sb.toString()
        if (!keepChomping && !stripChomping) {
            res = res.trimEnd('\n') + "\n"
        } else if (stripChomping) {
            res = res.trimEnd('\n')
        }
        return Value.Str(res)
    }


    private fun parseFlowMapping(s: String): Value {
        val inner = s.substring(1, s.length - 1).trim()
        val mapping = Mapping()
        if (inner.isEmpty()) return Value.Mapping(mapping)

        val pairs = splitFlowItems(inner)
        for (pair in pairs) {
            val colonIdx = findUnquotedColon(pair)
            if (colonIdx > 0) {
                val k = parseScalar(pair.substring(0, colonIdx).trim())
                val v = parseScalar(pair.substring(colonIdx + 1).trim())
                mapping[k] = v
            }
        }
        return Value.Mapping(mapping)
    }

    private fun parseFlowSequence(s: String): Value {
        val inner = s.substring(1, s.length - 1).trim()
        val list = mutableListOf<Value>()
        if (inner.isEmpty()) return Value.Sequence(list)

        val items = splitFlowItems(inner)
        for (item in items) {
            list.add(parseScalar(item.trim()))
        }
        return Value.Sequence(list)
    }

    private fun splitFlowItems(s: String): List<String> {
        val items = mutableListOf<String>()
        var inSingle = false
        var inDouble = false
        var depthBrace = 0
        var depthBracket = 0
        var start = 0

        for (i in s.indices) {
            val c = s[i]
            if (c == '\'' && !inDouble) inSingle = !inSingle
            else if (c == '"' && !inSingle) inDouble = !inDouble
            else if (!inSingle && !inDouble) {
                if (c == '{') depthBrace++
                else if (c == '}') depthBrace--
                else if (c == '[') depthBracket++
                else if (c == ']') depthBracket--
                else if (c == ',' && depthBrace == 0 && depthBracket == 0) {
                    items.add(s.substring(start, i))
                    start = i + 1
                }
            }
        }
        if (start < s.length) {
            items.add(s.substring(start))
        }
        return items
    }

    private fun parseScalar(s: String): Value {
        var str = s.trim()

        if (str.startsWith("@") || str.startsWith("`") || str == "]" || str == "}") {
            throw Error("syntax error: invalid plain scalar '$str'")
        }

        // Check anchor
        if (str.startsWith("&")) {
            val space = str.indexOf(' ')
            if (space > 0) {
                val anchorName = str.substring(1, space)
                val rest = str.substring(space + 1).trim()
                val v = parseScalar(rest)
                anchors[anchorName] = v
                return v
            } else {
                val anchorName = str.substring(1).trim()
                skipBlanksAndComments()
                if (lineIdx < lines.size) {
                    val nextLine = lines[lineIdx]
                    val nextIndent = getIndent(nextLine)
                    val v = parseBlockNode(nextIndent)
                    anchors[anchorName] = v
                    return v
                }
                anchors[anchorName] = Value.Null
                return Value.Null
            }
        }

        // Check alias
        if (str.startsWith("*")) {
            val aliasName = str.substring(1).trim()
            val v = anchors[aliasName] ?: throw Error("unknown anchor: $aliasName")
            return v
        }

        // Check block scalar
        if (str.startsWith("|") || str.startsWith(">")) {
            return parseBlockString(0, str.startsWith(">"))
        }

        // Check tag !Tag
        if (str.startsWith("!")) {
            if (str.startsWith("!!int") || str.startsWith("!!str") || str.startsWith("!!bool") || str.startsWith("!!float") || str.startsWith("!!null")) {
                val rest = str.substring(5).trim()
                if (rest.startsWith("|-") || rest.startsWith("|") || rest.startsWith(">")) {
                    val scalarVal = (parseBlockString(0, rest.startsWith(">")) as Value.Str).string
                    val num = Number.fromStrOrNull(scalarVal.trim())
                    return if (num != null) Value.Number(num) else Value.Str(scalarVal)
                }
                val scalarVal = if (rest.isEmpty()) {
                    skipBlanksAndComments()
                    if (lineIdx < lines.size) parseBlockNode(getIndent(lines[lineIdx])).asStr() ?: "" else ""
                } else rest
                val num = Number.fromStrOrNull(scalarVal.trim())
                return if (num != null) Value.Number(num) else Value.Str(scalarVal)
            }

            val space = str.indexOf(' ')
            if (space > 0) {
                val tagName = str.substring(1, space)
                val rest = str.substring(space + 1).trim()
                val innerVal = parseScalar(rest)
                return Value.Tagged(TaggedValue(Tag(tagName), innerVal))
            } else if (str.length > 1) {
                val tagName = str.substring(1)
                val savedIdx = lineIdx
                skipBlanksAndComments()
                if (lineIdx < lines.size) {
                    val nextLine = lines[lineIdx]
                    val nextIndent = getIndent(nextLine)
                    if (nextIndent > 0) {
                        val innerVal = parseBlockNode(nextIndent)
                        return Value.Tagged(TaggedValue(Tag(tagName), innerVal))
                    }
                }
                lineIdx = savedIdx
                return Value.Tagged(TaggedValue(Tag(tagName), Value.Null))
            }
        }

        // Single-quoted
        if (str.startsWith("'") && str.endsWith("'") && str.length >= 2) {
            val unquoted = str.substring(1, str.length - 1).replace("''", "'")
            return Value.Str(unquoted)
        }

        // Double-quoted
        if (str.startsWith("\"") && str.endsWith("\"") && str.length >= 2) {
            val unquoted = unescapeDoubleQuoted(str.substring(1, str.length - 1))
            return Value.Str(unquoted)
        }

        // Flow mapping / sequence
        if (str.startsWith("{") && str.endsWith("}")) {
            return parseFlowMapping(str)
        }
        if (str.startsWith("[") && str.endsWith("]")) {
            return parseFlowSequence(str)
        }

        // Null
        if (str == "null" || str == "Null" || str == "NULL" || str == "~" || str.isEmpty()) {
            return Value.Null
        }

        // Boolean (only standard YAML 1.2 boolean representations)
        if (str == "true" || str == "True" || str == "TRUE") {
            return Value.Bool(true)
        }
        if (str == "false" || str == "False" || str == "FALSE") {
            return Value.Bool(false)
        }

        // Number
        try {
            val num = Number.fromStr(str)
            return Value.Number(num)
        } catch (_: Exception) {
            // Not a number
        }

        return Value.Str(str)

    }

    private fun unescapeDoubleQuoted(s: String): String {
        val sb = StringBuilder()
        var i = 0
        while (i < s.length) {
            val c = s[i]
            if (c == '\\' && i + 1 < s.length) {
                i++
                when (s[i]) {
                    'n' -> sb.append('\n')
                    'r' -> sb.append('\r')
                    't' -> sb.append('\t')
                    '\\' -> sb.append('\\')
                    '"' -> sb.append('"')
                    '0' -> sb.append('\u0000')
                    'a' -> sb.append('\u0007')
                    'b' -> sb.append('\b')
                    'e' -> sb.append('\u001b')
                    'f' -> sb.append('\u000c')
                    'v' -> sb.append('\u000b')
                    'x' -> {
                        if (i + 2 < s.length) {
                            val hex = s.substring(i + 1, i + 3)
                            sb.append(hex.toInt(16).toChar())
                            i += 2
                        }
                    }
                    'u' -> {
                        if (i + 4 < s.length) {
                            val hex = s.substring(i + 1, i + 5)
                            sb.append(hex.toInt(16).toChar())
                            i += 4
                        }
                    }
                    else -> sb.append(s[i])
                }
            } else {
                sb.append(c)
            }
            i++
        }
        return sb.toString()
    }
}
