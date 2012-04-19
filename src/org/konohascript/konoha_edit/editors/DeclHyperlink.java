package org.konohascript.konoha_edit.editors;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IRegion;
import org.eclipse.pde.internal.ui.editor.text.AbstractHyperlink;

public class DeclHyperlink extends AbstractHyperlink{
	private IFile file;
	private int offset, length;
	String linkText;
	
	public DeclHyperlink(IRegion region, String element, IFile file, int offset, int length, String linkText){
		super(region, element);
		this.file = file;
		this.offset = offset;
		this.length = length;
		this.linkText = linkText;
	}
	
	public String getHyperlinkText(){
		return linkText;
	}
	
	public void open(){
		KonohaUtil.openEditor(file, offset, length);
	}
}