package com.pavelperc.treebuilder.tree

import com.pavelperc.treebuilder.exception.MultichoiceNotHandledException
import com.pavelperc.treebuilder.grammar.*

abstract class Element(
        var father: Repetition? = null,
        val ruleMap: RuleMap
) {
    companion object {
        /** Creates an element of type, specified in gElement for [rep] and attaches it to [rep]*/
        fun fromRepetition(rep: Repetition, position:Int = 0): Element {
            if (rep.isFilled)
                throw IllegalArgumentException("Can not attach element to filled rep: $rep")
            
            val gElement = rep.gRep.gElement
            val ruleMap = rep.father.father.ruleMap
            val newElement:Element
            if (gElement is GenericElementNode) {
                newElement = GroupNode(gElement, ruleMap, rep)
            } else if (gElement is GenericElementLeaf && gElement.isParserRuleId) {
                val gRule = ruleMap[gElement.text]!!
                newElement = RuleNode(gRule, ruleMap, rep)
            } else {
                // gElement is lexerRuleId or string const
                newElement = ElementLeaf(gElement as GenericElementLeaf, rep, ruleMap)
            }
            
            rep.elements.add(position, newElement)
            return newElement
        }
    }
    
}


/** Leaf of the Syntax tree. Represents string consts like '=' or lexer rules like NUMBER.*/
class ElementLeaf(
        val gElement: GenericElementLeaf,
        father: Repetition?,
        ruleMap: RuleMap,
        var text: String? = null
) : Element(father, ruleMap) {
    init {
        if (!gElement.isString && !gElement.isLexerRuleId)
            throw IllegalArgumentException("Can not create ElementLeaf from $gElement, " +
                    "it should be string const (like '=') or lexer rule (like NUMBER)")
        
        // define text for consts
        if (gElement.isString) {
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
                if (concs.size == 1)
                    concs[0] = value
                else {
                    // size = 0
                    concs.add(value)
                }
            }
        }
    
//    data class PathSegment(
//        val gAlt: GenericAlteration,
//        val gConc: GenericConcatenation,
//        val gRep: GenericRepetition,
//        val gElement: GenericElement
//    )
//    
//    /** Generates a path from the root gAlteration to this [gElement]*/
//    fun generatePathToRule(gElement: GenericElement): List<PathSegment> {
//        // TODO generatePathToRule: move out of RuleNode
//        val segment = PathSegment(
//            gElement.father.father.father,
//            gElement.father.father,
//            gElement.father,
//            gElement
//        )
//        
//        val fatherElement = gElement.father.father.father.father
//        if (fatherElement == null) {
//            return listOf(segment)
//        } else {
//            return generatePathToRule(fatherElement).plus(segment)
//        }
//    }
//    
//    /**
//     * Adds [leaf] to the rule, knowing only [positionInRep] to insert in the last (the lowest) rep.
//     * It builds all the tree above, if there are no any problems.
//     * If the rule not matches - throws an exception.
//     * If the path from the element contains upper groups and some group can be inserted ambiguously into its repetition,
//     * throws an exception. 
//     * */
//    fun addElementLeaf(leaf: ElementLeaf, positionInRep: Int = 0) {
//        // TODO addElementLeaf: generalize for Node and Leaf
//        if (leaf.gElement.gRule != gAlteration.gRule) {
//            throw IllegalArgumentException("gRule mismatch! Tried to add the leaf $leaf with gRule " +
//                    "${leaf.gElement.gRule} to ElementNode with gRule ${gAlteration.gRule}")
//        }
//        
//        val path = generatePathToRule(leaf.gElement)
//        
//        val startSegment = path[0]
//        
//        // TODO addElementLeaf: handle multichoice
//        if (conc == null) {
//            // just selecting one alternative
//            conc = Concatenation(startSegment.gConc, this)
//        }
//        
//        val positionInConc = startSegment.gRep.positionInFather
//        val rep = conc!!.repetitions[positionInConc]
//        
//        
//        if (path.size > 1) {
//            // adding to upper segment, so we don't take positionInRep into account
//            
//            if (rep.elements.isNotEmpty()) {
//                throw Exception("Ambiguous insertion: element $leaf path to its gRule contains rep, that is partially filled")
//            } else {
//                // elements is empty
//                val groupNode = GroupNode(startSegment.gElement as GenericElementNode, ruleMap, rep)
//                rep.elements.add(groupNode)
//                // go in recursion
//                groupNode.addElementLeaf(leaf, positionInRep)
//            }
//        } else {
//            // check positionInRep
//            
//            if (positionInRep > rep.elements.size || positionInRep < 0)
//                throw IllegalArgumentException("Illegal positionInRep=$positionInConc " +
//                        "is out of elements's size=${rep.elements.size}")
//            
//            if (rep.isFilled)
//                throw IllegalArgumentException("Tried to add element in filled rep $rep")
//            
//            // add existing element to rep
//            rep.elements.add(positionInRep, leaf)
//        }
//    }
    
    /**
     * Chooses one conc among alternatives in [gAlteration] and builds the whole structure below.
     * If the conc with the given [position] is already chosen - does nothing. 
     * @return chosen conc
     * */
    fun chooseConc(position: Int = 0): Concatenation {
        // TODO chooseConc: handle multichoosing
        
        if (position < 0 || position >= gAlteration.gConcatenations.size) {
            throw IllegalArgumentException("chooseConc position $position is out of range for Element: $this")
        }
        
        // new chosen gConc
        val gConc = gAlteration.gConcatenations[position]
        
        if (conc?.gConc != gConc) {
            conc = Concatenation(gConc, this)
        }
        
        return conc!!
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
    val isFilled: Boolean
        get() = gRep.isNone && elements.size > 0
    
//    var firstElement: Element?
//        get() = elements[0]
//        set(value) {
//            if (value == null) {
//                if (elements.size == 1) {
//                    elements.clear()
//                } else if (elements.size > 1)
//            }
//            
//            if (elements.isEmpty()) {
//                elements.add(value)
//            }
//        }
}





