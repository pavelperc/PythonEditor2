package com.pavelperc.treebuilder.tree

import com.pavelperc.treebuilder.takeWhileIncl
import com.pavelperc.treebuilder.grammar.*


interface Attachable {
    val gLeaf: GenericElementLeaf
    fun attachMe(): ElementLeaf
}


/** This is a Node of a SuggestionTree.
 * [father] should have gLeaf, which represents the rootRule rule of [gLeaf]*/
data class SuggestionNode(val father: SuggestionNode?, val gLeaf: GenericElementLeaf) {
    init {
        // check the father rule relation
        if (father != null) {
            if (!father.gLeaf.isParserRuleId || father.gLeaf.text != gLeaf.gRule.id)
                throw IllegalArgumentException("father ${father.gLeaf.text} not equals the rootRule rule id " +
                        "for $gLeaf, which is ${gLeaf.gRule.id}")
        }
    }
    
    fun findRoot() = revSequence.last()
    
    /** Returns a sequence from the current SuggestionNode to the rootRule*/
    val revSequence: Sequence<SuggestionNode>
        get() = generateSequence(this) { it.father }
}


/** Represents all available alternatives, coming from one genericAlt*/
class SuggestionTree private constructor(
        val gAltRoot: GenericAlteration,
        /** Available alternatives for selection*/
        val leaves: List<SuggestionNode>
) {
    companion object {
        fun generateTree(gAltRoot: GenericAlteration, ruleMap: RuleMap): SuggestionTree {
            val leaves = mutableListOf<SuggestionNode>()
            gAltRoot.genTree(null, ruleMap, leaves)
            // call private constructor with valid parameters
            return SuggestionTree(gAltRoot, leaves)
        }
        
        private fun GenericAlteration.genTree(father: SuggestionNode?, ruleMap: RuleMap, leaves: MutableList<SuggestionNode>) {
            getAvailableLeaves(ruleMap).forEach { gLeaf ->
                val node = SuggestionNode(father, gLeaf)
                if (gLeaf.isParserRuleId) {
                    ruleMap[gLeaf.text]!!.gAlteration.genTree(node, ruleMap, leaves)
                } else {
                    leaves.add(node)
                }
            }
        }
        
        /** Returns all optional leaves of this gAlt and first non-optional in each alt branch*/
        fun GenericAlteration.getAvailableLeaves(ruleMap: RuleMap): List<GenericElementLeaf> =
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
        
        
    } // end of companion
    
    override fun toString() = "SuggestionTree(rev traces: ${leaves.map {
        it.revSequence.map { it.gLeaf.text }.joinToString(", ", "[", "]")
    }.joinToString()})"
}
