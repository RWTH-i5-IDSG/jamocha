package org.jamocha.apps.eclipse.jamochaeditor;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;

import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

/*
 * It loads a Vector from a file ignoring lines that start with ;
 */
public class VectorFromFile extends Vector {
	
	public VectorFromFile(String filename) {
		InputStream infile;
		String line;

		try {
			infile = JamochaPlugin.getDefault().openStream(new Path(filename));
			BufferedReader in = new BufferedReader (new InputStreamReader(infile));						
			while ((line = in.readLine())!= null) {
				if(line.charAt(0) != ';')//don't read comments / descriptions
						this.add(line);
			}						
			in.close();
			infile.close();
			//MessageDialog.openInformation(new Shell(),"Information","Functions read.");
		} catch(Exception e) {
			MessageDialog.openInformation(new Shell(),"Critical Error","The Jamocha plugin is damaged. The file \"" + filename + "\" is missing.");
		}			

	}

}
