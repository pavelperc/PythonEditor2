package com.pavelperc.treebuilder

import com.pavelperc.treebuilder.RealizedRule.Element
import com.pavelperc.treebuilder.RealizedRule.ElementLeaf
import com.pavelperc.treebuilder.RealizedRule.ElementNode

/**
 * It is responsible for navigating through the syntax tree. (Mostly amoung Chosen elements).
 * Also it contains [storage] with some data, special for its [element].
 * It is supposed that all concrete actions are described in [GenericContext].
 */
abstract class Context(open val element: Element) {
    
    /** It is a map: tag -> some raw data.
     * Concrete [GenericContext] objects can take or put here whatever they want.*/
    val storage = mutableMapOf<String, Any>()
    
    val gContext: GenericContext?
        get() = element.gElement.gContext
    
    /** Step object, which can go to upper levels of tree.*/
    val upStep: Step get() = UpStep(this)
    /** Step object, which can go left in neighbour conc or rep.*/
    val leftStep: Step get() = LeftStep(this)
    /** Step object, which can go right in neighbour conc or rep.*/
    val rightStep: Step get() = RightStep(this)
    /** Step object, which can go to the left realized child.*/
    val downLeftStep: Step get() = DownLeftStep(this)
    /** Step object, which can go to the right realized child.*/
    val downRightStep: Step get() = DownRightStep(this)
    /** Step object, which can go left even to different [Element.fatherElement] 
     * (another branch of the tree)*/
    val jumpLeftStep: Step get() = JumpLeftStep(this)
    
    private class UpStep(ctx: Context) : Step(ctx) {
        override fun Context.oneStep(): Context? =
                element.fatherElement?.context
    }
    
    
    private class LeftStep(ctx: Context) : Step(ctx) {
        override fun Context.oneStep(): Context? =
                element.run {
                    if (positionInFather > 0)
                    // move left inside repetition
                        father.realizedElements[positionInFather - 1].context
                    else
                    // try to move left inside concatenation
                    // but we should skip concs without reps with realized elements
                        father.let { rep ->
                            rep.father.repetitions
                                    .take(rep.positionInFather)// take only left brothers of rep
                                    .lastOrNull { it.realizedElements.size > 0 }// find last with realized reps
                                    ?.realizedElements?.last()?.context
                        }
                }
    }
    
    private class RightStep(ctx: Context) : Step(ctx) {
        override fun Context.oneStep(): Context? =
                element.run {
                    if (positionInFather < father.realizedElements.size - 1)
                    // move right inside repetition
                        father.realizedElements[positionInFather + 1].context
                    else
                    // try to move right inside concatenation
                    // but we should skip concs without reps with realized elements
                        father.let { rep ->
                            rep.father.repetitions
                                    .takeLast(rep.father.repetitions.size - rep.positionInFather - 1)
                                    .firstOrNull { it.realizedElements.size > 0 }
                                    ?.realizedElements?.first()?.context
                        }
                }
    }
    
    private class DownLeftStep(ctx: Context) : Step(ctx) {
        override fun Context.oneStep(): Context? =
                if (this is ContextNode) {
                    element.alteration.chosen?.repetitions
                            ?.firstOrNull { rep -> rep.realizedElements.size > 0 }
                            ?.realizedElements?.first()?.context
                } else
                    null
    }
    
    private class DownRightStep(ctx: Context) : Step(ctx) {
        override fun Context.oneStep(): Context? =
                if (this is ContextNode) {
                    element.alteration.chosen?.repetitions
                            ?.lastOrNull { rep -> rep.realizedElements.size > 0 }
                            ?.realizedElements?.last()?.context
                } else
                    null
    }
    
//    /** Returns the context of next left element in current depth level of the tree
//     * (or null if it doesn't exist).
//     * The returned element will be definitely realized.*/
//    fun jumpLeft(): Context? =
//            if (element.positionInFather > 0)
//            // move left inside repetition
//                element.father.realizedElements[element.positionInFather - 1].context
//            else
//            // try to move left inside concatenation
//            // but we should skip concs without reps with realized elements
//                element.father.father.repetitions
//                        .take(element.father.positionInFather)// take only left brothers of conc
//                        .lastOrNull { it.realizedElements.size > 0 }// find last with realized reps
//                        ?.realizedElements?.last()?.context// get its context
//                // elvis: if there are no appropriate concs found on the left
//                        ?: let {
//                            // move left in upper level of the tree while we can't return down to this level of tree
//                            var leftAbove = element.fatherElement?.context?.jumpLeft()// recursive call for upper element
//                            while (leftAbove != null && leftAbove.element is RealizedRule.ElementLeaf) {
//                                leftAbove = leftAbove.jumpLeft()
//                            }
//                            // here leftAbove is either null or context of ElementNode with realized concs
//                            leftAbove?.run {
//                                // convert leftAbove to its right realized element (if it is not null)
//                                (element as RealizedRule.ElementNode).alteration.chosen!!.repetitions
//                                        .last { it.realizedElements.size > 0 }.realizedElements.last().context
//                            }
//                        }
    
