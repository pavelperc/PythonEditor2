package com.pavelperc.treebuilder.graphviz

import com.pavelperc.treebuilder.grammar.*

object GenericRulesDrawer {
    
    fun drawGv(ruleMap: RuleMap, fileName: String, graphLabel: String) {
        val graph = Graph(fileName, graphLabel)
        ruleMap.toGv(graph)
        graph.writeToFile()
    }
    
    private fun RuleMap.toGv(graph: Graph) {
        for (rule in values) {
            val node = graph.newNode()
            node.label = rule.id
            
            rule.toGv(graph, node)
        }
    }
    
    
    private fun GenericRule.toGv(graph: Graph, me: GVNode) {
        // alt node
        val node = graph.newNode()
        node.label = "alt"
    
        graph.newEdge(me, node);
        
        gAlteration.toGv(graph, node)
    }
    
    private fun GenericAlteration.toGv(graph: Graph, me: GVNode) {
        
        
        for (conc in gConcatenations) {
            // conc node
            val node = graph.newNode()
            node.label = "conc"
            graph.newEdge(me, node)
            conc.toGv(graph, node)
        }
    }
    
    private fun GenericConcatenation.toGv(graph: Graph, me: GVNode) {
        for ((pos, repetition) in gRepetitions.withIndex()) {
            // rep node
            val node = graph.newNode()
            node.label = "rep$pos"
            
            val edge = graph.newEdge(me, node);
            
            if (repetition.repetitive == GenericRepetition.Repetitive.MULT)
                node.label += "*"
            else if (repetition.repetitive == GenericRepetition.Repetitive.PLUS)
                node.label += "+"
            
            repetition.toGv(graph, node)
        }
    }
    
    private fun GenericRepetition.toGv(graph: Graph, me: GVNode) {
        with(gElement) {
            // el node
            val node = graph.newNode()
            val edge = graph.newEdge(me, node)
            
            if (this is GenericElementLeaf) {
                node.label = text
            } else if (isGroup) {
                node.label = "group"
            } else if (isOption) {
                node.label = "option"
            }
            node.label += "\n$id"
            
            node.shape = "box"
            
            // если это похоже на parserRuleId
            if (isId && this is GenericElementLeaf && !this.isLexerRuleId) {
                node.color = "blue"
            } else if (isId || isString) {
                node.fillColor = "orange"
            }
            this.toGv(graph, node)
        }
    }
    
    private fun GenericElement.toGv(graph: Graph, me: GVNode) {
        if (this !is GenericElementNode)
            return
        
        // alt node
        val node = graph.newNode()
        val edge = graph.newEdge(me, node)
        
        node.label = "alt"
        
        gAlteration.toGv(graph, node)
    }
    
}
