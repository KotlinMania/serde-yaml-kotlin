package io.github.kotlinmania.serdeyaml

// port-lint: source de.rs

public class Progress(public var pos: Int = 0)

public class StreamDeserializer(private val input: String) : Iterable<Value> {
    private val loader = Loader(input)

    override fun iterator(): Iterator<Value> = object : Iterator<Value> {
        private var nextVal: Value? = null
        private var hasCheckedNext = false

        private fun prepareNext() {
            if (!hasCheckedNext) {
                nextVal = loader.nextDocument()
                hasCheckedNext = true
            }
        }

        override fun hasNext(): Boolean {
            prepareNext()
            return nextVal != null
        }

        override fun next(): Value {
            prepareNext()
            val cur = nextVal ?: throw NoSuchElementException()
            hasCheckedNext = false
            nextVal = null
            return cur
        }
    }
}

public class Deserializer(private val input: String) : Iterable<Value> {
    private val loader = Loader(input)

    public fun deserialize(): Value {
        val first = loader.nextDocument() ?: return Value.Null
        val second = loader.nextDocument()
        if (second != null) {
            throw newError(ErrorImpl.MoreThanOneDocument)
        }
        return first
    }

    public fun next(): Value? = loader.nextDocument()

    override fun iterator(): Iterator<Value> = StreamDeserializer(input).iterator()


    public companion object {
        public fun from_str(s: String): Deserializer = Deserializer(s)
        public fun from_slice(v: ByteArray): Deserializer = Deserializer(v.decodeToString())
        public fun from_reader(reader: Any): Deserializer = Deserializer(reader.toString())

        public fun fromStr(s: String): Deserializer = Deserializer(s)
        public fun fromSlice(v: ByteArray): Deserializer = Deserializer(v.decodeToString())
        public fun fromReader(reader: Any): Deserializer = Deserializer(reader.toString())
    }
}

public fun fromStr(s: String): Value = Deserializer.fromStr(s).deserialize()
public fun fromSlice(v: ByteArray): Value = Deserializer.fromSlice(v).deserialize()
public fun fromReader(reader: Any): Value = Deserializer.fromReader(reader).deserialize()
public fun <T> fromValue(value: Value): T = @Suppress("UNCHECKED_CAST") (value as T)

public fun from_str(s: String): Value = fromStr(s)
public fun from_slice(v: ByteArray): Value = fromSlice(v)
public fun from_reader(reader: Any): Value = fromReader(reader)
public fun <T> from_value(value: Value): T = fromValue(value)

