package io.github.kotlinmania.serdeyaml

// port-lint: source mapping.rs

import io.github.kotlinmania.serdeyaml.value.TaggedValue

public typealias DuplicateKeyError = String
public typealias HashLikeValue = Value

/**
 * A YAML mapping in which the keys and values are both `serde_yaml::Value`.
 */
public class Mapping internal constructor(
    private val map: LinkedHashMap<Value, Value>,
) : Map<Value, Value> by map, Comparable<Mapping> {

    public constructor() : this(LinkedHashMap())

    public constructor(capacity: Int) : this(LinkedHashMap(capacity))

    public fun reserve(additional: Int) {
        // No-op for LinkedHashMap in Kotlin
    }

    public fun shrink_to_fit() {
        // No-op for LinkedHashMap in Kotlin
    }

    public fun shrinkToFit() {
        shrink_to_fit()
    }

    public fun insert(k: Value, v: Value): Value? = map.put(k, v)

    public fun getOrPut(key: Value, defaultValue: () -> Value): Value = map.getOrPut(key, defaultValue)

    public fun containsKey(index: String): Boolean = map.containsKey(Value.Str(index))

    public fun containsKey(index: Long): Boolean = map.containsKey(Value.Number(Number.from(index)))

    public fun containsKey(index: Int): Boolean = map.containsKey(Value.Number(Number.from(index)))

    public fun get(index: String): Value? = map[Value.Str(index)]

    public fun get(index: Long): Value? = map[Value.Number(Number.from(index))]

    public fun get(index: Int): Value? = map[Value.Number(Number.from(index))]

    public fun getMut(key: Value): Value? = map[key]

    public fun getMut(index: String): Value? = map[Value.Str(index)]

    public fun getMut(index: Long): Value? = map[Value.Number(Number.from(index))]

    public fun getMut(index: Int): Value? = map[Value.Number(Number.from(index))]

    public fun get_mut(key: Value): Value? = getMut(key)

    public fun entry(k: Value): Entry {
        return if (map.containsKey(k)) {
            Entry.Occupied(OccupiedEntry(k, map))
        } else {
            Entry.Vacant(VacantEntry(k, map))
        }
    }

    public fun remove(index: String): Value? = map.remove(Value.Str(index))

    public fun remove(index: Long): Value? = map.remove(Value.Number(Number.from(index)))

    public fun remove(index: Int): Value? = map.remove(Value.Number(Number.from(index)))

    public fun removeEntry(key: Value): Pair<Value, Value>? {
        val v = map.remove(key) ?: return null
        return Pair(key, v)
    }

    public fun remove_entry(key: Value): Pair<Value, Value>? = removeEntry(key)

    public fun removeEntry(index: String): Pair<Value, Value>? = removeEntry(Value.Str(index))

    public fun removeEntry(index: Long): Pair<Value, Value>? = removeEntry(Value.Number(Number.from(index)))

    public fun swapRemove(key: Value): Value? = map.remove(key)

    public fun swap_remove(key: Value): Value? = swapRemove(key)

    public fun swapRemove(index: String): Value? = map.remove(Value.Str(index))

    public fun swapRemove(index: Long): Value? = map.remove(Value.Number(Number.from(index)))

    public fun swapRemoveEntry(key: Value): Pair<Value, Value>? = removeEntry(key)

    public fun swap_remove_entry(key: Value): Pair<Value, Value>? = swapRemoveEntry(key)

    public fun shiftRemove(key: Value): Value? = map.remove(key)

    public fun shift_remove(key: Value): Value? = shiftRemove(key)

    public fun shiftRemove(index: String): Value? = map.remove(Value.Str(index))

    public fun shiftRemove(index: Long): Value? = map.remove(Value.Number(Number.from(index)))

    public fun shiftRemoveEntry(key: Value): Pair<Value, Value>? = removeEntry(key)

    public fun shift_remove_entry(key: Value): Pair<Value, Value>? = shiftRemoveEntry(key)

    public fun retain(keep: (Value, Value) -> Boolean) {
        val iterator = map.entries.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (!keep(entry.key, entry.value)) {
                iterator.remove()
            }
        }
    }

    public fun capacity(): Int = map.size

    public fun len(): Int = map.size

    public fun is_empty(): Boolean = isEmpty()

    override val size: Int get() = map.size

    override fun isEmpty(): Boolean = map.isEmpty()

    public fun put(key: Value, value: Value): Value? = map.put(key, value)

    public fun putAll(from: Map<out Value, Value>) {
        map.putAll(from)
    }

    public operator fun set(key: Value, value: Value): Value? = map.put(key, value)

    public fun remove(key: Value): Value? = map.remove(key)

    public fun clear(): Unit = map.clear()

    public fun iter(): Iterable<Map.Entry<Value, Value>> = map.entries

    public fun iter_mut(): Iterable<Map.Entry<Value, Value>> = map.entries

    public fun intoKeys(): List<Value> = map.keys.toList()

    public fun into_keys(): List<Value> = intoKeys()

    public fun intoValues(): List<Value> = map.values.toList()

    public fun into_values(): List<Value> = intoValues()

    override fun compareTo(other: Mapping): Int {
        val iterA = map.entries.iterator()
        val iterB = other.map.entries.iterator()
        while (iterA.hasNext() && iterB.hasNext()) {
            val a = iterA.next()
            val b = iterB.next()
            val kCmp = a.key.compareTo(b.key)
            if (kCmp != 0) return kCmp
            val vCmp = a.value.compareTo(b.value)
            if (vCmp != 0) return vCmp
        }
        return if (iterA.hasNext()) 1 else if (iterB.hasNext()) -1 else 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Mapping) return false
        return map == other.map
    }

    override fun hashCode(): Int = map.hashCode()

    override fun toString(): String = map.toString()

    public sealed class Entry {
        public data class Occupied(public val entry: OccupiedEntry) : Entry()
        public data class Vacant(public val entry: VacantEntry) : Entry()

        public fun orInsert(default: Value): Value = when (this) {
            is Occupied -> entry.get()
            is Vacant -> entry.insert(default)
        }

        public fun or_insert(default: Value): Value = orInsert(default)
    }

    public class OccupiedEntry internal constructor(
        private val key: Value,
        private val map: LinkedHashMap<Value, Value>,
    ) {
        public fun key(): Value = key
        public fun get(): Value = map[key] ?: error("Occupied entry key missing")
        public fun getMut(): Value = get()
        public fun get_mut(): Value = getMut()
        public fun insert(value: Value): Value = map.put(key, value) ?: value
        public fun remove(): Value = map.remove(key) ?: error("Occupied entry key missing")
        public fun removeEntry(): Pair<Value, Value> = Pair(key, remove())
        public fun remove_entry(): Pair<Value, Value> = removeEntry()
    }

    public class VacantEntry internal constructor(
        private val key: Value,
        private val map: LinkedHashMap<Value, Value>,
    ) {
        public fun key(): Value = key
        public fun insert(value: Value): Value {
            map[key] = value
            return value
        }
    }

    public companion object {
        public fun new(): Mapping = Mapping()
        public fun with_capacity(capacity: Int): Mapping = Mapping(capacity)
        public fun withCapacity(capacity: Int): Mapping = Mapping(capacity)
    }
}

