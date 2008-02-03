package org.jamocha.apps.eclipse.jamochaeditor;


import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;



public class JamochaPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	public JamochaPreferencePage() {
		super(GRID);
		
		IPreferenceStore prefs = JamochaPlugin.getDefault().getPreferenceStore();
		
		this.setPreferenceStore(prefs);
	}
	
	@Override
	protected void createFieldEditors() {
		Composite parent = getFieldEditorParent();

		addField(
				new BooleanFieldEditor(
					JamochaPlugin.PREF_BOOL_BRACKET,
				"    Enable Matching &Brackets:",
				parent)) ;
	 	addField(
				new ColorFieldEditor(
					JamochaPlugin.PREF_COLOR_BRACKET,
					"    B&racket color:",
					parent)) ;
	 	addField(
			new ColorFieldEditor(
					JamochaPlugin.PREF_COLOR_COMMENT,
				"    &Comment color:",
				parent)) ;
	 	addField(
				new ColorFieldEditor(
					JamochaPlugin.PREF_COLOR_STRING,
					"    &String color:",
					parent)) ;
	 	addField(
				new ColorFieldEditor(
					JamochaPlugin.PREF_COLOR_FUNCTION,
					"    &Function color:",
					parent)) ;

	}

	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub

	}

}
