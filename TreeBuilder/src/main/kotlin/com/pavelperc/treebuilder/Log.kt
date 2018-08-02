package com.pavelperc.treebuilder


/** Global object for logging. Easy to turn on/off.
 * All debug logs should be invoked like this: log?.println("hello")*/
object Log {

    var logPrinter: ((Any) -> Unit)? = null

    fun println(str: Any = "") {
        logPrinter?.invoke(str)
    }
}