/** Class for CLIPS built in Functions.
	Functions are either built in and implemented in Java,
	or they are defined in the CLIPS code, then they contain an AST...
	@author Ory Chowaw-Liebman
	
*/
import java.lang.*;

public class CLIPSFuncExit implements CLIPSFunction
{
	public String getDocString()
	{ return "Quit the interpreter."; }

	public CLIPSData execute(CLIPSData [] params)
	{ System.exit(0); return null; }
}