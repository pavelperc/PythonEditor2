package com.pavelperc.treebuilder


// все статические вложенные классы для GenericRule
import com.pavelperc.treebuilder.GenericRule.*
import com.pavelperc.treebuilder.abnf.AbnfBaseListener
import com.pavelperc.treebuilder.abnf.AbnfBaseVisitor
import com.pavelperc.treebuilder.abnf.AbnfLexer
import com.pavelperc.treebuilder.abnf.AbnfParser
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CharStream
import org.antlr.v4.runtime.CommonTokenStream
import java.io.FileInputStream
import java.io.IOException
import java.util.*

/**
 * Создаёт map со всеми правилами для грамматики
 *
 * Created by pavel on 13.11.2017.
 */
object MyVisitor {
    fun generateRuleMap(grammar: String): MutableMap<String, GenericRule> {
        //        InputStream fin = new FileInputStream("grammar.txt");
//        val fin = FileInputStream(filename)
        
        val input = ANTLRInputStream(grammar)
        // Настраиваем лексер на этот поток
        val lexer = AbnfLexer(input)
        // Создаем поток токенов на основе лексера
        val tokens = CommonTokenStream(lexer)
        // Создаем парсер
        val parser = AbnfParser(tokens)
        
        val ruleListVisitor = RuleListVisitor()
        
        val ruleMap = ruleListVisitor.visitRulelist(parser.rulelist())
//         закрываем файл, так как мы уже точно всё считали
//        fin.close()
        return ruleMap
    }
    
    fun drawGv(ruleMap: MutableMap<String, GenericRule>, fileName: String, graphLabel: String) {
        val graph = Graph(fileName, graphLabel)
        toGv(ruleMap, graph)
        graph.writeToFile()
    }
    
