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
            father.rightReps
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
    
    fun findAlternatives(tree: Tree) = with (tree) {
        if (cursor != null)
            findAlternativesFromCursor(cursor!!, ruleMap, elementCreator)
        else
            findAlternativesFromRoot(root, ruleMap, elementCreator)
    }
    
    fun findAlternativesFromCursor(cursor: ElementLeaf, ruleMap: RuleMap, elementCreator: ElementCreator): List<Attachable> {
        val insertionPoints = findAllInsertionPoints(cursor, ruleMap)
        
        return insertionPoints.map { (rep, pos) ->
            val gElement = rep.gRep.gElement
            
            if (gElement.isLexerRuleId || gElement.isString) {
                // lexer rule or const string
                listOf(LeafAttachable(rep, pos, elementCreator, gElement as GenericElementLeaf))
            } else {
                // parser rule, group or option
                val childAlteration = when (gElement) {
                    is GenericElementLeaf -> ruleMap[gElement.text]!!.gAlteration
                    is GenericElementNode -> gElement.gAlteration
                }
                
                val st = SuggestionTree.generateTree(childAlteration, ruleMap)
                st.leaves.map { RepAttachable(rep, pos, it, elementCreator) }
            }
        }.flatten()
    }
    
    // TODO comments
    fun findAlternativesFromRoot(root: RuleNode, ruleMap: RuleMap, elementCreator: ElementCreator): List<Attachable> {
        val st = SuggestionTree.generateTree(root.gAlteration, ruleMap)
        
        return st.leaves.map { ElemNodeAttachable(root, it, elementCreator) }
    }
    
    /**
     * Attaches [gLeaf] to [repToAttach] in pos [posInRep] using [elementCreator].
     * [gLeaf] should be the child of [repToAttach]
     */
    private class LeafAttachable(
            private val repToAttach: Repetition,
            private val posInRep: Int,
            val elementCreator: ElementCreator,
            override val gLeaf: GenericElementLeaf
    ) : Attachable {
        
        init {
            if (repToAttach.gRep.gElement != gLeaf) {
                throw IllegalArgumentException("LeafAttachable: gLeaf $gLeaf id not an repToAttach $repToAttach child ")
            }
        }
        
        override fun attachMe(): ElementLeaf {
            return elementCreator.fromRepetition(repToAttach, posInRep) as ElementLeaf
        }
    }
    
    /**
     * Attaches a branch of the SuggestionTree, starting from [leafSuggestion], to [firstElemNode], using [elementCreator].
     * [leafSuggestion] root should be in the same branch as [firstElemNode].
     * [leafSuggestion] shouldn't have a parserRule gLeaf.
     * */
    private class ElemNodeAttachable(
            private val firstElemNode: ElementNode,
            private val leafSuggestion: SuggestionNode,
            private val elementCreator: ElementCreator
    ) : Attachable {
        init {
            if (leafSuggestion.gLeaf.isParserRuleId) {
                throw IllegalArgumentException("ElemNodeAttachable: SuggestionNode $leafSuggestion is a parserRuleId")
            }
        }
        
        override val gLeaf: GenericElementLeaf get() = leafSuggestion.gLeaf
        
        override fun attachMe(): ElementLeaf {
            val firstGAlt = firstElemNode.gAlteration
            
            // go up the SuggestionNodes and remember all choices in gAlts
            val altChoices = leafSuggestion.revSequence
                    .map { it.gLeaf }
                    .flatMap { gLeaf ->
                        // generating a reversed sequence of gConcs for each SuggestionNode
                        generateSequence(gLeaf.father.father) { it.father.father?.father?.father }
                    }
                    .takeWhileIncl { gConc ->
                        // stop when we got first gAlt after our rep
                        gConc.father != firstGAlt
                    }
                    .map { it.positionInFather }
                    .toList()
                    .reversed()


//            return altChoices.fold(firstElemNode as Element) { acc, concPos ->
//                elementCreator.fromRepetition((acc as ElementNode).chooseConc(concPos).repetitions[0])
//            } as ElementLeaf
            
            // build elements, using elementCreator and chosen conc positions.
            var element: Element = firstElemNode
            altChoices.forEach { concPos ->
                val rep = (element as ElementNode).chooseConc(concPos).repetitions[0]
                element = elementCreator.fromRepetition(rep)
            }
            
            return element as ElementLeaf
        }
    }
    
    
    /**
     * Attaches a branch of the SuggestionTree, starting from [leafSuggestion], to [repToAttach], using [elementCreator].
     * [leafSuggestion] root should be in the same branch as the child element of [repToAttach].
     * [leafSuggestion] shouldn't have a parserRule gLeaf.
     * [repToAttach] shouldn't have gElement, which is LexerRuleId ot String const
     * */
    private class RepAttachable(
            private val repToAttach: Repetition,
            private val posInRep: Int,
            private val leafSuggestion: SuggestionNode,
            private val elementCreator: ElementCreator
    ) : Attachable {
        init {
            if (leafSuggestion.gLeaf.isParserRuleId) {
                throw IllegalArgumentException("SuggestionNode $leafSuggestion is a parserRuleId")
            }
            
            if (repToAttach.gRep.gElement.isLexerRuleId || repToAttach.gRep.gElement.isString) {
                throw IllegalArgumentException("RepAttachable: repToAttach's child is lexerRuleId or string const!")
            }
        }
        
        override val gLeaf: GenericElementLeaf
            get() = leafSuggestion.gLeaf
        
        override fun attachMe(): ElementLeaf {
            val firstElemNode = elementCreator.fromRepetition(repToAttach, posInRep) as ElementNode
            // do through ElemNodeAttachable with new created element
            val elemNodeAttachable = ElemNodeAttachable(firstElemNode, leafSuggestion, elementCreator)
            return elemNodeAttachable.attachMe()
        }
    }
    
    
}