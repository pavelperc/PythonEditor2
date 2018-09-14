package com.pavelperc.treebuilder.grammar

import com.pavelperc.treebuilder.dropInd
import com.pavelperc.treebuilder.Grammars
import org.amshove.kluent.*
import org.junit.Test

import org.junit.Assert.*
import java.lang.IllegalArgumentException

class GrammarRulesTest {
    
    
    @Test
    fun testFullGrammar() {
        println("Working Directory = ${System.getProperty("user.dir")}")
        
        val ruleMap = Grammars.parseFullGrammar()
        println("full_grammar parsed rules:")
        println(ruleMap.values.joinToString("\n"))
        
    }
    
    /**
     * Checks the content of the created ruleMap from custom grammar string
     */
    @Test
    fun generateRuleMap() {
        val grammar = """
            stmt: NAME '=' NUM (sign NUM)* END
            sign: '+' | '-'
        """.trimIndent()
        
        val ruleMap = MyVisitor.generateRuleMap(grammar)
        
        println("parsed grammar:")
        println(ruleMap.values.joinToString("\n"))
        
        val stmt = ruleMap["stmt"] ?: throw AssertionError("stmt not found in ruleMap")
        val sign = ruleMap["sign"] ?: throw AssertionError("sign not found in ruleMap")
        
        assertEquals("stmt", stmt.id)
        assertEquals("sign", sign.id)
        
        stmt.gAlteration.apply {
            assertEquals(1, gConcatenations.size)
            
            gConcatenations[0].apply {
                assertEquals(5, gRepetitions.size)
                assertTrue(gRepetitions.dropInd(3).all { it.isNone })
                assertTrue(gRepetitions[3].isMult)
                
                val name = gRepetitions[0].gElement
                val eq = gRepetitions[1].gElement
                val group = gRepetitions[3].gElement
                
                assertTrue(name is GenericElementLeaf && name.isLexerRuleId)
                assertTrue(eq is GenericElementLeaf && eq.isString && eq.text == "=")
                assertTrue(group is GenericElementNode && group.isGroup)
                
                
                (group as GenericElementNode).gAlteration.also { alt ->
                    assertTrue(alt.father == group)
                }
                
                val signLeaf = group.gAlteration.gConcatenations[0].gRepetitions[0].gElement
                
                assertTrue(signLeaf is GenericElementLeaf && signLeaf.isParserRuleId && signLeaf.text == "sign")
            }
        }
        
        sign.gAlteration.apply {
            assertEquals(2, gConcatenations.size)
        }
    }
    
    @Test(expected = IllegalArgumentException::class)
    fun testLowerCaseLexerRules() {
        val badGrammar = """
            stmt: name '=' NUM (sign NUM)* END
            sign: '+' | '-'
        """.trimIndent()
        
        MyVisitor.generateRuleMap(badGrammar)
    }
    
    @Test(expected = IllegalArgumentException::class)
    fun testUpperCaseGrammarRules() {
        val badGrammar = """
            stmt: NAME '=' NUM (SIGN NUM)* END
            SIGN: '+' | '-'
        """.trimIndent()
        
        MyVisitor.generateRuleMap(badGrammar)
    }
    
    @Test
    fun testGenericRuleAllElements() {
        val ruleMap = MyVisitor.generateRuleMap(
                """
            stmt: NAME '=' NUM (sign NUM)* END
            sign: '+' | '-'
        """.trimIndent()
        )
        val stmt = ruleMap["stmt"]!!
        val allElements = stmt.allElements
        allElements.size shouldEqualTo 5
        
        allElements[0] shouldBeInstanceOf GenericElementLeaf::class
        allElements[1] shouldBeInstanceOf GenericElementLeaf::class
        allElements[2] shouldBeInstanceOf GenericElementLeaf::class
        allElements[3] shouldBeInstanceOf GenericElementNode::class
        allElements[4] shouldBeInstanceOf GenericElementLeaf::class
        
        (allElements[0] as GenericElementLeaf).text shouldEqual "NAME"
        (allElements[1] as GenericElementLeaf).text shouldEqual "="
        (allElements[2] as GenericElementLeaf).text shouldEqual "NUM"
        allElements[3].isGroup.shouldBeTrue()
        (allElements[4] as GenericElementLeaf).text shouldEqual "END"
    }
    
    @Test
    fun testGenericRuleAllLeaves() {
        val ruleMap = MyVisitor.generateRuleMap("""
            stmt: NAME '=' NUM (sign NUM)* END
            sign: '+' | '-'
        """.trimIndent())
        
        val stmt = ruleMap["stmt"]!!
        val allLeaves = stmt.allLeaves.toList()
        allLeaves.size shouldEqualTo 6
        
        allLeaves[0].text shouldEqual "NAME"
        allLeaves[1].text shouldEqual "="
        allLeaves[2].text shouldEqual "NUM"
        allLeaves[3].text shouldEqual "sign"
        allLeaves[4].text shouldEqual "NUM"
        allLeaves[5].text shouldEqual "END"
    }
    
    
    @Test
    fun testLateInitProps() {
        val ruleMap = MyVisitor.generateRuleMap("""
            stmt: NAME '=' NUM (sign NUM)* END
            sign: '+' | '-'
        """.trimIndent())
        
        val stmt = ruleMap["stmt"]!!
        val num = stmt.allElements[2]
        val group = stmt.allElements[3] as GenericElementNode
        val sign = group.gAlteration.allElements[0]
        
        stmt.gAlteration.apply { 
            gRule shouldNotBe null
            father shouldBe null
        }
        
        num.apply {
            gRule shouldNotBe null
            father shouldNotBe null
        }
        
        group.apply {
            gRule shouldNotBe null
            father shouldNotBe null
        }
        
        sign.apply {
            gRule shouldNotBe null
            father shouldNotBe null
        }
    }
}