    private class JumpLeftStep(ctx: Context) : Step(ctx) {
        override fun Context.oneStep(): Context? =
                this.leftStep.go() ?: run {
                    // move left in upper level of the tree while we can't return down to this level of tree
                    var leftAbove = upStep.go()?.oneStep()// recursive call for upper element
                    while (leftAbove != null && leftAbove.element is RealizedRule.ElementLeaf) {
                        leftAbove = leftAbove.oneStep()
                    }
                    // here leftAbove is either null or context of ElementNode with realized concs
                    leftAbove?.downRightStep?.go()
                }
    }


//    inline fun <reified T : GenericContext> jumpLeftTyped(): Context? {
//        var left = jumpLeft()
//        while (left != null && left.gContext !is T) {
//            left = jumpLeft()
//        }
//        return left
//    }


//    /** Sequence of all contexts with [gContext] of type [T] */
//    inline fun <reified T : GenericContext> leftTypedSequence() = Sequence {
//        object : Iterator<Context> {
//            var next = jumpLeftTyped<T>()
//            
//            override fun hasNext() = next != null
//            
//            override fun next(): Context {
//                val saved = next
//                next = jumpLeftTyped<T>()
//                return saved ?: throw Exception("next called when hasNext was false")
//            }
//        }
//    }
    
    /** Defines navigation through the tree.
     * Can go with typed filtersin some direction or generate sequences.
     * @param ctx Initial point for step.*/
    abstract class Step(open val ctx: Context) {
        
        /**One step in a given direction. Should be overridden in concrete steps.*/
        protected abstract fun Context.oneStep(): Context?
        
        /** public wrapper of [oneStep]. Returns context of next step.*/
        open fun go(): Context? = ctx.oneStep()
                .also { Log.println("after ${this::class.java.simpleName}: ${ctx} to $it") }
        
        /** Lazy generation of list with elements in given direction.*/
        open fun asSequence() = Sequence<Context> {
            object : Iterator<Context> {
                
                private var cashedNext: Context? = null
                
                private var curr: Context = ctx
                
                private var hasCashedNext = false
                
                override fun hasNext(): Boolean {
                    if (!hasCashedNext) {
                        cashedNext = curr.oneStep()
                        
                        hasCashedNext = true
                    }
                    
                    Log.println("after hasNext in ${this@Step::class.java.simpleName}: $curr to $cashedNext")
                    
                    return cashedNext != null
                }
                
                override fun next(): Context {
                    if (hasCashedNext) {
                        curr = cashedNext ?: throw Exception("next at the end of sequence")
                        hasCashedNext = false
                    } else {
                        curr = curr.oneStep() ?: throw Exception("next at the end of sequence")
                    }
                    
                    return curr
                }
                
            }
        }
        
        /** Finds first context with genericContext of type [T].
         * @return a pair of found realized context and its genericContext, casted to T*/
        inline fun <reified T : GenericContext> goTyped(): Pair<Context, T>? {
            return asSequence()
                    .firstOrNull { it.gContext is T }
                    ?.let { Pair(it, it.gContext as T) }
        }
        
        /** Combination of [asSequence] and [goTyped]*/
        inline fun <reified T : GenericContext> asTypedSequence() =
                asSequence()
                        .filter { it.gContext is T }
                        .map { Pair(it, it.gContext as T) }
    }
    
    override fun toString(): String {
        return element.toString()
    }
}

/** Extension of [Context] for [ElementLeaf]*/
class ContextLeaf(override val element: ElementLeaf) : Context(element) {
    val leftLeafStep: StepLeaf get() = LeftLeafStep(this)
    val rightLeafStep: StepLeaf get() = RightLeafStep(this)
    
    /** Finds first realized leaf on the right.
     * Works only with leaves. Uses cashed [ElementLeaf.rightLeaf].*/
    private class RightLeafStep(ctx: ContextLeaf) : StepLeaf(ctx) {
        override fun Context.oneStep(): ContextLeaf? {
            
            return (this as? ContextLeaf)?.element?.rightLeaf?.context
            
        }
    }
    
    /** Finds first realized leaf on the left. Uses cashed [ElementLeaf.rightLeaf].*/
    private class LeftLeafStep(ctx: ContextLeaf) : StepLeaf(ctx) {
        override fun Context.oneStep(): ContextLeaf? {
            
            // return saved leftLeaf!!!
            return (this as? ContextLeaf)?.element?.leftLeaf?.context
    
//            var curr: Context = this
//            // up
//            while (curr.leftStep.go() == null) {
//                curr = curr.upStep.go() ?: return null
//            }
//            // left
//            curr = curr.leftStep.go()!!
//            // down
//            while (curr !is ContextLeaf) {
//                // always can go down in not leaf
//                curr = curr.downRightStep.go() ?: throw Exception("Can not go down from not ContextLeaf.")
//            }
//            return curr
        }
    }
    
    /** Extension of [Step] for [ContextLeaf]*/
    abstract class StepLeaf(override val ctx: ContextLeaf) : Step(ctx) {
        
        /**One step in a given direction.*/
        abstract override fun Context.oneStep(): ContextLeaf?
        
        /** public wrapper of [oneStep]. Returns [ContextLeaf] of next step.*/
        override fun go(): ContextLeaf? = (super.go() as ContextLeaf?)
                .also { Log.println("after StepLeaf: ${this::class.java.simpleName}: ${ctx} to $it") }
        
        
        override fun asSequence() = super.asSequence().map { it as ContextLeaf }
        
        /** Wrapper for [goTyped], returning a pair [Context], casted to [ContextLeaf]*/
        inline fun <reified T : GenericContext> goTypedLeaf(): Pair<ContextLeaf, T>? =
                goTyped<T>()?.let { Pair(it.first as ContextLeaf, it.second) }
        
        
        /** Wrapper for [asTypedSequence], returning a pair [Context], casted to [ContextLeaf]*/
        inline fun <reified T : GenericContext> asTypedSequenceLeaves() =
                asTypedSequence<T>().map { Pair(it.first as ContextLeaf, it.second) }
    }
}

/** Extension of [Context] for [ElementNode]*/
class ContextNode(override val element: ElementNode) : Context(element) {
    
}



