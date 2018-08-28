package com.pavelperc.treebuilder.grammar

import java.util.ArrayList

object GrammarOptimizer {

    /** recursively simplifies groups in such cases:
     *
     * (a | b) | c -> a | b | c
     *
     * (a | b) c* -> a c* | b c*
     * */
    fun GenericAlteration.reduceGroups() {
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

}
