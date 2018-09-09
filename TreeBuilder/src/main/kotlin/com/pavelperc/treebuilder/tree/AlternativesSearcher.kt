package com.pavelperc.treebuilder.tree

import com.pavelperc.treebuilder.grammar.GenericElement
import com.pavelperc.treebuilder.grammar.GenericElementLeaf
import com.pavelperc.treebuilder.grammar.GenericRule

class Alternative(
        val repToAttach: Repetition,
        position: Int
)

class AlternativesSearcher() {
    
    
    /** Goes up and find first node, who has some place on the right to past something new*/
    fun findFirstInsertionPoint(searchStart: Element): Element? {
        var current: Element? = searchStart
        
        while (current != null && !current.canPastRight()) {
            current = current.father?.father?.father
        }
        return current
    }
    
    fun findAlternatives(cursor: ElementLeaf?): List<Alternative> {
        
        
        
        return emptyList()
    }
    
    private fun Element.canPastRight(): Boolean {
        val father = father ?: return false
        
        if (father.isFilled && father.rightRep?.isFilled != false) {
            return false
        }
        
        return true
    }
    
    
//    fun searchRuleStacks(gElement: GenericElement):List<RuleStack> {
//        if (gElement is GenericElementLeaf && !gElement.isParserRuleId) {
//            return listOf(RuleStack(gElement, gElement))
//        }
//        
//        
//    }

}
