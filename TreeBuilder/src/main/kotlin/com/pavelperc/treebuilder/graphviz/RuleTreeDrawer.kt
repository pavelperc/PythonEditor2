package com.pavelperc.treebuilder.graphviz

import com.pavelperc.treebuilder.tree.*


class RuleTreeDrawer(
        val rootRule: RuleNode,
        val graph: Graph
) {
    fun draw() {
        rootRule.toGv(null)
        graph.writeToFile()
    }
    
    private fun RuleNode.toGv(father: GVNode?) {
        
        val me = graph.newNode()
        me.label = gRule.id
        me.color = "blue"
        me.shape = "box"
        
        if (father != null) {
            val edge = graph.newEdge(father, me)
        }
        
        concs.forEach { it.toGv(me) }
    }
    
    private fun Concatenation.toGv(father: GVNode) {
        
        val me = graph.newNode()
        me.label = "conc"
        
        val edge = graph.newEdge(father, me)
        
        repetitions.forEach { it.toGv(me) }
    }
    
    private fun Repetition.toGv(father: GVNode) {
        val me = graph.newNode()
        me.label = "rep$positionInFather"
        if (gRep.isMult)
            me.label += "*"
        else if (gRep.isPlus)
            me.label += "+"
        
        graph.newEdge(father, me)
        
        for (element in elements) {
            when (element) {
                is ElementLeaf -> element.toGv(me)
                is GroupNode -> element.toGv(me)
                is RuleNode -> element.toGv(me)
                else -> throw Exception("Unreachable")
            }
        }
        
        if (elements.isEmpty()) {
            // drawing nonexistent child
            val child = graph.newNode()
            child.label = gRep.gElement.toString()
            child.shape = "box"
            if (!gRep.isMult) {
                child.color = "red"
            }
            val edge = graph.newEdge(me, child)
            edge.style = "dotted"
        }
    }
    
    private fun ElementLeaf.toGv(father: GVNode) {
        val me = graph.newNode()
        val edge = graph.newEdge(father, me)
        
        me.fillColor = "orange"
        me.shape = "box"
        
        if (gElement.isString) {
            me.label = "'${gElement.text}'"
        } else {
            me.label = "${gElement.text}\n$text"
        }
    }
    
    private fun GroupNode.toGv(father: GVNode) {
        val me = graph.newNode()
        me.shape = "box"
        val edge = graph.newEdge(father, me)
        
        if (gElementNode.isOption) {
            me.label = "option${gElementNode.id}"
        } else {
            me.label = "group${gElementNode.id}"
        }
        
        concs.forEach { it.toGv(me) }
    }
    
    
}