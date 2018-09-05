package com.pavelperc.treebuilder.tree

import com.pavelperc.treebuilder.grammar.MyVisitor
import org.junit.Test

class AlternativesSearcherTest {
    
    fun loadRuleMap() = MyVisitor.generateRuleMap("""
            stmt: NAME '=' NUM (sign NUM)* END
            sign: '+' | '-'
        """.trimIndent())
    
    
    @Test
    fun findAlternativesTest() {
        val ruleMap = loadRuleMap()
        
        val cursor: ElementLeaf? = null
        
        val rootGenericRule = ruleMap["stmt"]!!
        val alternativesSearcher = AlternativesSearcher(rootGenericRule)
        
        val alternatives = alternativesSearcher.findAlternatives(cursor)
    }
    
    @Test
    fun findFirstInsertionPointTest() {
        val ruleMap = loadRuleMap()
        val rootGRule = ruleMap["stmt"]!!
        val alternativesSearcher = AlternativesSearcher(rootGRule)
        
        val root = RuleNode(rootGRule, ruleMap, null)
        
        val name = ElementLeaf(rootGRule.allElements[0], null, ruleMap, "hello")
        
        root.addElementLeaf(name, 0)
        
//        alternativesSearcher.findFirstInsertionPoint()
        
    }
}