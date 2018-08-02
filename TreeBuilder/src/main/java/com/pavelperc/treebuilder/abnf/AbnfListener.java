package com.pavelperc.treebuilder.abnf;// Generated from Abnf.g4 by ANTLR 4.7
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link AbnfParser}.
 */
public interface AbnfListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link AbnfParser#rulelist}.
	 * @param ctx the parse tree
	 */
	void enterRulelist(AbnfParser.RulelistContext ctx);
	/**
	 * Exit a parse tree produced by {@link AbnfParser#rulelist}.
	 * @param ctx the parse tree
	 */
	void exitRulelist(AbnfParser.RulelistContext ctx);
	/**
	 * Enter a parse tree produced by {@link AbnfParser#rule_}.
	 * @param ctx the parse tree
	 */
	void enterRule_(AbnfParser.Rule_Context ctx);
	/**
	 * Exit a parse tree produced by {@link AbnfParser#rule_}.
	 * @param ctx the parse tree
	 */
	void exitRule_(AbnfParser.Rule_Context ctx);
	/**
	 * Enter a parse tree produced by {@link AbnfParser#elements}.
	 * @param ctx the parse tree
	 */
	void enterElements(AbnfParser.ElementsContext ctx);
	/**
	 * Exit a parse tree produced by {@link AbnfParser#elements}.
	 * @param ctx the parse tree
	 */
	void exitElements(AbnfParser.ElementsContext ctx);
	/**
	 * Enter a parse tree produced by {@link AbnfParser#alternation}.
	 * @param ctx the parse tree
	 */
	void enterAlternation(AbnfParser.AlternationContext ctx);
	/**
	 * Exit a parse tree produced by {@link AbnfParser#alternation}.
	 * @param ctx the parse tree
	 */
	void exitAlternation(AbnfParser.AlternationContext ctx);
	/**
	 * Enter a parse tree produced by {@link AbnfParser#concatenation}.
	 * @param ctx the parse tree
	 */
	void enterConcatenation(AbnfParser.ConcatenationContext ctx);
	/**
	 * Exit a parse tree produced by {@link AbnfParser#concatenation}.
	 * @param ctx the parse tree
	 */
	void exitConcatenation(AbnfParser.ConcatenationContext ctx);
	/**
	 * Enter a parse tree produced by {@link AbnfParser#repetition}.
	 * @param ctx the parse tree
	 */
	void enterRepetition(AbnfParser.RepetitionContext ctx);
	/**
	 * Exit a parse tree produced by {@link AbnfParser#repetition}.
	 * @param ctx the parse tree
	 */
	void exitRepetition(AbnfParser.RepetitionContext ctx);
	/**
	 * Enter a parse tree produced by {@link AbnfParser#repeat}.
	 * @param ctx the parse tree
	 */
	void enterRepeat(AbnfParser.RepeatContext ctx);
	/**
	 * Exit a parse tree produced by {@link AbnfParser#repeat}.
	 * @param ctx the parse tree
	 */
	void exitRepeat(AbnfParser.RepeatContext ctx);
	/**
	 * Enter a parse tree produced by {@link AbnfParser#element}.
	 * @param ctx the parse tree
	 */
	void enterElement(AbnfParser.ElementContext ctx);
	/**
	 * Exit a parse tree produced by {@link AbnfParser#element}.
	 * @param ctx the parse tree
	 */
	void exitElement(AbnfParser.ElementContext ctx);
	/**
	 * Enter a parse tree produced by {@link AbnfParser#group}.
	 * @param ctx the parse tree
	 */
	void enterGroup(AbnfParser.GroupContext ctx);
	/**
	 * Exit a parse tree produced by {@link AbnfParser#group}.
	 * @param ctx the parse tree
	 */
	void exitGroup(AbnfParser.GroupContext ctx);
	/**
	 * Enter a parse tree produced by {@link AbnfParser#option}.
	 * @param ctx the parse tree
	 */
	void enterOption(AbnfParser.OptionContext ctx);
	/**
	 * Exit a parse tree produced by {@link AbnfParser#option}.
	 * @param ctx the parse tree
	 */
	void exitOption(AbnfParser.OptionContext ctx);
	/**
	 * Enter a parse tree produced by {@link AbnfParser#id}.
	 * @param ctx the parse tree
	 */
	void enterId(AbnfParser.IdContext ctx);
	/**
	 * Exit a parse tree produced by {@link AbnfParser#id}.
	 * @param ctx the parse tree
	 */
	void exitId(AbnfParser.IdContext ctx);
}