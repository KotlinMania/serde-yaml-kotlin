package io.github.kotlinmania.serdeyaml

// port-lint: source with.rs

import io.github.kotlinmania.serdeyaml.value.Tag
import io.github.kotlinmania.serdeyaml.value.TaggedValue

public object singleton_map {
    public fun serialize(value: Value): Value = With.SingletonMap.serialize(value)

    public fun deserialize(value: Value): Value = With.SingletonMap.deserialize(value)
}

public object singleton_map_recursive {
    public fun serialize(value: Value): Value = With.SingletonMapRecursive.serialize(value)

    public fun deserialize(value: Value): Value = With.SingletonMapRecursive.deserialize(value)
}

public object With {
    public object SingletonMap {
        public fun serialize(value: Value): Value =
            when (value) {
                is Value.Tagged -> {
                    val map = Mapping()
                    map[Value.Str(value.tagged.tag.string)] = value.tagged.value
                    Value.Mapping(map)
                }
                else -> value
            }

        public fun deserialize(value: Value): Value =
            when (value) {
                is Value.Mapping -> {
                    if (value.mapping.size == 1) {
                        val entry = value.mapping.entries.first()
                        val keyStr = entry.key.asStr()
                        if (keyStr != null) {
                            Value.Tagged(TaggedValue(Tag(keyStr), entry.value))
                        } else {
                            value
                        }
                    } else {
                        value
                    }
                }
                else -> value
            }
    }

    public object SingletonMapRecursive {
        public fun serialize(value: Value): Value =
            when (value) {
                is Value.Tagged -> {
                    val map = Mapping()
                    map[Value.Str(value.tagged.tag.string)] = serialize(value.tagged.value)
                    Value.Mapping(map)
                }
                is Value.Sequence -> Value.Sequence(value.sequence.map { serialize(it) })
                is Value.Mapping -> {
                    val map = Mapping()
                    for ((k, v) in value.mapping) {
                        map[serialize(k)] = serialize(v)
                    }
                    Value.Mapping(map)
                }
                else -> value
            }

        public fun deserialize(value: Value): Value =
            when (value) {
                is Value.Mapping -> {
                    if (value.mapping.size == 1) {
                        val entry = value.mapping.entries.first()
                        val keyStr = entry.key.asStr()
                        if (keyStr != null) {
                            Value.Tagged(TaggedValue(Tag(keyStr), deserialize(entry.value)))
                        } else {
                            val map = Mapping()
                            for ((k, v) in value.mapping) {
                                map[deserialize(k)] = deserialize(v)
                            }
                            Value.Mapping(map)
                        }
                    } else {
                        val map = Mapping()
                        for ((k, v) in value.mapping) {
                            map[deserialize(k)] = deserialize(v)
                        }
                        Value.Mapping(map)
                    }
                }
                is Value.Sequence -> Value.Sequence(value.sequence.map { deserialize(it) }.toMutableList())
                else -> value
            }
    }
}
