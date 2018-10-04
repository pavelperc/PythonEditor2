package com.pavelperc.treebuilder.simpleLauncher

import com.pavelperc.treebuilder.grammar.MyVisitor
import com.pavelperc.treebuilder.tree.*
import java.io.FileReader
import java.util.*


object SimpleLauncher {
    private val cin = Scanner(System.`in`)
    
    /**
     * Asks while [assignValue] is false or throws an Exception.
     * [assignValue] receives got string in params
     */
    private fun askWhile(question: String, assignValue: (String) -> Boolean) {
        var str = ""
        // flag if we should repeat request
        var flag = false
        do {
            System.err.println(question)
            str = cin.nextLine()
            
            try {
                flag = !assignValue(str)
                
            } catch (e: Exception) {
                System.err.println(e)
                flag = true
            }
            
        } while (flag)
    }
    
    private fun printAttachables(attachables: List<Attachable>) =
            attachables
                    .withIndex()
                    .chunked(10) { list ->
                        list.joinToString("\t\t") { (i, att) ->
                            "$i - ${att.gLeaf}"
                        }
                    }
                    .joinToString("\n")
                    .also { println("Select:\n$it") }
    
    
    private fun Tree.allLeavesFromRoot(): List<ElementLeaf> {
        fun ElementNode.allLeaves(): List<ElementLeaf> = concs.flatMap {
            it.repetitions.flatMap {
                it.elements.flatMap {
                    when (it) {
                        is ElementLeaf -> listOf(it)
                        is ElementNode -> it.allLeaves()
                    }
                }
            }
        }
        return root.allLeaves()
    }
    
    private fun printAllText(tree: Tree) {
        println("All text: ${tree.allLeavesFromRoot().joinToString(" ")}")
    }
    
    private fun askAndChoose(tree: Tree) {
        while (true) {
            val attachables = AlternativesSearcher.findAlternatives(tree)
            printAllText(tree)
            printAttachables(attachables)
            
            if (attachables.isEmpty())
                break
            
            Thread.sleep(100)
            var pos: Int = 0
            if (attachables.size == 1) {
                println("autochoose")
                cin.nextLine()
                
            } else {
                askWhile("Your number: ") {
                    pos = it.toInt()
                    true
                }
            }
            tree.cursor = attachables[pos].attachMe()
//            printAllText(tree)
        }
    }
    
    @JvmStatic
    fun main(args: Array<String>) {
        println("Working Directory = ${System.getProperty("user.dir")}")
        val grammarStr = FileReader("full_grammar.txt").readText()
        
//        val grammarStr = """
//            file_input: (NEWLINE | stmt)* ENDMARKER
//            stmt: simple_stmt | complex_stmt
//            simple_stmt: NAME ('=' | augassign) expr NEWLINE
//            augassign: '+=' | '-=' | '*=' | '/='
//            expr: NUM (sign NUM)*
//            sign: '+' | '-'
//            complex_stmt: 'if' (TRUE | FALSE) ':' NEWLINE (TAB stmt NEWLINE)+
//        """.trimIndent()
        
        val ruleMap = MyVisitor.generateRuleMap(grammarStr)
        println("loaded grammar")
        
        val tree = Tree(ruleMap, "file_input", ElementCreator(ruleMap))
        
        askAndChoose(tree)


//    println(ruleMap.values.joinToString("\n"))
    }
}