package com.pavelperc.treebuilder.grammar

import com.pavelperc.treebuilder.*

/**
 * Вершина для хранения любого грамматического правила.
 *
 * Отличия от структуры Abnf.g4 :
 * Пропущены классы repetition, repeat
 *
 * У класса GenericElement нет NumberValue и ProseValue
 * Пока нет деления на ID_BIG и ID_SMALL
 * Created by pavel on 28.12.2017.
 */
class GenericRule(
        /** Имя правила из грамматики.  */
        val id: String,
        /** начальное alteration */
        val gAlteration: GenericAlteration
) {
    
    init {
        if (id == id.toUpperCase())
            throw IllegalArgumentException("Grammar rule $id: id mustn't be upperCase!!!")
        
        gAlteration.gRule = this
    }
    
    /** all leaf elements under this rule*/
    val allLeaves: Sequence<GenericElementLeaf>
        get() = gAlteration.allLeaves
    
    /** All child elements, on this level, including groups and options*/
    val allElements: List<GenericElement>
        get() = gAlteration.allElements
    
    override fun toString() = "$id:\n\t$gAlteration"
    
    
    // TODO remove all grouping tags from GenericRule. And from this module at all.
    /** Spreads grouping tag to all allLeaves of this generic rule*/
    fun setGroupingTag(tag: GroupingTag) {
        allLeaves.forEach { it.groupingTag = tag }
    }
}


class GenericAlteration(
        val gConcatenations: MutableList<GenericConcatenation>,
        var gRule: GenericRule? = null
) {
    init {
        gConcatenations.forEach { it.father = this }
    }
    
    /** The father is initialized in the constructor of gElementNode if we have bound to it*/
    var father: GenericElementNode? = null
    
    
    /** All child elements, on this level, including groups and options*/
    val allElements: List<GenericElement>
        get() = gConcatenations.flatMap { it.gRepetitions.map { it.gElement } }
    
    /** all leaf elements under this alteration*/
    val allLeaves:Sequence<GenericElementLeaf>
        get() = gConcatenations.asSequence().flatMap { it.gRepetitions.asSequence().flatMap { it.gElement.allLeaves } }
    
    override fun toString() = gConcatenations.joinToString(" | ")
    
    
}

class GenericConcatenation(
        val gRepetitions: MutableList<GenericRepetition>
) {
    
    init {
        gRepetitions.forEach { it.father = this }
    }
    
    val gRule: GenericRule?
        get() = father.gRule
    
    /** Отец заполняется в конструкторе gAlt*/
    lateinit var father: GenericAlteration
    
    override fun toString() = gRepetitions.joinToString(" ")
    
}

class GenericRepetition(
        val repetitive: Repetitive = Repetitive.NONE,
        var gElement: GenericElement
) {
    /** The father is initialized in the constructor of gConc*/
    lateinit var father: GenericConcatenation
    
    init {
        gElement.father = this
    }
    
    val gRule: GenericRule?
        get() = father.gRule
    
    val isNone: Boolean
        get() = repetitive == Repetitive.NONE
    
    val isPlus: Boolean
        get() = repetitive == Repetitive.PLUS
    
    val isMult: Boolean
        get() = repetitive == Repetitive.MULT
    
    enum class Repetitive constructor(var symbol: String) {
        NONE(""), PLUS("+"), MULT("*");
        
        override fun toString() = symbol
    }
    
    
    /** Возвращает gElement, проверяя, что это не лист, а узел*/
    val gElementNode: GenericElementNode
        get() =
            if (gElement is GenericElementNode)
                gElement as GenericElementNode
            else
                throw Exception("Can not get gElement with gAlteration.")
    
    val gElementLeaf: GenericElementLeaf
        get() =
            if (gElement is GenericElementLeaf)
                gElement as GenericElementLeaf
            else
                throw Exception("Can not get gElement with text.")
    
    val positionInFather: Int by lazy {
        father.gRepetitions.indexOf(this)
    }
    
    
    override fun toString() = gElement.toString() + repetitive.toString()
    
}

