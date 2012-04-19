package org.konohascript.konoha_edit.editors;

import org.eclipse.ui.editors.text.TextEditor;

public class KonohaEditor extends TextEditor {

	private ColorManager colorManager;
	
	public KonohaEditor() {
		super();
		colorManager = new ColorManager();
		setSourceViewerConfiguration(new KonohaConfiguration(colorManager));
		setDocumentProvider(new KonohaDocumentProvider());
	}
	public void dispose() {
		colorManager.dispose();
		super.dispose();
	}
}

