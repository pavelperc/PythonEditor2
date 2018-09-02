package com.pavelperc.treebuilder

import com.pavelperc.treebuilder.grammar.*
import org.junit.Test

import org.junit.Assert.*
import java.lang.IllegalArgumentException

class MyVisitorTest {
    
    
    @Test
    fun testFullGrammar() {
        println("Working Directory = ${System.getProperty("user.dir")}")
        
        val ruleMap = Grammars.parseFullGrammar()
        println("full_grammar parsed rules:")
        println(ruleMap.values.joinToString("\n"))
        
    }
    
    private fun <T> List<T>.takeInd(vararg indices: Int) = filterIndexed { index, t -> indices.contains(index) }
    private fun <T> List<T>.dropInd(vararg indices: Int) = filterIndexed { index, t -> !indices.contains(index) }
    
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
                
                
                (group as GenericElementNode).gAlteration.also {alt ->
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
    
    
}