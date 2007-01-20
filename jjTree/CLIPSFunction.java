/** Class representing a CLIPS Function.
	Functions are either built in and implemented in Java,
	or they are defined in the CLIPS code, then they contain an AST...
	@author Ory Chowaw-Liebman
*/
import java.lang.*;

public interface CLIPSFunction
{
	/** Return the Documentation Comment for this function.
		Built in Functions can return a constant string, deffunctions
		should have this set by the parser.
	*/
	public String getDocString();

	/** Perform this functions' Action.
	*/
	public CLIPSData execute(CLIPSData [] params);
}