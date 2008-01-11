package jamochaeditor;


import org.eclipse.ui.editors.text.TextEditor;

public class JamochaEditor extends TextEditor {
	
	public JamochaEditor() {
		super();
		setDocumentProvider(new JamochaDocumentProvider());
		setSourceViewerConfiguration(new JamochaViewerConfiguration());
	}

}