abstract class GenericElement(
        val elementType: ElementType
) {
    
    companion object {
        private var lastId = 0
    }
    
    val gRule: GenericRule?
        get() = father.gRule
    
    /** Identical number of Generic element among all*/
    val id = lastId++
    
    
    /** The father is initialized in the constructor of gRep*/
    lateinit var father: GenericRepetition
    
    /** all leaf elements from this branch, including itself*/
    val allLeaves: Sequence<GenericElementLeaf>
        get() = when {
            this is GenericElementLeaf -> listOf(this).asSequence()
            this is GenericElementNode ->
                gAlteration.allLeaves
            else -> throw Exception("Unreachable")
        }
    
    
    /**
     * Группирующий тег. Если текущий элемент - [GenericElementNode], то при присвоении группы
     * она также присваивается и всем его детям
     */
    var groupingTag: GroupingTag = GroupingTag.defaultTag
        set(value) {
            field = value
            if (this is GenericElementNode) {
                gAlteration.gConcatenations.forEach {
                    it.gRepetitions.forEach {
                        it.gElement.groupingTag = value
                    }
                }
            }
        }
    
    val isString: Boolean
        get() = elementType == ElementType.STRING
    
    val isGroup: Boolean
        get() = elementType == ElementType.GROUP
    
    /** Проверка только на [] */
    val isOption: Boolean
        get() = elementType == ElementType.OPTION
    
    
    val isId: Boolean
        get() = elementType == ElementType.ID
    
    /** Is a grammar rule. (Like stmt, assign)*/
    open val isParserRuleId: Boolean = false
    
    /** Is a lexeme, not grammar rule. (Like NAME, NUMBER)*/
    open val isLexerRuleId: Boolean = false
    
    enum class ElementType {
        /** либо lexer rule либо parser rule  */
        ID,
        GROUP,
        OPTION,
        /** Константная строка. Например: '=' */
        STRING
    }
}

/** element which is GROUP or OPTION*/
class GenericElementNode(
        val gAlteration: GenericAlteration,
        /** OPTION or GROUP */
        isOptional: Boolean
) : GenericElement(
        if (isOptional) ElementType.OPTION else ElementType.GROUP
) {
    init {
        gAlteration.father = this
    }
    
    override fun toString(): String {
        when (elementType) {
//                ElementType.GROUP -> return "($gAlteration)"
//                ElementType.OPTION -> return "[$gAlteration]"
            ElementType.GROUP -> return "group: $id"
            ElementType.OPTION -> return "option: $id"
            else -> throw IllegalArgumentException("unknown elementType")
        }
    }
}

/** STRING (constant in single quotes) or ID (parser rule or grammar rule)*/
class GenericElementLeaf(
        /** Содержимое элемента. Только если это string или id.  */
        val text: String,
        /** lexer rule or parser rule id, not a STRING const*/
        isId: Boolean
) : GenericElement(if (isId) ElementType.ID else ElementType.STRING) {
    
    /** Is a lexeme, not grammar rule. (Like NAME, NUMBER)*/
    override val isLexerRuleId: Boolean = isId && text == text.toUpperCase()
    
    /** Is a grammar rule. (Like stmt, assign)*/
    override val isParserRuleId: Boolean = isId && !isLexerRuleId
    
    
    override fun toString() =
            when (elementType) {
                ElementType.ID -> text
                ElementType.STRING -> "\'" + text + "\'"
                else -> throw IllegalArgumentException("unknown elementType")
            }
    
    
    /** Checks if the current elementLeaf matches [id].
     * @throws Exception If it matches and [GenericElementLeaf.text] != [checkText]*/
    fun checkById(id: Int, checkText: String): Boolean =
            if (id == this.id && checkText != text)
                throw Exception("Element leaf with id $id doesn't match the text $checkText. Real text is $text")
            else id == this.id
}