package com.pavelperc.treebuilder.abnf;// Generated from Abnf.g4 by ANTLR 4.7
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link AbnfParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface AbnfVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link AbnfParser#rulelist}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRulelist(AbnfParser.RulelistContext ctx);
	/**
	 * Visit a parse tree produced by {@link AbnfParser#rule_}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule_(AbnfParser.Rule_Context ctx);
	/**
	 * Visit a parse tree produced by {@link AbnfParser#elements}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElements(AbnfParser.ElementsContext ctx);
	/**
	 * Visit a parse tree produced by {@link AbnfParser#alternation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAlternation(AbnfParser.AlternationContext ctx);
	/**
	 * Visit a parse tree produced by {@link AbnfParser#concatenation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConcatenation(AbnfParser.ConcatenationContext ctx);
	/**
	 * Visit a parse tree produced by {@link AbnfParser#repetition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRepetition(AbnfParser.RepetitionContext ctx);
	/**
	 * Visit a parse tree produced by {@link AbnfParser#repeat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRepeat(AbnfParser.RepeatContext ctx);
	/**
	 * Visit a parse tree produced by {@link AbnfParser#element}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElement(AbnfParser.ElementContext ctx);
	/**
	 * Visit a parse tree produced by {@link AbnfParser#group}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGroup(AbnfParser.GroupContext ctx);
	/**
	 * Visit a parse tree produced by {@link AbnfParser#option}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOption(AbnfParser.OptionContext ctx);
	/**
	 * Visit a parse tree produced by {@link AbnfParser#id}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitId(AbnfParser.IdContext ctx);
}