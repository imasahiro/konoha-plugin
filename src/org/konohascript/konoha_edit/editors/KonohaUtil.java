package org.konohascript.konoha_edit.editors;

import java.util.LinkedList;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

public class KonohaUtil{
	
	public static IWorkbenchPage getActivePage(){
		IWorkbenchWindow win = PlatformUI.getWorkbench().getActiveWorkbenchWindow();		
		if(win == null) return null;
		return win.getActivePage();
	}

	public static IEditorPart getActiveEditor(){
		IWorkbenchPage page = getActivePage();
		if(page == null) return null;
		return page.getActiveEditor();
	}
	
	public static IFile getFile(IEditorInput input){
		if(!(input instanceof IFileEditorInput)) return null;	
		return ((IFileEditorInput)input).getFile();
	}

	public static IFile getActiveFile(){
		IEditorPart ep = getActiveEditor();
		if(ep == null) return null;
		return getFile(ep.getEditorInput());
	}
	
	public static IProject getProject(){
		IFile file = getActiveFile();
		if(file == null) return null;
		return file.getProject();
	}
	
	public static boolean search(String targ, String[] arr){
		for(int i=0; i<arr.length; i++) if(targ.equals(arr[i])) return true;
		return false;
	}
	
	static class MyFileVisit implements IResourceVisitor{
		String[] exts;
		LinkedList<IFile> list;
		
		MyFileVisit(String[] exts){
			this.exts = exts;
			list = new LinkedList<IFile>();
		}
		
		public boolean visit(IResource r) throws CoreException{
			if(r.getType() != IResource.FILE) return true;
			IFile file = (IFile)r;
			String ext = file.getFullPath().getFileExtension();
			if(ext == null) return false;
			if(KonohaUtil.search(ext, exts)) list.add(file);
			return false;
		}
		
		IFile[] getFiles(){
			return list.toArray(new IFile[0]);
		}
	}
	
	public static IFile[] getProjectFiles(String[] exts){
		IProject project = getProject();
		if(project == null) return null;
		MyFileVisit visitor = new MyFileVisit(exts);
		try{
			project.accept(visitor);
		}catch(CoreException e){
			e.printStackTrace();
			return null;
		}
		return visitor.getFiles();
	}
	
	public static IFile getProjectFile(String name){
		int dot = name.lastIndexOf(".");
		if(dot == -1) return null;

		String ext = name.substring(dot + 1);
		IFile[] files = getProjectFiles(new String[]{ ext });
		if(files == null) return null;
		for(int i=0; i<files.length; i++){
			if(name.equals(files[i].getName())) return files[i];
		}
		return null;
	}
	
	public static IEditorInput getEditorInput(IEditorReference er){
		IEditorInput input = null;
		try{
			input = er.getEditorInput();
		}catch(CoreException e){
			e.printStackTrace();
		}
		return input;
	}
	
	public static IDocument getDocument(IEditorReference er){
		IEditorPart ep = er.getEditor(false);
		if(!(ep instanceof ITextEditor)) return null;
		ITextEditor te = (ITextEditor)ep;
		
		IDocumentProvider dp = te.getDocumentProvider();
		if(dp == null) return null;
		
		IEditorInput input = getEditorInput(er);
		if(input == null) return null;
		
		return dp.getDocument(input);
	}
	
	public static IDocument getDocFromEditor(IFile file){ 
		IWorkbenchPage page = getActivePage();
		if(page == null) return null;
		IEditorReference[] ers = page.getEditorReferences();
		for(int i=0; i<ers.length; i++){
			IEditorInput input = getEditorInput(ers[i]);
			if(input == null) continue;
			IFile f = getFile(input);
			if(!f.equals(file)) continue;
			return getDocument(ers[i]);
		}
		return null;
	}
	
	public static IDocument getDocFromFile(IFile file){
		IPath path = file.getFullPath();
		ITextFileBufferManager mgr = FileBuffers.getTextFileBufferManager();
		try{
			mgr.connect(path, LocationKind.IFILE, null);
		}catch(CoreException e){
			e.printStackTrace();
			return null;
		}
		ITextFileBuffer buf = mgr.getTextFileBuffer(path, LocationKind.IFILE);
		return buf.getDocument();
	}
	
	public static IDocument getDocument(IFile file){
		IDocument doc = getDocFromEditor(file);
		if(doc == null) doc = getDocFromFile(file);
		return doc;
	}
	
	public static int getLineOffset(IFile file, int line){
		int offset = 0;
		IDocument doc = getDocument(file);
		if(doc == null) return offset;
		try{
			offset = doc.getLineOffset(line - 1);
		}catch(BadLocationException e){
			e.printStackTrace();
		}
		return offset;
	}
	
	public static int getLineOfOffset(IFile file, int offset){
		int line = 0;
		IDocument doc = getDocument(file);
		if(doc == null) return line + 1;
		try{
			line = doc.getLineOfOffset(offset);
		}catch(BadLocationException e){
			e.printStackTrace();
		}
		return line + 1;
	}
	
	public static void openEditor(IFile file, int offset, int length){
		IEditorPart ep = null;
		try{
			ep = EditorUtility.openInEditor(file);
		}catch(PartInitException e){
			e.printStackTrace();
		}
		if(ep != null) EditorUtility.revealInEditor(ep, offset, length);
	}
	
	public static void openEditor(String name, int line){
		IFile file = getProjectFile(name);
		if(file == null) return;
		int offset = getLineOffset(file, line);
		openEditor(file, offset, 0);
	}
}