/** Class for CLIPS built in multiplication Function.
	@author Ory Chowaw-Liebman
*/
import java.lang.*;

public class CLIPSFuncMul implements CLIPSFunction
{
	public String getDocString()
	{ return "Calculate the product of a list of numbers."; }

	public CLIPSData execute(CLIPSData [] params)
	{ 
		boolean isfloat=false;
		int i;
		for (i=0;i<params.length;i++) 
		{
			isfloat|=params[i].isFloat();
			if (isfloat) break;
		}
		if (isfloat)
		{
			double retval=1.0;
			for (i=0;i<params.length;i++) 
				retval*=params[i].toFloat();
			return new CLIPSFloat(retval);
		} else
		{
			long retval=1;
			for (i=0;i<params.length;i++) 
				retval*=params[i].toInteger();
			return new CLIPSInteger(retval);
		}
	}	
}