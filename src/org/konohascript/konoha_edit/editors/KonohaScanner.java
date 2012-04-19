package org.konohascript.konoha_edit.editors;

import org.eclipse.jdt.internal.ui.text.JavaWordDetector;
import org.eclipse.jface.text.rules.*;
import org.eclipse.jface.text.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;

public class KonohaScanner extends RuleBasedScanner {

	ColorManager manager;
	WordRule wordRule;
	
	IToken getAttrToken(RGB color, int style){
		return new Token(new TextAttribute(manager.getColor(color), null, style));
	}
	
	void addWords(String[] words, RGB color, int style){
		IToken token = getAttrToken(color, style);
		for(int i=0; i<words.length; i++) wordRule.addWord(words[i], token);
	}
	
	public KonohaScanner(ColorManager manager) {
		this.manager = manager;
		
		IWordDetector detector = new JavaWordDetector(){
			public boolean isWordStart(char c){
				return c == '@' ? true : super.isWordStart(c);
			}
		};
		IToken wd_default = getAttrToken(IKonohaColorConstants.DEFAULT, SWT.NORMAL);
		wordRule = new WordRule(detector, wd_default);
		
		addWords( new String[]{
				"boolean", "int", "float", "String", "Bytes",
				"InputStream", "OutputStream", "Array", "Map",
				"Func", "var", "void", "Class", "Tuple",
				
				"Int", "Integer", "short", "long", "char",
				"Float", "double", "Double",
				
				"include", "using", "class", "if", "else", "switch", "for", "break",
				
				"do", "while", "continue", "foreach", "in",
				"try", "throw", "new", "catch", "finally", "case", "default",
				"function", "return", "typeof", "instanceof",
				"defined", "print", "delegate",
				
				"or", "and", "not", "from", "until",
				
				"OUT", "ERR", "IN",
				
				"true", "false", "enum", "null", "static", 
				"public", "private", "protected", "final",
				"extends", "implements", "this", "super",
			},
			IKonohaColorConstants.KONOHA_KEYWORD, SWT.BOLD
		);

		addWords( new String[]{
				"@Public", "@Singleton", "@Final", "@native",
				"@Static", "@Native", "@FastCall", "@Virtual",
				"@Overload", "@Override",
			},
			IKonohaColorConstants.KONOHA_ANNOTATION, SWT.NORMAL
		);
		
		IRule[] rules = new IRule[2];
		//Add rule for processing instructions
		rules[0] = wordRule;
		// Add generic whitespace rule.
		rules[1] = new WhitespaceRule(new KonohaWhitespaceDetector());

		setRules(rules);
	}
}
