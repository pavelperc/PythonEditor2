package com.pavelperc

public inline fun <T> Iterable<T>.takeWhileIncl(predicate: (T) -> Boolean): List<T> {
    val list = ArrayList<T>()
    for (item in this) {
        if (!predicate(item)) {
            list.add(item)
            break
        }
        list.add(item)
    }
    return list
}

//fun main(args: Array<String>) {
//    listOf(1, 2, 3, 4, 5).takeWhileIncl { it < 3 }.also { println(it) }
//}