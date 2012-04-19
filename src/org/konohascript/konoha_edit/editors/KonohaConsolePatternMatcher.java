package org.konohascript.konoha_edit.editors;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.ui.console.IHyperlink;
import org.eclipse.ui.console.IPatternMatchListenerDelegate;
import org.eclipse.ui.console.PatternMatchEvent;
import org.eclipse.ui.console.TextConsole;

public class KonohaConsolePatternMatcher implements IPatternMatchListenerDelegate{
	TextConsole tc;
	
	class ErrLink implements IHyperlink{
		String name;
		int line;
		
		ErrLink(String name, int line){
			this.name = name;
			this.line = line;
		}
		
		public void linkEntered(){
		}
		
		public void linkExited(){
		}

		public void linkActivated(){
			KonohaUtil.openEditor(name, line);
		}
	}
	
	public void connect(TextConsole console){
		tc = console;
	}
	
	public void disconnect(){
	}
	
	public void matchFound(PatternMatchEvent event){
		int offset = event.getOffset() + 1;
		int length = event.getLength() - 2;
		String str = tc.getDocument().get().substring(offset, offset + length);
		
		int delim = str.indexOf(":");
		if(delim < 0) return;
		String name = str.substring(0, delim);
		int line = Integer.parseInt(str.substring(delim + 1));
		
		IHyperlink link = new ErrLink(name, line);
		try{
			tc.addHyperlink(link, offset, length);
		}catch(BadLocationException e){
			e.printStackTrace();
		}
	}
}