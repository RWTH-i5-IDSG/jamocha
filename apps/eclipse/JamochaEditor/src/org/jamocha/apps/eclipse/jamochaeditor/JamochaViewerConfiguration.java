package org.jamocha.apps.eclipse.jamochaeditor;

import org.jamocha.apps.eclipse.jamochaeditor.JamochaFunctionPropProcessor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.ui.texteditor.HippieProposalProcessor;


public class JamochaViewerConfiguration extends SourceViewerConfiguration {
	private JamochaRuleScanner scanner;
	
	
	protected JamochaRuleScanner getJamochaRuleScanner() {
		if (scanner == null) {
			scanner = new JamochaRuleScanner();
		}
		return scanner;
	}

	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();

		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(getJamochaRuleScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
		return reconciler;
	}
	
	
	// returns the content assistant of the editor
	// HippieProposalProcessor is the basic processor to generate proposal by prefixes
	// on the basis of known words
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer){
		ContentAssistant assistant = new ContentAssistant();
		ContentAssistant functionAssistant = new ContentAssistant();
		assistant.setContentAssistProcessor(new HippieProposalProcessor(),IDocument.DEFAULT_CONTENT_TYPE);
		functionAssistant.setContentAssistProcessor(new JamochaFunctionPropProcessor(), IDocument.DEFAULT_CONTENT_TYPE);
		functionAssistant.enableAutoActivation(true);
		
		/*add description tooltips*/
		assistant.setInformationControlCreator(getInformationControlCreator(sourceViewer));
		functionAssistant.setInformationControlCreator(getInformationControlCreator(sourceViewer));
		
		
		//now we have two different Proposal Processors available:
		// HippieProposalProcessor and JamochaFunctionsPropProcessor
		
		//return assistant; //using the HippieProposalProcessor
		//now let's use the functionAssistantProcessor
		AutocompletionPreferences Tmp = new AutocompletionPreferences();

		if (Tmp.getselection())
		{
			return functionAssistant;
		}
		else
		{
			return assistant;
			
		}
		
		
	}
	
	
	
	
	

}
