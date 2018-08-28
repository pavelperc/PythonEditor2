package com.pavelperc.treebuilder

import com.pavelperc.treebuilder.grammar.GenericRule

/**
 * Расширение для RealizedRule.
 * Это класс, в котором будет храниться реализация основного (стартового) правила всей грамматики.
 * То есть вся программа на питоне.
 * Поэтому здесь будет храниться текущая позиция курсора,
 * будут методы для поиска всех подходящих элементов для реализации и.т.д.
 *
 * Created by pavel on 06.01.2018.
 */
abstract class MainRule(gRule: GenericRule, ruleMap: Map<String, GenericRule>) : RealizedRule(gRule, ruleMap, null) {
    
    /** Элемент выбранный ранее элемент, после которого должен быть найден currentElement.
    //     *  Если cursor равен null, то он левее самого первого */
//    var cursor: ElementLeaf? = null


//    /** Changes cursor and calls [findAlternatives].*/
//    abstract fun addToChain(leaf: ElementLeaf)
    
    
    /** Настройка начального currentElement */
    protected fun setupFirstElement(): ElementLeaf {
        // идём вниз по своему правилу
        var ans = ruleAlteration.concatenations[0].repetitions[0].firstElement
        
        while (ans is ElementNode) {
            // теперь идём вниз либо по чужим правилам либо по нашему правилу
            ans = ans.alteration.concatenations[0].repetitions[0].firstElement
        }
        Log.println("setup first currentElement: " + ans.gElement)
        return ans as ElementLeaf
    }
    
    
    /**
     * Retrieves alternatives, starting from cursor position.
     * After that it asks alternatives with [ask].
     * Then if an alternative is chosen method [build] should be called inside [ask].
     */
    fun findAlternatives() {
        ask(findAlternativesAndReturn())
    }
    
    /**
     * Retrieves alternatives, starting from cursor position and returns them
     */
    @Synchronized
    fun findAlternativesAndReturn(): List<ElementLeaf> {
        // if the cursor is null, then it is set to the left of the first Element
        
        /** first element, which will be proposed to choose.
         * may be null when [ElementLeaf.findNewCurrent] returns null */
        val currentElement = trueCursor.let {
            if (it == null)
                setupFirstElement()
            else
                it.findNewCurrent()
        }
        
        currentElement?.apply {
            Log.println("found new currentElement: $gElement : ${thisRule.gRule.id}")
        }
        
        val alternatives = currentElement?.findAlternativesUp(false) ?: emptyList()
        return alternatives
    }
    
    
    /** Outputs all alternatives and chooses one.
     * It must call method [build] itself after it chooses right alternative.*/
    abstract fun ask(alternatives: List<ElementLeaf>)
    
//    /** When we are trying to do autocompletion,
//     * [leaf] should be chosen by default without asking.*/
//    abstract fun autoChoose(leaf: ElementLeaf)
    
    /** In any realization cursor never points at indent or dedent.
    * Points only at VISIBLE element.*/
    var cursor: ElementLeaf? = null
    
    /** Finds right pointer for indent or dedent unlike [cursor] does.*/
    val trueCursor
        get() = cursor?.gContext?.let {
            if (it is GenericContextNewline)
                it.getLastIndentOrDedentToken(cursor!!.context)// nullable
            else null
        } ?: cursor
    
    
    /** Updates tree. Connects [leftLeaf] with [chosen]*/
    fun build(leftLeaf: ElementLeaf?, chosen: ElementLeaf) {
        Log.println("----------IN BUILD----------")
        
        chosen.updateAllChosen()

//        val leftLeaf = trueCursor
        
        // connect with previous
        leftLeaf?.rightLeaf = chosen;
        chosen.leftLeaf = leftLeaf;
        Log.println("In Build: connected '$chosen' with '$leftLeaf'")
        
        
        chosen.gContext.also {
            if (it is GenericContextLeaf)
                it.onChoose(chosen.context)
        }
    }
}
