package org.jamocha.apps.eclipse.jamochaeditor;



import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ContextInformation;
import org.eclipse.jface.text.contentassist.ContextInformationValidator;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import java.util.*;
//only for testing purposes with MessageDialog
//import org.eclipse.jface.dialogs.MessageDialog;
//import org.eclipse.swt.widgets.Shell;

/*
 * Implements a Proposal Processor which can autocomplete all
 * predefined Jamocha-functions (and only them)
 * gets the list of all functions by using the class FunctionsListParser 
 */


public class JamochaFunctionPropProcessor implements IContentAssistProcessor{

	
	
    private static final ICompletionProposal[] NO_PROPOSALS = new ICompletionProposal[0];
	private Vector<String> proposals;
	private HashMap<String,String> descriptionMap;
	
	public JamochaFunctionPropProcessor(){
		proposals = new VectorFromFile("files/functions.txt");
		descriptionMap = new HashMapFromFile("files/functions.txt");
	}
	
	
	// get the prefix on whose basis the proposals for autocompletion are computed
    private String getPrefix(ITextViewer viewer, int offset) throws BadLocationException {
        IDocument doc = viewer.getDocument();
        if (doc == null || offset > doc.getLength())
            return null;

        int length= 0;
        while (--offset >= 0 && Character.isJavaIdentifierPart(doc.getChar(offset)))
            length++;

        String result = new String(); 
        result = doc.get(offset + 1, length);
        //testing purposes
        //MessageDialog.openInformation(new Shell(),"Information","Prefix: "+result);
        return result;
    }
	
	
	
    @Override
    public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,
    		int offset) {

    	try {
    		String prefix = getPrefix(viewer,offset);
    		if (prefix == null || prefix.length() == 0)
    		// no prefix seems to be typed, so just list all the functions
    		{
    			ICompletionProposal[] propResults  = new ICompletionProposal[proposals.size()];
    			for(int i=0; i<proposals.size(); i++){
    				propResults[i] = getCompletionProposal(proposals.get(i),
							offset, prefix.length());

    			}
    			return propResults;
    		}
    		else{
    			
    			//create a vector with all functions that start with the prefix
    			Vector<String> validProposals = new Vector<String>(0,1);

    			for(int i=0; i<proposals.size(); i++){
    				if(proposals.get(i).startsWith(prefix)){
    					validProposals.add(proposals.get(i));
    				}
    			}
    			//no proposals found?
    			if(validProposals.size()==0)
    			{
    				return NO_PROPOSALS;
    			}
    			//now transfer the computed proposals to the demanded array
    			ICompletionProposal[] propResults  = new ICompletionProposal[validProposals.size()];

    			for(int j=0; j<validProposals.size(); j++){
    				//here the prefix is replaced by the full proposal,
    				//eventually with function description
    					propResults[j] = getCompletionProposal(validProposals.get(j),
    							offset, prefix.length());
    			}
    			validProposals.clear(); //clear vector of possible proposals for next time use

    			return propResults;
    		}
    	}
    	catch (BadLocationException e)
    	{
    		return NO_PROPOSALS;	
    	}
    }
    
    //a little helper function to generate CompletionProposals with or without descriptions
    private CompletionProposal getCompletionProposal(String name,int offset,
    		int prefixlength) {
    	CompletionProposal ret;
    	String desc;
    	
		desc = descriptionMap.get(name);
		
		if(desc==null)
			ret = new CompletionProposal(name,
					offset-prefixlength,
					prefixlength,
					name.length());
		else
			ret = new CompletionProposal(
				name,//replacement string
				offset-prefixlength,
				prefixlength,//length
				name.length(),//cursor position
				null,//image
				name,//displaystring. same as replacement string
				/*new ContextInformation("first","second")*/null,//context information
				desc
				);
		
		return ret;    	
    }


	@Override
	public IContextInformation[] computeContextInformation(ITextViewer viewer,
			int offset) {
		/*
		try {
			String prefix = getPrefix(viewer,offset);
			
			if(prefix=="test") {
				ContextInformation [] contextResults  = new ContextInformation[1];				
				contextResults[0] = new ContextInformation("contextDisplayString", "informationDisplayString");
				return contextResults;
			}
			
		} catch(BadLocationException e) {
			return null;
		}
		*/
		
		
		return null;
	}

	@Override
	public char[] getCompletionProposalAutoActivationCharacters() {
		char[] autochars = {'('}; //add all characters that should automatically trigger completion here
		return autochars;	
	}

	@Override
	public char[] getContextInformationAutoActivationCharacters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IContextInformationValidator getContextInformationValidator() {
		//return new ContextInformationValidator(this);//uncomment for tooltip after insertion
		return null;
	}

	@Override
	public String getErrorMessage() {
		// TODO Auto-generated method stub
		return null;
	}
}
	
	
