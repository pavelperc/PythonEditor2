package com.pavelperc.treebuilder.tree

import com.pavelperc.treebuilder.grammar.GenericElement
import com.pavelperc.treebuilder.grammar.MyVisitor
import org.amshove.kluent.*
import org.junit.Test

class AlternativesSearcherTest {
    
    @Test
    fun findAlternativesTest() {
        val ruleMap = MyVisitor.generateRuleMap("""
            stmt: NAME '=' NUM (sign NUM)* END
            sign: '+' | '-'
        """.trimIndent())
        
        val cursor: ElementLeaf? = null
        
        val rootGRule = ruleMap["stmt"]!!
        val alternativesSearcher = AlternativesSearcher()
        
        val alternatives = alternativesSearcher.findAlternatives(cursor)
        
        
        // ...
        
        // hanging on NUM
        
        
    }
    
    @Test
    fun findFirstInsertionPointTest() {
        val ruleMap = MyVisitor.generateRuleMap("""
            stmt: NAME '=' NUM (sign NUM)* END
            sign: '+' | '-'
        """.trimIndent())
        
        val rootGRule = ruleMap["stmt"]!!
        val root = RuleNode(rootGRule)
        
        val conc = root.chooseConc(0)
        
        val elemCreator = ElementCreator(ruleMap)
        
        val name = elemCreator.fromRepetition(conc.repetitions[0]) as ElementLeaf
        val eq = elemCreator.fromRepetition(conc.repetitions[1]) as ElementLeaf
        val num = elemCreator.fromRepetition(conc.repetitions[2]) as ElementLeaf
        
        
        val alternativesSearcher = AlternativesSearcher()
        
        alternativesSearcher.findFirstInsertionPoint(num) shouldBe num
        alternativesSearcher.findFirstInsertionPoint(eq) shouldBe null
        
        val group1 = elemCreator.fromRepetition(conc.repetitions[3]) as GroupNode
        
        group1.chooseConc()
        val group1Conc = group1.chooseConc()
        val sign = elemCreator.fromRepetition(group1Conc.repetitions[0]) as RuleNode
        
        val signConc = sign.chooseConc(0) // chose plus
        val plus = elemCreator.fromRepetition(signConc.repetitions[0]) as ElementLeaf
        
        alternativesSearcher.findFirstInsertionPoint(plus) shouldBe sign
        
        val num2 = elemCreator.fromRepetition(group1Conc.repetitions[1]) as ElementLeaf
        
        alternativesSearcher.findFirstInsertionPoint(num2) shouldBe group1
        
    }
    
    
    @Test
    fun searchRuleStacksTest() {
        
    }
}