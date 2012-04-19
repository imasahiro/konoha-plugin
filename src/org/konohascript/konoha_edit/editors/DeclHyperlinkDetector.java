package org.konohascript.konoha_edit.editors;

import java.util.LinkedList;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;

public class DeclHyperlinkDetector implements IHyperlinkDetector{
	
	String curWord;
	IRegion curRegion;
	LinkedList<IHyperlink> list;
	IFile file;
	Pattern pat;
	
	private boolean isWordChar(String s, int i){
		char c = s.charAt(i);
		return Character.isLetterOrDigit(c) || c == '_';
	}
	
	private boolean findWord(String source, int offset){
		if(!isWordChar(source, offset)) return false;
		int start = offset;
		for(; start >= 0 && isWordChar(source, start); start--);
		start++;
		int end = offset;
		for(; end < source.length() && isWordChar(source, end); end++);
		curWord = source.substring(start, end);
		curRegion = new Region(start, end - start);
		String regex = curWord.replaceAll("_", "");
		regex = regex.replaceAll(".", "$0_*").replace("_\\*$", "");
		pat = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		return true;
	}
	
	class DeclVisit extends ASTVisitor{
		
		private boolean isSelf(int offset){
			IFile curFile = KonohaUtil.getActiveFile();
			if(curFile == null) return false;
			return curFile.equals(file) && offset == curRegion.getOffset();
		}
		
		public boolean visit(SimpleName node){
			String name = node.toString();
			if(!pat.matcher(name).matches()) return super.visit(node); 
			int offset = node.getStartPosition();
			if(isSelf(offset)) return super.visit(node);
			int length = node.getLength();
			int line = KonohaUtil.getLineOfOffset(file, offset);
			String linkText = file.getName() + ":" + line; 
			if(!name.equals(curWord)) linkText += ":(" + name + ")";			
			if(node.isDeclaration()){
				int nodeType = node.getParent().getNodeType();
				String typeStr = ASTNode.nodeClassForType(nodeType).getSimpleName();
				linkText += ":" + typeStr;
			}
			list.add(new DeclHyperlink(curRegion, curWord, file, offset, length, linkText));
			return super.visit(node);
		}
	}		
	
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks){
		IProject project = KonohaUtil.getProject();
		if(project == null) return null;
		String source = textViewer.getDocument().get();
		if(!findWord(source, region.getOffset())) return null;
		
		String[] exts = new String[]{ "k", "c", "java" };
		IFile[] files = KonohaUtil.getProjectFiles(exts);
		if(files == null) return null;
		
		list = new LinkedList<IHyperlink>();
		for(int i=0; i<files.length; i++){
			file = files[i];
			IDocument doc = KonohaUtil.getDocument(file);
			if(doc == null) continue;
			
			ASTParser parser = ASTParser.newParser(AST.JLS3);
			parser.setKind(ASTParser.K_COMPILATION_UNIT);
			parser.setSource(doc.get().toCharArray());
			CompilationUnit cu_node = (CompilationUnit) parser.createAST(null);
			cu_node.accept(new DeclVisit());
		}
		if(list.size() <= 0) return null;
		return list.toArray(new IHyperlink[0]);
	}
}