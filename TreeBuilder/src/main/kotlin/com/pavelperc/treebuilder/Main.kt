package com.pavelperc.treebuilder

import java.io.FileReader

/**
 * Created by pavel on 06.01.2018.
 */
fun main(args: Array<String>) {
    
    log = object : Log {
        override fun println(str: Any) {
            kotlin.io.println(str)
        }
    }
    
    println("Working Directory = ${System.getProperty("user.dir")}")
//    val grammar = FileReader("app/src/main/res/raw/full_grammar.txt").readText()
    
//    println("read grammar: size=${grammar.length}")
//    println()
    
    
    
//    val ruleMap = setupFullGrammar(
//            FileReader("app/src/main/res/raw/full_grammar.txt").readText(),
//            true,
//            true
//    )
    
    val ruleMap = setupSmallGrammar(
            FileReader("small_grammar.txt").readText(),
            true,
            true
    )
    
//    for (rule in ruleMap.values) {
//        println(rule.toString())
//    }
    
    println()
    println()
    
    
    val file_input = MainRuleConsole(ruleMap["stmt"]!!, ruleMap)
    
    file_input.findAlternatives()
}

/** Enum with colors. Color can be created from [colorString] like #FFFFFF
 * and retrieved from property [color] as int*/
enum class Palette(colorString: String) {


//    GREEN("#22d012"),
//    YELLOW("#F2E394"),
//    ORANGE("#F2AE72"),
//    RED("#D96459"),
//    BROWN("#8C4646"),
//    BlUE("#408ec9"),
//    LIGHT_GREEN("#b1f96d"),
//    PURPLE("#c19fec"),
//    PINK("#f5d1ff"),
//    DARK_GREEN("#2c9a6b");
    
    Red("#e6194b"),
    Green("#3cb44b"),
    Yellow("#ffe119"),
    Blue("#7ab3ef"),
    Orange("#f58231"),
    Purple("#911eb4"),
    Cyan("#9af1ff"),
    Magenta("#f032e6"),
    Lime("#d2f53c"),
    Pink("#fabebe"),
    Teal("#008080"),
    Lavender("#e6beff"),
    Brown("#aa6e28"),
    Beige("#fffac8"),
    Maroon("#800000"),
    Mint("#aaffc3"),
    Olive("#808000"),
    Coral("#ffd8b1"),
    DarkBlue("#1666ba"),
    Grey("#808080"),
    White("#FFFFFF"),
    Black("#000000");
    
    /** Color converted to integer in hex format 0xAARRGGBB*/
    val color: Int = parseColor(colorString)
    
    /** Was copied from android Color class*/
    private fun parseColor(colorString: String): Int {
        if (colorString[0] == '#') {
            // Use a long to avoid rollovers on #ffXXXXXX
            var color = java.lang.Long.parseLong(colorString.substring(1), 16)
            if (colorString.length == 7) {
                // Set the alpha value
                color = color or -0x1000000
            } else if (colorString.length != 9) {
                throw IllegalArgumentException("Unknown color")
            }
            return color.toInt()
        }
        throw IllegalArgumentException("Unknown color")
    }
}

///**
// * Generates ruleMap from [grammar],
// * connects all contexts to rules,
// * setups grouping tags.
// * @return ruleMap
// */
//fun setupSimpleGrammar(grammar: String, optimizeRules: Boolean = true, drawGV: Boolean): MutableMap<String, GenericRule> {
//    val ruleMap = MyVisitor.generateRuleMap(grammar)
//    
//    val allLeaves = ruleMap.values.flatMap { it.leaves.asIterable() }.toSet()
//    
//    
//    //-----------Setup tags
//    
//    
//    /** map [String] tag to object of [GroupingTag]*/
//    val tags = mapOf(
//            "augassign" to Palette.Green,
//            "comp_op" to Palette.Yellow,
//            "sign_op" to Palette.Orange,
//            "sign" to Palette.Brown,
//            "bool_op" to Palette.Red,
//            "boolean" to Palette.Blue,
//            "round_br" to Palette.Lime,
//            "square_br" to Palette.Teal
//    )
//            .map { Pair(it.key, GroupingTag(it.key, it.value.color)) }
//            .toMap()
//    
//    
//    
//    ruleMap["augassign"]!!.setGroupingTag(tags["augassign"]!!)
//    ruleMap["comp_op"]!!.setGroupingTag(tags["comp_op"]!!)
//    
//    
//    ruleMap["term"]!!.setGroupingTag(tags["sign_op"]!!)
//    ruleMap["expr"]!!.setGroupingTag(tags["sign_op"]!!)
////    ruleMap["arith_expr"]!!.setGroupingTag(tags["sign_op"]!!)
//    ruleMap["factor"]!!.setGroupingTag(tags["sign"]!!)// + - ~ before expr
//    ruleMap["power"]!!.setGroupingTag(tags["sign_op"]!!)
//    
//    
//    allLeaves.forEach {
//        when (it.text) {
//            "or", "and", "not" -> it.groupingTag = tags["bool_op"]!!
//            "True", "False" -> it.groupingTag = tags["boolean"]!!
//            "(", ")" -> it.groupingTag = tags["round_br"]!!
//            "[", "]" -> it.groupingTag = tags["square_br"]!!
//        }
//    }
//    
//    
//    //-------------Setup Contexts
//
////        ruleMap["atom"]!!.leaves.find { it.text == "NAME" }?.also { 
////            it.gContext = GenericContextName(it)
////        } ?: throw Exception("Not found NAME in atom")
////        
//    
//    
//    allLeaves
//            .filter { it.text == "NEWLINE" }
//            .also { if (it.isEmpty()) throw Exception("NEWLINE not found ") }
//            .forEach { it.gContext = GenericContextNewline(it) }
//    
//    
//    allLeaves
//            .filter { it.text == "NAME" }
//            .also { if (it.isEmpty()) throw Exception("NAME not found ") }
//            .forEach { it.gContext = GenericContextName(it) }
//    
//    
//    allLeaves
//            .filter { it.text == "NUMBER" }
//            .also { if (it.isEmpty()) throw Exception("NUMBER not found ") }
//            .forEach { it.gContext = GenericContextNumber(it) }
//    
//    
//    allLeaves
//            .filter { it.text == "STRING" }
//            .also { if (it.isEmpty()) throw Exception("STRING not found ") }
//            .forEach { it.gContext = GenericContextString(it) }
//    
//    
//    
//    if (drawGV) MyVisitor.drawGv(ruleMap, "chains/GenericRulesOriginal.gv", "original rules")
//    if (optimizeRules) MyVisitor.optimizeRuleMap(ruleMap)
//    if (drawGV && optimizeRules) MyVisitor.drawGv(ruleMap, "chains/GenericRulesOptimized.gv", "optimized rules")
//    
//    return ruleMap
//}


