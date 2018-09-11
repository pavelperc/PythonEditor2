package com.pavelperc.treebuilder.tree

import com.pavelperc.takeWhileIncl
import com.pavelperc.treebuilder.grammar.*


/** Represents all available alternatives, coming from one genericElement*/
class AlternativesSubtree private constructor(
        val rootRule: GenericRule,
        val leaves: List<Node>
) {
    companion object {
        fun generateTree(rootRule: GenericRule, ruleMap: RuleMap) {
            
            
        }
        
        fun getAvailableLeaves(gRule: GenericRule, ruleMap: RuleMap) =
                gRule.gAlteration.getAvailableLeaves(ruleMap)
        
        /** Returns all optional leaves of this gAlt and first non-optional in each alt branch*/
        private fun GenericAlteration.getAvailableLeaves(ruleMap: RuleMap): List<GenericElementLeaf> =
                gConcatenations
                        .flatMap {
                            it.gRepetitions
                                    // take all optional and one not optional (if it exists)
                                    .takeWhileIncl { AlternativesSearcher.gRepIsOptional(it, ruleMap) }
                        }
                        .map { it.gElement }
                        .map { optEl ->
                            when (optEl) {
                                is GenericElementLeaf -> listOf(optEl)
                                is GenericElementNode -> optEl.gAlteration.getAvailableLeaves(ruleMap)
                            }
                        }.flatMap { it }
        
        
    }
    
    
    /** checks if all leaves converge to rootRule*/
    private fun validateLeaves() {
        
        for (leaf in leaves) {
            if (leaf.gLeaf.isParserRuleId)
                throw IllegalArgumentException("leaf $leaf is parserRuleId")
        }
        
        
        for (leaf in leaves) {
            if (leaf.findRoot().gLeaf.gRule != rootRule) {
                throw IllegalArgumentException("leaf $leaf has wrong root")
            }
        }
    }
    
    /** This is a Node of AlternativesSubtree.
     * [father] should have gLeaf, which represents the rootRule rule of [gLeaf]*/
    data class Node(val father: Node?, val gLeaf: GenericElementLeaf) {
        
        init {
            // check the father rule relation
            if (father != null) {
                if (!father.gLeaf.isParserRuleId || father.gLeaf.text != gLeaf.gRule.id)
                    throw IllegalArgumentException("father ${father.gLeaf.text} not equals the rootRule rule id " +
                            "for $gLeaf, which is ${gLeaf.gRule.id}")
                
                
            }
        }
        
        fun findRoot() = revSequence.last()
        
        /** Returns a sequence from the current Node to the rootRule*/
        val revSequence: Sequence<Node>
            get() = generateSequence(this) { it.father }
    }
}
