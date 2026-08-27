package io.github.kotlinmania.serdeyaml.libyaml

// port-lint: source serde_yaml/src/libyaml/util.rs

/**
 * Pure Kotlin representation of Owned memory pointer.
 */
public class Owned<T>(public var value: T?) {
    public fun get(): T = value ?: error("Uninitialized Owned")

    public fun set(newValue: T) {
        value = newValue
    }

    public fun deref(): T = get()

    public fun drop() {
        value = null
    }

    public companion object {
        public fun <T> new_uninit(): Owned<T> = newUninit()

        public fun <T> newUninit(): Owned<T> = Owned(null)

        public fun <T> assume_init(definitelyInit: Owned<T>): Owned<T> = assumeInit(definitelyInit)

        public fun <T> assumeInit(definitelyInit: Owned<T>): Owned<T> = definitelyInit
    }
}

public class InitPtr<T>(public var ptr: T?)

