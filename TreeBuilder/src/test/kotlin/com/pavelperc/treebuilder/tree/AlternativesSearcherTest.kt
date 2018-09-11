package com.pavelperc.treebuilder.tree

import com.pavelperc.treebuilder.grammar.GenericElement
import com.pavelperc.treebuilder.grammar.MyVisitor
import org.amshove.kluent.*
import org.junit.Test

class AlternativesSearcherTest {
    
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
    fun testGAltIsOptional() {
        val ruleMap = MyVisitor.generateRuleMap("""
            # non optional
            a: b | X X 
            b: X [X | X]
            
            # optional
            c: d e | f
            d: X*
            e: X+
            f: X* ([X X] | X+)
        """.trimIndent())
        
        val a = ruleMap["a"]!!
        val b = ruleMap["b"]!!
        val c = ruleMap["c"]!!
        val d = ruleMap["d"]!!
        val e = ruleMap["e"]!!
        val f = ruleMap["f"]!!
        
        AlternativesSearcher.gAltIsOptional(a.gAlteration, ruleMap) shouldBe false
        AlternativesSearcher.gAltIsOptional(b.gAlteration, ruleMap) shouldBe false
        
        AlternativesSearcher.gAltIsOptional(c.gAlteration, ruleMap) shouldBe true
        AlternativesSearcher.gAltIsOptional(d.gAlteration, ruleMap) shouldBe true
        AlternativesSearcher.gAltIsOptional(e.gAlteration, ruleMap) shouldBe false
        AlternativesSearcher.gAltIsOptional(f.gAlteration, ruleMap) shouldBe true
    }
    
    
    @Test
    fun testGAltIsOptionalWithRecursion() {
        val ruleMap = MyVisitor.generateRuleMap("""
            # smth recursive non optional
            g: e | f
            e: X [e]
            f: X* g
            
            # smth recursive optional
            m: k | n
            n: [X]
            k: X* m
        """.trimIndent())
        val e = ruleMap["e"]!!
        val g = ruleMap["g"]!!
        val m = ruleMap["m"]!!
        val n = ruleMap["n"]!!
        AlternativesSearcher.gAltIsOptional(e.gAlteration, ruleMap) shouldBe false
        AlternativesSearcher.gAltIsOptional(g.gAlteration, ruleMap) shouldBe false
        
        AlternativesSearcher.gAltIsOptional(n.gAlteration, ruleMap) shouldBe true
        AlternativesSearcher.gAltIsOptional(m.gAlteration, ruleMap) shouldBe true
        
        
        
    }
    
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
}
