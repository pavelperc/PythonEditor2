package com.pavelperc.treebuilder.tree

import com.pavelperc.treebuilder.exception.MultichoiceNotHandledException
import com.pavelperc.treebuilder.grammar.*

abstract class Element(
        var father: Repetition? = null,
        val ruleMap: RuleMap
)


/** Leaf of the Syntax tree. Represents string consts like '=' or lexer rules like NUMBER.*/
class ElementLeaf(
        val gElement: GenericElement,
        father: Repetition?,
        ruleMap: RuleMap,
        var text: String? = null
) : Element(father, ruleMap) {
    init {
        if (!gElement.isString || !gElement.isLexerRuleId)
            throw IllegalArgumentException("Can not create ElementLeaf from $gElement, " +
                    "it should be string const (like '=') or lexer rule (like NUMBER)")
        
        // define text for consts
        if (gElement is GenericElementLeaf && gElement.isString) {
            text = gElement.text
        }
    }
}

abstract class ElementNode(
        val gAlteration: GenericAlteration,
        ruleMap: RuleMap,
        father: Repetition? = null
) : Element(father, ruleMap) {
    /** Mostly it is a Single element list*/
    val concs = mutableListOf<Concatenation>()
    
    /** In most cases child conc is single (except cases with multichoosing).
     * So, this property returns first conc if concs has one element.
     * Returns null if concs is empty
     * and throws an Exception if concs has more than one element.
     * Setter works similarly. If concs size is more than one, thows Exception,
     * else sets or replaces first element in concs.
     * */
    var conc: Concatenation?
        get() {
            if (concs.size > 1)
                throw MultichoiceNotHandledException("Multichosen concatenation. Can not get a conc.")
            return concs.getOrNull(0)
        }
        set(value) {
            if (concs.size > 1)
                throw MultichoiceNotHandledException("Multichosen concatenation. Can not set a conc.")
            
            if (value == null) {
                concs.clear()
            } else {
                concs.add(0, value)
            }
        }
    
}

open class GroupNode(
        val gElementNode: GenericElementNode,
        ruleMap: RuleMap,
        var fatherRep: Repetition
) : ElementNode(gElementNode.gAlteration, ruleMap, fatherRep) {
    
}

class RuleNode(
        val gRule: GenericRule,
        ruleMap: RuleMap,
        father: Repetition? = null
) : ElementNode(gRule.gAlteration, ruleMap, father) {
    
    
    private data class PathSegment(
            val gAlt: GenericAlteration,
            val gConc: GenericConcatenation,
            val gRep: GenericRepetition,
            val gElement: GenericElement
    )
    
    private fun generatePathToRule(gElement: GenericElement): List<PathSegment> {
        val segment = PathSegment(
                gElement.father.father.father,
                gElement.father.father,
                gElement.father,
                gElement
        )
        
        val fatherElement = gElement.father.father.father.father
        
        if (fatherElement == null) {
            return listOf(segment)
        } else {
            return generatePathToRule(fatherElement).plus(segment)
        }
    }
    
    /**
     * Adds [leaf] to the rule, knowing only [positionInRep] to insert in the last (the lowest) rep.
     * It builds all the tree above, if there are no any problems.
     * If the rule not matches - throws an exception.
     * If the path from the element contains upper groups and some group can be inserted ambiguously into its repetition,
     * throws an exception.
     * */
    fun addElementLeaf(leaf: ElementLeaf, positionInRep: Int = 0) {
        if (leaf.gElement.gRule != gRule) {
            throw IllegalArgumentException("gRule mismatch! Tried to add the leaf $leaf with gRule " +
                    "${leaf.gElement.gRule} to RuleNode with gRule $gRule")
        }
        
        val path = generatePathToRule(leaf.gElement)
        
        val startSegment = path[0]
        
        // TODO addElementLeaf: handle multichoice
        if (conc == null) {
            // just selecting one alternative
            conc = Concatenation(startSegment.gConc, this)
        }
        
        val position = startSegment.gRep.positionInFather
        val rep = conc!!.repetitions[position]
        
        
        if (path.size > 1) {
            // adding to upper segment, so we don't take positionInRep into account
            
            if (rep.elements.isNotEmpty()) {
                throw Exception("Ambiguous insertion: element $leaf path to its gRule contains rep, that is partially filled")
            } else {
                rep.elements.add(GroupNode(startSegment.gElement as GenericElementNode, ruleMap, rep))
                // go in recursion
                todo
            }
        } else {
            // check positionInRep
            todo
        }
    }
    
}

class Concatenation(
        val gConc: GenericConcatenation,
        val father: ElementNode
) {
    val repetitions = List(gConc.gRepetitions.size) { i -> Repetition(gConc.gRepetitions[i], this) }
}

class Repetition(
        val gRep: GenericRepetition,
        val father: Concatenation
) {
    val elements = mutableListOf<Element>()
}





