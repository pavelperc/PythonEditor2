package com.pavelperc.treebuilder.tree

import com.pavelperc.treebuilder.grammar.GenericElement
import com.pavelperc.treebuilder.grammar.GenericElementLeaf
import org.antlr.v4.gui.Trees


/** Represents all available alternatives, coming from one empty genericElement*/
class AlternativesSubtree(
        val root: Node,
        val leaves: List<Node>
) {
    init {
//        validateLeaves()
    }
    
    
    
    
    
    /** checks if all leaves converge to root*/
    private fun validateLeaves() {
    
        for (leaf in leaves) {
            if (leaf.gLeaf.isParserRuleId)
                throw IllegalArgumentException("leaf $leaf is parserRuleId")
        }
        
        
        for (leaf in leaves) {
            if (leaf.findRoot() != root) {
                throw IllegalArgumentException("leaf $leaf has wrong root")
            }
        }
    }
    
    /** This is a Node of AlternativesSubtree.
     * [father] should have gLeaf, which represents the root rule of [gLeaf]*/
    data class Node(val father: Node?, val gLeaf: GenericElementLeaf) {
        
        init {
            // check the father rule relation
            if (father != null) {
                if (!father.gLeaf.isParserRuleId || father.gLeaf.text != gLeaf.gRule.id)
                    throw IllegalArgumentException("father ${father.gLeaf.text} not equals the root rule id " +
                            "for $gLeaf, which is ${gLeaf.gRule.id}")
                
                
            }
        }
        
        fun findRoot() = revSequence.last()
        
        /** Returns a sequence from the current Node to the root*/
        val revSequence: Sequence<Node>
                get() = generateSequence(this) { it.father }
    }
    
}
