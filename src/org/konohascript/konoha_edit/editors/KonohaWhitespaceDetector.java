package org.konohascript.konoha_edit.editors;

import org.eclipse.jface.text.rules.IWhitespaceDetector;

public class KonohaWhitespaceDetector implements IWhitespaceDetector {

	public boolean isWhitespace(char c) {
		return (c == ' ' || c == '\t' || c == '\n' || c == '\r');
	}
}
