package com.pavelperc.treebuilder.graphviz

import com.pavelperc.treebuilder.Grammars
import org.junit.Test

import org.junit.Assert.*

class GenericRulesDrawerTest {
    
    @Test
    fun drawFullGrammarGw() {
        val ruleMap = Grammars.parseFullGrammar()
        GenericRulesDrawer(ruleMap,
                Graph("chains/DrawFullGrammarTest.gv", "original rules: ${ruleMap.size}")).drawGv()
        
    }
}