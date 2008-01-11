package jamochaeditor;

import org.eclipse.jface.text.rules.*;
import org.eclipse.jface.dialogs.*;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.core.runtime.Path;


import java.io.*;

/*
 * Extends WorldRule 
 * Adds a constructor that allows adding words from a textfile
 */
public class WordRuleFromFile extends WordRule {
	private IToken token;
	
	/**
	 * 
	 * @param filename	name of the file that contains words to highlight
	 * @param default_token	token to apply to text that does NOT match
	 * @param highlightcolor string that represents rgb values (e.g. "255,100,100")
	 */
	public WordRuleFromFile(String filename,IToken default_token,String highlightcolor,boolean bold,boolean italic) {
		super(new LetterDigitWordDetector(),default_token);
		token = new EasyToken(highlightcolor,bold,italic);
		InputStream infile;
		String line;
		
		try {
			infile = Activator.getDefault().openStream(new Path(filename));
			BufferedReader in = new BufferedReader (new InputStreamReader(infile));						
			while ((line = in.readLine())!= null) {				
				this.addWord(line,token);			
			}						
			in.close();
			infile.close();
		} catch(Exception e) {
			MessageDialog.openInformation(new Shell(),"Critical Error","The Jamocha plugin is damaged. The file \"" + filename + "\" is missing.");
		}			

		/*
		//read file and add words to ruleset
		FileReader infile = null;
		LineNumberReader reader = null;
		String line;
		try {
			infile = new FileReader(file);
		} catch(FileNotFoundException e) {
			
			MessageDialog.openInformation(new Shell(),"Critical Error","The Jamocha plugin is damaged. The file \"" + filename + "\" is missing.");
			
			//Activator.getDefault().getLog().log(status);
		}
		reader = new LineNumberReader(infile);
		
		try {
			do {
				line = reader.readLine();
				if(line!=null) this.addWord(line, token);//beware: maybe tokens need to be persistent?!
			} while(line!=null);
		}
		catch(Exception e) {}
		
		try { reader.close(); } catch(Exception e) {}
		try { infile.close(); } catch(Exception e) {}
		*/
		
	}
		
	
}
