package com.pavelperc.treebuilder

import java.util.*


/** Console implementation of program interface for debug.
 * It generates GraphViz trees after each step, using, [drawGv] function.*/
class MainRuleConsole(
        gRule: GenericRule,
        ruleMap: Map<String, GenericRule>
) : MainRule(gRule, ruleMap) {
    
    companion object {
        private val cin = Scanner(System.`in`)
        
    }
    
    /** Счётчик файлов graphviz */
    private var gvFileCounter = 0
    
    
    /** Формирует форматированную строку */
    private val chainString: String
        get() {
            val ans = StringBuilder("All chain: <\n" + GenericContextNewline.PREFIX_SPACE)
            
            if (cursor === null)
                ans.append("___ ")
            
            
            for (element in chain) {
                if (element.gContext is GenericContextNewline) {
                    ans.append("\n")
                }
                
                ans.append(element.realizedToken ?: element.toString()).append(" ")
                if (element === cursor) {
                    ans.append("___ ")
                }
            }
            ans.append("\n>")
            return ans.toString()
        }
    
    
    /** The code of the whole program in python */
    val chain = mutableListOf<ElementLeaf>()
    
    
    fun addToChain(leaf: ElementLeaf) {
        chain.add(chain.indexOf(cursor) + 1, leaf)
    }
    
    private fun drawGvFull() {
        val graph = Graph("chains/${this.gRule.id}${gvFileCounter}.gv", chainString)
        toGv(graph, false)
        graph.writeToFile()
    }
    
    private fun drawGvChosen() {
        val graph = Graph("chains/chosen_${this.gRule.id}${gvFileCounter}.gv", chainString)
        toGv(graph, true)
        graph.writeToFile()
    }
    
    private fun drawGv() {
        drawGvFull()
        drawGvChosen()
        gvFileCounter++
    }
    
//    /** For now console autocompletion just invokes ask with one element.*/
//    override fun autoChoose(leaf: ElementLeaf) {
//        ask(listOf(leaf))
//    }
    
    /** Вывод всех альтернатив и выбор одной*/
    override fun ask(alternatives: List<ElementLeaf>) {
        val alternatives = alternatives.sortedBy { it.groupingTagForButton }
        
        // Start of input into serr
        Thread.sleep(100)
        
        System.err.println(chainString)
        
        var chosen: RealizedRule.ElementLeaf? = null
        
        if (alternatives.size == 1) {
            System.err.println("auto choose: " + alternatives[0].gElement)
            cin.nextLine()
            chosen = alternatives[0]
//            return build(alternatives[0])
        } else {
            val grouped = alternatives.groupBy {
                it.groupingTagForButton
            }
            
            var i = 0
            grouped.forEach { tag, list ->
                System.err.print("%10s: ".format(tag.tag))
                list.forEach {
                    System.err.print("${i} - ${alternatives[i].gElement}, ")
                    i++
                }
                System.err.println()
            }
            System.err.println()
            
            
            
            askWhile("Your number: ") {
                chosen = alternatives[it.toInt()]
                true
            }
        }
        
        
        // set realized token
        val gContext = chosen!!.gContext
        if (gContext is GenericContextId) {
//            log?.println("!!!!! $chosen is GenericContextId. defTocken = ${gContext.defaultRealizedToken}")
            if (gContext.defaultRealizedToken != null) {
                chosen!!.realizedToken = gContext.defaultRealizedToken
            } else {
                val ctx = chosen!!.context
                val suggestions = gContext.quickHints(ctx).map { it.nameForButton }
                if (suggestions.isNotEmpty()) {
                    System.err.println("Some suggestions: $suggestions")
                }
                
                askWhile("Enter your value for $chosen:") {
                    chosen!!.realizedToken = it
                    true
                }
            }
        }
        
        // End of input into serr
        Thread.sleep(100)
        
        build(trueCursor, chosen!!)
        
        Thread.sleep(100)
        
        // adding to chain after newline indent has been set
        if (gContext !is GenericContextIndentOrDedent) {
            addToChain(chosen!!)
            cursor = chosen!!
        }
        // after indent or dedent don't move cursor
        
        
        System.err.println(chainString)
        
        moveCursor()
        
        drawGv()
        
        findAlternatives()
        
    }
    
    
    private fun moveCursor() {
        var flag = true
        
        var cursorInd = chain.indexOf(cursor)
        
        askWhile("Move cursor?") {
            when (it) {
                "<" -> cursorInd--
                ">" -> cursorInd++
                else -> {
                    System.err.println(chainString)
                    return@askWhile true// leave cycle
                }
            }
            // update cursor
            cursor = chain.getOrNull(cursorInd)
            System.err.println(chainString)
            
            return@askWhile false// go in repeat
        }
    }
    
    /**
     * Asks while [assignValue] is false or throws an Exception
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
}