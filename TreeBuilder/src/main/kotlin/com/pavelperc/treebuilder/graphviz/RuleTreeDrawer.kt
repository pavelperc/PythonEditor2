package com.pavelperc.treebuilder.graphviz

import com.pavelperc.treebuilder.tree.*


class RuleTreeDrawer(val rootRule: RealizedRule, val graph: Graph, val onlyChosen: Boolean) {
    
    fun draw() {
        rootRule.toGv(graph, onlyChosen)
        graph.writeToFile()
    }
    
    
    private fun RealizedRule.toGv(graph: Graph, onlyChosen: Boolean) {
        val me = graph.newNode()
        me.label = gRule.id
        if (onlyChosen) {
            ruleAlteration.chosen?.repetitions?.forEach {
                it.toGv(graph, me, onlyChosen)
            }
            return
        }
        
        // alt node
        val node = graph.newNode()
        
        node.label = "alt"
        
        val edge = graph.newEdge(me, node)
        if (!ruleAlteration.isChosen) {
            edge.style = "dotted"
        }
        
        ruleAlteration.toGv(graph, node, onlyChosen)
    }
    
    private fun Alteration.toGv(graph: Graph, me: GVNode, onlyChosen: Boolean) {
        
        for (conc in concatenations) {
            if (onlyChosen && conc != chosen)
                continue
            
            // conc node
            val node = graph.newNode()
            node.label = "conc"
            val edge = graph.newEdge(me, node)
            
            
            if (conc !== chosen) {
                edge.style = "dotted"
            }
            
            conc.toGv(graph, node, onlyChosen)
        }
    }
    
    private fun Concatenation.toGv(graph: Graph, me: GVNode, onlyChosen: Boolean) {
        for (rep in repetitions) {
            
            if (onlyChosen && rep.realizedElements.size == 0)
                continue
            
            // rep node
            val node = graph.newNode()
            node.label = "rep" + rep.positionInFather
            if (rep.isMult)
                node.label += "*"
            else if (rep.isPlus)
                node.label += "+"
            
            
            val edge = graph.newEdge(me, node)
            
            if (rep.realizedElements.size == 0) {
                edge.style = "dotted"
            }
            
            rep.toGv(graph, node, onlyChosen)
        }
    }
    
    private fun Repetition.drawGvElement(
            element: Element,
            graph: Graph,
            me: GVNode,
            onlyChosen: Boolean
    ) {
        // el node
        val node = graph.newNode()
        val edge = graph.newEdge(me, node)
        
        
        node.label = element.gElement.toString()
//            if (element.gElement.isGroup) {
//                node.label = "group"
//            } else if (element.gElement.isOption) {
//                node.label = "option"
//            } else if (element.gElement is GenericElementLeaf) {
//                node.label = (element.gElement as GenericElementLeaf).text
//            }
        
        node.shape = "box"
        
        if (element is ElementNode && element.isParserRuleId) {
            node.color = "blue"
        }
        
        
        if (!element.isEmpty && element is ElementLeaf) {
            node.fillColor = "orange"
        }
        
        if (element.isEmpty) {
            edge.style = "dotted"
        }
        
        element.toGv(graph, node, onlyChosen)
    }
    
    private fun Repetition.toGv(graph: Graph, me: GVNode, onlyChosen: Boolean) {
        // для начала рисуем пустой элемент
        if (!onlyChosen && !isFull && lastCreatedEmptyElement != null && lastCreatedEmptyElement!!.isEmpty) {
            //                createEmptyElement(0);
            drawGvElement(lastCreatedEmptyElement!!, graph, me, onlyChosen)
        }
        
        
        for (element in realizedElements) {
//                if (onlyChosen && element.isEmpty)
//                    continue
            drawGvElement(element, graph, me, onlyChosen)
        }
    }
    
    private fun Element.toGv(graph: Graph, me: GVNode, onlyChosen: Boolean) {
        if (this !is ElementNode)
            return
        
        if (onlyChosen) {
            alteration.chosen?.repetitions?.forEach {
                // пропускаем alt и conc и добавляем сразу все реализованные conc
                it.toGv(graph, me/*!!!*/, onlyChosen)
            }
            return
        }
        
        // alt node
        val node = graph.newNode()
        node.label = "alt"
        
        val edge = graph.newEdge(me, node)
        
        if (isEmpty) {
            edge.style = "dotted"
        }
        
        
        alteration.toGv(graph, node, onlyChosen)
    }
}