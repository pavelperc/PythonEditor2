package com.pavelperc.treebuilder.tree

import com.pavelperc.treebuilder.exception.MultichoiceNotHandledException
import com.pavelperc.treebuilder.grammar.*

class Tree(
        val ruleMap: RuleMap,
        gRoot: GenericRule,
        val elementCreator: ElementCreator
) {
}


sealed class Element(
        var father: Repetition? = null
)


/** Leaf of the Syntax tree. Represents string consts like '=' or lexer rules like NUMBER.*/
class ElementLeaf(
        val gElement: GenericElementLeaf,
        father: Repetition?,
        var text: String? = null
) : Element(father) {
    init {
        if (!gElement.isString && !gElement.isLexerRuleId)
            throw IllegalArgumentException("Can not create ElementLeaf from $gElement, " +
                    "it should be string const (like '=') or lexer rule (like NUMBER)")
        
        // define text for consts
        if (gElement.isString) {
            text = gElement.text
        }
    }
    
    override fun toString() =
            if (gElement.isString)
                "'${gElement.text}'"
            else
                "${gElement.text}($text)"
}

sealed class ElementNode(
        val gAlteration: GenericAlteration,
        father: Repetition? = null
) : Element(father) {
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
        var fatherRep: Repetition
) : ElementNode(gElementNode.gAlteration, fatherRep) {
    override fun toString() =
            if (gElementNode.isGroup)
                "group${gElementNode.id}"
            else
                "option${gElementNode.id}"
}

open class RuleNode(
        val gRule: GenericRule,
        father: Repetition? = null
) : ElementNode(gRule.gAlteration, father) {
    override fun toString(): String = gRule.id
}


class Concatenation(
        val gConc: GenericConcatenation,
        val father: ElementNode
) {
    val repetitions = List(gConc.gRepetitions.size) { i -> Repetition(gConc.gRepetitions[i], this, i) }
}

class Repetition(
        val gRep: GenericRepetition,
        val father: Concatenation,
        val positionInFather: Int
) {
    val elements = mutableListOf<Element>()
    
    
    val isFilled: Boolean
        get() = gRep.isNone && elements.size > 0
    
    val rightRep: Repetition?
        get() = father.repetitions.getOrNull(positionInFather + 1)
}





