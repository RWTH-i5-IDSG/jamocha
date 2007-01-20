/** Class for CLIPS built in division Function.
	@author Ory Chowaw-Liebman
*/
import java.lang.*;

public class CLIPSFuncDiv implements CLIPSFunction
{
	public String getDocString() { return "Division Function"; };
	
	public CLIPSData execute(CLIPSData [] params)
	{ 
		if (params[0].isFloat()|params[1].isFloat())
			return new CLIPSFloat(params[0].toFloat()/params[1].toFloat());
		else
			return new CLIPSInteger(params[0].toInteger()/params[1].toInteger());
		
	}	
}