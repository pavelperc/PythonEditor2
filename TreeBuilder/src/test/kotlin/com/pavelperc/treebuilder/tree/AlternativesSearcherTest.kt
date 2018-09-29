package com.pavelperc.treebuilder.tree

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
            val group1Conc = group1.chooseConc(0)
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
        val grammarStr = """
            file_input: (NEWLINE | stmt)* ENDMARKER
            stmt: simple_stmt | complex_stmt
            simple_stmt: NAME ('=' | augassign) expr NEWLINE
            augassign: '+=' | '-=' | '*=' | '/='
            expr: NUM (sign NUM)*
            sign: '+' | '-'
            complex_stmt: 'if' (TRUE | FALSE) ':' NEWLINE (TAB stmt NEWLINE)+
        """.trimIndent()
        val ruleMap = MyVisitor.generateRuleMap(grammarStr)
        
        
        val gRoot = ruleMap["stmt"]!!
        val root = RuleNode(gRoot)
        val elementCreator = ElementCreator(ruleMap)
    
    
        var attachables = AlternativesSearcher.findAlternativesFromRoot(root, ruleMap, elementCreator)
        attachables.map { it.gLeaf.text } shouldEqual listOf("NAME", "if")
        var cursor = attachables[0].attachMe() // NAME
    
        attachables = AlternativesSearcher.findAlternatives(cursor, ruleMap, elementCreator)
        attachables.map { it.gLeaf.text } shouldEqual listOf("=", "+=", "-=", "*=", "/=")
        cursor = attachables[1].attachMe() // '+='
        
        attachables = AlternativesSearcher.findAlternatives(cursor, ruleMap, elementCreator)
        attachables.map { it.gLeaf.text } shouldEqual listOf("NUM")
        cursor = attachables[0].attachMe() // NUM
        
        attachables = AlternativesSearcher.findAlternatives(cursor, ruleMap, elementCreator)
        attachables.map { it.gLeaf.text } shouldEqual listOf("+", "-", "NEWLINE")
        cursor = attachables[1].attachMe() // '-'
        
        attachables = AlternativesSearcher.findAlternatives(cursor, ruleMap, elementCreator)
        attachables.map { it.gLeaf.text } shouldEqual listOf("NUM")
        cursor = attachables[0].attachMe() // 'NUM'
        
        
        
        
        
        
    }
}
