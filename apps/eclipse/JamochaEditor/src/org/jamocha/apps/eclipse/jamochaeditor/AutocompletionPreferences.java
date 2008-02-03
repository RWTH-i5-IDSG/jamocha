package org.jamocha.apps.eclipse.jamochaeditor;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class AutocompletionPreferences implements
		IWorkbenchWindowActionDelegate {

	private static boolean selectionActive = true;
	
	public boolean getselection()
	{
		return selectionActive;
		
	}
	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IWorkbenchWindow window) {
		// TODO Auto-generated method stub

	}

	@Override
	public void run(IAction action) {
		selectionActive = !selectionActive;
		
		
		

	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub

	}

}
