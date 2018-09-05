package com.pavelperc.treebuilder.tree

import com.pavelperc.treebuilder.grammar.GenericElementNode
import com.pavelperc.treebuilder.grammar.MyVisitor
import org.amshove.kluent.*
import org.junit.Test

class TreeNodesTest {
    
    /**generate a path to rule from generic element of this rule*/
    @Test
    fun generatePathToRule() {
        val ruleMap = MyVisitor.generateRuleMap(
                """
            stmt: NAME '=' NUM (sign NUM)* END
            sign: '+' | '-'
        """.trimIndent()
        )
        val gRoot = ruleMap["stmt"]!!
    
        val root = RuleNode(gRoot, ruleMap, null)
    
        val eq = ElementLeaf(gRoot.allElements[1], null, ruleMap)
        
        var path = root.generatePathToRule(eq.gElement)
        path.size shouldEqualTo 1
        path[0] shouldEqual ElementNode.PathSegment(
                gRoot.gAlteration,
                gRoot.gAlteration.gConcatenations[0],
                gRoot.gAlteration.gConcatenations[0].gRepetitions[1],
                gRoot.gAlteration.gConcatenations[0].gRepetitions[1].gElement
        )
        
        val num2 = ElementLeaf(gRoot.allLeaves.elementAt(4), null, ruleMap, "hello")
        val group = gRoot.allElements[3] as GenericElementNode
        
        // check if the group is properly bound to father
        group shouldEqual gRoot.gAlteration.gConcatenations[0].gRepetitions[3].gElement
        
        path = root.generatePathToRule(num2.gElement)
        path.size shouldEqualTo 2
        path[0] shouldEqual ElementNode.PathSegment(
                gRoot.gAlteration,
                gRoot.gAlteration.gConcatenations[0],
                gRoot.gAlteration.gConcatenations[0].gRepetitions[3],
                gRoot.gAlteration.gConcatenations[0].gRepetitions[3].gElement
        )
        path[1] shouldEqual ElementNode.PathSegment(
                group.gAlteration,
                group.gAlteration.gConcatenations[0],
                group.gAlteration.gConcatenations[0].gRepetitions[1],
                group.gAlteration.gConcatenations[0].gRepetitions[1].gElement
        )
    }
    
    @Test
    fun addElementLeafTest() {
        val ruleMap = MyVisitor.generateRuleMap(
                """
            stmt: NAME '=' NUM (sign NUM)* END
            sign: '+' | '-'
        """.trimIndent()
        )
        
        val rootGRule = ruleMap["stmt"]!!
        
        val root = RuleNode(rootGRule, ruleMap, null)
        
        val name = ElementLeaf(rootGRule.allElements[0], null, ruleMap, "hello")
        val eq = ElementLeaf(rootGRule.allElements[1], null, ruleMap)
        
        root.addElementLeaf(name)
        root.addElementLeaf(eq)
        
        root.conc.shouldNotBeNull()
        
        root.conc!!.repetitions.size shouldEqualTo  5
        
        root.conc!!.repetitions[0].elements[0] shouldEqual name
        root.conc!!.repetitions[1].elements[0] shouldEqual eq
    }
}