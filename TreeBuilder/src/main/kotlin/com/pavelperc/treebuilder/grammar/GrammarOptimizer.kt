package com.pavelperc.treebuilder.grammar

import java.util.ArrayList

object GrammarOptimizer {

    /** recursively simplifies groups in such cases:
     *
     * (a | b) | c -> a | b | c
     *
     * (a | b) c* -> a c* | b c*
     * */
    private fun GenericAlteration.reduceGroups() {
        // conc to remove from gConcs or add
        val forRemoval = ArrayList<GenericConcatenation>()
        val forAddition = ArrayList<GenericConcatenation>()



        for (conc in gConcatenations) {
            // упрощаем случай лишних скобок:  (a | b) | c -> a | b | c
            if (conc.gRepetitions.size == 1
                    && conc.gRepetitions[0].isNone
                    && conc.gRepetitions[0].gElement.isGroup) {
                forAddition.addAll(conc.gRepetitions[0].gElementNode.gAlteration.gConcatenations)
                forRemoval.add(conc)
            }
        }
        gConcatenations.removeAll(forRemoval)
        gConcatenations.addAll(forAddition)

        // идём в рекурсию
        for (conc in gConcatenations) {
            for (rep in conc.gRepetitions) {
                if (rep.gElement is GenericElementNode) {
                    rep.gElementNode.gAlteration.reduceGroups()
//                        log?.println("reduce groups: in rep: $rep")
                }
            }
        }


        forRemoval.clear()
        forAddition.clear()
        // теперь заново проходимся по всем conc
        for (conc in gConcatenations) {
            // раскрытие скобок: (a | b) c* -> a c* | b c*
            if (conc.gRepetitions.size == 2
                    && conc.gRepetitions[0].isNone
                    && conc.gRepetitions[0].gElement.isGroup
                    && conc.gRepetitions[1].gElement !is GenericElementNode) {

                forRemoval.add(conc)

                val concsInGroup = conc.gRepetitions[0].gElementNode.gAlteration.gConcatenations
                val simpleFactor = conc.gRepetitions[1]

                concsInGroup.forEach { it -> it.gRepetitions.add(simpleFactor) }

                forAddition.addAll(concsInGroup)
            }
            //                // раскрытие скобок: c* (a | b) -> c* a | c* b
            //                else if (conc.gRepetitions.size() == 2
            //                        && conc.gRepetitions.get(1).isNone()
            //                        && conc.gRepetitions.get(1).gElement.isGroup()
            //                        && !conc.gRepetitions.get(0).gElement.isGroupOrOption()) {
            //                    
            //                    forRemoval.add(conc);
            //    
            //                    List<GenericConcatenation> concsInGroup = conc.gRepetitions.get(1).gElement.gAlteration.gConcatenations;
            //                    GenericRepetition simpleFactor = conc.gRepetitions.get(0);
            //                    
            //                    // добавляем новый множитель а начало
            //                    concsInGroup.forEach(it -> it.gRepetitions.add(0, simpleFactor));
            //    
            //                    forAddition.addAll(concsInGroup);
            //                }
        }

        gConcatenations.removeAll(forRemoval)
        gConcatenations.addAll(forAddition)


        // ещё раз идём в рекурсию
        for (conc in gConcatenations) {
            for (rep in conc.gRepetitions) {
                if (rep.gElement is GenericElementNode) {
                    rep.gElementNode.gAlteration.reduceGroups()
                }
            }
        }
    }
    
    
    /**
     * Replaces all rules with groups if they occur only once.
     * Removes unused rules.
     * And simplifies groups like this:
     *
     * (a | b) | c -> a | b | c
     *
     * (a | b) c* -> a c* | b c*
     */
    fun optimizeRuleMap(ruleMap: MutableRuleMap) {
        
        val usagesMap = mutableMapOf<GenericRule, Int>()
        
        for (rule in ruleMap.values) {
            usagesMap[rule] = 0
        }
        
        
        // заполняем количество использований правил
        for (rule in ruleMap.values) {
            val leaves = rule.allLeaves
            for (leaf in leaves) {
                
                // листы, имена которых есть в списке правил
                if (ruleMap.contains(leaf.text)) {
                    // то правило, на которое ссылается ребёнок
                    val childRule = ruleMap[leaf.text]!!
                    
                    usagesMap[childRule] = usagesMap[childRule]!! + 1
                }
            }
        }

//        usagesMap.forEach { key, value ->
//            println("${key.id}: $value")
//        }
        
        val forRemoval = mutableListOf<GenericRule>()
        
        // заменяем все правила на группы, если они встречаются не более 1 раза
        for (rule in ruleMap.values) {
            val leaves = rule.allLeaves
            for (leaf in leaves) {
                // листы, имена которых есть в списке правил (то есть правила)
                if (ruleMap.containsKey(leaf.text)) {
                    // то правило, на которое ссылается ребёнок
                    val childRule = ruleMap[leaf.text]!!
                    
                    // избегаем рекурсии
                    if (childRule === rule)
                        continue


//                    if (childRule.gAlteration.let { 
//                                it.gConcatenations.size == 1 
//                            && it.gConcatenations[0].gRepetitions.size == 1}) {
//                        
//                        childRule.gAlteration.gConcatenations[0].gRepetitions[0].gElement.also {
//                            if (it is GenericElementLeaf) {
//                                // всё скопировать
//                            }
//                        }
//                                    
//                    }
                    
                    if (usagesMap[childRule]!! <= 1) {
//                        log?.println("attached: ${rule.id} <-- ${childRule.id}")
                        // осздаём новую группу вместо листа
                        // внутри конструктора у childRule.gAlteration появляется отец
                        val newNode = GenericElementNode(childRule.gAlteration, false);
                        
                        // искать usages <= 2, то может присвоиться одно и то же gAlteration двум вершинам
                        
                        // связываем отца и сына
                        newNode.father = leaf.father
                        leaf.father!!.gElement = newNode
                        
                        // уменьшаем количество использований правила
                        usagesMap[childRule] = usagesMap[childRule]!! - 1
                        
                        // очищаем ненужные правила
                        if (usagesMap[childRule]!! <= 0) {
                            usagesMap.remove(childRule)
                            forRemoval.add(childRule)
                        }
                    }
                }
            }
        }
        
        forRemoval.forEach { rule -> ruleMap.remove(rule.id) }
        
        
        
        for (rule in ruleMap.values) {
            rule.gAlteration.reduceGroups()
        }
    }

}