/**
 * Generates ruleMap from [grammar],
 * connects all contexts to rules,
 * setups grouping tags.
 * @return ruleMap
 */
fun setupFullGrammar(grammar: String, optimizeRules: Boolean = true, drawGV: Boolean): MutableMap<String, GenericRule> {
    val ruleMap = MyVisitor.generateRuleMap(grammar)
    
    val allLeaves = ruleMap.values.flatMap { it.leaves.asIterable() }.toSet()
    
    
    //-----------Setup tags
    
    /** map of [String] tag to object of [GroupingTag]*/
    val tags = listOf(
            GroupingTag("augassign", Palette.Green.color, 4),
            GroupingTag("comp_op", Palette.Yellow.color, 4),
            GroupingTag("sign_op", Palette.Orange.color, 3),
            GroupingTag("sign", Palette.Pink.color, 3),// + - ~ before expr
            GroupingTag("bool_op", Palette.Red.color, 3),// and not
            GroupingTag("boolean", Palette.Cyan.color, 3),// True False None
            GroupingTag("round_br", Palette.Lime.color, 3),
            GroupingTag("square_br", Palette.Mint.color, 3),
            GroupingTag("curly_br", Palette.Lavender.color, 5),// {} for dict
            GroupingTag("return", Palette.Maroon.color, 2),
            GroupingTag("compound", Palette.Purple.color, 3),// if, for ...
            GroupingTag("name", Palette.Blue.color, 1),// name
            GroupingTag("string", Palette.Coral.color, 1),// string
            GroupingTag("number", Palette.Pink.color, 1),// number
            GroupingTag("assign", Palette.Yellow.color, 1),
            GroupingTag("newline", Palette.White.color, 1),// newline, indent, dedent
            GroupingTag(GroupingTag.FUNC_ARG_TAG, Palette.Blue.color, 3)// (arg, arg, arg) [ ] .NAME
    )
            .map { Pair(it.tag, it) }
            .toMap()
    
    
    
    // spread tags concretely to the leaves of the rules
    
    ruleMap["augassign"]!!.setGroupingTag(tags["augassign"]!!)
    
    // to avoid conflict with 'not' in comp_op
    ruleMap["or_test"]!!.setGroupingTag(tags["bool_op"]!!)
    ruleMap["and_test"]!!.setGroupingTag(tags["bool_op"]!!)
    ruleMap["not_test"]!!.setGroupingTag(tags["bool_op"]!!)
    
    ruleMap["term"]!!.setGroupingTag(tags["sign_op"]!!)
    ruleMap["arith_expr"]!!.setGroupingTag(tags["sign_op"]!!)
    ruleMap["factor"]!!.setGroupingTag(tags["sign"]!!)// + - ~ before expr
    ruleMap["power"]!!.setGroupingTag(tags["sign_op"]!!)
    
    
    
    // assign same tags to multiple leaves. (rules with the same text can meet many times)
    allLeaves.forEach {
        when (it.text) {
            "or", "and", "not" -> it.groupingTag = tags["bool_op"]!!
            "True", "False", "None" -> it.groupingTag = tags["boolean"]!!
            "(", ")" -> it.groupingTag = tags["round_br"]!!
            "[", "]" -> it.groupingTag = tags["square_br"]!!
            "{", "}" -> it.groupingTag = tags["curly_br"]!!
            "for", "if", "else", "elif", "try", ":", "while", "in" -> it.groupingTag = tags["compound"]!!
            "break", "continue", "return", "yield" -> it.groupingTag = tags["return"]!!
            "NAME" -> it.groupingTag = tags["name"]!!
            "STRING" -> it.groupingTag = tags["string"]!!
            "NUMBER" -> it.groupingTag = tags["number"]!!
            "=" -> it.groupingTag = tags["assign"]!!
            "NEWLINE", "INDENT", "DEDENT" -> it.groupingTag = tags["newline"]!!
        }
    }
    
    
    // continue spreading tags on concrete rules
    
    ruleMap["comp_op"]!!.setGroupingTag(tags["comp_op"]!!)
    
    
    // set ',' '(' ')' '[' ']' '.' in function and classdef as func_arg
    ruleMap["trailer"]!!.leaves
            .plus(ruleMap["arglist"]!!.leaves)
            .plus(ruleMap["classdef"]!!.leaves)
            .filterNot { it.text == "NAME" || it.text == ":" }
            .filterNot { it.checkById(593, ",") }// remove comma after last argument
            .forEach { it.groupingTag = tags[GroupingTag.FUNC_ARG_TAG]!! }
    
    
    
    
    //-------------Setup Contexts

//        ruleMap["atom"]!!.leaves.find { it.text == "NAME" }?.also { 
//            it.gContext = GenericContextName(it)
//        } ?: throw Exception("Not found NAME in atom")
//        
    
    
    // All rules are not optimized here!!!
    
    
    allLeaves
            .filter { it.text == "NEWLINE" }
            .also { if (it.isEmpty()) throw Exception("NEWLINE not found ") }
            .forEach { it.gContext = GenericContextNewline(it) }
    
    
    allLeaves
            .filter { it.text == "NAME" }
            .also { if (it.isEmpty()) throw Exception("NAME not found ") }
            .forEach { it.gContext = GenericContextName(it, Palette.Lavender.color, Palette.Beige.color) }
    
    
    allLeaves
            .filter { it.text == "NUMBER" }
            .also { if (it.isEmpty()) throw Exception("NUMBER not found ") }
            .forEach { it.gContext = GenericContextNumber(it) }
    
    
    allLeaves
            .filter { it.text == "STRING" }
            .also { if (it.isEmpty()) throw Exception("STRING not found ") }
            .forEach { it.gContext = GenericContextString(it) }
    
    
    allLeaves
            .filter { it.text == "ENDMARKER" }
            .also { if (it.isEmpty()) throw Exception("ENDMARKER not found ") }
            .forEach { it.gContext = GenericContextId(it, "END") }
    
    allLeaves
            .filter { it.text == "AWAIT" }
            .also { if (it.isEmpty()) throw Exception("AWAIT not found ") }
            .forEach { it.gContext = GenericContextId(it, "await") }
    
    allLeaves
            .filter { it.text == "INDENT" }
            .also { if (it.isEmpty()) throw Exception("INDENT not found ") }
            .forEach { it.gContext = GenericContextIndentOrDedent(it, true, "  ->  ") }
    
    allLeaves
            .filter { it.text == "DEDENT" }
            .also { if (it.isEmpty()) throw Exception("DEDENT not found ") }
            .forEach { it.gContext = GenericContextIndentOrDedent(it, false, "  <-  ") }
    
    allLeaves
            .filter { it.text == "stmt" }
            .also { if (it.isEmpty()) throw Exception("stmt not found ") }
            .forEach { it.gContext = GenericContextStmt(it) }
    
    
    ruleMap["expr_stmt"]!!.leaves
            .first { it.checkById(176, "=") }
            .also { leaf -> leaf.gContext = GenericContextAssign(leaf) }
    
    
    if (drawGV) {
        MyVisitor.drawGv(ruleMap, "chains/GenericRulesOriginal.gv", "original rules: ${ruleMap.size}")
    }
    if (optimizeRules) {
        MyVisitor.optimizeRuleMap(ruleMap)
        MyVisitor.optimizeRuleMap(ruleMap)
        
        if (drawGV)
            MyVisitor.drawGv(ruleMap,
                    "chains/GenericRulesOptimized.gv",
                    "optimized rules: ${ruleMap.size}")
    }
    return ruleMap
}




/**
 * Small grammar for debug.
 */
fun setupSmallGrammar(grammar: String, optimizeRules: Boolean = true, drawGV: Boolean): MutableMap<String, GenericRule> {
    val ruleMap = MyVisitor.generateRuleMap(grammar)
    
//    val allLeaves = ruleMap.values.flatMap { it.leaves.asIterable() }.toSet()
    
    
    if (drawGV) {
        MyVisitor.drawGv(ruleMap, "chains/GenericRulesOriginal.gv", "original rules: ${ruleMap.size}")
    }
    if (optimizeRules) {
        MyVisitor.optimizeRuleMap(ruleMap)
        MyVisitor.optimizeRuleMap(ruleMap)
        
        if (drawGV)
            MyVisitor.drawGv(ruleMap,
                    "chains/GenericRulesOptimized.gv",
                    "optimized rules: ${ruleMap.size}")
    }
    return ruleMap
}