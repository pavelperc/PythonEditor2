package com.pavelperc.treebuilder.tree

import com.pavelperc.treebuilder.grammar.GenericElementLeaf
import com.pavelperc.treebuilder.grammar.GenericElementNode
import com.pavelperc.treebuilder.grammar.RuleMap

open class ElementCreator(
        private val ruleMap: RuleMap
) {
    /** Creates an element of type, specified in gElement for [rep] and attaches it to [rep]*/
    fun fromRepetition(rep: Repetition, position:Int = 0): Element {
        if (rep.isFilled)
            throw IllegalArgumentException("Can not attach element to filled rep: $rep")
        
        val gElement = rep.gRep.gElement
        val newElement: Element
        if (gElement is GenericElementNode) {
            newElement = GroupNode(gElement, rep)
        } else if (gElement is GenericElementLeaf && gElement.isParserRuleId) {
            val gRule = ruleMap[gElement.text]!!
            newElement = RuleNode(gRule, rep)
        } else {
            // gElement is lexerRuleId or string const
            newElement = ElementLeaf(gElement as GenericElementLeaf, rep)
        }
        
        rep.elements.add(position, newElement)
        return newElement
    }
}