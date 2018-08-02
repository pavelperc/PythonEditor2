package com.pavelperc.treebuilder.abnf;// Generated from Abnf.g4 by ANTLR 4.7
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class AbnfLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.7", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, NumberValue=9, 
		ProseValue=10, ID_BIG=11, ID_SMALL=12, INT=13, COMMENT=14, WS=15, STRING=16;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "NumberValue", 
		"BinaryValue", "DecimalValue", "HexValue", "ProseValue", "ID_BIG", "ID_SMALL", 
		"INT", "COMMENT", "WS", "STRING", "BIT", "DIGIT", "HEX_DIGIT"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "':'", "'|'", "'*'", "'+'", "'('", "')'", "'['", "']'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, "NumberValue", "ProseValue", 
		"ID_BIG", "ID_SMALL", "INT", "COMMENT", "WS", "STRING"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public AbnfLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Abnf.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\22\u00d0\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\3\2\3\2\3\3\3"+
		"\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3\n\3\n\3\n\3\n\5\n"+
		"D\n\n\3\13\3\13\6\13H\n\13\r\13\16\13I\3\13\3\13\6\13N\n\13\r\13\16\13"+
		"O\6\13R\n\13\r\13\16\13S\3\13\3\13\6\13X\n\13\r\13\16\13Y\5\13\\\n\13"+
		"\3\f\3\f\6\f`\n\f\r\f\16\fa\3\f\3\f\6\ff\n\f\r\f\16\fg\6\fj\n\f\r\f\16"+
		"\fk\3\f\3\f\6\fp\n\f\r\f\16\fq\5\ft\n\f\3\r\3\r\6\rx\n\r\r\r\16\ry\3\r"+
		"\3\r\6\r~\n\r\r\r\16\r\177\6\r\u0082\n\r\r\r\16\r\u0083\3\r\3\r\6\r\u0088"+
		"\n\r\r\r\16\r\u0089\5\r\u008c\n\r\3\16\3\16\7\16\u0090\n\16\f\16\16\16"+
		"\u0093\13\16\3\16\3\16\3\17\3\17\7\17\u0099\n\17\f\17\16\17\u009c\13\17"+
		"\3\20\3\20\7\20\u00a0\n\20\f\20\16\20\u00a3\13\20\3\21\6\21\u00a6\n\21"+
		"\r\21\16\21\u00a7\3\22\3\22\7\22\u00ac\n\22\f\22\16\22\u00af\13\22\3\22"+
		"\5\22\u00b2\n\22\3\22\3\22\3\22\3\22\3\23\3\23\3\23\3\23\3\24\3\24\3\24"+
		"\3\24\5\24\u00c0\n\24\3\24\3\24\7\24\u00c4\n\24\f\24\16\24\u00c7\13\24"+
		"\3\24\3\24\3\25\3\25\3\26\3\26\3\27\3\27\2\2\30\3\3\5\4\7\5\t\6\13\7\r"+
		"\b\17\t\21\n\23\13\25\2\27\2\31\2\33\f\35\r\37\16!\17#\20%\21\'\22)\2"+
		"+\2-\2\3\2\13\3\2@@\4\2C\\aa\6\2//\62;C\\aa\4\2aac|\6\2//\62;aac|\4\2"+
		"\f\f\17\17\5\2\13\f\17\17\"\"\3\2))\5\2\62;CHch\2\u00e6\2\3\3\2\2\2\2"+
		"\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2"+
		"\2\2\21\3\2\2\2\2\23\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2"+
		"!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\3/\3\2\2\2\5\61\3\2\2\2\7"+
		"\63\3\2\2\2\t\65\3\2\2\2\13\67\3\2\2\2\r9\3\2\2\2\17;\3\2\2\2\21=\3\2"+
		"\2\2\23?\3\2\2\2\25E\3\2\2\2\27]\3\2\2\2\31u\3\2\2\2\33\u008d\3\2\2\2"+
		"\35\u0096\3\2\2\2\37\u009d\3\2\2\2!\u00a5\3\2\2\2#\u00a9\3\2\2\2%\u00b7"+
		"\3\2\2\2\'\u00bf\3\2\2\2)\u00ca\3\2\2\2+\u00cc\3\2\2\2-\u00ce\3\2\2\2"+
		"/\60\7<\2\2\60\4\3\2\2\2\61\62\7~\2\2\62\6\3\2\2\2\63\64\7,\2\2\64\b\3"+
		"\2\2\2\65\66\7-\2\2\66\n\3\2\2\2\678\7*\2\28\f\3\2\2\29:\7+\2\2:\16\3"+
		"\2\2\2;<\7]\2\2<\20\3\2\2\2=>\7_\2\2>\22\3\2\2\2?C\7\'\2\2@D\5\25\13\2"+
		"AD\5\27\f\2BD\5\31\r\2C@\3\2\2\2CA\3\2\2\2CB\3\2\2\2D\24\3\2\2\2EG\7d"+
		"\2\2FH\5)\25\2GF\3\2\2\2HI\3\2\2\2IG\3\2\2\2IJ\3\2\2\2J[\3\2\2\2KM\7\60"+
		"\2\2LN\5)\25\2ML\3\2\2\2NO\3\2\2\2OM\3\2\2\2OP\3\2\2\2PR\3\2\2\2QK\3\2"+
		"\2\2RS\3\2\2\2SQ\3\2\2\2ST\3\2\2\2T\\\3\2\2\2UW\7/\2\2VX\5)\25\2WV\3\2"+
		"\2\2XY\3\2\2\2YW\3\2\2\2YZ\3\2\2\2Z\\\3\2\2\2[Q\3\2\2\2[U\3\2\2\2[\\\3"+
		"\2\2\2\\\26\3\2\2\2]_\7f\2\2^`\5+\26\2_^\3\2\2\2`a\3\2\2\2a_\3\2\2\2a"+
		"b\3\2\2\2bs\3\2\2\2ce\7\60\2\2df\5+\26\2ed\3\2\2\2fg\3\2\2\2ge\3\2\2\2"+
		"gh\3\2\2\2hj\3\2\2\2ic\3\2\2\2jk\3\2\2\2ki\3\2\2\2kl\3\2\2\2lt\3\2\2\2"+
		"mo\7/\2\2np\5+\26\2on\3\2\2\2pq\3\2\2\2qo\3\2\2\2qr\3\2\2\2rt\3\2\2\2"+
		"si\3\2\2\2sm\3\2\2\2st\3\2\2\2t\30\3\2\2\2uw\7z\2\2vx\5-\27\2wv\3\2\2"+
		"\2xy\3\2\2\2yw\3\2\2\2yz\3\2\2\2z\u008b\3\2\2\2{}\7\60\2\2|~\5-\27\2}"+
		"|\3\2\2\2~\177\3\2\2\2\177}\3\2\2\2\177\u0080\3\2\2\2\u0080\u0082\3\2"+
		"\2\2\u0081{\3\2\2\2\u0082\u0083\3\2\2\2\u0083\u0081\3\2\2\2\u0083\u0084"+
		"\3\2\2\2\u0084\u008c\3\2\2\2\u0085\u0087\7/\2\2\u0086\u0088\5-\27\2\u0087"+
		"\u0086\3\2\2\2\u0088\u0089\3\2\2\2\u0089\u0087\3\2\2\2\u0089\u008a\3\2"+
		"\2\2\u008a\u008c\3\2\2\2\u008b\u0081\3\2\2\2\u008b\u0085\3\2\2\2\u008b"+
		"\u008c\3\2\2\2\u008c\32\3\2\2\2\u008d\u0091\7>\2\2\u008e\u0090\n\2\2\2"+
		"\u008f\u008e\3\2\2\2\u0090\u0093\3\2\2\2\u0091\u008f\3\2\2\2\u0091\u0092"+
		"\3\2\2\2\u0092\u0094\3\2\2\2\u0093\u0091\3\2\2\2\u0094\u0095\7@\2\2\u0095"+
		"\34\3\2\2\2\u0096\u009a\t\3\2\2\u0097\u0099\t\4\2\2\u0098\u0097\3\2\2"+
		"\2\u0099\u009c\3\2\2\2\u009a\u0098\3\2\2\2\u009a\u009b\3\2\2\2\u009b\36"+
		"\3\2\2\2\u009c\u009a\3\2\2\2\u009d\u00a1\t\5\2\2\u009e\u00a0\t\6\2\2\u009f"+
		"\u009e\3\2\2\2\u00a0\u00a3\3\2\2\2\u00a1\u009f\3\2\2\2\u00a1\u00a2\3\2"+
		"\2\2\u00a2 \3\2\2\2\u00a3\u00a1\3\2\2\2\u00a4\u00a6\4\62;\2\u00a5\u00a4"+
		"\3\2\2\2\u00a6\u00a7\3\2\2\2\u00a7\u00a5\3\2\2\2\u00a7\u00a8\3\2\2\2\u00a8"+
		"\"\3\2\2\2\u00a9\u00ad\7%\2\2\u00aa\u00ac\n\7\2\2\u00ab\u00aa\3\2\2\2"+
		"\u00ac\u00af\3\2\2\2\u00ad\u00ab\3\2\2\2\u00ad\u00ae\3\2\2\2\u00ae\u00b1"+
		"\3\2\2\2\u00af\u00ad\3\2\2\2\u00b0\u00b2\7\17\2\2\u00b1\u00b0\3\2\2\2"+
		"\u00b1\u00b2\3\2\2\2\u00b2\u00b3\3\2\2\2\u00b3\u00b4\7\f\2\2\u00b4\u00b5"+
		"\3\2\2\2\u00b5\u00b6\b\22\2\2\u00b6$\3\2\2\2\u00b7\u00b8\t\b\2\2\u00b8"+
		"\u00b9\3\2\2\2\u00b9\u00ba\b\23\2\2\u00ba&\3\2\2\2\u00bb\u00bc\7\'\2\2"+
		"\u00bc\u00c0\7u\2\2\u00bd\u00be\7\'\2\2\u00be\u00c0\7k\2\2\u00bf\u00bb"+
		"\3\2\2\2\u00bf\u00bd\3\2\2\2\u00bf\u00c0\3\2\2\2\u00c0\u00c1\3\2\2\2\u00c1"+
		"\u00c5\7)\2\2\u00c2\u00c4\n\t\2\2\u00c3\u00c2\3\2\2\2\u00c4\u00c7\3\2"+
		"\2\2\u00c5\u00c3\3\2\2\2\u00c5\u00c6\3\2\2\2\u00c6\u00c8\3\2\2\2\u00c7"+
		"\u00c5\3\2\2\2\u00c8\u00c9\7)\2\2\u00c9(\3\2\2\2\u00ca\u00cb\4\62\63\2"+
		"\u00cb*\3\2\2\2\u00cc\u00cd\4\62;\2\u00cd,\3\2\2\2\u00ce\u00cf\t\n\2\2"+
		"\u00cf.\3\2\2\2\33\2CIOSY[agkqsy\177\u0083\u0089\u008b\u0091\u009a\u00a1"+
		"\u00a7\u00ad\u00b1\u00bf\u00c5\3\2\3\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}