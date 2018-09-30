package com.pavelperc.treebuilder.tree

import com.pavelperc.treebuilder.grammar.RuleMap


/** Syntax Tree*/
class Tree(
        val ruleMap: RuleMap,
        gRootName: String,
        val elementCreator: ElementCreator
) {
    val gRoot = ruleMap[gRootName]!!
    val root = RuleNode(gRoot)
    
    var cursor: ElementLeaf? = null
    
}