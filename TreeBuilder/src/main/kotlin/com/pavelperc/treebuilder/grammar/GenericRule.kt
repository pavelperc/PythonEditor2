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
    }
    
    val leaves: Sequence<GenericElementLeaf>
        get() = gAlteration.gConcatenations.asSequence().flatMap {
            it.gRepetitions.asSequence().flatMap {
                it.gElement.leaves
            }
        }
    
    override fun toString() = "$id:\n\t$gAlteration"
    
    
    // TODO remove all grouping tags from GenericRule. And from this module at all.
    /** Spreads grouping tag to all leaves of this generic rule*/
    fun setGroupingTag(tag: GroupingTag) {
        leaves.forEach { it.groupingTag = tag }
    }

//------------ nested Classes:
    
}


class GenericAlteration(
        val gConcatenations: MutableList<GenericConcatenation>
) {
    /** Отец заполняется в конструкторе gElementNode если мы к нему подключимся*/
    var father: GenericElementNode? = null
    
    
    init {
        gConcatenations.forEach { it.father = this }
    }
    
    
    override fun toString() = gConcatenations.joinToString(" | ")
    
    
}

class GenericConcatenation(
        val gRepetitions: MutableList<GenericRepetition>
) {
    
    /** Отец заполняется в конструкторе gAlt*/
    var father: GenericAlteration? = null
    
    init {
        gRepetitions.forEach { it.father = this }
    }
    
    override fun toString() = gRepetitions.joinToString(" ")
    
}

class GenericRepetition(
        val repetitive: Repetitive = Repetitive.NONE,
        var gElement: GenericElement,
        /** Отец заполняется в конструкторе gConc*/
        var father: GenericConcatenation? = null
) {
    
    init {
        gElement.father = this
    }
    
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
    
    
    override fun toString() = gElement.toString() + repetitive.toString()
    
}

abstract class GenericElement(
        val elementType: ElementType
) {
    
    companion object {
        private var lastId = 0
    }
    
    /** Identical number of Generic element among all*/
    val id = lastId++
    
    
    /** Означает, что соответствующий реализованный элемент
     * точно не может быть optional, даже если он полностью пустой.
     * Пометка true может установиться только внутри [RealizedRule.Element.isOptional]*/
    var checkedNonOptional: Boolean = false
    
    
    /** Отец заполняется в конструкторе gRep*/
    var father: GenericRepetition? = null
    
    /** все листья этой подветви, включая себя*/
    val leaves: Sequence<GenericElementLeaf>
        get() = when {
            this is GenericElementLeaf -> listOf(this).asSequence()
            this is GenericElementNode ->
                gAlteration.gConcatenations.asSequence().flatMap {
                    it.gRepetitions.asSequence().flatMap {
                        it.gElement.leaves
                    }
                }
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
    
    
    var gContext: GenericContext? = null
    
    
    val isString: Boolean
        get() = elementType == ElementType.STRING
    
    val isGroup: Boolean
        get() = elementType == ElementType.GROUP
    
    /** Проверка только на [] */
    val isOption: Boolean
        get() = elementType == ElementType.OPTION
    
    
    val isId: Boolean
        get() = elementType == ElementType.ID
    
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
    val isLexerRuleId: Boolean = isId && text == text.toUpperCase()
    
    val isParserRuleId: Boolean = isId && !isLexerRuleId
    
    
    
    init {
        if (this.isLexerRuleId) {
            // TODO delete GenericContext from GenericRule and this module
            gContext = GenericContextId(this)
        }
        
    }
    
    
    override fun toString() =
            when (elementType) {
                ElementType.ID -> text
                ElementType.STRING -> "\'" + text + "\'"
                else -> throw IllegalArgumentException("unknown elementType")
            }
    
    
    /** Checks if current elementLeaf matches [id].
     * @throws Exception If it matches and [GenericElementLeaf.text] != [checkText]*/
    fun checkById(id: Int, checkText: String): Boolean =
            if (id == this.id && checkText != text)
                throw Exception("Element leaf with id $id doesn't match the text $checkText. Real text is $text")
            else id == this.id
}