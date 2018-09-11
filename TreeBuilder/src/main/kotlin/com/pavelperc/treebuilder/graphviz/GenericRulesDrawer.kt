package com.pavelperc.treebuilder.graphviz

import com.pavelperc.treebuilder.grammar.*

class GenericRulesDrawer(
        val ruleMap: RuleMap,
        val graph: Graph
) {
    
    fun drawGv() {
        ruleMap.toGv()
        graph.writeToFile()
    }
    
    private fun RuleMap.toGv() {
        for (rule in values) {
            val node = graph.newNode()
            node.label = rule.id
            
            rule.toGv(node)
        }
    }
    
    
    private fun GenericRule.toGv(me: GVNode) {
        // alt node
        val node = graph.newNode()
        node.label = "alt"
    
        graph.newEdge(me, node);
        
        gAlteration.toGv(node)
    }
    
    private fun GenericAlteration.toGv(me: GVNode) {
        
        
        for (conc in gConcatenations) {
            // conc node
            val node = graph.newNode()
            node.label = "conc"
            graph.newEdge(me, node)
            conc.toGv(node)
        }
    }
    
    private fun GenericConcatenation.toGv(me: GVNode) {
        for ((pos, repetition) in gRepetitions.withIndex()) {
            // rep node
            val node = graph.newNode()
            node.label = "rep$pos"
            
            graph.newEdge(me, node);
            
            if (repetition.repetitive == GenericRepetition.Repetitive.MULT)
                node.label += "*"
            else if (repetition.repetitive == GenericRepetition.Repetitive.PLUS)
                node.label += "+"
            
            repetition.toGv(node)
        }
    }
    
    private fun GenericRepetition.toGv(me: GVNode) {
        with(gElement) {
            // el node
            val node = graph.newNode()
            graph.newEdge(me, node)
            
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
            this.toGv(node)
        }
    }
    
    private fun GenericElement.toGv(me: GVNode) {
        if (this !is GenericElementNode)
            return
        
        // alt node
        val node = graph.newNode()
        graph.newEdge(me, node)
        
        node.label = "alt"
        
        gAlteration.toGv(node)
    }
    
}
