package org.jamocha.apps.eclipse.jamochaeditor;

//import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.jamocha.apps.eclipse.jamochaeditor.AutocompletionPreferences;

public class MenuActions implements  IWorkbenchWindowActionDelegate{

	IWorkbenchWindow activeWindow = null;
	
	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		// do nothing
	}

	@Override
	public void init(IWorkbenchWindow window) {
		activeWindow = window;
		
	}

	@Override
	public void run(IAction action) {
		
	AutocompletionPreferences Tmp = new AutocompletionPreferences();
	
			if (Tmp.getselection())
			{
	
			MessageDialog.openInformation(null, "about JEP", "Jamocha Editor Plugin\n Markus Arndt\n Ingmar Gebhardt\n RWTH 2007\nOn");
			}
			else
			{
				MessageDialog.openInformation(null, "about JEP", "Jamocha Editor Plugin\n Markus Arndt\n Ingmar Gebhardt\n RWTH 2007\nOff");
			}
			
		
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		// do nothing
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
}