package com.pavelperc.treebuilder.tree

import com.pavelperc.treebuilder.grammar.GenericElement
import com.pavelperc.treebuilder.grammar.MyVisitor
import com.pavelperc.treebuilder.takeInd
import org.amshove.kluent.*
import org.junit.Test

class AlternativesSearcherTest {
    
    @Test(timeout = 500)
    fun findInsertionPointsTest() {
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
        
        with(AlternativesSearcher) {
    
            findAllInsertionPoints(num, ruleMap)
                    .map { it.repToAttach } shouldEqual conc.repetitions.takeInd(3, 4)
    
            // we don't look if NUM is filled
            findAllInsertionPoints(eq, ruleMap)
                    .map { it.repToAttach } shouldEqual conc.repetitions.takeInd(2)
    
    
            val group1 = elemCreator.fromRepetition(conc.repetitions[3]) as GroupNode
            val group1Conc = group1.chooseConc()
            val sign = elemCreator.fromRepetition(group1Conc.repetitions[0]) as RuleNode
    
            val signConc = sign.chooseConc(0) // choose plus
            val plus = elemCreator.fromRepetition(signConc.repetitions[0]) as ElementLeaf
            
            // don't climb up, because right gRep is not optional
            findAllInsertionPoints(plus, ruleMap)
                    .map { it.repToAttach } shouldEqual group1Conc.repetitions.takeInd(1)
            
            val num2 = elemCreator.fromRepetition(group1Conc.repetitions[1]) as ElementLeaf
            
            // go up
            findAllInsertionPoints(num2, ruleMap) shouldEqual listOf(
                    AlternativesSearcher.InsertionPoint(conc.repetitions[3], 1),
                    AlternativesSearcher.InsertionPoint(conc.repetitions[4], 0)
            )
            
            Unit // return type for with
        }
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
        
        a.gAlteration.allElements.any { it.father.cachedIsOptional != null } shouldBe true
        f.gAlteration.allElements.forEach { it.father.cachedIsOptional shouldNotBe null }
        
        
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
    
        g.gAlteration.allElements.forEach { it.father.cachedIsOptional shouldBe false }
    
    }
    
    @Test
    fun findAlternativesTest() {
        val ruleMap = MyVisitor.generateRuleMap("""
            stmt: NAME '=' NUM (sign NUM)* END
            sign: '+' | '-'
        """.trimIndent())
        
        val cursor: ElementLeaf? = null
        
        val rootGRule = ruleMap["stmt"]!!
        
//        val alternatives = AlternativesSearcher.findAlternatives(cursor)
        
        
        // ...
        
        // hanging on NUM
        
        
    }
}
