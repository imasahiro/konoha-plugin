package org.konohascript.konoha_edit.editors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.editors.text.FileDocumentProvider;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;

public class KonohaEditor extends TextEditor {

	private ColorManager colorManager;
	
	public KonohaEditor() {
		super();
		colorManager = new ColorManager();
		setSourceViewerConfiguration(new KonohaConfiguration(colorManager));
	}
	static void setDocumentPartitioner(IDocument document){
		IDocumentPartitioner partitioner =
			new FastPartitioner(
					new KonohaPartitionScanner(),
					new String[] {
						KonohaPartitionScanner.KONOHA_COMMENT,
						KonohaPartitionScanner.KONOHA_STRING });
		partitioner.connect(document);
		document.setDocumentPartitioner(partitioner);
	}
	protected void doSetInput(IEditorInput input) throws CoreException{
		setDocumentProvider(
				(input instanceof IStorageEditorInput) ?
				new FileDocumentProvider() {
					protected IDocument createDocument(Object element) throws CoreException {
						IDocument document = super.createDocument(element);
						if(document != null) setDocumentPartitioner(document);
						return document;
					}
				} :
				new TextFileDocumentProvider() {
					protected FileInfo createFileInfo(Object element) throws CoreException{
						FileInfo info = super.createFileInfo(element);
						if(info == null)
							info = createEmptyFileInfo();
						IDocument document = info.fTextFileBuffer.getDocument();
						if(document != null)
							setDocumentPartitioner(document);
						return info;
					}
				});
		super.doSetInput(input);
	}
	public void dispose() {
		colorManager.dispose();
		super.dispose();
	}
}

