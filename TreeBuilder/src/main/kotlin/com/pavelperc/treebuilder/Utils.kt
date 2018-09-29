package com.pavelperc.treebuilder


/** The version of takeWhile, which includes first invalid element if it exists.*/
public fun <T> Iterable<T>.takeWhileIncl(predicate: (T) -> Boolean): List<T> =
        this.asSequence().takeWhileIncl(predicate).toList()


/** The version of takeWhile for Sequences, which includes first invalid element if it exists.*/
public fun <T> Sequence<T>.takeWhileIncl(predicate: (T) -> Boolean): Sequence<T> {
    var shouldContinue = true
    return takeWhile {
        val result = shouldContinue
        shouldContinue = predicate(it)
        result
    }
}




fun <T> List<T>.takeInd(vararg indices: Int) = filterIndexed { index, _ -> indices.contains(index) }
fun <T> List<T>.dropInd(vararg indices: Int) = filterIndexed { index, _ -> !indices.contains(index) }

fun <T> Sequence<T>.takeInd(vararg indices: Int) = filterIndexed { index, _ -> indices.contains(index) }
fun <T> Sequence<T>.dropInd(vararg indices: Int) = filterIndexed { index, _ -> !indices.contains(index) }



//fun main(args: Array<String>) {
//    listOf(1, 2, 3, 4, 5).takeWhileIncl { it < 3 }.also { println(it) }
//}