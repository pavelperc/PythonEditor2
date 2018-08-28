package com.pavelperc.treebuilder

import com.pavelperc.treebuilder.grammar.MyVisitor
import org.junit.Test

import org.junit.Assert.*

class MyVisitorTest {
    
    
    @Test
    fun generateRuleMap() {
        
        val grammar = """
            stmt: NAME '=' NUM (sign NUM)* END
            sign: '+' | '-'
        """.trimIndent()
        
        val ruleMap = MyVisitor.generateRuleMap(grammar)
        
        assertNotNull(ruleMap["stmt"])
        assertNotNull(ruleMap["sign"])
        
        println(ruleMap.values.joinToString("\n"))
    }
    
    @Test
    fun testFullGrammar() {
        println("Working Directory = ${System.getProperty("user.dir")}")
        
        val ruleMap = Grammars.parseFullGrammar()
        println(ruleMap.values.joinToString("\n"))
    }
}