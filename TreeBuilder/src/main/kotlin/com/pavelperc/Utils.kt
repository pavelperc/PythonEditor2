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

fun <T> List<T>.takeInd(vararg indices: Int) = filterIndexed { index, _ -> indices.contains(index) }
fun <T> List<T>.dropInd(vararg indices: Int) = filterIndexed { index, _ -> !indices.contains(index) }

fun <T> Sequence<T>.takeInd(vararg indices: Int) = filterIndexed { index, _ -> indices.contains(index) }
fun <T> Sequence<T>.dropInd(vararg indices: Int) = filterIndexed { index, _ -> !indices.contains(index) }



//fun main(args: Array<String>) {
//    listOf(1, 2, 3, 4, 5).takeWhileIncl { it < 3 }.also { println(it) }
//}