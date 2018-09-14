package com.pavelperc.treebuilder.tree

import com.pavelperc.treebuilder.grammar.GenericElementNode
import com.pavelperc.treebuilder.takeInd
import com.pavelperc.treebuilder.grammar.MyVisitor
import com.pavelperc.treebuilder.graphviz.GenericRulesDrawer
import com.pavelperc.treebuilder.graphviz.Graph
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldEqual
import org.junit.Test
import kotlin.test.assertFailsWith

class SuggestionTreeTest {
    
    
    @Test
    fun drawGrammar() {
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
        GenericRulesDrawer(ruleMap,
                Graph("chains/testNodeCreationTest.gv", grammarStr, 30)).drawGv()
    }
    
    
    operator fun <T> Sequence<T>.get(i: Int) = elementAt(i)
    
    @Test(timeout = 1000)
    fun testNodeCreation() {
        // may differ from the one from drawGrammar()
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
        
        
        val newLineNode = SuggestionNode(null, file_input.allLeaves[0])
        val stmtNode = SuggestionNode(null, file_input.allLeaves[1])
        stmtNode.gLeaf.gRule shouldBe file_input
        
        // creating with wrong father
        assertFailsWith<IllegalArgumentException> {
            SuggestionNode(newLineNode, file_input.allLeaves[1])
        }
        // creating with correct father
        val simpleStmtNode = SuggestionNode(stmtNode, stmt.allLeaves[0])
        
        simpleStmtNode.findRoot() shouldBe stmtNode
        
        val nameNode = SuggestionNode(simpleStmtNode, simple_stmt.allLeaves[0])
        
        nameNode.revSequence.toList() shouldEqual listOf(nameNode, simpleStmtNode, stmtNode)
    }
    
    
    @Test
    fun getAvailableLeavesTest() {
        val ruleMap = MyVisitor.generateRuleMap("""
            a: X X | b X
            # not opt
            b: c+ X X | [X] X
            # opt
            c: (X | X)*
        """.trimIndent())
        
        val a = ruleMap["a"]!!
        val b = ruleMap["b"]!!
        val c = ruleMap["c"]!!
        
        // accessing getAvailableLeaves
        SuggestionTree.apply {
            a.gAlteration.getAvailableLeaves(ruleMap) shouldEqual
                    a.allLeaves.takeInd(0, 2).toList()
    
            b.gAlteration.getAvailableLeaves(ruleMap) shouldEqual
                    b.allLeaves.takeInd(0, 1, 3, 4).toList()
    
            c.gAlteration.getAvailableLeaves(ruleMap) shouldEqual
                    c.allLeaves.takeInd(0, 1).toList()
        }
    }
    
    
    @Test
    fun generateTreeTest() {
        val ruleMap = MyVisitor.generateRuleMap("""
            file_input: (NEWLINE | stmt)* ENDMARKER
            stmt: simple_stmt | complex_stmt
            simple_stmt: NAME ('=' | augassign) expr NEWLINE
            augassign: '+=' | '-=' | '*=' | '/='
            expr: NUM (sign NUM)*
            sign: '+' | '-'
            complex_stmt: 'if' (TRUE | FALSE) ':' NEWLINE (TAB stmt NEWLINE)+
        """.trimIndent())
    
        val file_input = ruleMap["file_input"]!!
        
        val fiTree = SuggestionTree.generateTree(file_input.gAlteration, ruleMap)
        fiTree.gAltRoot shouldBe file_input.gAlteration
        fiTree.leaves.map { it.gLeaf.text } shouldEqual listOf("NEWLINE", "NAME", "if", "ENDMARKER")
        
        fiTree.leaves.forEach { it.findRoot().gLeaf.gRule shouldBe file_input}
        
        val complex_stmt = ruleMap["complex_stmt"]!!
        
        val tabGroup = complex_stmt.allElements.last() as GenericElementNode
        
        val csTree = SuggestionTree.generateTree(tabGroup.gAlteration, ruleMap)
        csTree.gAltRoot shouldBe tabGroup.gAlteration
        csTree.leaves.map { it.gLeaf.text } shouldEqual listOf("TAB")
    }
    
}