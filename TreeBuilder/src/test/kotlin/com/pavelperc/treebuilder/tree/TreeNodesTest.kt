package com.pavelperc.treebuilder.tree

import com.pavelperc.treebuilder.exception.MultichoiceNotHandledException
import com.pavelperc.treebuilder.grammar.MyVisitor
import org.amshove.kluent.*
import org.junit.Test


class TreeNodesTest {
    
//    /**generate a path to rule from generic element of this rule*/
//    @Test
//    fun generatePathToRule() {
//        val ruleMap = MyVisitor.generateRuleMap("""
//            stmt: NAME '=' NUM (sign NUM)* END
//            sign: '+' | '-'
//        """.trimIndent())
//        
//        val gRoot = ruleMap["stmt"]!!
//        
//        val root = RuleNode(gRoot, ruleMap, null)
//        
//        val eq = ElementLeaf(gRoot.allLeaves.elementAt(1), null, ruleMap)
//        
//        var path = root.generatePathToRule(eq.gElement)
//        path.size shouldEqualTo 1
//        path[0] shouldEqual ElementNode.PathSegment(
//                gRoot.gAlteration,
//                gRoot.gAlteration.gConcatenations[0],
//                gRoot.gAlteration.gConcatenations[0].gRepetitions[1],
//                gRoot.gAlteration.gConcatenations[0].gRepetitions[1].gElement
//        )
//        
//        val num2 = ElementLeaf(gRoot.allLeaves.elementAt(4), null, ruleMap, "hello")
//        val group = gRoot.allElements[3] as GenericElementNode
//        
//        // check if the group is properly bound to father
//        group shouldEqual gRoot.gAlteration.gConcatenations[0].gRepetitions[3].gElement
//        
//        path = root.generatePathToRule(num2.gElement)
//        path.size shouldEqualTo 2
//        path[0] shouldEqual ElementNode.PathSegment(
//                gRoot.gAlteration,
//                gRoot.gAlteration.gConcatenations[0],
//                gRoot.gAlteration.gConcatenations[0].gRepetitions[3],
//                gRoot.gAlteration.gConcatenations[0].gRepetitions[3].gElement
//        )
//        path[1] shouldEqual ElementNode.PathSegment(
//                group.gAlteration,
//                group.gAlteration.gConcatenations[0],
//                group.gAlteration.gConcatenations[0].gRepetitions[1],
//                group.gAlteration.gConcatenations[0].gRepetitions[1].gElement
//        )
//    }
    
    /** choose one conc among alternatives in gAlteration */
    
    @Test
    fun singleConcInElementTest() {
        val ruleMap = MyVisitor.generateRuleMap("""
            rule: A | B C*
        """.trimIndent())
        val gRule = ruleMap["rule"]!!
        val rule = RuleNode(gRule, ruleMap)
        
        // empty conc
        rule.conc.shouldBeNull()
        rule.concs.size shouldEqualTo 0
        
        // fill conc
        rule.conc = Concatenation(gRule.gAlteration.gConcatenations[0], rule)
        rule.concs.size shouldEqualTo 1
        rule.conc.shouldNotBeNull()
        
        // change conc
        rule.conc = Concatenation(gRule.gAlteration.gConcatenations[1], rule)
        rule.concs.size shouldEqualTo 1
        rule.conc.shouldNotBeNull()
        
        // multiconc
        rule.concs.add(Concatenation(gRule.gAlteration.gConcatenations[0], rule))
        val f = {rule.conc} shouldThrow MultichoiceNotHandledException::class
        
    }
    
    
    @Test
    fun chooseConcInElementNode() {
        val ruleMap = MyVisitor.generateRuleMap("""
            rule1: A | B C*
            rule2: D
        """.trimIndent())
        val gRule1 = ruleMap["rule1"]!!
        val gRule2 = ruleMap["rule2"]!!
        
        val rule1 = RuleNode(gRule1, ruleMap)
        
        rule1.conc.shouldBeNull()
        
        val chosen = rule1.chooseConc(0)
        rule1.conc.shouldNotBeNull()
        rule1.conc shouldBe chosen
        
        chosen.repetitions.size shouldEqualTo 1
        
        rule1.chooseConc(1)
        rule1.conc.shouldNotBeNull()
        rule1.conc!!.repetitions.size shouldEqualTo 2
        
        val rule2 = RuleNode(gRule2, ruleMap)
        
        // initial conc is null even if there are no alternatives
        rule2.conc.shouldBeNull()
        rule2.chooseConc(0)
        
        val f = {
            rule2.chooseConc(1)
        } shouldThrow IllegalArgumentException::class
    }
    
    @Test
    fun createElementFromRepTest() {
        val ruleMap = MyVisitor.generateRuleMap("""
            stmt: NAME '=' NUM (sign NUM)* END
            sign: '+' | '-'
        """.trimIndent())
    
        val rootGRule = ruleMap["stmt"]!!
        val root = RuleNode(rootGRule, ruleMap, null)
        
        val conc = root.chooseConc(0)
        
        val name = Element.fromRepetition(conc.repetitions[0]) as ElementLeaf
        val eq = Element.fromRepetition(conc.repetitions[1]) as ElementLeaf
        name.gElement.isLexerRuleId.shouldBeTrue()
        eq.gElement.isString.shouldBeTrue()
        
        val group1 = Element.fromRepetition(conc.repetitions[3]) as GroupNode
        val group2 = Element.fromRepetition(conc.repetitions[3], 0) as GroupNode
        
        conc.repetitions[3].elements.size shouldEqualTo 2
        // group2 should be inserted to the beginning!!
        conc.repetitions[3].elements shouldEqual listOf(group2, group1)
        
        
        val group1Conc = group1.chooseConc()
        
        val sign = Element.fromRepetition(group1Conc.repetitions[0]) as RuleNode
        
        val signConc = sign.chooseConc(1)
        val minus = Element.fromRepetition(signConc.repetitions[0]) as ElementLeaf
        
        minus.gElement.text shouldEqual "-"
        
        minus.father shouldBe signConc.repetitions[0]
        sign.father shouldBe group1Conc.repetitions[0]
        group1.father shouldBe conc.repetitions[3]
        
        // TODO add graphviz
    }
}