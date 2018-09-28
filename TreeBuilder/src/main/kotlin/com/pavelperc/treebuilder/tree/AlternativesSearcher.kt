package com.pavelperc.treebuilder.tree

import com.pavelperc.treebuilder.grammar.*
import com.pavelperc.treebuilder.takeWhileIncl

/** Alternatives searcher main algorithm*/
object AlternativesSearcher {
    /** Checks if [gRep] and all its children (even in other rules) are optional.
     * If cached value [GenericRepetition.cachedIsOptional] is null - fills it.
     * If it has recursive call - it will be considered as not optional
     * */
    fun gRepIsOptional(gRep: GenericRepetition, ruleMap: RuleMap) =// TODO remove gRepIsOptional and leave only extension fun
            gRep.isOptional(ruleMap, mutableSetOf(gRep.gRule))
    
    /** Checks, that at least one subtree in this alt is optional*/
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
    
    data class InsertionPoint(val repToAttach: Repetition, val position: Int)
    
    /** Goes up the tree and finds all elements, which has some place to insert something on the right.
     * This is always either rep with plus or star or right rep in the conc.
     * Stops moving up when meets the rule, which has non optional rep on the right.
     * Returns the list of pairs: (rep, posToInsert), which are called [InsertionPoint]
     * */
    fun findAllInsertionPoints(searchStart: Element, ruleMap: RuleMap): List<InsertionPoint> {
        val result = mutableListOf<InsertionPoint>()
        
        // current element for searching
        var current = searchStart
        
        while (current.father != null) {
            val father = current.father ?: break
            
            // this isFilled refers to 'left' elements, so it should be checked even we want to replace some on the right
            if (!father.isFilled) {
                result.add(InsertionPoint(father, current.positionInFather + 1))
            }
            
            var foundNonOptional = false
            father.rightReps.asIterable()
                    .takeWhileIncl {
                        foundNonOptional = !gRepIsOptional(it.gRep, ruleMap)
                        !foundNonOptional
                    }
                    .forEach {
                        // 'it' may be filled, but we suggest it nevertheless
                        result.add(InsertionPoint(it, 0))
                    }
            // stop climbing up if we met a non optional rep on the right.
            if (foundNonOptional)
                break
            else
                current = father.father.father
        }
        
        return result
    }
//    fun searchRuleStacks(gElement: GenericElement):List<RuleStack> {
//        if (gElement is GenericElementLeaf && !gElement.isParserRuleId) {
//            return listOf(RuleStack(gElement, gElement))
//        }
//        
//        
//    }
    
    
}