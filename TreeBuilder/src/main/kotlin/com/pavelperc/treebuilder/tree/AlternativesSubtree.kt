package com.pavelperc.treebuilder.tree

import com.pavelperc.treebuilder.grammar.GenericElement
import com.pavelperc.treebuilder.grammar.GenericElementLeaf


/** Represents all available alternatives, coming from one empty genericElement*/
class AlternativesSubtree(
        val root: GenericElement
) {
    val leaves = mutableListOf<GenericElementLeaf>()
    
    init {
        if (root is GenericElementLeaf && !root.isParserRuleId) {
            leaves.add(root)
        }
    }
}
