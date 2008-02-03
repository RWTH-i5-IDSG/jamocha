package org.jamocha.apps.eclipse.jamochaeditor;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

/*
 * Loads hashmap from file and maps 'key' -> 'data\ndata_line_2'
 * when the file looks like this:
 * 
 * key
 * ;data
 * ;data_line_2
 * 
 * Remark:
 * when there are data lines before the first key they are ignored
 */
public class HashMapFromFile extends HashMap {
	
	public HashMapFromFile(String filename) {
		InputStream infile;
		String line;
		String key = null;
		String data = null;
		boolean key_added = true;
		boolean data_added = true;

		try {
			infile = JamochaPlugin.getDefault().openStream(new Path(filename));
			BufferedReader in = new BufferedReader (new InputStreamReader(infile));

			while ((line = in.readLine())!= null) {
				
				if(key==null && line.charAt(0)!=';') {
					//this is the first key
					key = line;
					key_added = false;//we have a new key
				}
				else if(key != null && line.charAt(0)!=';') {
					//a new key
					
					//if old key isn't added yet and has data add it now
					if(!key_added && !data_added) {
						this.put(key, data);
						key_added = data_added = true;
					}
					
					key = line;
					key_added = false;
				}
				else if(key != null && line.charAt(0)==';') {
					//a new data line
					if(data_added) {/*the current data was already added, so the new data must
									belong to another key */
						data = line;
						data_added = false;
					} else { //this one must be concatenated
						data = data.concat("\n" + line);
						data_added = false;
					}
				}
				
			}
			//it may occur that the last key data set has not been added yet
			if(!key_added && !data_added) {
				this.put(key, data);
				key_added = data_added = true;
			}
			
			in.close();
			infile.close();
		} catch(Exception e) {
			MessageDialog.openInformation(new Shell(),"Critical Error","The Jamocha plugin is damaged. The file \"" + filename + "\" is missing.");
		}	
		
	}

}
