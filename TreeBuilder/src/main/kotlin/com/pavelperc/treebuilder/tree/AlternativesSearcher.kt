package com.pavelperc.treebuilder.tree

import com.pavelperc.treebuilder.grammar.*

class Alternative(
        val repToAttach: Repetition,
        position: Int
)

/** Alternatives searcher main algorithm*/
class AlternativesSearcher {// TODO: AlternativesSearcher convert to object
    
    companion object {
        
        /** Checks if [gRep] and all its children (even in other rules) are optional.
         * If cached value [GenericRepetition.cachedIsOptional] is null - fills it.
         * If it has recursive call - it will be considered as not optional
         * */
        fun gRepIsOptional(gRep: GenericRepetition, ruleMap: RuleMap) =
                gRep.isOptional(ruleMap, mutableSetOf(gRep.gRule))
        
        /** Checks, that at least one branch in this alt is optional*/
        fun gAltIsOptional(gAlt: GenericAlteration, ruleMap: RuleMap) =
                gAlt.isOptional(ruleMap, mutableSetOf(gAlt.gRule))
        
        private fun GenericRepetition.isOptional(ruleMap: RuleMap, recursiveRules: MutableSet<GenericRule>): Boolean {
            if (cachedIsOptional != null)
                return cachedIsOptional!!
            
            val result: Boolean
            if (isMult || gElement.isOption) {
                result = true
            } else if (gElement is GenericElementLeaf) {
                if (gElement.isParserRuleId) {
                    val nextRule = ruleMap[(gElement as GenericElementLeaf).text]!!
                    if (nextRule in recursiveRules) {
                        result = false
                    } else {
                        recursiveRules.add(nextRule)
                        result = nextRule.gAlteration.isOptional(ruleMap, recursiveRules)
                        recursiveRules.remove(nextRule)
                    }
                } else
                // lexer rule id or string
                    result = false
            } else {
                result = (gElement as GenericElementNode)
                        .gAlteration
                        .isOptional(ruleMap, recursiveRules)
            }
            cachedIsOptional = result
            return result
        }
        
        private fun GenericAlteration.isOptional(ruleMap: RuleMap, recursiveRules: MutableSet<GenericRule>) =
                gConcatenations
                        .any {
                            it.gRepetitions.all { it.isOptional(ruleMap, recursiveRules) }
                        }
    }
    
    /** Goes up and find first node, who has some place on the right to past something new*/
    fun findFirstInsertionPoint(searchStart: Element): Element? {
        var current: Element? = searchStart
        
        while (current != null && !current.canPastRight()) {
            current = current.father?.father?.father
        }
        return current
    }
    
    private fun Element.canPastRight(): Boolean {
        val father = father ?: return false
        
        if (father.isFilled && father.rightRep?.isFilled != false) {
            return false
        }
        
        return true
    }
    
    fun findAlternatives(cursor: ElementLeaf?): List<Alternative> {
        
        return emptyList()
    }


//    fun searchRuleStacks(gElement: GenericElement):List<RuleStack> {
//        if (gElement is GenericElementLeaf && !gElement.isParserRuleId) {
//            return listOf(RuleStack(gElement, gElement))
//        }
//        
//        
//    }
    
}
