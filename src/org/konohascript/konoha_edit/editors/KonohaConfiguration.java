package org.konohascript.konoha_edit.editors;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlinkPresenter;
import org.eclipse.jface.text.hyperlink.MultipleHyperlinkPresenter;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.graphics.RGB;

public class KonohaConfiguration extends SourceViewerConfiguration {
	private KonohaDoubleClickStrategy doubleClickStrategy;
	private KonohaScanner scanner;
	private ColorManager colorManager;
	private DeclHyperlinkDetector declHyperlinkDetector;
		
	public KonohaConfiguration(ColorManager colorManager) {
		this.colorManager = colorManager;
	}
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] {
			IDocument.DEFAULT_CONTENT_TYPE,
			KonohaPartitionScanner.KONOHA_COMMENT,
			KonohaPartitionScanner.KONOHA_STRING };
	}
	public ITextDoubleClickStrategy getDoubleClickStrategy(
		ISourceViewer sourceViewer,
		String contentType) {
		if (doubleClickStrategy == null)
			doubleClickStrategy = new KonohaDoubleClickStrategy();
		return doubleClickStrategy;
	}

	protected KonohaScanner getKonohaScanner() {
		if (scanner == null) {
			scanner = new KonohaScanner(colorManager);
			scanner.setDefaultReturnToken(
				new Token(
					new TextAttribute(
						colorManager.getColor(IKonohaColorConstants.DEFAULT))));
		}
		return scanner;
	}

	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();

		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(getKonohaScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		NonRuleBasedDamagerRepairer ndr =
			new NonRuleBasedDamagerRepairer(
				new TextAttribute(
					colorManager.getColor(IKonohaColorConstants.KONOHA_COMMENT)));
		reconciler.setDamager(ndr, KonohaPartitionScanner.KONOHA_COMMENT);
		reconciler.setRepairer(ndr, KonohaPartitionScanner.KONOHA_COMMENT);

		ndr = new NonRuleBasedDamagerRepairer(
				new TextAttribute(
					colorManager.getColor(IKonohaColorConstants.KONOHA_STRING)));
		reconciler.setDamager(ndr, KonohaPartitionScanner.KONOHA_STRING);
		reconciler.setRepairer(ndr, KonohaPartitionScanner.KONOHA_STRING);
		
		return reconciler;
	}
	
	public DeclHyperlinkDetector getDeclHyperlinkDetector(){
		if(declHyperlinkDetector == null){
			declHyperlinkDetector = new DeclHyperlinkDetector();
		}
		return declHyperlinkDetector;
	}

	public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer){
		IHyperlinkDetector[] parentDetectors = super.getHyperlinkDetectors(sourceViewer);
		IHyperlinkDetector[] myDetectors = new IHyperlinkDetector[parentDetectors.length + 1];
		System.arraycopy(parentDetectors, 0, myDetectors, 0, parentDetectors.length);
		myDetectors[myDetectors.length - 1] = getDeclHyperlinkDetector();
		return myDetectors;
	}
	
	public IHyperlinkPresenter getHyperlinkPresenter(ISourceViewer sourceViewer){
		return new MultipleHyperlinkPresenter(new RGB(0, 0, 255));
	}
}