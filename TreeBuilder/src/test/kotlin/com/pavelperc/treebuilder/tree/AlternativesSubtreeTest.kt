package com.pavelperc.treebuilder.tree

import com.pavelperc.treebuilder.grammar.MyVisitor
import com.pavelperc.treebuilder.graphviz.GenericRulesDrawer
import com.pavelperc.treebuilder.graphviz.Graph
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldEqual
import org.junit.Assert.*
import org.junit.Test
import kotlin.test.assertFails
import kotlin.test.assertFailsWith

class AlternativesSubtreeTest {
    
    
    @Test
    fun drawGrammar() {
        val grammarStr = """
            file_input: (NEWLINE | stmt)* ENDMARKER
            stmt: simple_stmt | complex_stmt
            simple_stmt: NAME ('=' | augassign) expr
            augassign: '+=' | '-=' | '*=' | '/='
            expr: NUM sign NUM
            sign: '+' | '-'
            complex_stmt: 'if' (TRUE | FALSE) ':' NEWLINE (TAB stmt NEWLINE)+
        """.trimIndent()
        val ruleMap = MyVisitor.generateRuleMap(grammarStr)
        GenericRulesDrawer(ruleMap, 
                Graph("chains/testNodeCreationTest.gv", grammarStr, 30)).drawGv()
    }
    
    
    operator fun <T> Sequence<T>.get(i: Int) = elementAt(i)
    
    @Test(timeout = 1000)
    fun testNodeCreation() {
        val ruleMap = MyVisitor.generateRuleMap("""
            file_input: (NEWLINE | stmt)* ENDMARKER
            stmt: simple_stmt | complex_stmt
            simple_stmt: NAME ('=' | augassign) expr
            augassign: '+=' | '-=' | '*=' | '/='
            expr: NUM sign NUM
            sign: '+' | '-'
            complex_stmt: 'if' (TRUE | FALSE) ':' NEWLINE (TAB stmt NEWLINE)+
        """.trimIndent())
        
        val file_input = ruleMap["file_input"]!!
        val stmt = ruleMap["stmt"]!!
        val simple_stmt = ruleMap["simple_stmt"]!!
        
        
        val newLineNode = AlternativesSubtree.Node(null, file_input.allLeaves[0])
        val stmtNode = AlternativesSubtree.Node(null, file_input.allLeaves[1])
        stmtNode.gLeaf.gRule shouldBe file_input
        
        // creating with wrong father
        assertFailsWith<IllegalArgumentException> {
            AlternativesSubtree.Node(newLineNode, file_input.allLeaves[1])
        }
        // creating with correct father
        val simpleStmtNode = AlternativesSubtree.Node(stmtNode, stmt.allLeaves[0])
        
        simpleStmtNode.findRoot() shouldBe stmtNode
        
        val nameNode = AlternativesSubtree.Node(simpleStmtNode, simple_stmt.allLeaves[0])
        
        nameNode.revSequence.toList() shouldEqual listOf(nameNode, simpleStmtNode, stmtNode)
    }
    
    @Test
    fun generateTreeTest() {
        
    }
    
}