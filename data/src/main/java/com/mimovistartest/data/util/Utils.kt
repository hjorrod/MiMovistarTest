package com.mimovistartest.data.util

/**
 * checks if an element is not null
 */
fun <T : Any> T?.notNull(f: (it: T) -> Unit) {
    if (this != null) f(this)
}

/**
 * checks if an element is  null
 */
fun <T : Any> T?.isNull(f: (it: T?) -> Unit) {
    if (this == null) f(this)
}