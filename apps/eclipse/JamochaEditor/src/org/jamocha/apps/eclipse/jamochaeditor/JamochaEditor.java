 package org.jamocha.apps.eclipse.jamochaeditor;


import java.util.ResourceBundle;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.DefaultCharacterPairMatcher;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.ContentAssistAction;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;
import org.eclipse.jface.text.source.ISourceViewer;

public class JamochaEditor extends TextEditor {
	protected char[] fBrackets = {'(',')'};
	
	//overrides the createAction method from TextEditor
	//installs content assistant within the editor
	
	protected void createActions() {
		super.createActions();
		IAction action= new ContentAssistAction((ResourceBundle) JamochaPlugin.getDefault().getResourceBundle(),"ContentAssistProposal.",this);
		action.setActionDefinitionId(ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS);
		setAction("ContentAssist", action);

		}
	
	protected SourceViewerDecorationSupport getSourceViewerDecorationSupport(ISourceViewer viewer) {
		if (fSourceViewerDecorationSupport == null) {
			fSourceViewerDecorationSupport 
			= new SourceViewerDecorationSupport(viewer, getOverviewRuler(), getAnnotationAccess(), getSharedColors());
			configureSourceViewerDecorationSupport(fSourceViewerDecorationSupport);
		}
		return fSourceViewerDecorationSupport;
	}
	
	protected void configureSourceViewerDecorationSupport(SourceViewerDecorationSupport support) {
		super.configureSourceViewerDecorationSupport(support);

		support.setCharacterPairMatcher(new DefaultCharacterPairMatcher(fBrackets));
		support.setMatchingCharacterPainterPreferenceKeys(
				JamochaPlugin.PREF_BOOL_BRACKET,
				JamochaPlugin.PREF_COLOR_BRACKET);

	}
	

	
	public void initializeEditor () {
		super.initializeEditor();
		setDocumentProvider(new JamochaDocumentProvider());
		setSourceViewerConfiguration(new JamochaViewerConfiguration());
		setPreferenceStore(JamochaPlugin.getDefault().getPreferenceStore());

		/*char[] chars = {'(',')'};
		DefaultCharacterPairMatcher pairMatcher = new DefaultCharacterPairMatcher(chars);
		SourceViewerDecorationSupport support =
			new SourceViewerDecorationSupport(getSourceViewer(),getOverviewRuler(),
					getAnnotationAccess(),getSharedColors());
		support.setCharacterPairMatcher(pairMatcher);
		configureSourceViewerDecorationSupport(support);*/
	}

}