    private fun toGv(ruleMap: Map<String, GenericRule>, graph: Graph) {
        
        val nodes = ArrayList<String>()
        val edges = ArrayList<String>()
        
        for (rule in ruleMap.values) {
            
            val node = graph.newNode()
            node.label = rule.id
            
            rule.toGv(graph, node)
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
    fun optimizeRuleMap(ruleMap: MutableMap<String, GenericRule>) {
        
        val usagesMap = mutableMapOf<GenericRule, Int>()
        
        for (rule in ruleMap.values) {
            usagesMap[rule] = 0
        }
        
        
        // заполняем количество использований правил
        for (rule in ruleMap.values) {
            val leaves = rule.leaves
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
            val leaves = rule.leaves
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
    
    /**
     * Визитор для списка правил.
     * Когда посещаем список всех правил - возвращаем map
     */
    class RuleListVisitor : AbnfBaseVisitor<Map<String, GenericRule>>() {
        
        override fun visitRulelist(ctx: AbnfParser.RulelistContext): MutableMap<String, GenericRule> {
            val ruleMap = mutableMapOf<String, GenericRule>()
            
            // По всем детям списка правил. (то есть по всем описаниям правил.)
            for (rule_context in ctx.rule_()) {
                val ruleVisitor = RuleVisitor()
                // Посещаем описание правила визитором для правил
                // получаем наш основной класс с правилом
                val rule = rule_context.accept(ruleVisitor)
                // добавляем в map
                ruleMap[rule.id] = rule
            }
            return ruleMap
        }
    }
    
    /**
     * Визитор для одного правила
     */
    private class RuleVisitor : AbnfBaseVisitor<GenericRule>() {
        override fun visitRule_(ctx: AbnfParser.Rule_Context): GenericRule {
            // добавляем gAlteration, посещая ребёнка через AlterationVisitor
            val rule = GenericRule(
                    ctx.id().text,
                    ctx.elements().alternation().accept(AlterationVisitor())
            )
            return rule
        }
    }
    
    /**
     * Визитор для GenericAlteration
     */
    private class AlterationVisitor : AbnfBaseVisitor<GenericAlteration>() {
        override fun visitAlternation(ctx: AbnfParser.AlternationContext): GenericAlteration {
            // Берём всех детей из контекста alteration и преобразовываем их 
            // в список GenericConcatenation, посещая их через ConcatenationVisitor
            val concatenations = ctx.concatenation()
                    .map { it.accept(ConcatenationVisitor()) }.toMutableList()
            
            return GenericAlteration(concatenations)
        }
    }
    
    /**
     * Визитор для GenericConcatenation
     */
    private class ConcatenationVisitor : AbnfBaseVisitor<GenericConcatenation>() {
        override fun visitConcatenation(ctx: AbnfParser.ConcatenationContext): GenericConcatenation {
            val repetitions = ctx.repetition()
                    .map { rep ->
                        // посещаем каждый repetition вместе с repeat но с помощью лисенера
                        val repetitionListener = RepetitionListener()
                        repetitionListener.enterRepetition(rep)
                        repetitionListener.getGenericRepetition()// возвращаем объект GenericRepetition
                    }.toMutableList()
            
            return GenericConcatenation(repetitions)
        }
        
        /**
         * Посещение repetition и repeat через listener
         */
        internal inner class RepetitionListener : AbnfBaseListener() {
            
            private var gRepetition: GenericRepetition? = null
            private var repetitive: GenericRepetition.Repetitive = GenericRepetition.Repetitive.NONE
            
            /**
             * Получить repetition из листенера
             * @return объект GenericRepetition
             */
            fun getGenericRepetition(): GenericRepetition = gRepetition
                    ?: throw NullPointerException("repetition has not been visited with RepetitionListener")
            
            
            override fun enterRepetition(ctx: AbnfParser.RepetitionContext) {
                val element = ctx.element().accept(ElementVisitor())
                
                
                // заполняет поле repetitive
                // либо repetitive остаётся NONE по умолчанию
                ctx.repeat()?.enterRule(this)
                
                
                gRepetition = GenericRepetition(repetitive, element)
                
            }
            
            override fun enterRepeat(ctx: AbnfParser.RepeatContext) {
                if (ctx.text == "+")
                    repetitive = GenericRepetition.Repetitive.PLUS
                else if (ctx.text == "*")
                    repetitive = GenericRepetition.Repetitive.MULT
                else
                    throw Error("unrecognized symbol instead of + or *")
            }
        }
    }
    
    /**
     * Визитор для GenericElement
     */
    private class ElementVisitor : AbnfBaseVisitor<GenericElement>() {
        override fun visitElement(ctx: AbnfParser.ElementContext): GenericElement {
            // выбираем тип элемента: id | group | option | STRING | NumberValue | ProseValue
            return when {
                ctx.group() != null -> // создаём элемент как группу, передаём alternation
                    GenericElementNode(GroupVisitor().visitGroup(ctx.group()), false)
                ctx.option() != null -> // создаём элемент как опциональную группу, передаём alternation
                    GenericElementNode(OptionVisitor().visitOption(ctx.option()), true)
                ctx.id() != null -> GenericElementLeaf(ctx.text, true)
                ctx.STRING() != null -> GenericElementLeaf(ctx.text.substring(1, ctx.text.length - 1), false)
                else -> throw RuntimeException("unhandled type of GenericElement: NumberValue or ProseValue")
            }
        }
    }
    
    /**
     * Визитор для Group
     */
    private class GroupVisitor : AbnfBaseVisitor<GenericAlteration>() {
        override fun visitGroup(ctx: AbnfParser.GroupContext): GenericAlteration {
            return AlterationVisitor().visitAlternation(ctx.alternation())
        }
    }
    
    /**
     * Визитор для Option
     */
    private class OptionVisitor : AbnfBaseVisitor<GenericAlteration>() {
        override fun visitOption(ctx: AbnfParser.OptionContext): GenericAlteration {
            return AlterationVisitor().visitAlternation(ctx.alternation())
        }
    }
}
