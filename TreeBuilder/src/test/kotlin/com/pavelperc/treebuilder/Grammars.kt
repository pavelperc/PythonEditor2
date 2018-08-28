package com.pavelperc.treebuilder

import com.pavelperc.treebuilder.grammar.MyVisitor
import com.pavelperc.treebuilder.grammar.RuleMap
import java.io.FileReader

object Grammars{
    fun parseFullGrammar(): RuleMap {
        val grammar = FileReader("full_grammar.txt").readText()
        return MyVisitor.generateRuleMap(grammar)
    }
    
    fun parseSimpleGrammar(): RuleMap {
        val grammar = FileReader("simple_grammar.txt").readText()
        return MyVisitor.generateRuleMap(grammar)
    }
    
    fun genGrammar1() {
        val grammar = """
                rule1: rule2 | (A B) | [A B]
                rule2:
                
            """.trimIndent()
    }
}