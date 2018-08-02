package com.pavelperc.treebuilder


/** Interface for logging. Can be implemented differently for console or android logging.*/
interface Log {
    fun println(str:Any = "")
}


/** Global variable for logging. Easy to turn on/off.
 * All debug logs should be invoked like this: log?.println("hello")*/
var log: Log? = null