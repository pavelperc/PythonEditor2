package com.pavelperc.treebuilder.tree

import com.pavelperc.treebuilder.grammar.GenericRule

class Alternative(
        val repToAttach: Repetition,
        position: Int,
        val ruleStack: RuleStack
)

class AlternativesSearcher(
        rootGenericRule: GenericRule
) {
    
    
    /** Goes up and find first node, who has some place on the right to past something new*/
    fun findFirstInsertionPoint(searchStart: Element): Element? {
        return null
    }
    
    fun findAlternatives(cursor: ElementLeaf?): List<Alternative> {
        return emptyList()
    }
    
}