package com.pavelperc.treebuilder.grammar


// все статические вложенные классы для GenericRule
import com.pavelperc.treebuilder.abnf.AbnfBaseListener
import com.pavelperc.treebuilder.abnf.AbnfBaseVisitor
import com.pavelperc.treebuilder.abnf.AbnfLexer
import com.pavelperc.treebuilder.abnf.AbnfParser
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream

typealias MutableRuleMap = MutableMap<String, GenericRule>

typealias RuleMap = Map<String, GenericRule>

/**
 * Создаёт map со всеми правилами для грамматики
 *
 * Created by pavel on 13.11.2017.
 */
object MyVisitor {
    fun generateRuleMap(grammar: String): MutableRuleMap {
        
        val input = ANTLRInputStream(grammar)
        // Настраиваем лексер на этот поток
        val lexer = AbnfLexer(input)
        // Создаем поток токенов на основе лексера
        val tokens = CommonTokenStream(lexer)
        // Создаем парсер
        val parser = AbnfParser(tokens)
        
        val ruleListVisitor = RuleListVisitor()
        
        val ruleMap = ruleListVisitor.visitRulelist(parser.rulelist())
        
        checkParserRuleNames(ruleMap)
        
        return ruleMap
    }
    
    /** Checks if all parser rule names (lowercase) are contained in the ruleMap*/
    private fun checkParserRuleNames(ruleMap: MutableRuleMap) {
        ruleMap.values
                .forEach { rule ->
                    rule.leaves
                            .filter { it.isParserRuleId }
                            .forEach {
                                if (!ruleMap.containsKey(it.text))
                                    throw IllegalArgumentException("Rule ${rule.id} has lowerCase leaf element ${it.text}, " +
                                            "which is not const and not parser rule (type is ${it.elementType}! " +
                                            "All lexer rules should be upperCase!")
                            }
                }
        
    }
    
    
    /**
     * Визитор для списка правил.
     * Когда посещаем список всех правил - возвращаем map
     */
    class RuleListVisitor : AbnfBaseVisitor<RuleMap>() {
        
        override fun visitRulelist(ctx: AbnfParser.RulelistContext): MutableRuleMap {
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
