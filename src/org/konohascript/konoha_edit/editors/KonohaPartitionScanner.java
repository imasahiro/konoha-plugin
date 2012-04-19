package org.konohascript.konoha_edit.editors;

import org.eclipse.jface.text.rules.*;

public class KonohaPartitionScanner extends RuleBasedPartitionScanner {
	public final static String KONOHA_COMMENT = "__konoha_comment";
	public final static String KONOHA_STRING = "__konoha_string";

	public KonohaPartitionScanner() {

		IPredicateRule[] rules = new IPredicateRule[7];

		int i = 0;
		rules[i++] = new EndOfLineRule("//", new Token(KONOHA_COMMENT));
		rules[i++] = new MultiLineRule("/*", "*/", new Token(KONOHA_COMMENT));
		rules[i++] = new MultiLineRule("\"\"\"", "\"\"\"", new Token(KONOHA_STRING));
		rules[i++] = new MultiLineRule("'''", "'''", new Token(KONOHA_STRING));
		rules[i++] = new SingleLineRule("\"", "\"", new Token(KONOHA_STRING));
		rules[i++] = new SingleLineRule("'", "'", new Token(KONOHA_STRING));
		rules[i++] = new EndOfLineRule("##", new Token(KONOHA_STRING));

		setPredicateRules(rules);
	}
}
